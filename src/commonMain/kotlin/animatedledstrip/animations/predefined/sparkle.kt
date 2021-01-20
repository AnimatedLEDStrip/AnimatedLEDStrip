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
import animatedledstrip.leds.animationmanagement.pixels
import animatedledstrip.leds.animationmanagement.randomDouble
import animatedledstrip.leds.colormanagement.setPixelAndRevertAfterDelay
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

val sparkle = DefinedAnimation(
    Animation.AnimationInfo(
        name = "Sparkle",
        abbr = "SPK",
        description = "Each LED is changed to `colors[0]` for `sparkleDuration` milliseconds " +
                      "before reverting.\n" +
                      "A separate coroutine is created for each pixel.\n" +
                      "Each coroutine waits up to `maxDelayBeforeSparkle` milliseconds before " +
                      "sparkling its pixel.",
        runCountDefault = -1,
        minimumColors = 1,
        unlimitedColors = false,
        dimensionality = Dimensionality.anyDimensional,
        directional = false,
        intParams = listOf(AnimationParameter("sparkleDuration", "Length of sparkle", 50),
                           AnimationParameter("maxDelayBeforeSparkle",
                                              "Maximum amount of time before a pixel will sparkle",
                                              5000)),
    )
) { leds, params, scope ->
    val color = params.colors[0]
    val sparkleDuration = params.intParams.getValue("sparkleDuration").toLong()
    val maxDelayBeforeSparkle = params.intParams.getValue("maxDelayBeforeSparkle")

    leds.apply {
        pixels.map { n ->
            scope.launch {
                delay((randomDouble() * maxDelayBeforeSparkle).toLong())
                yield() // don't set color if the animation was cancelled during the delay
                setPixelAndRevertAfterDelay(n, color, sparkleDuration)
            }
        }.joinAll()
    }
}
