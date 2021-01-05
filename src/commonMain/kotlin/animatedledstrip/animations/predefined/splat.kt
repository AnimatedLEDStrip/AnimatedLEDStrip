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
import animatedledstrip.leds.colormanagement.setPixelProlongedColor
import animatedledstrip.leds.stripmanagement.PixelLocation
import kotlinx.coroutines.delay

val splat = PredefinedAnimation(
    Animation.AnimationInfo(
        name = "Splat",
        abbr = "SPT",
        description = "Similar to a [Ripple](Ripple) but the pixels don't " +
                      "fade back.\n" +
                      "Runs two Wipe animations in opposite directions starting " +
                      "from `center`, stopping after traveling `distance` or at " +
                      "the end of the section, whichever comes first.",
        signatureFile = "splat.png",
        runCountDefault = 1,
        minimumColors = 1,
        unlimitedColors = false,
        dimensionality = Dimensionality.anyDimensional,
        directional = false,
        intParams = listOf(AnimationParameter("interMovementDelay", "Delay between movements in the animation", 15)),
        doubleParams = listOf(AnimationParameter("movementPerIteration",
                                                 "How far to move during each iteration of the animation",
                                                 10.0)),
        locationParams = listOf(AnimationParameter("center", "The center of the splat")),
        distanceParams = listOf(AnimationParameter("distance", "How far the splat should reach")),
//        center = ParamUsage.USED,
//        delay = ParamUsage.USED,
//        delayDefault = 5,
//        direction = ParamUsage.NOTUSED,
//        distance = ParamUsage.USED,
//        spacing = ParamUsage.NOTUSED,
    )
) { leds, params, _ ->
    val color = params.colors[0]

    val interMovementDelay = params.intParams.getValue("interMovementDelay").toLong()

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

        for (i in 0 until iteration) {
            for (pixel in pixelsToModifyPerIteration[i]!!)
                leds.setPixelProlongedColor(pixel, color)
            delay(interMovementDelay)
        }
    }
}
