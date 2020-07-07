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

val stackOverflow = PredefinedAnimation(
    Animation.AnimationInfo(
        name = "Stack Overflow",
        abbr = "STO",
        description = "Two [Stack](Stack) animations are started from opposite" +
                "ends of the strip/section.\n" +
                "The stacks meet in the middle and 'overflow' their half.\n" +
                "And yes, the pun was very much intended.\n" +
                "Note that this animation has a quadratic time complexity," +
                "meaning it gets very long very quickly.",
        signatureFile = "stack_overflow.png",
        repetitive = true,
        minimumColors = 2,
        unlimitedColors = false,
        center = ParamUsage.NOTUSED,
        delay = ParamUsage.USED,
        delayDefault = 2,
        direction = ParamUsage.NOTUSED,
        distance = ParamUsage.NOTUSED,
        spacing = ParamUsage.NOTUSED
    )
) { leds, data, scope ->
    val color0 = data.pCols[0]
    val color1 = data.pCols[1]
    val delay = data.delay

    leds.apply {
        val baseAnimation = AnimationData()
            .animation("Stack")
            .delay(delay)

        runParallelAndJoin(
            scope,
            Pair(
                baseAnimation.copy(
                    colors = listOf(color0),
                    direction = Direction.FORWARD
                ),
                this
            ),
            Pair(
                baseAnimation.copy(
                    colors = listOf(color1),
                    direction = Direction.BACKWARD
                ),
                this
            )
        )
    }
}
