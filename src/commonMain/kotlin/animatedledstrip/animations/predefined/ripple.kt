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
import animatedledstrip.leds.colormanagement.setPixelFadeColor
import animatedledstrip.leds.locationmanagement.PixelLocation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val ripple = PredefinedAnimation(
    Animation.AnimationInfo(
        name = "Ripple",
        abbr = "RPL",
        description = "Starts two [Meteor](Meteor) animations running in opposite " +
                      "directions from `center`, stopping after traveling `distance` " +
                      "or at the end of the strip/section, whichever comes first.\n" +
                      "Does not wait for the Meteor animations to be complete before " +
                      "returning, giving a ripple-like appearance when run continuously.",
        signatureFile = "ripple.png",
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

    val distance = params.distanceParams.getValue("distance").maxDistance

    @Suppress("DuplicatedCode")
    leds.apply {
        val pixelsToModifyPerIteration: MutableMap<Int, MutableList<Int>> = mutableMapOf()
        val changedPixels: MutableList<PixelLocation> = mutableListOf()

        var iteration = 0
        var currentDistance = 0.0

        while (currentDistance < distance) {
            pixelsToModifyPerIteration[iteration] = mutableListOf()
            for (pixel in leds.sectionManager.stripManager.pixelLocationManager.pixelLocations) {
                if (pixel !in changedPixels && pixel.location.distanceFrom(center) < currentDistance) {
                    pixelsToModifyPerIteration[iteration]!!.add(pixel.index)
                    changedPixels.add(pixel)
                }
            }
            iteration++
            currentDistance += movementPerIteration
        }

        scope.launch {
            for (i in 0 until iteration) {
                for (pixel in pixelsToModifyPerIteration[i]!!)
                    leds.setPixelFadeColor(pixel, color)
                delay(interMovementDelay)
            }
        }
        delay(interAnimationDelay)
    }
}
