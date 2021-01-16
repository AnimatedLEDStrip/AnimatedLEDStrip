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
import animatedledstrip.leds.animationmanagement.iterateOver
import animatedledstrip.leds.animationmanagement.numLEDs
import animatedledstrip.leds.colormanagement.setPixelAndRevertAfterDelay
import animatedledstrip.leds.colormanagement.setPixelFadeColor

val bounce = DefinedAnimation(
    Animation.AnimationInfo(
        name = "Bounce",
        abbr = "BNC",
        description = "Pixel 'bounces' back and forth, with " +
                      "pixels at the end of each bounce fading back to their prolonged " +
                      "color after being set from `colors[0]`.\n\n" +
                      "Note that this animation has a quadratic time complexity.",
        runCountDefault = -1,
        minimumColors = 1,
        unlimitedColors = false,
        dimensionality = Dimensionality.oneDimensional,
        directional = false,
        intParams = listOf(AnimationParameter("interMovementDelay",
                                              "Delay between movements",
                                              5)),
    )
) { leds, params, _ ->
    val color = params.colors[0]

    val interMovementDelay = params.intParams.getValue("interMovementDelay").toLong()

    leds.apply {
        iterateOver(0 until numLEDs / 2) { i ->
            for (j in i until numLEDs - i - 1) {
                setPixelAndRevertAfterDelay(j, color, interMovementDelay)
            }
            setPixelFadeColor(numLEDs - i - 1, color)

            for (j in numLEDs - i - 2 downTo i + 1) {
                setPixelAndRevertAfterDelay(j, color, interMovementDelay)
            }
            setPixelFadeColor(i, color)
        }
        if (numLEDs % 2 == 1) setPixelFadeColor(numLEDs / 2, color)
    }
}
