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
import animatedledstrip.leds.runParallelAndJoin
import kotlin.math.max
import kotlin.math.min

val splat = PredefinedAnimation(
    Animation.AnimationInfo(
        name = "SPLAT",
        abbr = "SPT",
        repetitive = false,
        numReqColors = 1,
        center = ParamUsage.USED,
        delay = ParamUsage.USED,
        delayDefault = 5,
        direction = ParamUsage.NOTUSED,
        distance = ParamUsage.USED,
        spacing = ParamUsage.NOTUSED
    )
) { leds, data, scope ->
    val color0 = data.pCols[0]
    val center = data.center
    val delay = data.delay
    val distance = data.distance

    leds.apply {
        val baseAnimation = AnimationData()
            .animation("Wipe")
            .color(color0)
            .delay(delay)

        runParallelAndJoin(
            scope,
            Pair(
                baseAnimation.copy(direction = Direction.FORWARD),
                getSubSection(center, min(center + distance, numLEDs - 1))
            ),
            Pair(
                baseAnimation.copy(direction = Direction.BACKWARD),
                getSubSection(max(center - distance, 0), center)
            )
        )
    }
}
