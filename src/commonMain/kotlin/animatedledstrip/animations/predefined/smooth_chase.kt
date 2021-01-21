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
import animatedledstrip.colors.PreparedColorContainer
import animatedledstrip.colors.shift
import animatedledstrip.leds.animationmanagement.PixelModificationLists
import animatedledstrip.leds.animationmanagement.PixelsToModify
import animatedledstrip.leds.colormanagement.setPixelProlongedColors
import animatedledstrip.leds.locationmanagement.groupPixelsAlongLine
import kotlinx.coroutines.delay

val smoothChase = DefinedAnimation(
    Animation.AnimationInfo(
        name = "Smooth Chase",
        abbr = "SCH",
        description = "Each pixel is set to its respective color in `colors[0]`.\n" +
                      "Then, if the direction is `Direction.FORWARD`, each pixel is set " +
                      "to `colors[0][i + 1]`, then `colors[0][i + 2]`, etc. to create " +
                      "the illusion that the animation is 'moving'.\n" +
                      "If the direction is `Direction.BACKWARD`, the same happens but " +
                      "with indices `i`, `i-1`, `i-2`, etc.\n" +
                      "Works best with a ColorContainer with multiple colors.",
        runCountDefault = -1,
        minimumColors = 1,
        unlimitedColors = false,
        dimensionality = Dimensionality.oneDimensional,
        directional = true,
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

        val color = params.extraData.getOrPut("color") {
            params.colors[0].prepare(pixelsToModifyPerIteration.size)
        } as PreparedColorContainer

        for (i in pixelsToModifyPerIteration.indices) {
            val sColor = color.shift(i)
            for (g in pixelsToModifyPerIteration.indices) {
                setPixelProlongedColors(pixelsToModifyPerIteration[g].allSetPixels, sColor[g])
            }
            delay(interMovementDelay)
        }
    }
}
