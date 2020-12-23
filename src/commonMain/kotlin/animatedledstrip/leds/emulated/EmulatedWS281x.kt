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
 *
 *
 *  Parts of this file were converted to Kotlin by IntelliJ from a file with this
 *  copyright.
 *
 *  #%L
 *  Organisation: mattjlewis
 *  Project:      Device I/O Zero - WS281x Java Wrapper
 *  Filename:     WS281x.java
 *
 *  This file is part of the diozero project. More information about this project
 *  can be found at http://www.diozero.com/
 *  %%
 *  Copyright (C) 2016 - 2017 mattjlewis
 *  %%
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
 *  #L%
 */

package animatedledstrip.leds.emulated

import animatedledstrip.leds.NativeLEDStrip

/**
 * Modification on the `WS281x` class from the diozero-ws281x-java library that
 * doesn't attempt to send data to any LEDs.
 *
 * @property numLEDs Number of LEDs in the strip
 */
class EmulatedWS281x(override val numLEDs: Int) : NativeLEDStrip {

    /** Empty override for `render()` */
    override fun render() {}

    /** Empty override for `close()` */
    override fun close() {}

    /**
     * A `ByteBuffer` emulating the `ByteBuffer` used by the regular `WS281x` class.
     */
    var ledArray: EmulatedStripBuffer = EmulatedStripBuffer(numLEDs)

    /**
     * Get the color of a pixel in the strip.
     *
     * @param pixel The pixel to check
     */
    override fun getPixelColor(pixel: Int): Int {
        return ledArray.getInt(pixel)
    }

    /**
     * Set the color of a pixel in the strip.
     *
     * @param pixel The pixel to set
     * @param color The color to set the pixel to
     */
    override fun setPixelColor(pixel: Int, color: Int) {
        ledArray.putInt(pixel, color)
    }
}
