/*
 *  Copyright (c) 2018-2020 AnimatedLEDStrip
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package animatedledstrip.animations.predefined

import animatedledstrip.animations.*
import animatedledstrip.leds.animationmanagement.iterateOver
import animatedledstrip.leds.animationmanagement.numLEDs
import animatedledstrip.leds.animationmanagement.runSequential
import animatedledstrip.leds.colormanagement.setPixelFadeColor
import animatedledstrip.leds.sectionmanagement.getSubSection

val bounce = PredefinedAnimation(
    Animation.AnimationInfo(
        name = "Bounce",
        abbr = "BNC",
        dimensionality = Dimensionality.ONE_DIMENSIONAL,
        description = "Similar to [Bounce to Color](Bounce-to-Color) but the " +
                      "pixels at the end of each bounce fade back to their prolonged " +
                      "color after being set from `pCols[0]`.\n\n" +
                      "Note that this animation has a quadratic time " +
                      "complexity, meaning it gets very long very quickly.",
        signatureFile = "bounce.png",
        runCountDefault = -1,
        minimumColors = 1,
        unlimitedColors = false,
        center = ParamUsage.NOTUSED,
        delay = ParamUsage.USED,
        delayDefault = 10,
        direction = ParamUsage.NOTUSED,
        distance = ParamUsage.NOTUSED,
        spacing = ParamUsage.NOTUSED,
    )
) { leds, params, _ ->
    val color0 = params.colors[0]

    leds.apply {
        iterateOver(0 until numLEDs / 2) { i ->
            runSequential(
                animation = params.withModifications(animation = "Pixel Run",
                                                     direction = Direction.FORWARD),
                section = getSubSection(i, numLEDs - i - 1),
            )
            setPixelFadeColor(numLEDs - i - 1, color0)

            runSequential(
                animation = params.withModifications(animation = "Pixel Run",
                                                     direction = Direction.BACKWARD),
                section = getSubSection(i, numLEDs - i - 2),
            )
            setPixelFadeColor(i, color0)
        }
        if (numLEDs % 2 == 1) setPixelFadeColor(numLEDs / 2, color0)
    }
}
