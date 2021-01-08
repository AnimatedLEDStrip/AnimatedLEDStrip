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
import animatedledstrip.colors.offsetBy
import animatedledstrip.leds.animationmanagement.iterateOverPixels
import animatedledstrip.leds.animationmanagement.iterateOverPixelsReverse
import animatedledstrip.leds.colormanagement.setStripProlongedColor
import kotlinx.coroutines.delay

val smoothChase = PredefinedAnimation(
    Animation.AnimationInfo(
        name = "Smooth Chase",
        abbr = "SCH",
        description = "Each pixel is set to its respective color in `pCols[0]`.\n" +
                      "Then, if the direction is `Direction.FORWARD`, each pixel is set " +
                      "to `pCols[0][i + 1]`, then `pCols[0][i + 2]`, etc. to create " +
                      "the illusion that the animation is 'moving'.\n" +
                      "If the direction is `Direction.BACKWARD`, the same happens but " +
                      "with indices `i`, `i-1`, `i-2`, etc.\n" +
                      "Works best with a ColorContainer with multiple animatedledstrip.colors.",
        signatureFile = "smooth_chase.png",
        runCountDefault = -1,
        minimumColors = 1,
        unlimitedColors = false,
        dimensionality = Dimensionality.oneDimensional,
        directional = true,
        intParams = listOf(AnimationParameter("delay", "Delay used during animation", 50)),
    )
) { leds, params, _ ->
    val color0 = params.colors[0]
    val delay = params.intParams.getValue("delay").toLong()
    val direction = params.direction

    leds.apply {
        when (direction) {
            Direction.FORWARD ->
                iterateOverPixels { m ->
                    setStripProlongedColor(color0.offsetBy(m))
                    delay(delay)
                }
            Direction.BACKWARD ->
                iterateOverPixelsReverse { m ->
                    setStripProlongedColor(color0.offsetBy(m))
                    delay(delay)
                }
        }
    }
}