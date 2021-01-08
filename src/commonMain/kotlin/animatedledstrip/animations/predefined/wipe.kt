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
import animatedledstrip.leds.animationmanagement.numLEDs
import animatedledstrip.leds.colormanagement.setPixelProlongedColor
import animatedledstrip.leds.locationmanagement.PixelLocation
import animatedledstrip.leds.locationmanagement.PixelLocationManager
import animatedledstrip.leds.locationmanagement.transformLocations
import kotlinx.coroutines.delay

val wipe = PredefinedAnimation(
    Animation.AnimationInfo(
        name = "Wipe",
        abbr = "WIP",
        description = "Wipes a plane through all pixels, leaving behind colors[0].\n\n" +
                      "Note: Two-dimensional operation requires plane to be rotated around the X axis " +
                      "(probably by Pi/2 radians in most use cases)",
        signatureFile = "wipe.png",
        runCountDefault = 1,
        minimumColors = 1,
        unlimitedColors = false,
        dimensionality = Dimensionality.anyDimensional,
        directional = false,
        intParams = listOf(AnimationParameter("interMovementDelay", "Delay between movements in the animation", 30)),
        doubleParams = listOf(AnimationParameter("movementPerIteration",
                                                 "How far to move during each iteration of the animation",
                                                 10.0),
                              AnimationParameter("zRotation", "Rotation around the Z axis (radians)"),
                              AnimationParameter("xRotation", "Rotation around the X axis (radians)")),
    )
) { leds, params, _ ->
    val color = params.colors[0]

    val interMovementDelay = params.intParams.getValue("interMovementDelay").toLong()
    val movementPerIteration = params.doubleParams.getValue("movementPerIteration")
    val zRotation = params.doubleParams.getValue("zRotation")
    val xRotation = params.doubleParams.getValue("xRotation")

    val newManager = PixelLocationManager(
        leds.transformLocations(zRotation, xRotation),
        leds.numLEDs)

    leds.apply {
        val pixelsToModifyPerIteration: MutableMap<Int, MutableList<Int>> = mutableMapOf()
        val changedPixels: MutableList<PixelLocation> = mutableListOf()

        var iteration = 0
        var currentZ = newManager.zMin
        do {
            currentZ += movementPerIteration
            pixelsToModifyPerIteration[iteration] = mutableListOf()
            for (pixel in newManager.pixelLocations) {
                if (pixel !in changedPixels && pixel.location.z <= currentZ) {
                    pixelsToModifyPerIteration[iteration]!!.add(pixel.index)
                    changedPixels.add(pixel)
                }
            }
            iteration++
        } while (currentZ < newManager.zMax)

        for (i in 0 until iteration) {
            for (pixel in pixelsToModifyPerIteration[i]!!)
                leds.setPixelProlongedColor(pixel, color)
            delay(interMovementDelay)
        }
    }
}
