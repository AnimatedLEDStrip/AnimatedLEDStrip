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
import animatedledstrip.leds.animationmanagement.numLEDs
import animatedledstrip.leds.animationmanagement.runSequential
import animatedledstrip.leds.colormanagement.setPixelProlongedColor
import animatedledstrip.leds.sectionmanagement.getSubSection

val stack = DefinedAnimation(
    Animation.AnimationInfo(
        name = "Stack",
        abbr = "STK",
        description = "Pixels are run from one end of the strip to the " +
                      "other, 'stacking' up.\n" +
                      "Each pixel has to travel a shorter distance than the last.\n\n" +
                      "Note that this animation has a quadratic time complexity.",
        signatureFile = "stack.png",
        runCountDefault = 1,
        minimumColors = 1,
        unlimitedColors = false,
        dimensionality = Dimensionality.oneDimensional,
        directional = true,
        intParams = listOf(AnimationParameter("interMovementDelay", "Delay between movements in the animation", 10)),
    )
) { leds, params, _ ->
    val color = params.colors[0]
    val direction = params.direction

    leds.apply {
        when (direction) {
            Direction.FORWARD ->
                iterateOverPixelsReverse {
                    runSequential(
                        animation = params.withModifications(animation = "Pixel Run"),
                        section = getSubSection(0, it),
                    )
                    setPixelProlongedColor(it, color)
                }
            Direction.BACKWARD ->
                iterateOverPixels {
                    runSequential(
                        animation = params.withModifications(animation = "Pixel Run"),
                        section = getSubSection(it, numLEDs - 1),
                    )
                    setPixelProlongedColor(it, color)
                }
        }
    }
}
