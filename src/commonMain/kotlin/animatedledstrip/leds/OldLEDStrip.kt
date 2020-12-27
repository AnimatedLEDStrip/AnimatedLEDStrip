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
import animatedledstrip.leds.colormanagement.PixelColor
import animatedledstrip.leds.colormanagement.PixelColorType
import animatedledstrip.leds.stripmanagement.NativeLEDStrip
import animatedledstrip.leds.stripmanagement.StripInfo

/**
 * Class that represents a LED strip that supports concurrency.
 *
 * @param stripInfo Information about this strip, such as number of
 * LEDs, etc.
 */
expect abstract class OldLEDStrip(stripInfo: StripInfo) {

    /**
     * The 'actual' LED strip
     */
    abstract val ledStrip: NativeLEDStrip


    /* Number of LEDs */

    /**
     * Number of LEDs
     */
    open val numLEDs: Int

    val pixelColors: List<PixelColor>

    val validIndices: List<Int>

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

    fun setPixelColor(pixel: Int, color: ColorContainerInterface, colorType: PixelColorType)

    fun setPixelColor(pixel: Int, color: Int, colorType: PixelColorType)

    /**
     * Revert a pixel to its prolonged color. If it is in the middle
     * of a fade, don't revert.
     */
    protected fun revertPixel(pixel: Int)


    /* Get pixel color */

    fun getPixelColor(pixel: Int, colorType: PixelColorType): Int

    val pixelActualColorList: List<Int>

    val pixelFadeColorList: List<Int>

    /**
     * Get the prolonged animatedledstrip.colors of all pixels as a `List<Long>`
     */
    val pixelProlongedColorList: List<Int>
    /**
     * Get the temporary animatedledstrip.colors of all pixels as a `List<Long>`
     */
    val pixelTemporaryColorList: List<Int>

}
