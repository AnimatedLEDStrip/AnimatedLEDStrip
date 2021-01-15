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
import animatedledstrip.leds.colormanagement.setPixelFadeColors
import animatedledstrip.leds.locationmanagement.groupPixelsByDistance
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val ripple = DefinedAnimation(
    Animation.AnimationInfo(
        name = "Ripple",
        abbr = "RPL",
        description = "Starts two [Meteor](Meteor) animations running in opposite " +
                      "directions from `center`, stopping after traveling `distance` " +
                      "or at the end of the strip/section, whichever comes first.\n" +
                      "Does not wait for the Meteor animations to be complete before " +
                      "returning, giving a ripple-like appearance when run continuously.",
        runCountDefault = -1,
        minimumColors = 1,
        unlimitedColors = false,
        dimensionality = Dimensionality.anyDimensional,
        directional = false,
        intParams = listOf(AnimationParameter("interMovementDelay", "Delay between movements in the animation", 30),
                           AnimationParameter("interAnimationDelay",
                                              "Time between start of one animation and start of the next",
                                              500)),
        doubleParams = listOf(AnimationParameter("movementPerIteration",
                                                 "How far to move during each iteration of the animation",
                                                 1.0)),
        locationParams = listOf(AnimationParameter("center", "The center of the ripple")),
        distanceParams = listOf(AnimationParameter("distance", "How far the ripple should travel")),
    )
) { leds, params, scope ->
    val color = params.colors[0]
    val interMovementDelay = params.intParams.getValue("interMovementDelay").toLong()
    val interAnimationDelay = params.intParams.getValue("interAnimationDelay").toLong()
    val movementPerIteration = params.doubleParams.getValue("movementPerIteration")
    val center = params.locationParams.getValue("center")
    val distance = params.distanceParams.getValue("distance")

    leds.apply {
        val pixelsToModifyPerIteration: List<List<Int>> =
            groupPixelsByDistance(center, distance, movementPerIteration)

        scope.launch {
            for (pixelList in pixelsToModifyPerIteration) {
                setPixelFadeColors(pixelList, color)
                delay(interMovementDelay)
            }
        }
        delay(interAnimationDelay)
    }
}
