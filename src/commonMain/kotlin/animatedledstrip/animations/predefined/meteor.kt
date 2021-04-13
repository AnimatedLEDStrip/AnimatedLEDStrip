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
import animatedledstrip.animations.parameters.Distance
import animatedledstrip.animations.parameters.Equation
import animatedledstrip.animations.parameters.Rotation
import animatedledstrip.leds.animationmanagement.PixelModificationLists
import animatedledstrip.leds.animationmanagement.PixelsToModify
import animatedledstrip.leds.colormanagement.revertPixel
import animatedledstrip.leds.colormanagement.setPixelFadeColor
import animatedledstrip.leds.colormanagement.setPixelTemporaryColor
import animatedledstrip.leds.locationmanagement.groupPixelsAlongLine
import kotlinx.coroutines.delay

val meteor = DefinedAnimation(
    Animation.AnimationInfo(
        name = "Meteor",
        abbr = "MET",
        description = "Like a [Pixel Run](Pixel-Run) animation, but the " +
                      "'running' pixel has a trail behind it where the pixels " +
                      "fade back from `colors[0]`.",
        runCountDefault = -1,
        minimumColors = 1,
        unlimitedColors = false,
        dimensionality = Dimensionality.anyDimensional,
        intParams = listOf(AnimationParameter("interMovementDelay", "Delay between movements in the animation", 10)),
        doubleParams = listOf(AnimationParameter("movementPerIteration",
                                                 "How far to move along the X axis during each iteration of the animation",
                                                 1.0),
                              AnimationParameter("maximumInfluence",
                                                 "How far away from the line a pixel can be affected",
                                                 1.0)),
        distanceParams = listOf(AnimationParameter("offset",
                                                   "Offset of the line in the XYZ directions",
                                                   Distance.NO_DISTANCE)),
        rotationParams = listOf(AnimationParameter("rotation", "Rotation of the line around the XYZ axes",
                                                   Rotation.NO_ROTATION)),
        equationParams = listOf(AnimationParameter("lineEquation",
                                                   "The equation representing the line the meteor will follow",
                                                   Equation())),
    )
) { leds, params, _ ->
    val color = params.colors[0]
    val interMovementDelay = params.intParams.getValue("interMovementDelay").toLong()
    val movementPerIteration = params.doubleParams.getValue("movementPerIteration")
    val maximumInfluence = params.doubleParams.getValue("maximumInfluence")
    val offset = params.distanceParams.getValue("offset")
    val rotation = params.rotationParams.getValue("rotation")
    val lineEquation = params.equationParams.getValue("lineEquation")
    val completedRuns = params.extraData["completedRuns"]!! as Int

    leds.apply {
        val pixelModLists =
            (params.extraData.getOrPut("modLists") {
                groupPixelsAlongLine(lineEquation, rotation, offset, maximumInfluence, movementPerIteration)
            } as PixelModificationLists)
        val pixelsToModifyPerIteration: List<PixelsToModify> = pixelModLists.modLists

        for (i in pixelsToModifyPerIteration.indices) {
            for ((sPixel, fPixel) in pixelsToModifyPerIteration[i].pairedSetRevertPixels) {
                setPixelTemporaryColor(sPixel, color)
                setPixelFadeColor(fPixel, color)
                revertPixel(fPixel)
            }
            for (pixel in pixelsToModifyPerIteration[i].unpairedSetPixels)
                setPixelTemporaryColor(pixel, color)
            for (pixel in pixelsToModifyPerIteration[i].unpairedRevertPixels) {
                setPixelFadeColor(pixel, color)
                revertPixel(pixel)
            }
            delay(interMovementDelay)
        }

        if (completedRuns + 1 == params.runCount)
            for (pixel in pixelModLists.lastRevertList) {
                setPixelFadeColor(pixel, color)
                revertPixel(pixel)
            }
    }
}
