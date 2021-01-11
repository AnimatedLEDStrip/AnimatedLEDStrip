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
import animatedledstrip.leds.animationmanagement.iterateOver
import animatedledstrip.leds.animationmanagement.numLEDs
import animatedledstrip.leds.colormanagement.setPixelProlongedColor
import kotlinx.coroutines.delay

val runwayLightsToColor = DefinedAnimation(
    Animation.AnimationInfo(
        name = "Runway Lights to Color",
        abbr = "RTC",
        description = "Similar to [Runway Lights](Runway-Lights) but LEDs " +
                      "do not revert back to their prolonged color.",
        signatureFile = "runway_lights_to_color.png",
        runCountDefault = 1,
        minimumColors = 1,
        unlimitedColors = false,
        dimensionality = Dimensionality.oneDimensional,
        directional = true,
        intParams = listOf(AnimationParameter("interMovementDelay", "Delay between movements in the animation", 150),
                           AnimationParameter("spacing", "Spacing between lit pixels", 3)),
    )
) { leds, params, _ ->
    val color = params.colors[0]
    val interMovementDelay = params.intParams.getValue("interMovementDelay").toLong()
    val direction = params.direction
    val spacing = params.intParams.getValue("spacing")

    leds.apply {
        when (direction) {
            Direction.FORWARD ->
                iterateOver(spacing - 1 downTo 0) { s ->
                    iterateOver(0 until numLEDs step spacing) { i ->
                        if (i + s < numLEDs)
                            setPixelProlongedColor(i + s, color)
                    }
                    delay(interMovementDelay)
                }
            Direction.BACKWARD ->
                iterateOver(0 until spacing) { s ->
                    iterateOver(0 until numLEDs step spacing) { i ->
                        if (i + s < numLEDs)
                            setPixelProlongedColor(i + s, color)
                    }
                    delay(interMovementDelay)
                }
        }
    }
}
