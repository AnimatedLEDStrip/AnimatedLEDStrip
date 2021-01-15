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
import animatedledstrip.leds.colormanagement.revertPixels
import animatedledstrip.leds.colormanagement.setPixelTemporaryColors
import animatedledstrip.leds.locationmanagement.groupPixelsAlongLine
import kotlinx.coroutines.delay

val pixelRun = DefinedAnimation(
    Animation.AnimationInfo(
        name = "Pixel Run",
        abbr = "PXR",
        description = "A pixel colored from `colors[0]` runs along a line, " +
                      "affecting pixels within `maximumInfluence` of the line.",
        runCountDefault = -1,
        minimumColors = 1,
        unlimitedColors = false,
        dimensionality = Dimensionality.anyDimensional,
        directional = false,
        intParams = listOf(AnimationParameter("interMovementDelay", "Delay between movements in the animation", 10)),
        doubleParams = listOf(AnimationParameter("movementPerIteration",
                                                 "How far to move along the X axis during each iteration of the animation",
                                                 1.0),
                              AnimationParameter("maximumInfluence",
                                                 "How far away from the line a pixel can be affected",
                                                 1.0)),
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
        val pixelsToModifyPerIteration: List<List<Int>> =
            groupPixelsAlongLine(lineEquation, rotation, offset, maximumInfluence, movementPerIteration)

        for (i in pixelsToModifyPerIteration.indices) {
            setPixelTemporaryColors(pixelsToModifyPerIteration[i], color)
            revertPixels(pixelsToModifyPerIteration[(i - 1 + pixelsToModifyPerIteration.size) % pixelsToModifyPerIteration.size] - pixelsToModifyPerIteration[i])
            delay(interMovementDelay)
        }
        revertPixels(pixelsToModifyPerIteration[pixelsToModifyPerIteration.size - 1])
    }
}
