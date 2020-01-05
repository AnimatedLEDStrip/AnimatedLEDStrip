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

package animatedledstrip.leds.sections

import animatedledstrip.animationutils.Animation
import animatedledstrip.animationutils.AnimationData
import animatedledstrip.leds.AnimatedLEDStrip

/**
 * Used to run animations on only part of an LED strip.
 *
 * When this instance's run method is called, it modifies the `startPixel` and
 * `endPixel` parameters in the `AnimationData` instance before passing it on to
 * the `ledStrip` specified.
 *
 * @property startPixel First pixel in the section
 * @property endPixel Last pixel in the section (inclusive)
 * @property ledStrip Strip that this section is a part of
 */
class LEDStripSection(val startPixel: Int, val endPixel: Int, val ledStrip: AnimatedLEDStrip) {

    /**
     * Create a `LEDStripSection` using a range of indices (inclusive)
     */
    constructor(pixels: IntRange, ledStrip: AnimatedLEDStrip) : this(pixels.first, pixels.last, ledStrip)

    /**
     * Run an animation on this section of the LED strip.
     */
    fun addAnimation(animation: AnimationData): AnimatedLEDStrip.RunningAnimation? {
        return when (animation.animation) {
            Animation.COLOR -> {
                ledStrip.setSectionColor(
                    startPixel, endPixel,
                    animation.colors[0].prepare(endPixel - startPixel + 1, startPixel),
                    prolonged = true
                )
                null
            }
            else -> {
                animation.startPixel = startPixel
                animation.endPixel = endPixel
                ledStrip.addAnimation(animation)
            }
        }
    }
}