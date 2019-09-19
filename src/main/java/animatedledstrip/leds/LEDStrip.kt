package animatedledstrip.leds

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


import animatedledstrip.colors.ColorContainer
import animatedledstrip.colors.ColorContainerInterface
import animatedledstrip.leds.sections.LEDStripSection
import animatedledstrip.utils.blend
import animatedledstrip.utils.delayBlocking
import animatedledstrip.utils.tryWithLock
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.tinylog.Logger
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*


/**
 * A subclass of [LEDStripNonConcurrent] adding support for concurrency.
 *
 * @param numLEDs Number of LEDs in the strip
 * @param imageDebugging Should a csv file be created containing all renders of
 * the strip?
 * @param fileName Specify a name for the image debug file (only useful if imageDebugging
 * is enabled)
 */
abstract class LEDStrip(
    numLEDs: Int,
    private val imageDebugging: Boolean = false,
    fileName: String? = null,
    private val rendersBeforeSave: Int = 1000
) : LEDStripNonConcurrent(numLEDs) {

    /**
     * `Map` containing `Mutex` instances for locking access to each led while it is
     * being used.
     */
    internal val pixelLocks = mutableMapOf<Int, Mutex>()

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
     * Tracks if the strip is rendering. Starts `false` and is set to `true` in init.
     */
    var rendering = false
        private set

    private val _fileName =
        fileName ?: "signature_${SimpleDateFormat("MMDDYY_hhmmss").format(Date())}.csv"

    /**
     * The file that the csv output will be saved to if image debugging is enabled.
     */
    private lateinit var outFile: FileWriter

    /**
     * Buffer that stores renders until `renderNum` in `toggleRender` reaches 1000,
     * at which point buffer is appended to `outFile` and cleared.
     */
    private val buffer = if (imageDebugging) StringBuilder() else null

    /**
     * `Mutex` tracking if a thread is saving to `outFile` in order to prevent
     * overlaps.
     */
    private val outLock = Mutex()

    val prolongedColors = mutableListOf<Long>().apply {
        for (i in 0 until numLEDs) add(0)
    }

    /**
     * Map of pixel indices to `FadePixel` instances.
     */
    private val fadeMap = mutableMapOf<Int, FadePixel>()

    init {
        if (fileName != null) require(imageDebugging)
        for (i in 0 until numLEDs) {
            pixelLocks += Pair(i, Mutex())
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
                if (imageDebugging) outFile = FileWriter(_fileName, true)   // Open debug file if appropriate
                GlobalScope.launch(renderThread) {
                    delay(5000)
                    var renderNum = 0
                    while (rendering) {
                        try {
                            ledStrip.render()
                        } catch (e: NullPointerException) {
                            Logger.error { "LEDStrip NullPointerException" }
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
                                    Logger.debug { "Wrote $rendersBeforeSave renders to file" }
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

    /**
     * Sets a pixel's color. If another thread has locked the pixel's `Mutex`,
     * this skips setting the pixel's color and returns.
     *
     * @param pixel The pixel to change
     * @param color The color to set the pixel to
     */
    override fun setPixelColor(pixel: Int, color: ColorContainerInterface) {
        pixelLocks[pixel]?.tryWithLock(owner = "Pixel $pixel") {
            super.setPixelColor(pixel, color)
        } ?: Logger.warn { "Pixel $pixel does not exist" }
    }

    override fun setPixelColor(pixel: Int, color: Long) {
        pixelLocks[pixel]?.tryWithLock(owner = "Pixel $pixel") {
            super.setPixelColor(pixel, color)
        } ?: Logger.warn { "Pixel $pixel does not exist" }
    }

    fun setProlongedPixelColor(pixel: Int, colorValues: ColorContainerInterface) {
        val colors = colorValues.prepare(numLEDs)
        prolongedColors[pixel] = colors[pixel]
        if (fadeMap[pixel]?.isFading == false) setPixelColor(pixel, colors[pixel])
    }

    fun setProlongedPixelColor(pixel: Int, color: Long) {
        setProlongedPixelColor(pixel, ColorContainer(color))
    }

    // TODO: Maybe rethink the guard here
    fun revertPixel(pixel: Int) {
        if (fadeMap[pixel]?.isFading == false) setPixelColor(pixel, prolongedColors[pixel])
    }


    /* Set strip color */

    override fun setStripColor(colorValues: ColorContainerInterface) {
        setStripColor(colorValues, true)
    }

    override fun setStripColor(color: Long) {
        setStripColor(color, true)
    }

    fun setStripColor(colorValues: ColorContainerInterface, prolonged: Boolean) {
        val colors = colorValues.prepare(numLEDs)
        if (prolonged) for (i in 0 until numLEDs) setProlongedPixelColor(i, colors)
        else super.setStripColor(colors)
    }

    fun setStripColor(color: Long, prolonged: Boolean) {
        if (prolonged) for (i in 0 until numLEDs) setProlongedPixelColor(i, color)
        else super.setStripColor(color)
    }


    /* Set section color */

    override fun setSectionColor(start: Int, end: Int, colorValues: ColorContainerInterface) {
        setSectionColor(start, end, colorValues, true)
    }

    override fun setSectionColor(start: Int, end: Int, color: Long) {
        setSectionColor(start, end, color, true)
    }

    fun setSectionColor(start: Int, end: Int, colorValues: ColorContainerInterface, prolonged: Boolean) {
        require(end >= start)
        val colors = colorValues.prepare(end - start + 1)
        if (prolonged) for (i in start..end) setProlongedPixelColor(i, colors[i - start])
        else super.setSectionColor(start, end, colors)
    }

    fun setSectionColor(start: Int, end: Int, color: Long, prolonged: Boolean) {
        setSectionColor(start, end, ColorContainer(color), prolonged)
    }


    /* Get methods */

    /**
     * Get the prolonged color of a pixel.
     *
     * @param pixel The pixel to find the color of
     * @return The color of the pixel
     * @throws IllegalArgumentException If pixel is not a valid index
     */
    override fun getPixelColor(pixel: Int): Long {
        require(pixel in prolongedColors.indices)
        return prolongedColors[pixel]
    }

    /**
     * Get the actual color of a pixel.
     *
     * @param pixel The pixel to find the color of
     * @return The color of the pixel
     * @throws IllegalArgumentException If pixel is not a valid index
     */
    fun getActualPixelColor(pixel: Int): Long = super.getPixelColor(pixel)

    /**
     * Get the colors of all pixels as a `List<Long>`
     */
    override val pixelColorList: List<Long>
        get() {
            val temp = mutableListOf<Long>()
            for (i in 0 until numLEDs) temp.add(getActualPixelColor(i))
            return temp
        }

    /**
     * Overrides LEDStripNonConcurrent's show() to stop any manual renders.
     * Renders are handled by a thread created during an init block above.
     */
    override fun show() {}


    /**
     * Helper class for fading a pixel from its current color to its prolonged color.
     *
     * @property pixel The pixel associated with this instance
     */
    inner class FadePixel(private val pixel: Int) {
        /**
         * Which thread is currently fading this pixel -
         * used so another thread can take over mid-fade if necessary.
         */
        var owner = ""

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
         *
         * @param amountOfOverlay How much the pixel should fade in each iteration
         * @param delay Time in milliseconds between iterations
         */
        fun fade(amountOfOverlay: Int = 25, delay: Int = 30) {
            val myName = Thread.currentThread().name
            owner = myName
            var i = 0
            while (getActualPixelColor(pixel) != prolongedColors[pixel] && i <= 40) {
                if (owner != myName) break
                isFading = true
                i++
                setPixelColor(
                    pixel,
                    blend(
                        getActualPixelColorOrNull(pixel) ?: continue,
                        prolongedColors[pixel], amountOfOverlay
                    )
                )
                delayBlocking(delay)
            }
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
