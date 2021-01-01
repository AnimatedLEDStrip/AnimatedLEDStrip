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
 */

package animatedledstrip.animations.predefined

import animatedledstrip.animations.*
import animatedledstrip.leds.animationmanagement.AnimationToRunParams
import animatedledstrip.leds.animationmanagement.animation
import animatedledstrip.leds.animationmanagement.delay
import animatedledstrip.leds.animationmanagement.runParallelAndJoin

val stackOverflow = PredefinedAnimation(
    Animation.AnimationInfo(
        name = "Stack Overflow",
        abbr = "STO",
        dimensionality = Dimensionality.ONE_DIMENSIONAL,
        description = "Two [Stack](Stack) animations are started from opposite " +
                      "ends of the strip/section.\n" +
                      "The stacks meet in the middle and 'overflow' their half.\n" +
                      "And yes, the pun was very much intended.\n\n" +
                      "Note that this animation has a quadratic time complexity, " +
                      "meaning it gets very long very quickly.",
        signatureFile = "stack_overflow.png",
        runCountDefault = -1,
        minimumColors = 2,
        unlimitedColors = false,
        center = ParamUsage.NOTUSED,
        delay = ParamUsage.USED,
        delayDefault = 2,
        direction = ParamUsage.NOTUSED,
        distance = ParamUsage.NOTUSED,
        spacing = ParamUsage.NOTUSED,
    )
) { leds, params, _ ->
    val color0 = params.colors[0]
    val color1 = params.colors[1]
    val delay = params.delay

    leds.apply {
        val baseAnimation = AnimationToRunParams()
            .animation("Stack")
            .delay(delay)

        runParallelAndJoin(
            Pair(
                baseAnimation.copy(
                    colors = mutableListOf(color0),
                    direction = Direction.FORWARD,
                ),
                sectionManager,
            ),
            Pair(
                baseAnimation.copy(
                    colors = mutableListOf(color1),
                    direction = Direction.BACKWARD,
                ),
                sectionManager,
            )
        )
    }
}
