/*
 * Copyright (c) 2018-2021 AnimatedLEDStrip
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package animatedledstrip.animations.predefined

import animatedledstrip.animations.*
import animatedledstrip.leds.animationmanagement.runParallelAndJoin

val stackOverflow = DefinedAnimation(
    Animation.AnimationInfo(
        name = "Stack Overflow",
        abbr = "STO",
        description = "Two [Stack](Stack) animations are started from opposite " +
                      "ends of the strip.\n" +
                      "The stacks meet in the middle and 'overflow' their half.\n" +
                      "And yes, the pun was very much intended.\n\n" +
                      "Note that this animation has a quadratic time complexity.",
        runCountDefault = -1,
        minimumColors = 2,
        unlimitedColors = false,
        dimensionality = Dimensionality.oneDimensional,
        intParams = listOf(AnimationParameter("interMovementDelay", "Delay between movements in the animation", 10)),
        doubleParams = listOf(AnimationParameter("movementPerIteration",
                                                 "How far to move along the X axis during each iteration of the animation",
                                                 1.0),
                              AnimationParameter("maximumInfluence",
                                                 "How far away from the line a pixel can be affected",
                                                 0.9)),
        distanceParams = listOf(AnimationParameter("offset",
                                                   "Offset of the line in the XYZ directions",
                                                   AbsoluteDistance(0.0, 0.0, 0.0))),
        rotationParams = listOf(AnimationParameter("rotation", "Rotation of the line around the XYZ axes")),
        equationParams = listOf(AnimationParameter("lineEquation",
                                                   "The equation representing the line the the pixel will follow")),
    )
) { leds, params, _ ->
    leds.apply {
        runParallelAndJoin(
            Pair(
                params.withModifications(animation = "Stack", colors = listOf(params.colors[0])),
                sectionManager,
            ),
            Pair(
                params.withModifications(animation = "Stack",
                                         colors = listOf(params.colors[1]),
                                         doubleParamMods = mapOf("movementPerIteration" to -params.doubleParams.getValue(
                                             "movementPerIteration"))),
                sectionManager,
            )
        )
    }
}
