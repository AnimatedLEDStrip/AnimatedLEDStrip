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

package animatedledstrip.leds.emulation

import animatedledstrip.leds.stripmanagement.NativeLEDStrip
import animatedledstrip.leds.stripmanagement.StripInfo

/**
 * A NativeLEDStrip that doesn't attempt to send data to any LEDs.
 *
 * @property numLEDs Number of LEDs in the strip
 */
class EmulatedWS281x(stripInfo: StripInfo) : NativeLEDStrip {

    constructor(numLEDs: Int) : this(StripInfo(numLEDs))

    override val numLEDs: Int = stripInfo.numLEDs

    /** Empty override for `render()` */
    override fun render() {}

    /** Empty override for `close()` */
    override fun close() {}

    /**
     * An array standing in for a LED strip
     */
    var ledArray = IntArray(numLEDs)

    /**
     * Get the color of a pixel in the strip
     */
    fun getPixelColor(pixel: Int): Int = ledArray[pixel]

    /**
     * Set the color of a pixel in the strip
     */
    override fun setPixelColor(pixel: Int, color: Int) {
        ledArray[pixel] = color
    }
}
