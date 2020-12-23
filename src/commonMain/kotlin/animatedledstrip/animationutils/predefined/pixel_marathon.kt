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

import animatedledstrip.animationutils.*
import animatedledstrip.colors.isNotEmpty
import animatedledstrip.utils.randomInt
import kotlinx.coroutines.delay

val pixelMarathon = PredefinedAnimation(
    Animation.AnimationInfo(
        name = "Pixel Marathon",
        abbr = "PXM",
        description = "Watch pixels race each other along the strip.\n\n" +
                      "Note that in the animation signature that there are a couple " +
                      "points where the slope of the line gets shallower (meaning the " +
                      "pixel is 'moving' faster).\n" +
                      "If you look closely, you'll see that there are actually two " +
                      "[Pixel Run](Pixel-Run) subanimations at the same spot engaged " +
                      "in a race condition - quite fitting for this animation.\n" +
                      "This happens because when an animation such as Pixel Run changes " +
                      "a pixel's temporary color, it puts a lock on that pixel so the " +
                      "pixel's color doesn't change until it's done with that pixel.",
        signatureFile = "pixel_marathon.png",
        runCountDefault = -1,
        minimumColors = 1,
        unlimitedColors = true,
        center = ParamUsage.NOTUSED,
        delay = ParamUsage.USED,
        delayDefault = 8,
        direction = ParamUsage.USED,
        distance = ParamUsage.NOTUSED,
        spacing = ParamUsage.NOTUSED,
    )
) { leds, params, scope ->
    val color = params.colors.random()
    val delay = params.delay
    val direction = params.direction

    leds.apply {
        if (color.isNotEmpty()) {
            runParallel(
                AnimationToRunParams()
                    .animation("Pixel Run")
                    .color(color)
                    .direction(direction)
                    .delay(delay),
                scope = scope,
            )
            delay(randomInt() * delay * 100)
        }
    }
}
