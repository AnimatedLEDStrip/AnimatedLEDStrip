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


import animatedledstrip.colors.ColorContainerInterface
import animatedledstrip.leds.sections.SectionableLEDStrip


/**
 * Class that represents an LED strip.
 *
 * @param numLEDs Number of LEDs in the strip
 */
abstract class LEDStripNonConcurrent(var numLEDs: Int) : SectionableLEDStrip {

    /**
     * The LED Strip
     */
    abstract var ledStrip: LEDStripInterface

    /* Set individual pixels */

    /**
     * Sets a pixel's color with a `ColorContainer`.
     *
     * @param pixel The pixel to change
     * @param color The color to set the pixel to
     */
    open fun setPixelColor(pixel: Int, color: ColorContainerInterface) {
        require(pixel in 0 until numLEDs)
        val colors = color.prepare(numLEDs)
        ledStrip.setPixelColor(pixel, colors[pixel].toInt())
    }

    /**
     * Set a pixel's color with a `Long`, such as a 24-bit integer.
     *
     * @param pixel The pixel to change
     * @param color The color to set the pixel to
     */
    open fun setPixelColor(pixel: Int, color: Long) {
        require(pixel in 0 until numLEDs)
        ledStrip.setPixelColor(pixel, color.toInt())
    }


    /* Set whole strip */

    /**
     * Set the color of the whole strip.
     */
    var color: ColorContainerInterface
        get() = error("Cannot get color of whole strip")
        set(value) {
            setStripColor(value)
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
        val colors = colorValues.prepare(end - start + 1)
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


    /* Get methods */

    /**
     * Get the color of a pixel.
     *
     * @param pixel The pixel to find the color of
     * @return The color of the pixel
     * @throws IllegalArgumentException If pixel is not a valid index
     */
    open fun getPixelColor(pixel: Int): Long {
        require(pixel in 0 until numLEDs)       // TODO: Test
        return ledStrip.getPixelColor(pixel).toLong()
    }

    /**
     * Use index operator for getPixelColor operations.
     *
     * @param pixel The pixel to find the color of
     */
    operator fun get(pixel: Int) = getPixelColor(pixel)

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
     * Send data to the LEDs.
     */
    open fun show() {
        ledStrip.render()
    }
}
