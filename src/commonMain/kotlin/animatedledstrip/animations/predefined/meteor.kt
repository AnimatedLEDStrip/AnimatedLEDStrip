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
import animatedledstrip.leds.animationmanagement.iterateOverPixels
import animatedledstrip.leds.animationmanagement.iterateOverPixelsReverse
import animatedledstrip.leds.colormanagement.setPixelFadeColor
import kotlinx.coroutines.delay

val meteor = PredefinedAnimation(
    Animation.AnimationInfo(
        name = "Meteor",
        abbr = "MET",
        description = "Like a [Pixel Run](Pixel-Run) animation, but the " +
                      "'running' pixel has a trail behind it where the pixels " +
                      "fade back from `pCols[0]`.",
        signatureFile = "meteor.png",
        runCountDefault = -1,
        minimumColors = 1,
        unlimitedColors = false,
        dimensionality = Dimensionality.oneDimensional,
        directional = true,
        intParams = listOf(AnimationParameter("delay", "Delay used during animation", 10)),
    )
) { leds, params, _ ->
    val color = params.colors[0]
    val delay = params.intParams.getValue("delay").toLong()

    leds.apply {
        when (params.direction) {
            Direction.FORWARD -> iterateOverPixels {
                setPixelFadeColor(it, color)
                delay(delay)
            }
            Direction.BACKWARD -> iterateOverPixelsReverse {
                setPixelFadeColor(it, color)
                delay(delay)
            }
        }
    }
}
