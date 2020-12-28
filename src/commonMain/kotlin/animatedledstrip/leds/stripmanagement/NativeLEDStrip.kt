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

package animatedledstrip.leds.stripmanagement

/**
 * Interface defining what is needed from a native LED strip class.
 *
 * A native LED strip class bridges the gap between this library
 * and the actual strip, whether that is through another library
 * or directly.
 */
interface NativeLEDStrip {
    /**
     * Close the LED strip's communication channel and release memory associated
     * with it
     */
    fun close()

    /**
     * The number of LEDs in the strip
     */
    val numLEDs: Int

    /**
     * Send data to the LED strip
     */
    fun render()

    /**
     * Set a pixel's color
     */
    fun setPixelColor(pixel: Int, color: Int)
}
