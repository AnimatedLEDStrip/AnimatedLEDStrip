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

import animatedledstrip.animations.Animation
import animatedledstrip.animations.AnimationParameter
import animatedledstrip.animations.DefinedAnimation
import animatedledstrip.animations.Dimensionality
import animatedledstrip.leds.animationmanagement.runSequential
import kotlinx.coroutines.delay

val alterFade = DefinedAnimation(
    Animation.AnimationInfo(
        "AlterFade",
        "ALF",
        "Strip fades* between each color in colors, " +
        "delaying `alternationPeriod` milliseconds between changes.",
        -1,
        2,
        true,
        Dimensionality.anyDimensional,
        false,
        intParams = listOf(AnimationParameter("alternationPeriod",
                                              "Delay in milliseconds between alternations",
                                              1000)),
    )
) { leds, params, _ ->
    val nextColorIndex = params.extraData.getOrPut("nextColorIndex") { 0 } as Int
    val alternationPeriod = params.intParams.getValue("alternationPeriod").toLong()

    leds.apply {
        runSequential(params.withModifications(animation = "Fade to Color",
                                               colors = mutableListOf(params.colors[nextColorIndex])))
    }

    delay(alternationPeriod)

    params.extraData["nextColorIndex"] = if (nextColorIndex == params.colors.lastIndex) 0 else nextColorIndex + 1
}
