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
import kotlin.text.StringBuilder


/**
 * An LED Strip with concurrency added.
 *
 * @param numLEDs Number of LEDs in the strip
 * @param pin GPIO pin connected for signal
 * @param imageDebugging Should a csv file be created containing all renders of
 * the strip?
 */
@Suppress("EXPERIMENTAL_API_USAGE")
abstract class LEDStrip(
    var numLEDs: Int,
    pin: Int,
    private val imageDebugging: Boolean = false
) {

    /**
     * The LED Strip. Chooses between `WS281x` and `EmulatedWS281x` based on value of emulated.
     */
    abstract var ledStrip: LEDStripInterface

    /**
     * `Map` containing `Mutex` instances for locking access to each led while it is
     * being used.
     */
    private val locks = mutableMapOf<Int, Mutex>()

    /**
     * The thread in which the rendering loop will run.
     */
    private val renderThread = newFixedThreadPoolContext(50, "Render Loops")

    /**
     * The thread used to save values to `outFile` so the program doesn't
     * experience slowdowns because of I/O.
     */
    private val outThread = newSingleThreadContext("Image Debug Save Thread")

    /**
     * Tracks if the strip is rendering. Starts `false` and is set to `true` in init.
     */
    private var rendering = false

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
        Logger.info("numLEDs: $numLEDs")
        Logger.info("using GPIO pin $pin")
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
                Thread.sleep(500)
                GlobalScope.launch(renderThread) {
                    var renderNum = 0
                    while (rendering) {
                        ledStrip.render()
                        if (imageDebugging) {
                            getPixelColorList().forEach { buffer!!.append("${(it and 0xFF0000 shr 16).toInt()},${(it and 0x00FF00 shr 8).toInt()},${(it and 0x0000FF).toInt()},") }
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
    }


    /**
     * Sets a pixel's color. If another thread has locked the pixel's `Mutex`,
     * this skips setting the pixel's color and returns.
     *
     * @param pixel The pixel to change
     * @param colorValues The color to set the pixel to
     */
    fun setPixelColor(pixel: Int, colorValues: ColorContainer) {
        try {
            runBlocking {
                locks[pixel]!!.tryWithLock(owner = "Pixel $pixel") {
                    ledStrip.setPixelColorRGB(pixel, colorValues.r, colorValues.g, colorValues.b)
                }
            }
        } catch (e: Exception) {
            Logger.error("ERROR in setPixelColor: $e\npixel: $pixel to color ${colorValues.hexString}")
        }
    }


    /**
     * Set a pixel's color with `r`, `g`, `b` (ranges 0-255). If another thread has
     * locked the pixel's `Mutex`, this skips setting the pixel's color and returns.
     *
     * @param pixel The pixel to change
     * @param rIn Red intensity of the color
     * @param gIn Green intensity of the color
     * @param bIn Blue intensity of the color
     */
    fun setPixelColor(pixel: Int, rIn: Int, gIn: Int, bIn: Int) {
        setPixelColor(pixel, ColorContainer(rIn, gIn, bIn))
    }


    /**
     * Set a pixel's color with a `Long`, such as a 24-bit integer. If another
     * thread has locked the pixel's `Mutex`, this skips setting the pixel's color
     * and returns.
     *
     * @param pixel The pixel to change
     * @param hexIn The color to set the pixel to
     */
    fun setPixelColor(pixel: Int, hexIn: Long) {
        setPixelColor(pixel, ColorContainer(hexIn))
    }


    /**
     * Loops through all pixels and sets their color to `colorValues`. If a pixel's
     * `Mutex` is locked by another thread, it is skipped.
     *
     * @param colorValues The color to set the strip to
     */
    fun setStripColor(colorValues: ColorContainer) {
        for (i in 0 until numLEDs) setPixelColor(i, colorValues.r, colorValues.g, colorValues.b)
    }


    /**
     * Set the strip color with a `Long`, such as a 24-bit integer. If a pixel's
     * `Mutex` is locked by another thread, it is skipped.
     *
     * @param hexIn The color to set the strip to
     */
    fun setStripColor(hexIn: Long) {
        for (i in 0 until numLEDs) setPixelColor(i, hexIn)
    }


    /**
     * Set the strip color with `r`, `g`, `b` (ranges 0-255). If a pixel's `Mutex` is
     * locked by another thread, it is skipped.
     *
     * @param rIn Red intensity of the color
     * @param gIn Green intensity of the color
     * @param bIn Blue intensity of the color
     */
    fun setStripColor(rIn: Int, gIn: Int, bIn: Int) {
        for (i in 0 until numLEDs) ledStrip.setPixelColorRGB(i, rIn, gIn, bIn)
    }

    /**
     * Set the color of a section of the strip. Loops through all leds between start
     * and end (inclusive) and sets their color to `colorValues`. If a pixel's `Mutex`
     * is locked by another thread, it is skipped.
     *
     * @param start First pixel in section
     * @param end Last pixel in section
     * @param colorValues The color to set the section to
     */
    fun setSectionColor(start: Int, end: Int, colorValues: ColorContainer) {
        for (i in start..end) setPixelColor(i, colorValues.r, colorValues.g, colorValues.b)
    }


    /**
     * Set a section's color with a `Long`, such as a 24-bit integer. If a pixel's
     * `Mutex` is locked by another thread, it is skipped.
     *
     * @param start First pixel in section
     * @param end Last pixel in section
     * @param hexIn The color to set the section to
     */
    fun setSectionColor(start: Int, end: Int, hexIn: Long) {
        for (i in start..end) setPixelColor(i, hexIn)
    }


    /**
     * Set a section's color with `r`, `g`, `b` (ranges 0-255). If a pixel's `Mutex` is
     * locked by another thread, it is skipped.
     *
     * @param start First pixel in section
     * @param end Last pixel in section
     * @param rIn Red intensity of the color
     * @param gIn Green intensity of the color
     * @param bIn Blue intensity of the color
     */
    fun setSectionColor(start: Int, end: Int, rIn: Int, gIn: Int, bIn: Int) {
        for (i in start..end) ledStrip.setPixelColorRGB(i, rIn, gIn, bIn)
    }


    /**
     * Get the color of a pixel. Waits until the pixel's `Mutex` is unlocked.
     *
     * @param pixel The pixel to find the color of
     * @return The color of the pixel
     */
    fun getPixelColor(pixel: Int): ColorContainer {
        try {
            return if (!imageDebugging) runBlocking {
                locks[pixel]!!.withLock {
                    return@runBlocking ColorContainer(ledStrip.getPixelColor(pixel).toLong())
                }
            }
            else ColorContainer(ledStrip.getPixelColor(pixel).toLong())
        } catch (e: Exception) {
            Logger.error("ERROR in getPixelColor: $e")
        }
        Logger.warn("Color not retrieved")
        return CCBlack
    }


    /**
     * Get the color of a pixel as a `Long`. Waits until the pixel's `Mutex` is
     * unlocked.
     *
     * @param pixel The pixel to find the color of
     * @return The color of the pixel as a Long
     */
    fun getPixelLong(pixel: Int): Long {
        return getPixelColor(pixel).hex
    }


    /**
     * Get the color of a pixel as a hexadecimal string. Waits until the pixel's
     * `Mutex` is unlocked.
     *
     * @param pixel The pixel to find the color of
     * @return A `String` containing the color of the pixel in hexadecimal
     */
    fun getPixelHexString(pixel: Int): String {
        return getPixelLong(pixel).toString(16)
    }


    /**
     * Get the colors of all pixels as a `List<Long>`. Waits until each pixel's
     * `Mutex` is unlocked.
     */
    fun getPixelColorList(): List<Long> {
        val temp = mutableListOf<Long>()
        for (i in 0 until numLEDs) temp.add(getPixelLong(i))
        return temp
    }


    /**
     * Set the color of the strip using a map with each pixel index mapped to a
     * `ColorContainer`.
     *
     * @param palette The map of colors
     * @param offset The index of the pixel that will be set to the color at
     * index 0
     */
    fun setStripColorWithPalette(palette: Map<Int, ColorContainer>, offset: Int = 0) =
        palette.forEach { i, j ->
            setPixelColor((i + offset) % numLEDs, j)
        }


    /**
     * Sets the color of the strip with a `List<ColorContainer>`. The list is converted to a map
     * before that map is sent to [setStripColorWithPalette] with an offset of 0.
     *
     * @param colorList The list of colors
     */
    fun setStripColorWithGradient(colorList: List<ColorContainer>) {
        val palette = colorsFromPalette(colorList, numLEDs)
        setStripColorWithPalette(palette)
    }


    /**
     * Method that used to be used to render the led strip. Now handled by a
     * thread created during an init block above.
     */
    @Deprecated("Strip is now constantly rendered, thus show() is useless")
    fun show() {
//        try {
//            runBlocking {
//                renderLock.tryWithLock(owner = "Render") {
//                    ledStrip.render()
//                }
//            }
//        } catch (e: Exception) {
//            Logger.error("ERROR in show: $e")
//        }
    }
}
