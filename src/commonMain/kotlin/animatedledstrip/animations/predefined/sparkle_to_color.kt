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
import animatedledstrip.leds.animationmanagement.pixelIndices
import animatedledstrip.leds.animationmanagement.randomDouble
import animatedledstrip.leds.colormanagement.setPixelProlongedColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

val sparkleToColor = DefinedAnimation(
    Animation.AnimationInfo(
        name = "Sparkle to Color",
        abbr = "STC",
        description = "Similar to the [Sparkle](Sparkle) animation, but the " +
                      "LEDs are not reverted after the sparkle.\n" +
                      "(Their prolonged color is changed as well.)\n" +
                      "A separate coroutine is created for each pixel.\n" +
                      "Each coroutine waits up to `maxDelayBeforeSparkle` seconds before " +
                      "sparkling its pixel.",
        runCountDefault = 1,
        minimumColors = 1,
        unlimitedColors = false,
        dimensionality = Dimensionality.anyDimensional,
        intParams = listOf(AnimationParameter("maxDelayBeforeSparkle",
                                              "Maximum amount of time before a pixel will sparkle",
                                              5000)),
    )
) { leds, params, scope ->
    val color = params.colors[0]
    val maxDelayBeforeSparkle = params.intParams.getValue("maxDelayBeforeSparkle")

    leds.apply {
        pixelIndices.map { n ->
            scope.launch {
                delay((randomDouble() * maxDelayBeforeSparkle).toLong())
                yield() // don't set color if the animation was cancelled during the delay
                setPixelProlongedColor(n, color)
            }
        }.joinAll()
    }
}
