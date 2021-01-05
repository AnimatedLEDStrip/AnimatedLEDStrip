/*
 * Copyright (c) 2018-2021 AnimatedLEDStrip
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package animatedledstrip.leds.colormanagement

import animatedledstrip.colors.PreparedColorContainer
import animatedledstrip.leds.stripmanagement.LEDStrip

/**
 * Tracks the color of pixels in the strip so they can be used, rendered and/or logged
 */
class LEDStripColorManager(val stripManager: LEDStrip) {

    /**
     * The list containing all the pixel color data
     */
    val pixelColors: List<PixelColor> = mutableListOf<PixelColor>().apply {
        for (i in IntRange(0, stripManager.numLEDs - 1)) add(PixelColor(i))
    }.toList()

    /**
     * Set a pixel's color
     *
     * @param colorType The color type to set (see [PixelColorType])
     */
    fun setPixelColor(pixel: Int, color: PreparedColorContainer, colorType: PixelColorType) =
        setPixelColor(pixel, color[pixel], colorType)

    /**
     * Set a pixel's color
     *
     * @param colorType The color type to set (see [PixelColorType])
     */
    fun setPixelColor(pixel: Int, color: Int, colorType: PixelColorType) {
        require(pixel in stripManager.validIndices) { "$pixel not in indices (${stripManager.validIndices.first()}..${stripManager.validIndices.last()})" }

        pixelColors[pixel].setColor(color, colorType)
    }

    /**
     * Revert a pixel (reset its temporary color)
     */
    fun revertPixel(pixel: Int) {
        pixelColors[pixel].revertColor()
    }

    /**
     * Get a pixel's color
     *
     * @param colorType The color type to get (see [PixelColorType])
     */
    fun getPixelColor(pixel: Int, colorType: PixelColorType): Int {
        require(pixel in stripManager.validIndices) { "$pixel not in indices (${stripManager.validIndices.first()}..${stripManager.validIndices.last()})" }

        return pixelColors[pixel].getColor(colorType)
    }
}
