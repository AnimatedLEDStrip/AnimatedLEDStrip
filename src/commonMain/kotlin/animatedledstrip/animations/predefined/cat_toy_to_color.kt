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
import animatedledstrip.animations.Dimensionality
import animatedledstrip.animations.PredefinedAnimation
import animatedledstrip.colors.inverse
import animatedledstrip.leds.animationmanagement.iterateOver
import animatedledstrip.leds.animationmanagement.randomDouble
import animatedledstrip.leds.animationmanagement.shuffledIndices
import animatedledstrip.leds.colormanagement.getPixelProlongedColor
import animatedledstrip.leds.colormanagement.setPixelAndRevertAfterDelay
import animatedledstrip.leds.colormanagement.setPixelProlongedColor
import kotlinx.coroutines.delay

val catToyToColor = PredefinedAnimation(
    Animation.AnimationInfo(
        name = "Cat Toy to Color",
        abbr = "CTC",
        description = "Using a process similar to the [Cat Toy](Cat-Toy) " +
                      "animation, set a strip's color.\n" +
                      "When the moving pixel goes past a pixel it has already set, " +
                      "it temporarily sets it to the inverse of that color before " +
                      "reverting it and moving on.",
        signatureFile = "cat_toy_to_color.png",
        runCountDefault = 1,
        minimumColors = 1,
        unlimitedColors = false,
        dimensionality = Dimensionality.oneDimensional,
        directional = false,
        intParams = listOf(AnimationParameter("delay", "Delay used during animation", 5)),
    )
) { leds, params, _ ->
    val color0 = params.colors[0]
    val inverseColor = params.colors[0].inverse()
    val delay = params.intParams.getValue("delay").toLong()

    leds.apply {
        val pixels = shuffledIndices()
        var oldPixel = 0

        for (newPixel in pixels) {
            when {
                oldPixel < newPixel ->
                    iterateOver(oldPixel until newPixel) {
                        setPixelAndRevertAfterDelay(
                            it,
                            if (getPixelProlongedColor(it) == color0[it]) inverseColor else color0,
                            delay,
                        )
                    }
                else ->
                    iterateOver(oldPixel downTo newPixel + 1) {
                        setPixelAndRevertAfterDelay(
                            it,
                            if (getPixelProlongedColor(it) == color0[it]) inverseColor else color0,
                            delay,
                        )
                    }
            }
            setPixelProlongedColor(newPixel, color0)
            delay((randomDouble() * delay * 100).toLong())
            oldPixel = newPixel
        }
    }
}
