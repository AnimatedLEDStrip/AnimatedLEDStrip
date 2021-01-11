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
import animatedledstrip.leds.colormanagement.setStripProlongedColor
import kotlinx.coroutines.delay

val smoothFade = DefinedAnimation(
    Animation.AnimationInfo(
        name = "Smooth Fade",
        abbr = "SMF",
        description = "Like a [Smooth Chase](Smooth-Chase) animation, but the " +
                      "whole strip is the same color while fading through `colors[0]`.",
        signatureFile = "smooth_fade.png",
        runCountDefault = -1,
        minimumColors = 1,
        unlimitedColors = false,
        dimensionality = Dimensionality.oneDimensional,
        directional = false,
        intParams = listOf(AnimationParameter("interColorChangeDelay", "Delay between color changes", 50),
                           AnimationParameter("colorsToFadeThrough",
                                              "Number of colors to fade through (used to prepare the ColorContainer)",
                                              100)),
    )
) { leds, params, _ ->
    val colorsToFadeThrough = params.intParams.getValue("colorsToFadeThrough")
    val color = params.colors[0].originalColorContainer().prepare(colorsToFadeThrough)
    val interColorChangeDelay = params.intParams.getValue("interColorChangeDelay").toLong()

    leds.apply {
        for (i in 0 until colorsToFadeThrough) {
            setStripProlongedColor(color[i])
            delay(interColorChangeDelay)
        }
    }
}
