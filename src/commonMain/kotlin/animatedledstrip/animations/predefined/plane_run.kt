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
import animatedledstrip.animations.parameters.Rotation
import animatedledstrip.leds.animationmanagement.PixelModificationLists
import animatedledstrip.leds.animationmanagement.PixelsToModify
import animatedledstrip.leds.colormanagement.revertPixel
import animatedledstrip.leds.colormanagement.revertPixels
import animatedledstrip.leds.colormanagement.setPixelTemporaryColor
import animatedledstrip.leds.locationmanagement.groupPixelsByXLocation
import kotlinx.coroutines.delay

val planeRun = DefinedAnimation(
    Animation.AnimationInfo(
        name = "Plane Run",
        abbr = "PLR",
        description = "Runs a plane through all pixels.\n\n" +
                      "Note: Two-dimensional operation requires plane to be rotated around the X axis " +
                      "(probably by Pi/2 radians in most use cases)",
        runCountDefault = -1,
        minimumColors = 1,
        unlimitedColors = false,
        dimensionality = Dimensionality.anyDimensional,
        intParams = listOf(AnimationParameter("interMovementDelay", "Delay between movements in the animation", 30)),
        doubleParams = listOf(AnimationParameter("movementPerIteration",
                                                 "How far to move during each iteration of the animation",
                                                 1.0)),
        rotationParams = listOf(AnimationParameter("rotation", "Rotation of the plane around the XYZ axes",
                                                   Rotation.NO_ROTATION)),
    )
) { leds, params, _ ->
    val color = params.colors[0]

    val interMovementDelay = params.intParams.getValue("interMovementDelay").toLong()
    val movementPerIteration = params.doubleParams.getValue("movementPerIteration")
    val rotation = params.rotationParams.getValue("rotation")
    val completedRuns = params.extraData["completedRuns"]!! as Int

    leds.apply {
        val pixelModLists =
            (params.extraData.getOrPut("modLists") {
                groupPixelsByXLocation(AbsoluteDistance.NO_DISTANCE, rotation, movementPerIteration)
            } as PixelModificationLists)
        val pixelsToModifyPerIteration: List<PixelsToModify> = pixelModLists.modLists

        for (i in pixelsToModifyPerIteration.indices) {
            for ((sPixel, rPixel) in pixelsToModifyPerIteration[i].pairedSetRevertPixels) {
                setPixelTemporaryColor(sPixel, color)
                revertPixel(rPixel)
            }
            for (pixel in pixelsToModifyPerIteration[i].unpairedSetPixels)
                setPixelTemporaryColor(pixel, color)
            for (pixel in pixelsToModifyPerIteration[i].unpairedRevertPixels)
                revertPixel(pixel)
            delay(interMovementDelay)
        }

        if (completedRuns + 1 == params.runCount) revertPixels(pixelModLists.lastRevertList)
    }
}
