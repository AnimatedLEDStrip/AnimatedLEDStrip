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
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.math.min

val ripple = PredefinedAnimation(
    Animation.AnimationInfo(
        name = "Ripple",
        abbr = "RPL",
        description = "Starts two [Meteor](Meteor) animations running in opposite " +
                      "directions from `center`, stopping after traveling `distance` " +
                      "or at the end of the strip/section, whichever comes first.\n" +
                      "Does not wait for the Meteor animations to be complete before " +
                      "returning, giving a ripple-like appearance when run continuously.",
        signatureFile = "ripple.png",
        runCountDefault = -1,
        minimumColors = 1,
        unlimitedColors = false,
        center = ParamUsage.USED,
        delay = ParamUsage.USED,
        delayDefault = 30,
        direction = ParamUsage.NOTUSED,
        distance = ParamUsage.USED,
        spacing = ParamUsage.NOTUSED,
    )
) { leds, params, scope ->
    val color0 = params.colors[0]
    val center = params.center
    val delay = params.delay
    val distance = params.distance

    leds.apply {
        val baseAnimation = AnimationToRunParams()
            .animation("Meteor")
            .color(color0)
            .delay(delay)

        runParallel(
            baseAnimation.copy(
                direction = Direction.FORWARD,
            ),
            section = getSubSection(center, min(center + distance, numLEDs - 1)),
            scope = scope,
        )
        runParallel(
            baseAnimation.copy(
                direction = Direction.BACKWARD,
            ),
            section = getSubSection(max(center - distance, 0), center),
            scope = scope,
        )
        delay(delay * 20)
    }
}
