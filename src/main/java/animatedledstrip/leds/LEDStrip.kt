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


import animatedledstrip.ccpresets.CCBlack
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.pmw.tinylog.Logger
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*


/**
 * A subclass of [LEDStripNonConcurrent] adding support for concurrency.
 *
 * @param numLEDs Number of LEDs in the strip
 * @param imageDebugging Should a csv file be created containing all renders of
 * the strip?
 */
abstract class LEDStrip(
        numLEDs: Int,
        private val imageDebugging: Boolean = false
) : LEDStripNonConcurrent(numLEDs){

    /**
     * `Map` containing `Mutex` instances for locking access to each led while it is
     * being used.
     */
    private val locks = mutableMapOf<Int, Mutex>()

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

    /**
     * The file that the csv output will be saved to if image debugging is enabled.
     */
    private val outFile = if (imageDebugging) FileWriter(
            "signature_${SimpleDateFormat("MMDDYY_hhmmss").format(Date())}.csv",
            true
    ) else null

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

    init {
        for (i in 0 until numLEDs) locks += Pair(i, Mutex())
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
            true -> false
            false -> {
                GlobalScope.launch(renderThread) {
                    delay(500)
                    var renderNum = 0
                    while (rendering) {
                        ledStrip.render()
                        if (imageDebugging) {
                            pixelColorList.forEach { buffer!!.append("${(it and 0xFF0000 shr 16).toInt()},${(it and 0x00FF00 shr 8).toInt()},${(it and 0x0000FF).toInt()},") }
                            buffer!!.append("0,0,0\n")

                            if (renderNum++ >= 1000) {
                                GlobalScope.launch(outThread) {
                                    outLock.withLock {
                                        outFile!!.append(buffer)
                                        buffer.clear()
                                    }
                                }
                                renderNum = 0
                            }
                        }
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


    /**
     * Sets a pixel's color. If another thread has locked the pixel's `Mutex`,
     * this skips setting the pixel's color and returns.
     *
     * @param pixel The pixel to change
     * @param colorValues The color to set the pixel to
     */
    override fun setPixelColor(pixel: Int, colorValues: ColorContainerInterface) {
        try {
            runBlocking {
                locks[pixel]!!.tryWithLock(owner = "Pixel $pixel") {
                    super.setPixelColor(pixel, colorValues)
                }
            }
        } catch (e: Exception) {
            Logger.error("ERROR in setPixelColor: $e\npixel: $pixel to color $colorValues")
        }
    }


    /**
     * Get the color of a pixel. Waits until the pixel's `Mutex` is unlocked.
     *
     * @param pixel The pixel to find the color of
     * @return The color of the pixel
     */
    override fun getPixelColor(pixel: Int): Long {
        try {
            return if (!imageDebugging) runBlocking {
                locks[pixel]!!.withLock {
                    return@runBlocking super.getPixelColor(pixel)
                }
            }
            else super.getPixelColor(pixel)
        } catch (e: Exception) {
            Logger.error("ERROR in getPixelColor: $e")
        }
        Logger.warn("Color not retrieved")
        return CCBlack.color
    }


    /**
     * Method that used to be used to render the led strip. Now handled by a
     * thread created during an init block above. Overrides LEDStripNonConcurrent's
     * show() to stop any manual renders.
     */
    override fun show() { }
}
