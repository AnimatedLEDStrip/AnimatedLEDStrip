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

package animatedledstrip.leds.stripmanagement

import animatedledstrip.leds.colormanagement.LEDStripColorLogger
import animatedledstrip.leds.colormanagement.LEDStripColorManager

/**
 * Manages rendering the colors on the strip
 *
 * @param stripColorManager The color manager to get the color data from
 */
expect class LEDStripRenderer(
    ledStrip: NativeLEDStrip,
    stripColorManager: LEDStripColorManager,
    renderDelay: Long,
) {
    /**
     * Should the renderer be actually sending data to the strip?
     */
    var isRendering: Boolean

    /**
     * A logger that logs the color state of the strip at each render to a file
     * for later use, such as debugging or animation signatures
     */
    val stripColorLogger: LEDStripColorLogger

    /**
     * Start rendering the strip
     */
    fun startRendering()

    /**
     * Stop/pause rendering the strip
     */
    fun stopRendering()
}
