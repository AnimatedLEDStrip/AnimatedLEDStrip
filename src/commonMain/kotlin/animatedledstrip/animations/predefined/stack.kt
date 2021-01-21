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
import animatedledstrip.leds.animationmanagement.PixelModificationLists
import animatedledstrip.leds.animationmanagement.PixelsToModify
import animatedledstrip.leds.colormanagement.revertPixel
import animatedledstrip.leds.colormanagement.revertPixels
import animatedledstrip.leds.colormanagement.setPixelProlongedColors
import animatedledstrip.leds.colormanagement.setPixelTemporaryColor
import animatedledstrip.leds.locationmanagement.groupPixelsAlongLine
import kotlinx.coroutines.delay

val stack = DefinedAnimation(
    Animation.AnimationInfo(
        name = "Stack",
        abbr = "STK",
        description = "Pixels are run from one end of the strip to the " +
                      "other, 'stacking' up.\n" +
                      "Each pixel has to travel a shorter distance than the last.\n\n" +
                      "Note that this animation has a quadratic time complexity.",
        runCountDefault = 1,
        minimumColors = 1,
        unlimitedColors = false,
        dimensionality = Dimensionality.oneDimensional,
        intParams = listOf(AnimationParameter("interMovementDelay", "Delay between movements in the animation", 10)),
        doubleParams = listOf(AnimationParameter("movementPerIteration",
                                                 "How far to move along the X axis during each iteration of the animation",
                                                 1.0),
                              AnimationParameter("maximumInfluence",
                                                 "How far away from the line a pixel can be affected",
                                                 0.9)),
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
    val offset = params.distanceParams.getValue("offset")
    val rotation = params.rotationParams.getValue("rotation")
    val lineEquation = params.equationParams.getValue("lineEquation")

    leds.apply {
        val pixelsToModifyPerIteration: List<PixelsToModify> =
            (params.extraData.getOrPut("modLists") {
                groupPixelsAlongLine(lineEquation, rotation, offset, maximumInfluence, movementPerIteration)
            } as PixelModificationLists).modLists

        for (r in pixelsToModifyPerIteration.indices.reversed()) {
            for (i in 0 until r) {
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
            if (r > 0) revertPixels(pixelsToModifyPerIteration[r - 1].allRevertPixels)
            setPixelProlongedColors(pixelsToModifyPerIteration[r].allSetPixels, color)
            delay(interMovementDelay)
        }
    }
}
