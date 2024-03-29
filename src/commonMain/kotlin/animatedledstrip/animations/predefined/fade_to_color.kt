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
import animatedledstrip.animations.DefinedAnimation
import animatedledstrip.animations.Dimensionality
import animatedledstrip.colors.ColorContainer
import animatedledstrip.leds.animationmanagement.numLEDs
import animatedledstrip.leds.colormanagement.pixelActualColorList
import animatedledstrip.leds.colormanagement.setPixelFadeColor
import animatedledstrip.leds.colormanagement.setPixelProlongedColor

val fadeToColor = DefinedAnimation(
    Animation.AnimationInfo(
        name = "Fade to Color",
        abbr = "FTC",
        description = "Fade the strip to a color.",
        runCountDefault = 1,
        minimumColors = 1,
        unlimitedColors = false,
        dimensionality = Dimensionality.anyDimensional,
    )
) { leds, params, _ ->
    leds.apply {
        val currentColor = ColorContainer(sectionManager.pixelActualColorList.toMutableList()).prepare(numLEDs)
        for (index in 0 until numLEDs) {
            setPixelProlongedColor(index, params.colors[0])
            setPixelFadeColor(index, currentColor)
        }
    }
}
