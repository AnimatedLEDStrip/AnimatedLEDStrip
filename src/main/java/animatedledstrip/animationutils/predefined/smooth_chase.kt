/*
 *  Copyright (c) 2020 AnimatedLEDStrip
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

package animatedledstrip.animationutils.predefined

import animatedledstrip.animationutils.Animation
import animatedledstrip.animationutils.Direction
import animatedledstrip.animationutils.ParamUsage
import animatedledstrip.animationutils.PredefinedAnimation
import animatedledstrip.leds.iterateOverPixels
import animatedledstrip.leds.iterateOverPixelsReverse
import animatedledstrip.leds.setProlongedStripColorWithOffset
import animatedledstrip.utils.delayBlocking

val smoothChase = PredefinedAnimation(
    Animation.AnimationInfo(
        name = "Smooth Chase",
        abbr = "SCH",
        description = "Each pixel is set to its respective color in `pCols[0]`.\n" +
                "Then, if the direction is `Direction.FORWARD`, each pixel is set" +
                "to `pCols[0][i + 1]`, then `pCols[0][i + 2]`, etc. to create" +
                "the illusion that the animation is 'moving'.\n" +
                "If the direction is `Direction.BACKWARD`, the same happens but" +
                "with indices `i`, `i-1`, `i-2`, etc.\n" +
                "Works best with a ColorContainer with multiple colors.",
        signatureFile = "smooth_chase.png",
        repetitive = true,
        minimumColors = 1,
        unlimitedColors = false,
        center = ParamUsage.NOTUSED,
        delay = ParamUsage.USED,
        delayDefault = 50,
        direction = ParamUsage.USED,
        distance = ParamUsage.NOTUSED,
        spacing = ParamUsage.NOTUSED
    )
) { leds, data, _ ->
    val color0 = data.pCols[0]
    val delay = data.delay
    val direction = data.direction

    leds.apply {
        when (direction) {
            Direction.FORWARD ->
                iterateOverPixels { m ->
                    setProlongedStripColorWithOffset(color0, m)
                    delayBlocking(delay)
                }
            Direction.BACKWARD ->
                iterateOverPixelsReverse { m ->
                    setProlongedStripColorWithOffset(color0, m)
                    delayBlocking(delay)
                }
        }
    }
}
