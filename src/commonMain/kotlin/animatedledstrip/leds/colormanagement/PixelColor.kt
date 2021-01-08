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

import animatedledstrip.colors.blend
import animatedledstrip.leds.stripmanagement.NativeLEDStrip

/**
 * Tracks a single pixel's colors. Each pixel has 4 colors, [actualColor],
 * [fadeColor], [prolongedColor] and [temporaryColor]. A pixel's color will
 * be set to [temporaryColor] if [temporaryColor] is not `-1`, otherwise to
 * [fadeColor] if [fadeColor] is not `-1`, otherwise to [prolongedColor].
 *
 * @property pixelNumber The index of the pixel this
 * @property actualColor The color seen on the strip
 * @property fadeColor A color that is being faded to the prolonged color
 * @property prolongedColor The color that should be shown if the temporary
 * and fade colors are not set, and the color that will be faded to
 * @property temporaryColor A color to show regardless of what the other colors
 * are, essentially overriding everything
 */
data class PixelColor(val pixelNumber: Int) {
    var actualColor: Int = 0
        private set
    var fadeColor: Int = -1
        private set
    var prolongedColor: Int = 0
        private set
    var temporaryColor: Int = -1
        private set

    /**
     * Set the pixel's color
     *
     * @param colorType The color type to set (see [PixelColorType])
     */
    fun setColor(color: Int, colorType: PixelColorType) {
        when (colorType) {
            PixelColorType.ACTUAL -> throw IllegalArgumentException("Cannot set actual color directly")
            PixelColorType.FADE -> fadeColor = color
            PixelColorType.PROLONGED -> prolongedColor = color
            PixelColorType.TEMPORARY -> temporaryColor = color
        }
    }

    /**
     * Get the pixel's color
     *
     * @param colorType The color type to get (see [PixelColorType])
     */
    fun getColor(colorType: PixelColorType): Int =
        when (colorType) {
            PixelColorType.ACTUAL -> actualColor
            PixelColorType.FADE -> fadeColor
            PixelColorType.PROLONGED -> prolongedColor
            PixelColorType.TEMPORARY -> temporaryColor
        }

    /**
     * Resets [temporaryColor] to `-1`, effectively reverting the pixel to
     * whatever state it would be in without the [temporaryColor] override
     */
    fun revertColor() {
        temporaryColor = -1
    }

    /**
     * Sends the color for a pixel to the strip. A pixel's color will
     * be set to [temporaryColor] if [temporaryColor] is not `-1`, otherwise to
     * [fadeColor] if [fadeColor] is not `-1`, otherwise to [prolongedColor].
     *
     * @param ledStrip The strip to send the color information to
     * @param doFade If true and [fadeColor] != `-1`, [fadeColor] will be faded
     * towards [prolongedColor] (after the color has been sent to the strip).
     * If [fadeColor] is now equal to [prolongedColor], [fadeColor] will be
     * set to `-1`.
     */
    fun sendColorToStrip(ledStrip: NativeLEDStrip, doFade: Boolean) {
        val colorToSet: Int = when {
            temporaryColor != -1 -> temporaryColor
            fadeColor != -1 -> fadeColor
            else -> prolongedColor
        }

        ledStrip.setPixelColor(pixelNumber, colorToSet)
        actualColor = colorToSet

        if (doFade && fadeColor != -1) {
            fadeColor = blend(fadeColor, prolongedColor, 40)
            if (fadeColor == prolongedColor) fadeColor = -1
        }
    }
}
