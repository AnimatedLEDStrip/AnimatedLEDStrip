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
import animatedledstrip.animationutils.ParamUsage
import animatedledstrip.animationutils.PredefinedAnimation
import animatedledstrip.leds.runBlockingNonCancellable
import animatedledstrip.leds.setAndFadePixel
import animatedledstrip.utils.delayBlocking
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

val sparkleFade = PredefinedAnimation(
    Animation.AnimationInfo(
        name = "Sparkle Fade",
        abbr = "SPF",
        description = "Similar to [Sparkle](Sparkle) but pixels fade back to" +
                "their prolonged color.",
        signatureFile = "sparkle_fade.png",
        repetitive = true,
        minimumColors = 1,
        unlimitedColors = false,
        center = ParamUsage.NOTUSED,
        delay = ParamUsage.USED,
        delayDefault = 50,
        direction = ParamUsage.NOTUSED,
        distance = ParamUsage.NOTUSED,
        spacing = ParamUsage.NOTUSED
    )
) { leds, data, scope ->
    val color0 = data.pCols[0]
    val delay = data.delay

    leds.apply {
        val jobs = indices.map { n ->
            scope.launch(sparkleThreadPool) {
                delayBlocking((Math.random() * delay * 100).toLong())
                setAndFadePixel(
                    pixel = n,
                    color = color0,
                    amountOfOverlay = 25,
                    context = sparkleThreadPool
                )
                delayBlocking(delay)
            }
        }
        runBlockingNonCancellable {
            jobs.joinAll()
        }
    }
}
