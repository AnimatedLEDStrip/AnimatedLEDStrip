/*
 *  Copyright (c) 2018-2020 AnimatedLEDStrip
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

import animatedledstrip.colors.ColorContainer
import animatedledstrip.colors.ColorContainerInterface
import animatedledstrip.colors.PreparedColorContainer
import animatedledstrip.utils.b
import animatedledstrip.utils.blend
import animatedledstrip.utils.g
import animatedledstrip.utils.r
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import org.pmw.tinylog.Logger
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

/**
 * Class that represents a LED strip that supports concurrency.
 *
 * @param stripInfo Information about this strip, such as number of
 * LEDs, etc.
 */
actual abstract class LEDStrip actual constructor(val stripInfo: StripInfo) {

    /**
     * The 'actual' LED strip
     */
    actual abstract val ledStrip: NativeLEDStrip


    /* Number of LEDs */

    /**
     * Number of LEDs
     */
    actual open val numLEDs: Int = stripInfo.numLEDs

    /**
     * A list of all LED indices
     */
    internal val physicalIndices: List<Int> = IntRange(0, stripInfo.numLEDs - 1).toList()


    /* Rendering */

    /**
     * Tracks if the strip is rendering. Starts `false` and is toggled to `true` in init.
     */
    actual var rendering: Boolean = false
        private set

    /**
     * The thread in which the rendering loop will run
     */
    @Suppress("EXPERIMENTAL_API_USAGE")
    private val renderThread = newFixedThreadPoolContext(50, "Render Loops")

    /**
     * `Map` containing `Mutex` instances for locking access to each
     * led while it is being used.
     */
    internal val pixelLocks = mutableMapOf<Int, Mutex>()

    internal val pixelChannels = mutableMapOf<Int, Channel<Long>>()

    val pixelSetters = mutableMapOf<Int, Job>()

    /**
     * A list that tracks the prolonged color of each pixel.
     *
     * Each pixel has two animatedledstrip.colors, its temporary color and then its prolonged color.
     * The prolonged color is a color for it to revert or fade back to.
     */
    protected actual val prolongedColors = mutableListOf<Long>().apply {
        for (i in IntRange(0, stripInfo.numLEDs - 1)) add(0)
    }

    /**
     * Map of pixel indices to `FadePixel` instances
     */
    private val fadeMap = mutableMapOf<Int, FadePixel>()


    /* Image Debugging */

    /**
     * Was image debugging enabled?
     */
    private val imageDebugging: Boolean = stripInfo.imageDebugging

    /**
     * Name of the output file for image debugging
     */
    private val fileName: String = when (stripInfo.fileName) {
        null -> "signature_${SimpleDateFormat("MMDDYY_hhmmss").format(Date())}.csv"
        else -> {
            if (stripInfo.fileName.endsWith(".csv")) stripInfo.fileName
            else "${stripInfo.fileName}.csv"
        }
    }

    /**
     * The file that the csv output will be saved to if image debugging is enabled
     */
    private lateinit var outFile: FileWriter

    /**
     * Renders before image debugging writes to `outFile`
     */
    private val rendersBeforeSave: Int = stripInfo.rendersBeforeSave ?: 1000

    /**
     * The thread used to save values to `outFile` so the program doesn't
     * experience slowdowns because of I/O
     */
    @Suppress("EXPERIMENTAL_API_USAGE")
    private val outThread = newSingleThreadContext("Image Debug Save Thread")

    val saveStateChannel = Channel<String>(Channel.UNLIMITED)

    init {
        if (imageDebugging) outFile = FileWriter(fileName, true)
    }

    private val saveStateBuffer = StringBuilder()

    val saveStateCoroutine = GlobalScope.launch(Dispatchers.IO) {
        for (save in saveStateChannel) {
            outFile.append(save)
            Logger.debug("Wrote $rendersBeforeSave renders to file")
        }
    }

    /**
     * `Map` containing `Mutex` instances for locking write access to
     * each led while it is being written.
     */
    private val writeLocks = mutableMapOf<Int, Mutex>()

    /**
     * `Mutex` tracking if a thread is saving to `outFile` in order to prevent
     * overlaps.
     */
    private val outLock = Mutex()

    /**
     * Buffer that stores renders until `renderNum` in `toggleRender`
     * reaches `rendersBeforeSave` at which point the buffer is appended
     * to `outFile` and cleared.
     */
    private val buffer = if (imageDebugging) StringBuilder() else null


    /* Initialize */

    init {
        if (stripInfo.fileName != null && !stripInfo.imageDebugging)
            Logger.warn("Output file name is specified but image debugging is disabled")
        for (i in IntRange(0, stripInfo.numLEDs - 1)) {
            pixelLocks += Pair(i, Mutex())
            pixelChannels += Pair(i, Channel(Channel.CONFLATED))
            pixelSetters += Pair(i, GlobalScope.launch {
                for (c in pixelChannels[i]!!)
                    ledStrip.setPixelColor(i, c.toInt())
            })
            writeLocks += Pair(i, Mutex())
            fadeMap += Pair(i, FadePixel(i))
        }
        runBlocking { delay(2000) }
        toggleRender()
    }


    /* Rendering */

    /**
     * Toggle strip rendering. If strip is not rendering, this will launch a new
     * thread that renders the strip constantly until this is called again.
     *
     * NOTE: If image debugging is enabled, then any renders waiting to lock
     * outLock when the program is terminated will be lost.
     */
    actual fun toggleRender() {
        rendering = when (rendering) {
            true -> false
            false -> {
                GlobalScope.launch(renderThread) {
                    delay(5000)
                    var renderNum = 0
                    while (rendering) {
                        try {
                            ledStrip.render()
                        } catch (e: NullPointerException) {
                            Logger.error("LEDStrip NullPointerException when rendering")
                            delay(1000)
                        }
                        if (imageDebugging) {
                            pixelTemporaryColorList.forEach {
                                saveStateBuffer.append("${it.r},${it.g},${it.b},")
                            }
                            saveStateBuffer.append("0,0,0\n")

                            if (renderNum++ >= rendersBeforeSave) {
                                saveStateChannel.send(saveStateBuffer.toString())
                                saveStateBuffer.clear()
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


    /* Set pixel color */

    /**
     * Set the pixel's color. If `prolonged` is true, set the prolonged color,
     * otherwise set the actual color.
     */
    protected actual fun setPixelColor(pixel: Int, color: ColorContainerInterface, prolonged: Boolean) {
        when (color) {
            is ColorContainer -> error("")
            is PreparedColorContainer -> setPixelColor(pixel, color[pixel], prolonged)
        }
    }

    /**
     * Set the pixel's color. If `prolonged` is true, set the prolonged color,
     * otherwise set the actual color.
     */
    protected actual fun setPixelColor(pixel: Int, color: Long, prolonged: Boolean) {
        require(pixel in physicalIndices) { "$pixel not in indices (${physicalIndices.first()}..${physicalIndices.last()})" }
        when (prolonged) {
            true -> {
                prolongedColors[pixel] = color
                // Call this function again with prolonged = false to set the actual color
                if (fadeMap[pixel]?.isFading == false) setPixelColor(pixel, color, prolonged = false)
            }
            false -> {
//                writeLocks[pixel]?.tryWithLock(owner = "Pixel $pixel") {
//                    ledStrip.setPixelColor(pixel, color.toInt())
//                } ?: Logger.warn("Pixel $pixel does not exist")
                runBlocking {
                    pixelChannels[pixel]?.send(color) ?: Logger.warn("err with set")
                }
            }
        }
    }

    /**
     * Revert a pixel to its prolonged color. If it is in the middle
     * of a fade, don't revert.
     */
    // TODO: Maybe rethink the guard here
    protected actual fun revertPixel(pixel: Int) {
        if (fadeMap[pixel]?.isFading == false) setPixelColor(pixel, prolongedColors[pixel], prolonged = false)
    }


    /* Get pixel color */

    /**
     * Get the color of a pixel. If `prolonged` is true, get the prolonged
     * color, otherwise get the pixel's actual color.
     */
    protected actual fun getPixelColor(pixel: Int, prolonged: Boolean): Long {
        require(pixel in physicalIndices) { "$pixel not in indices (${physicalIndices.first()}..${physicalIndices.last()})" }
        return when (prolonged) {
            true -> prolongedColors[pixel]
            false -> ledStrip.getPixelColor(pixel).toLong()
        }
    }

    /**
     * Get the prolonged animatedledstrip.colors of all pixels as a `List<Long>`
     */
    actual val pixelProlongedColorList: List<Long>
        get() {
            val temp = mutableListOf<Long>()
            for (i in physicalIndices) temp.add(getPixelColor(i, prolonged = true))
            return temp
        }

    /**
     * Get the temporary animatedledstrip.colors of all pixels as a `List<Long>`
     */
    actual val pixelTemporaryColorList: List<Long>
        get() {
            val temp = mutableListOf<Long>()
            for (i in physicalIndices) temp.add(getPixelColor(i, prolonged = false))
            return temp
        }

    /* Fading */

    /**
     * Helper class for fading a pixel from its current color to its prolonged color.
     */
    actual inner class FadePixel actual constructor(private val pixel: Int) {
        /**
         * Which thread is currently fading this pixel -
         * used so another thread can take over mid-fade if necessary.
         */
        actual var owner = ""

        /**
         * Track if the pixel is in the middle of a fade
         */
        actual var isFading = false
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
         * @param timeout How long before the fade should be aborted (in milliseconds)
         */
        actual fun fade(amountOfOverlay: Int, delay: Int, timeout: Int) {
            val myName = Thread.currentThread().name
            owner = myName
            val stopTime = LocalDateTime.now().plusNanos(timeout * 1000000L)

            var curTemp = getPixelColor(pixel, prolonged = false)
            var curPro = getPixelColor(pixel, prolonged = true)
            while (curTemp != curPro && LocalDateTime.now().isBefore(stopTime)) {
                if (owner != myName) break
                isFading = true
                withPixelLock(pixel) {
                    setPixelColor(
                        pixel,
                        blend(
                            existing = curTemp,
                            overlay = curPro,
                            amountOfOverlay = amountOfOverlay,
                        ),
                        prolonged = false,
                    )
                }
                runBlocking { delay(delay.toLong()) }
            }
            // If loop was not broken due to another thread taking over the fading,
            // reset the pixel and indicate that this pixel is no longer fading
            if (owner == myName) {
                isFading = false
                revertPixel(pixel)
            }
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
    protected actual fun fadePixel(pixel: Int, amountOfOverlay: Int, delay: Int, timeout: Int) {
        fadeMap[pixel]?.fade(amountOfOverlay = amountOfOverlay, delay = delay, timeout = timeout)
    }

}
