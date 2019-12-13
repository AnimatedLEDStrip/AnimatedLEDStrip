/*
 *  Copyright (c) 2019 AnimatedLEDStrip
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package animatedledstrip.leds

import animatedledstrip.colors.ColorContainerInterface
import animatedledstrip.leds.sections.LEDStripSection
import animatedledstrip.leds.sections.SectionableLEDStrip
import animatedledstrip.utils.blend
import animatedledstrip.utils.delayBlocking
import animatedledstrip.utils.tryWithLock
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.pmw.tinylog.Logger
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Class that represents a LED strip that supports concurrency.
 *
 * @param stripInfo Information about this strip, such as number of
 * LEDs, etc.
 */
abstract class LEDStrip(
    val stripInfo: StripInfo
) : SectionableLEDStrip {


    val numLEDs: Int = stripInfo.numLEDs
    private val imageDebugging: Boolean = stripInfo.imageDebugging
    private val fileName: String = when (stripInfo.fileName) {
        null -> "signature_${SimpleDateFormat("MMDDYY_hhmmss").format(Date())}.csv"
        else -> {
            if (stripInfo.fileName.endsWith(".csv")) stripInfo.fileName
            else "${stripInfo.fileName}.csv"
        }
    }
    private val rendersBeforeSave: Int = stripInfo.rendersBeforeSave ?: 1000

    val indices: List<Int> = IntRange(0, numLEDs - 1).toList()

    /**
     * The LED Strip
     */
    abstract var ledStrip: NativeLEDStrip

    /**
     * `Map` containing `Mutex` instances for locking access to each
     * led while it is being used.
     */
    internal val pixelLocks = mutableMapOf<Int, Mutex>()

    /**
     * `Map` containing `Mutex` instances for locking write access to
     * each led while it is being written.
     */
    private val writeLocks = mutableMapOf<Int, Mutex>()

    /**
     * The thread in which the rendering loop will run.
     */
    @Suppress("EXPERIMENTAL_API_USAGE")
    private val renderThread = newFixedThreadPoolContext(50, "Render Loops")

    /**
     * The thread used to save values to `outFile` so the program doesn't
     * experience slowdowns because of I/O.
     */
    @Suppress("EXPERIMENTAL_API_USAGE")
    private val outThread = newSingleThreadContext("Image Debug Save Thread")

    /**
     * Tracks if the strip is rendering. Starts `false` and is toggled to `true` in init.
     */
    var rendering = false
        private set

    /**
     * The file that the csv output will be saved to if image debugging is enabled.
     */
    private lateinit var outFile: FileWriter

    /**
     * Buffer that stores renders until `renderNum` in `toggleRender` reaches `rendersBeforeSave`
     * at which point buffer is appended to `outFile` and cleared.
     */
    private val buffer = if (imageDebugging) StringBuilder() else null

    /**
     * `Mutex` tracking if a thread is saving to `outFile` in order to prevent
     * overlaps.
     */
    private val outLock = Mutex()

    /**
     * A list that tracks the prolonged color of each pixel.
     *
     * Each pixel has two colors, its actual color and then its prolonged color.
     * The prolonged color is a color for it to revert or fade back to.
     */
    val prolongedColors = mutableListOf<Long>().apply {
        for (i in this@LEDStrip.indices) add(0)
    }

    /**
     * Map of pixel indices to `FadePixel` instances.
     */
    private val fadeMap = mutableMapOf<Int, FadePixel>()

    init {
        if (stripInfo.fileName != null && !stripInfo.imageDebugging)
            Logger.warn("Output file name is specified but image debugging is disabled")
        for (i in indices) {
            pixelLocks += Pair(i, Mutex())
            writeLocks += Pair(i, Mutex())
            fadeMap += Pair(i, FadePixel(i))
        }
        runBlocking { delay(2000) }
        toggleRender()
    }

    /**
     * Toggle strip rendering. If strip is not rendering, this will launch a new
     * thread that renders the strip constantly until this is called again.
     *
     * NOTE: If image debugging is enabled, then any renders waiting to lock
     * outLock when the program is terminated will be lost.
     */
    fun toggleRender() {
        rendering = when (rendering) {
            true -> {
                if (imageDebugging && ::outFile.isInitialized) outFile.close()      // Close debug file
                false
            }
            false -> {
                if (imageDebugging) outFile = FileWriter(fileName, true)   // Open debug file if appropriate
                GlobalScope.launch(renderThread) {
                    delay(5000)
                    var renderNum = 0
                    while (rendering) {
                        try {
                            ledStrip.render()
                        } catch (e: NullPointerException) {
                            Logger.error("LEDStrip NullPointerException")
                            delayBlocking(1000)
                        }
                        if (imageDebugging) {
                            pixelColorList.forEach { buffer!!.append("${(it and 0xFF0000 shr 16).toInt()},${(it and 0x00FF00 shr 8).toInt()},${(it and 0x0000FF).toInt()},") }
                            buffer!!.append("0,0,0\n")

                            if (renderNum++ >= rendersBeforeSave) {
                                GlobalScope.launch(outThread) {
                                    outLock.withLock {
                                        outFile.append(buffer)
                                        buffer.clear()
                                    }
                                    Logger.debug("Wrote $rendersBeforeSave renders to file")
                                }
                                renderNum = 0
                            }
                        }
                        delay(5)
                    }
                }
                true        // Set rendering to true
            }
        }
    }


    /**
     * Helper object for creating [LEDStripSection]s.
     */
    object SectionCreator {
        /**
         * Create a new [LEDStripSection].
         *
         * @param startPixel First pixel in the section
         * @param endPixel Last pixel in the section
         * @param ledStrip [AnimatedLEDStrip] instance to bind the section to
         */
        fun new(startPixel: Int, endPixel: Int, ledStrip: AnimatedLEDStrip) =
            LEDStripSection(startPixel, endPixel, ledStrip)

        /**
         * Create a new [LEDStripSection].
         *
         * @param pixels An IntRange denoting the pixels in the section
         * @param ledStrip [AnimatedLEDStrip] instance to bind the section to
         */
        fun new(pixels: IntRange, ledStrip: AnimatedLEDStrip) =
            LEDStripSection(pixels, ledStrip)
    }


    /* Set pixel color */

    fun setPixelColor(pixel: Int, color: ColorContainerInterface, prolonged: Boolean = false) =
        setPixelColor(pixel, color.prepare(numLEDs)[pixel], prolonged)

    fun setPixelColor(pixel: Int, color: Long, prolonged: Boolean = false) {
        require(pixel in indices)
        when (prolonged) {
            true -> {
                prolongedColors[pixel] = color
                // Call this function again with prolonged = false to set the actual color
                if (fadeMap[pixel]?.isFading == false) setPixelColor(pixel, color, prolonged = false)
            }
            false -> {
                writeLocks[pixel]?.tryWithLock(owner = "Pixel $pixel") {
                    ledStrip.setPixelColor(pixel, color.toInt())
                } ?: Logger.warn("Pixel $pixel does not exist")
            }
        }
    }

    /**
     * Revert a pixel to its prolonged color. If it is in the middle
     * of a fade, don't revert
     *
     * @param pixel The pixel to revert
     */
    // TODO: Maybe rethink the guard here
    fun revertPixel(pixel: Int) {
        if (fadeMap[pixel]?.isFading == false) setPixelColor(pixel, prolongedColors[pixel])
    }


    /* Set strip color */

    /**
     * Set the color of all pixels in the strip. If prolonged is true,
     * set the prolonged color, otherwise set the actual color.
     *
     * @param color The color to use
     */
    fun setStripColor(color: ColorContainerInterface, prolonged: Boolean = false) {
        for (i in indices) setPixelColor(i, color, prolonged)
    }

    /**
     * Set the color of all pixels in the strip. If prolonged is true,
     * set the prolonged color, otherwise set the actual color.
     *
     * @param color A `Long` representing the color to use
     */
    fun setStripColor(color: Long, prolonged: Boolean = false) {
        for (i in indices) setPixelColor(i, color, prolonged)
    }

    /**
     * Set the color of the whole strip.
     */
    var color: ColorContainerInterface
        get() = error("Cannot get color of whole strip")
        set(value) {
            setStripColor(value, prolonged = false)
        }

    /* Set section color */

    /**
     * Set the color of all pixels in a section of the strip. If prolonged is true,
     * set the prolonged color, otherwise set the pixel's actual color.
     *
     * `start` and `end` are inclusive. `start` should be less than or equal to `end`.
     */
    override fun setSectionColor(start: Int, end: Int, color: ColorContainerInterface, prolonged: Boolean) {
        require(start <= end)
        setSectionColor(start..end, color, prolonged)
    }

    /**
     * Set the color of all pixels in a section of the strip. If prolonged is true,
     * set the prolonged color, otherwise set the pixel's actual color.
     *
     * `start` and `end` are inclusive. `start` should be less than or equal to `end`.
     */
    override fun setSectionColor(start: Int, end: Int, color: Long, prolonged: Boolean) {
        require(start <= end)
        setSectionColor(start..end, color, prolonged)
    }

    /**
     * Set the color of all pixels in a section of the strip. If prolonged is true,
     * set the prolonged color, otherwise set the pixel's actual color.
     *
     * `range` is inclusive.
     */
    override fun setSectionColor(range: IntRange, color: ColorContainerInterface, prolonged: Boolean) {
        require(range.first in indices)
        require(range.last in indices)

        val pColor = color.prepare(range.last - range.first + 1)
        for (i in range) setPixelColor(i, pColor[i - range.first], prolonged)
    }

    /**
     * Set the color of all pixels in a section of the strip. If prolonged is true,
     * set the prolonged color, otherwise set the pixel's actual color.
     *
     * `range` is inclusive.
     */
    override fun setSectionColor(range: IntRange, color: Long, prolonged: Boolean) {
        require(range.first in indices)
        require(range.last in indices)

        for (i in range) setPixelColor(i, color, prolonged)
    }

    /* Get pixel color */

    fun getPixelColor(pixel: Int, prolonged: Boolean = false): Long {
        require(pixel in indices)
        return when (prolonged) {
            true -> {
                prolongedColors[pixel]
            }
            false -> {
                ledStrip.getPixelColor(pixel).toLong()
            }
        }
    }

    /**
     * Use index operator for getPixelColor operations.
     *
     * @param pixel The pixel to find the color of
     */
    operator fun get(pixel: Int) = getPixelColor(pixel, prolonged = false)

    /**
     * Get the colors of all pixels as a `List<Long>`
     */
    val pixelColorList: List<Long>
        get() {
            val temp = mutableListOf<Long>()
            for (i in indices) temp.add(getPixelColor(i, prolonged = false))
            return temp
        }

    val pixelProlongedColorList: List<Long>
        get() {
            val temp = mutableListOf<Long>()
            for (i in indices) temp.add(getPixelColor(i, prolonged = true))
            return temp
        }

    /**
     * Helper class for fading a pixel from its current color to its prolonged color.
     */
    inner class FadePixel(private val pixel: Int) {
        /**
         * Which thread is currently fading this pixel -
         * used so another thread can take over mid-fade if necessary.
         */
        var owner = ""

        /**
         * Track if the pixel is in the middle of a fade
         */
        var isFading = false
            private set

        /**
         * Fade a pixel from its current color to its current prolonged color.
         *
         * Blends the current color with pixel's current prolonged color using [blend] every
         * `delay` milliseconds until the pixel reaches its prolonged color or 40
         * iterations have passed, whichever comes first. The pixel's prolonged color
         * is reevaluated every iteration, allowing it to fade into a changing background
         * (i.e. a Smooth Chase animation).
         */
        fun fade(amountOfOverlay: Int = 25, delay: Int = 30) {
            val myName = Thread.currentThread().name
            owner = myName
            var i = 0
            while (getPixelColor(pixel, prolonged = false) != prolongedColors[pixel] && i <= 40) {
                if (owner != myName) break
                isFading = true
                i++
                var doDelay = true
                withPixelLock(pixel) {
                    setPixelColor(
                        pixel,
                        blend(
                            getPixelColorOrNull(pixel, prolonged = false)
                                ?: run { doDelay = false; return@withPixelLock Unit },
                            prolongedColors[pixel],
                            amountOfOverlay
                        )
                    )
                }
                if (doDelay) delayBlocking(delay)
            }
            // If loop was not broken due to another thread taking over the fading,
            // reset the pixel and indicate that this pixel is no longer fading
            if (owner == myName) revertPixel(pixel)
            if (owner == myName) isFading = false
        }
    }

    /**
     * Helper function for fading a pixel.
     *
     * @param pixel The pixel to be faded
     * @param amountOfOverlay How much the pixel should fade in each iteration
     * @param delay Time in milliseconds between iterations
     * @see FadePixel
     */
    fun fadePixel(pixel: Int, amountOfOverlay: Int = 25, delay: Int = 30) {
        fadeMap[pixel]?.fade(amountOfOverlay = amountOfOverlay, delay = delay)
    }
}
