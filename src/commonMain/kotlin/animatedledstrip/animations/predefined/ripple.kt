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
import animatedledstrip.animations.parameters.PercentDistance
import animatedledstrip.leds.animationmanagement.PixelModificationLists
import animatedledstrip.leds.animationmanagement.PixelsToModify
import animatedledstrip.leds.colormanagement.setPixelFadeColor
import animatedledstrip.leds.locationmanagement.Location
import animatedledstrip.leds.locationmanagement.groupPixelsByDistance
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val ripple = DefinedAnimation(
    Animation.AnimationInfo(
        name = "Ripple",
        abbr = "RPL",
        description = "Starts at a center point and travels in a sphere (or circle or two points) " +
                      "away from that center, setting LEDs that then fade back.\n" +
                      "Does not wait for the ripple to complete before starting the next, " +
                      "only waiting `interAnimationDelay` milliseconds.",
        runCountDefault = -1,
        minimumColors = 1,
        unlimitedColors = false,
        dimensionality = Dimensionality.anyDimensional,
        intParams = listOf(AnimationParameter("interMovementDelay", "Delay between movements in the animation", 30),
                           AnimationParameter("interAnimationDelay",
                                              "Time between start of one animation and start of the next",
                                              500)),
        doubleParams = listOf(AnimationParameter("movementPerIteration",
                                                 "How far to move during each iteration of the animation",
                                                 1.0)),
        locationParams = listOf(AnimationParameter("center", "The center of the ripple", Location.CENTER)),
        distanceParams = listOf(AnimationParameter("distance",
                                                   "How far the ripple should travel",
                                                   PercentDistance(100.0, 100.0, 100.0))),
    )
) { leds, params, scope ->
    val color = params.colors[0]
    val interMovementDelay = params.intParams.getValue("interMovementDelay").toLong()
    val interAnimationDelay = params.intParams.getValue("interAnimationDelay").toLong()
    val movementPerIteration = params.doubleParams.getValue("movementPerIteration")
    val center = params.locationParams.getValue("center")
    val distance = params.distanceParams.getValue("distance")

    leds.apply {
        val pixelsToModifyPerIteration: List<PixelsToModify> =
            (params.extraData.getOrPut("modLists") {
                groupPixelsByDistance(center, distance, movementPerIteration)
            } as PixelModificationLists).modLists

        scope.launch {
            for (i in pixelsToModifyPerIteration.indices) {
                for (pixel in pixelsToModifyPerIteration[i].allSetPixels)
                    setPixelFadeColor(pixel, color)
                delay(interMovementDelay)
            }
        }
        delay(interAnimationDelay)
    }
}
