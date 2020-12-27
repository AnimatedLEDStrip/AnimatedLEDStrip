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

package animatedledstrip.animations.predefined

import animatedledstrip.animations.Animation
import animatedledstrip.animations.ParamUsage
import animatedledstrip.animations.PredefinedAnimation
import animatedledstrip.leds.colormanagement.setPixelProlongedColor
import animatedledstrip.leds.animationmanagement.validIndices
import animatedledstrip.utils.randomInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

val sparkleToColor = PredefinedAnimation(
    Animation.AnimationInfo(
        name = "Sparkle to Color",
        abbr = "STC",
        description = "Similar to the [Sparkle](Sparkle) animation, but the " +
                      "LEDs are not reverted to their prolonged color after " +
                      "the sparkle.\n" +
                      "(Their prolonged color is changed as well.)\n" +
                      "A separate thread is created for each pixel.\n" +
                      "Each thread waits up to `delay * 100` seconds before sparkling " +
                      "its pixel.",
        signatureFile = "sparkle_to_color.png",
        runCountDefault = 1,
        minimumColors = 1,
        unlimitedColors = false,
        center = ParamUsage.NOTUSED,
        delay = ParamUsage.USED,
        delayDefault = 50,
        direction = ParamUsage.NOTUSED,
        distance = ParamUsage.NOTUSED,
        spacing = ParamUsage.NOTUSED,
    )
) { leds, params, scope ->
    val color0 = params.colors[0]
    val delay = params.delay

    leds.apply {
        val jobs = validIndices.map { n ->
            animationScope.launch {
                delay(randomInt() * delay * 100 % 4950)
                setPixelProlongedColor(n, color0)
                delay(delay)
            }
        }
        jobs.joinAll()
    }
}
