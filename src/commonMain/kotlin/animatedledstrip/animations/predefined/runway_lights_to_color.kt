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
import animatedledstrip.animations.parameters.AbsoluteDistance
import animatedledstrip.leds.animationmanagement.PixelsToModify
import animatedledstrip.leds.animationmanagement.numLEDs
import animatedledstrip.leds.colormanagement.setPixelProlongedColor
import animatedledstrip.leds.locationmanagement.groupGroupsOfPixelsAlongLine
import kotlinx.coroutines.delay

val runwayLightsToColor = DefinedAnimation(
    Animation.AnimationInfo(
        name = "Runway Lights to Color",
        abbr = "RTC",
        description = "Similar to [Runway Lights](Runway-Lights) but LEDs " +
                      "do not revert back to their prolonged color.",
        runCountDefault = 1,
        minimumColors = 1,
        unlimitedColors = false,
        dimensionality = Dimensionality.anyDimensional,
        intParams = listOf(AnimationParameter("interMovementDelay", "Delay between movements in the animation", 150)),
        doubleParams = listOf(AnimationParameter("movementPerIteration",
                                                 "How far to move along the X axis during each iteration of the animation",
                                                 1.0),
                              AnimationParameter("maximumInfluence",
                                                 "How far away from the line a pixel can be affected",
                                                 0.9),
                              AnimationParameter("spacing",
                                                 "Distance along the X axis between centers of lit points",
                                                 5.0)),
        distanceParams = listOf(AnimationParameter("offset",
                                                   "Offset of the line in the XYZ directions",
                                                   AbsoluteDistance(0.0, 0.0, 0.0))),
        rotationParams = listOf(AnimationParameter("rotation", "Rotation of the line around the XYZ axes")),
        equationParams = listOf(AnimationParameter("lineEquation",
                                                   "The equation representing the line the the pixel will follow")),
    )
) { leds, params, _ ->
    val color = params.colors[0]
    val interMovementDelay = params.intParams.getValue("interMovementDelay").toLong()
    val movementPerIteration = params.doubleParams.getValue("movementPerIteration")
    val maximumInfluence = params.doubleParams.getValue("maximumInfluence")
    val spacing = params.doubleParams.getValue("spacing")
    val offset = params.distanceParams.getValue("offset")
    val rotation = params.rotationParams.getValue("rotation")
    val lineEquation = params.equationParams.getValue("lineEquation")

    leds.apply {
        val setPixels = mutableListOf<Int>()
        val groupedPixels: List<PixelsToModify> = groupGroupsOfPixelsAlongLine(lineEquation,
                                                                               rotation,
                                                                               offset,
                                                                               maximumInfluence,
                                                                               movementPerIteration,
                                                                               spacing).modLists

        val pixelsToModifyPerIteration: MutableList<List<Int>> = mutableListOf()

        for (modList in groupedPixels) {
            val list = mutableListOf<Int>()
            for ((pixel, _) in modList.pairedSetRevertPixels) {
                if (pixel !in setPixels) {
                    list.add(pixel)
                    setPixels.add(pixel)
                }
            }
            for (pixel in modList.unpairedSetPixels) {
                if (pixel !in setPixels) {
                    list.add(pixel)
                    setPixels.add(pixel)
                }
            }
            pixelsToModifyPerIteration.add(list.toList())
            if (setPixels.size == numLEDs) break
        }

        for (i in pixelsToModifyPerIteration.indices) {
            for (pair in pixelsToModifyPerIteration[i])
                setPixelProlongedColor(pair, color)
            delay(interMovementDelay)
        }
    }
}
