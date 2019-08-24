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
import animatedledstrip.colors.PreparedColorContainer
import animatedledstrip.colors.ccpresets.CCBlack
import animatedledstrip.leds.sections.LEDStripSection
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
        fileName: String? = null
) : LEDStripNonConcurrent(numLEDs) {

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
                            Logger.error("LEDStrip NullPointerException")
                            delayBlocking(1000)
                        }
                        if (imageDebugging) {
                            pixelColorList.forEach { buffer!!.append("${(it and 0xFF0000 shr 16).toInt()},${(it and 0x00FF00 shr 8).toInt()},${(it and 0x0000FF).toInt()},") }
                            buffer!!.append("0,0,0\n")

                            if (renderNum++ >= 1000) {
                                GlobalScope.launch(outThread) {
                                    outLock.withLock {
                                        outFile.append(buffer)
                                        buffer.clear()
                                    }
                                }
                                renderNum = 0
                            }
                        }
                        delay(10)
//                        Logger.debug("Render")
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


    /* Set pixel color*/

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

    fun setProlongedPixelColor(pixel: Int, colorValues: ColorContainerInterface) {
        val colors = when (colorValues) {
            is PreparedColorContainer -> colorValues
            is ColorContainer -> colorValues.prepare(numLEDs)
            else -> throw IllegalArgumentException("colorValues must implement ColorContainerInterface")
        }
        prolongedColors[pixel] = colors[pixel]
        setPixelColor(pixel, colors[pixel])
    }

    fun setProlongedPixelColor(pixel: Int, color: Long) {
        setProlongedPixelColor(pixel, ColorContainer(color))
    }

    fun setProlongedPixelColor(pixel: Int, rIn: Int, gIn: Int, bIn: Int) {
        setProlongedPixelColor(pixel, ColorContainer(Triple(rIn, gIn, bIn)))
    }

    fun revertPixel(pixel: Int) {
        ledStrip.setPixelColor(pixel, prolongedColors[pixel].toInt())
    }


    /* Set strip color */

    override fun setStripColor(colorValues: ColorContainerInterface) {
        setStripColor(colorValues, true)
    }

    override fun setStripColor(color: Long) {
        setStripColor(color, true)
    }

    override fun setStripColor(rIn: Int, gIn: Int, bIn: Int) {
        setStripColor(rIn, gIn, bIn, true)
    }

    fun setStripColor(colorValues: ColorContainerInterface, prolonged: Boolean) {
        val colors = when (colorValues) {
            is PreparedColorContainer -> colorValues
            is ColorContainer -> colorValues.prepare(numLEDs)
            else -> throw IllegalArgumentException("colorValues must implement ColorContainerInterface")
        }
        if (prolonged) for (i in 0 until numLEDs) setProlongedPixelColor(i, colors)
        else super.setStripColor(colors)
    }

    fun setStripColor(color: Long, prolonged: Boolean) {
        setStripColor(ColorContainer(color), prolonged)
    }

    fun setStripColor(rIn: Int, gIn: Int, bIn: Int, prolonged: Boolean) {
        setStripColor(ColorContainer(Triple(rIn, gIn, bIn)), prolonged)
    }


    /* Set section color */

    override fun setSectionColor(start: Int, end: Int, colorValues: ColorContainerInterface) {
        setSectionColor(start, end, colorValues, true)
    }

    override fun setSectionColor(start: Int, end: Int, color: Long) {
        setSectionColor(start, end, color, true)
    }

    override fun setSectionColor(start: Int, end: Int, rIn: Int, gIn: Int, bIn: Int) {
        setSectionColor(start, end, rIn, gIn, bIn, true)
    }


    fun setSectionColor(start: Int, end: Int, colorValues: ColorContainerInterface, prolonged: Boolean) {
        val colors = when (colorValues) {
            is PreparedColorContainer -> colorValues
            is ColorContainer -> colorValues.prepare(numLEDs)
            else -> throw IllegalArgumentException("colorValues must implement ColorContainerInterface")
        }
        if (prolonged) for (i in start..end) setProlongedPixelColor(i, colors)
        else super.setSectionColor(start, end, colors)
    }

    fun setSectionColor(start: Int, end: Int, color: Long, prolonged: Boolean) {
        setSectionColor(start, end, ColorContainer(color), prolonged)
    }

    fun setSectionColor(start: Int, end: Int, rIn: Int, gIn: Int, bIn: Int, prolonged: Boolean) {
        setSectionColor(start, end, ColorContainer(Triple(rIn, gIn, bIn)), prolonged)
    }


    /* Get methods */

    /**
     * Get the color of a pixel.
     *
     * @param pixel The pixel to find the color of
     * @return The color of the pixel
     */
    override fun getPixelColor(pixel: Int): Long {
        try {
            return prolongedColors[pixel]
        } catch (e: Exception) {
            Logger.error("ERROR in getPixelColor: $e")
        }
        Logger.warn("Color not retrieved")
        return CCBlack.color
    }


    fun getActualPixelColor(pixel: Int): Long {
        try {
            return super.getPixelColor(pixel)
        } catch (e: Exception) {
            Logger.error("ERROR in getActualPixelColor: $e")
        }
        Logger.warn("Color not retrieved")
        return CCBlack.color
    }

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
}
