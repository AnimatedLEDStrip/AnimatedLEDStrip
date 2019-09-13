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
import animatedledstrip.leds.sections.SectionableLEDStrip
import org.tinylog.Logger


/**
 * Class that represents an LED strip.
 *
 * @param numLEDs Number of LEDs in the strip
 */
abstract class LEDStripNonConcurrent(var numLEDs: Int) : SectionableLEDStrip {

    /**
     * The LED Strip. Chooses between `WS281x` and `EmulatedWS281x` based on value of emulated.
     */
    abstract var ledStrip: LEDStripInterface

    init {
        Logger.info { "numLEDs: $numLEDs" }
    }


    /* Set individual pixels */

    /**
     * Sets a pixel's color with a `ColorContainer`.
     *
     * @param pixel The pixel to change
     * @param colorValues The color to set the pixel to
     */
    open fun setPixelColor(pixel: Int, colorValues: ColorContainerInterface) {
        val colors = when (colorValues) {
            is PreparedColorContainer -> colorValues
            is ColorContainer -> colorValues.prepare(numLEDs)
            else -> throw IllegalArgumentException("colorValues must implement ColorContainerInterface")
        }
        ledStrip.setPixelColor(pixel, colors[pixel].toInt())
    }


    /**
     * Set a pixel's color with `r`, `g`, `b` (ranges 0-255).
     *
     * @param pixel The pixel to change
     * @param rIn Red intensity of the color
     * @param gIn Green intensity of the color
     * @param bIn Blue intensity of the color
     */
    fun setPixelColor(pixel: Int, rIn: Int, gIn: Int, bIn: Int) {
        setPixelColor(pixel, ColorContainer(Triple(rIn, gIn, bIn)))
    }


    /**
     * Set a pixel's color with a `Long`, such as a 24-bit integer.
     *
     * @param pixel The pixel to change
     * @param color The color to set the pixel to
     */
    fun setPixelColor(pixel: Int, color: Long) {
        setPixelColor(pixel, ColorContainer(color))
    }

    operator fun set(vararg pixels: Int, color: Long) {
        for (pixel in pixels) {
            setPixelColor(pixel, color)
        }
    }

    operator fun set(pixels: IntRange, color: Long) {
        for (pixel in pixels) {
            setPixelColor(pixel, color)
        }
    }

    operator fun set(vararg pixels: Int, color: ColorContainerInterface) {
        for (pixel in pixels) {
            setPixelColor(pixel, color)
        }
    }

    operator fun set(pixels: IntRange, color: ColorContainerInterface) {
        for (pixel in pixels) {
            setPixelColor(pixel, color)
        }
    }

    /* Set whole strip */

    /**
     * Set the color of the whole strip.
     */
    var color: Any?
        get() = throw Exception("Cannot get color of whole strip")
        set(value) {
            when (value) {
                is ColorContainer -> setStripColor(value)
                is Long -> setStripColor(value)
                is Int -> setStripColor(value.toLong())
                else -> throw Exception("Invalid type")
            }
        }

    /**
     * Loops through all pixels and sets their color to `colorValues`.
     *
     * @param colorValues The color to set the strip to
     */
    open fun setStripColor(colorValues: ColorContainerInterface) {
        for (i in 0 until numLEDs) setPixelColor(i, colorValues)
        show()
    }

    /**
     * Set the strip color with a `Long`, such as a 24-bit integer.
     *
     * @param color The color to set the strip to
     */
    open fun setStripColor(color: Long) {
        for (i in 0 until numLEDs) setPixelColor(i, color)
        show()
    }

    /**
     * Set the strip color with `r`, `g`, `b` (ranges 0-255).
     *
     * @param rIn Red intensity of the color
     * @param gIn Green intensity of the color
     * @param bIn Blue intensity of the color
     */
    open fun setStripColor(rIn: Int, gIn: Int, bIn: Int) {
        for (i in 0 until numLEDs) setPixelColor(i, rIn, gIn, bIn)
        show()
    }


    /* Set a section of the strip */

    /**
     * Set the color of a section of the strip. Loops through all LEDs between start
     * and end (inclusive) and sets their color to `colorValues`.
     *
     * @param start First pixel in section
     * @param end Last pixel in section
     * @param colorValues The color to set the section to
     */
    override fun setSectionColor(start: Int, end: Int, colorValues: ColorContainerInterface) {
        val colors = when (colorValues) {
            is PreparedColorContainer -> colorValues
            is ColorContainer -> colorValues.prepare(numLEDs)
            else -> throw IllegalArgumentException("colorValues must implement ColorContainerInterface")
        }
        for (i in start..end) setPixelColor(i, colors[i - start])
        show()
    }


    /**
     * Set a section's color with a `Long`, such as a 24-bit integer.
     *
     * @param start First pixel in section
     * @param end Last pixel in section
     * @param color The color to set the section to
     */
    open fun setSectionColor(start: Int, end: Int, color: Long) {
        for (i in start..end) setPixelColor(i, color)
        show()
    }


    /**
     * Set a section's color with `r`, `g`, `b` (ranges 0-255).
     *
     * @param start First pixel in section
     * @param end Last pixel in section
     * @param rIn Red intensity of the color
     * @param gIn Green intensity of the color
     * @param bIn Blue intensity of the color
     */
    open fun setSectionColor(start: Int, end: Int, rIn: Int, gIn: Int, bIn: Int) {
        for (i in start..end) ledStrip.setPixelColorRGB(i, rIn, gIn, bIn)
        show()
    }


    /* Get methods */

    /**
     * Get the color of a pixel.
     *
     * @param pixel The pixel to find the color of
     * @return The color of the pixel
     */
    open fun getPixelColor(pixel: Int): Long =
        ColorContainer(ledStrip.getPixelColor(pixel).toLong()).color


    /**
     * Use index operator for getPixelColor operations.
     *
     * @param pixel The pixel to find the color of
     */
    operator fun get(pixel: Int) = getPixelColor(pixel)


    /**
     * Get the color of a pixel as a hexadecimal string.
     *
     * @param pixel The pixel to find the color of
     * @return A `String` containing the color of the pixel in hexadecimal
     */
    fun getPixelHexString(pixel: Int): String {
        return getPixelColor(pixel).toString(16)
    }

    /**
     * Get the colors of all pixels as a `List<Long>`
     */
    open val pixelColorList: List<Long>
        get() {
            val temp = mutableListOf<Long>()
            for (i in 0 until numLEDs) temp.add(getPixelColor(i))
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
    fun setStripColorWithOffset(palette: PreparedColorContainer, offset: Int = 0) {
        val temp = mutableListOf<Long>()

        for (i in 0 until palette.size) {
            temp += palette[(i + offset) % palette.size]
        }

        setStripColor(PreparedColorContainer(temp))
    }


    /**
     * Send data to the LEDs.
     */
    open fun show() {
        ledStrip.render()
    }
}
