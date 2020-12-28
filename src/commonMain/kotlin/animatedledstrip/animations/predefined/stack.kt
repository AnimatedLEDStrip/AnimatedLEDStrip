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
import animatedledstrip.leds.animationmanagement.*
import animatedledstrip.leds.colormanagement.setPixelProlongedColor
import animatedledstrip.leds.sectionmanagement.getSubSection

val stack = PredefinedAnimation(
    Animation.AnimationInfo(
        name = "Stack",
        abbr = "STK",
        description = "Pixels are run from one end of the strip/section to the " +
                      "other, 'stacking' up.\n" +
                      "Each pixel has to travel a shorter distance than the last.\n\n" +
                      "Note that this animation has a quadratic time complexity, " +
                      "meaning it gets very long very quickly.",
        signatureFile = "stack.png",
        runCountDefault = 1,
        minimumColors = 1,
        unlimitedColors = false,
        center = ParamUsage.NOTUSED,
        delay = ParamUsage.USED,
        delayDefault = 10,
        direction = ParamUsage.USED,
        distance = ParamUsage.NOTUSED,
        spacing = ParamUsage.NOTUSED,
    )
) { leds, params, _ ->
    val color0 = params.colors[0]
    val direction = params.direction

    leds.apply {
        val baseAnimation = params.withModifications(animation = "Pixel Run")

        when (direction) {
            Direction.FORWARD ->
                iterateOverPixelsReverse {
                    runSequential(
                        animation = baseAnimation,
                        section = getSubSection(0, it),
                    )
                    setPixelProlongedColor(it, color0)
                }
            Direction.BACKWARD ->
                iterateOverPixels {
                    runSequential(
                        animation = baseAnimation,
                        section = getSubSection(it, numLEDs - 1),
                    )
                    setPixelProlongedColor(it, color0)
                }
        }
    }
}
