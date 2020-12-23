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

import animatedledstrip.colors.ColorContainerInterface
import animatedledstrip.utils.blend

/**
 * Class that represents a LED strip that supports concurrency.
 *
 * @param stripInfo Information about this strip, such as number of
 * LEDs, etc.
 */
expect abstract class LEDStrip(stripInfo: StripInfo) {

    /**
     * The 'actual' LED strip
     */
    abstract val ledStrip: NativeLEDStrip


    /* Number of LEDs */

    /**
     * Number of LEDs
     */
    open val numLEDs: Int

    /**
     * A list of all LED indices
     */
//    val physicalIndices: List<Int>


    /* Rendering */

    /**
     * Tracks if the strip is rendering. Starts `false` and is toggled to `true` in init.
     */
    var rendering: Boolean
        private set


    /**
     * A list that tracks the prolonged color of each pixel.
     *
     * Each pixel has two animatedledstrip.colors, its temporary color and then its prolonged color.
     * The prolonged color is a color for it to revert or fade back to.
     */
    protected val prolongedColors: MutableList<Long>

    /**
     * Map of pixel indices to `FadePixel` instances
     */



    /* Image Debugging */

//    /**
//     * Was image debugging enabled?
//     */
//    val imageDebugging: Boolean

//    /**
//     * Name of the output file for image debugging
//     */
//    private val fileName: String = when (stripInfo.fileName) {
//        null -> "signature_${SimpleDateFormat("MMDDYY_hhmmss").format(Date())}.csv"
//        else -> {
//            if (stripInfo.fileName.endsWith(".csv")) stripInfo.fileName
//            else "${stripInfo.fileName}.csv"
//        }
//    }
//
//    /**
//     * The file that the csv output will be saved to if image debugging is enabled
//     */
//    private lateinit var outFile: FileWriter
//
//    /**
//     * Renders before image debugging writes to `outFile`
//     */
//    private val rendersBeforeSave: Int = stripInfo.rendersBeforeSave ?: 1000
//
//    /**
//     * The thread used to save values to `outFile` so the program doesn't
//     * experience slowdowns because of I/O
//     */
//    @Suppress("EXPERIMENTAL_API_USAGE")
//    private val outThread = newSingleThreadContext("Image Debug Save Thread")
//
//    val saveStateChannel = Channel<String>(Channel.UNLIMITED)
//
//    init {
//        if (imageDebugging) outFile = FileWriter(fileName, true)
//    }
//
//    private val saveStateBuffer = StringBuilder()
//
//    val saveStateCoroutine = GlobalScope.launch(Dispatchers.IO) {
//        for (save in saveStateChannel) {
//            outFile.append(save)
//            Logger.debug("Wrote $rendersBeforeSave renders to file")
//        }
//    }
//
//    /**
//     * `Map` containing `Mutex` instances for locking write access to
//     * each led while it is being written.
//     */
//    private val writeLocks = mutableMapOf<Int, Mutex>()
//
//    /**
//     * `Mutex` tracking if a thread is saving to `outFile` in order to prevent
//     * overlaps.
//     */
//    private val outLock = Mutex()
//
//    /**
//     * Buffer that stores renders until `renderNum` in `toggleRender`
//     * reaches `rendersBeforeSave` at which point the buffer is appended
//     * to `outFile` and cleared.
//     */
//    private val buffer = if (imageDebugging) StringBuilder() else null
//
//
//    /* Initialize */
//
//    init {
//        if (stripInfo.fileName != null && !stripInfo.imageDebugging)
//            Logger.warn("Output file name is specified but image debugging is disabled")
//        for (i in IntRange(0, stripInfo.numLEDs - 1)) {
//            pixelLocks += Pair(i, Mutex())
//            pixelChannels += Pair(i, Channel(Channel.CONFLATED))
//            pixelSetters += Pair(i, GlobalScope.launch {
//                for (c in pixelChannels[i]!!)
//                    ledStrip.setPixelColor(i, c.toInt())
//            })
//            writeLocks += Pair(i, Mutex())
//            fadeMap += Pair(i, FadePixel(i))
//        }
//        runBlocking { delay(2000) }
//        toggleRender()
//    }


    /* Rendering */

    /**
     * Toggle strip rendering. If strip is not rendering, this will launch a new
     * thread that renders the strip constantly until this is called again.
     *
     * NOTE: If image debugging is enabled, then any renders waiting to lock
     * outLock when the program is terminated will be lost.
     */
    fun toggleRender()


    /* Set pixel color */

    /**
     * Set the pixel's color. If `prolonged` is true, set the prolonged color,
     * otherwise set the actual color.
     */
    protected fun setPixelColor(pixel: Int, color: ColorContainerInterface, prolonged: Boolean)

    /**
     * Set the pixel's color. If `prolonged` is true, set the prolonged color,
     * otherwise set the actual color.
     */
    protected fun setPixelColor(pixel: Int, color: Long, prolonged: Boolean)

    /**
     * Revert a pixel to its prolonged color. If it is in the middle
     * of a fade, don't revert.
     */
    // TODO: Maybe rethink the guard here
    protected fun revertPixel(pixel: Int)


    /* Get pixel color */

    /**
     * Get the color of a pixel. If `prolonged` is true, get the prolonged
     * color, otherwise get the pixel's actual color.
     */
    protected fun getPixelColor(pixel: Int, prolonged: Boolean): Long

    /**
     * Get the prolonged animatedledstrip.colors of all pixels as a `List<Long>`
     */
    val pixelProlongedColorList: List<Long>
    /**
     * Get the temporary animatedledstrip.colors of all pixels as a `List<Long>`
     */
    val pixelTemporaryColorList: List<Long>

    /* Fading */

    /**
     * Helper class for fading a pixel from its current color to its prolonged color.
     */
    inner class FadePixel(pixel: Int) {
        /**
         * Which thread is currently fading this pixel -
         * used so another thread can take over mid-fade if necessary.
         */
        var owner: String

        /**
         * Track if the pixel is in the middle of a fade
         */
        var isFading: Boolean
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
        fun fade(amountOfOverlay: Int = 25, delay: Int = 30, timeout: Int = 2000)
    }

    /**
     * Helper function for fading a pixel.
     *
     * @param pixel The pixel to be faded
     * @param amountOfOverlay How much the pixel should fade in each iteration
     * @param delay Time in milliseconds between iterations
     * @see FadePixel
     */
    protected fun fadePixel(pixel: Int, amountOfOverlay: Int = 25, delay: Int = 30, timeout: Int = 2000)

}
