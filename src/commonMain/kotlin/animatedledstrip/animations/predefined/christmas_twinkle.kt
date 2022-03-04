/*
 * Copyright (c) 2018-2022 AnimatedLEDStrip
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
import animatedledstrip.colors.PreparedColorContainer
import animatedledstrip.leds.animationmanagement.numLEDs
import animatedledstrip.leds.animationmanagement.randomInt
import animatedledstrip.leds.colormanagement.setStripProlongedColor
import kotlinx.coroutines.delay
import kotlin.math.abs

val christmasTwinkle = DefinedAnimation(
    Animation.AnimationInfo(
        name = "Christmas Twinkle",
        abbr = "CRT",
        description = "Randomly selects each pixel's color from the given colors, then randomly changes groups of pixels on the specified interval",
        runCountDefault = -1,
        minimumColors = 1,
        unlimitedColors = true,
        dimensionality = Dimensionality.anyDimensional,
        intParams = listOf(
            AnimationParameter("numberToChangePerIteration", "Number of pixels to change during each iteration", 10),
            AnimationParameter("interTwinkleDelay", "Delay between twinkles", 500),
        )
    )
) { leds, params, _ ->
    leds.apply {
        val colors = (if (params.extraData["colors"] != null) {
            params.extraData["colors"] as PreparedColorContainer
        } else {
            PreparedColorContainer(sectionManager.pixels.map { params.colors.random()[0] })
        }).toColorContainer()

        val numPixelsToChange = params.intParams["numberToChangePerIteration"]!!
        val interTwinkleDelay = params.intParams["interTwinkleDelay"]!!.toLong()

        for (i in 0 until numPixelsToChange) {
            val index = abs(randomInt() % colors.size)
            colors[index] = params.colors.random()[0]
        }

        val preparedColors = colors.prepare(leds.numLEDs)

        setStripProlongedColor(preparedColors)

        params.extraData["colors"] = preparedColors

        delay(interTwinkleDelay)
    }
}
