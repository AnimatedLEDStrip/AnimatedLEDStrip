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
import animatedledstrip.colors.isNotEmpty
import animatedledstrip.leds.animationmanagement.SubAnimationToRunParams
import animatedledstrip.leds.animationmanagement.randomDouble
import animatedledstrip.leds.animationmanagement.runParallel
import kotlinx.coroutines.delay

val pixelMarathon = DefinedAnimation(
    Animation.AnimationInfo(
        name = "Pixel Marathon",
        abbr = "PXM",
        description = "Watch pixels race each other along the strip.\n" +
                      "A color is chosen randomly, then a pixel is sent down the strip.\n" +
                      "After waiting for up to `maxInterAnimationDelay` milliseconds, " +
                      "another pixel is sent.\n\n" +
                      "Note that in the animation signature that there are a couple " +
                      "points where the slope of the line gets shallower (meaning the " +
                      "pixel is 'moving' faster).\n" +
                      "If you look closely, you'll see that there are actually two " +
                      "[Pixel Run](Pixel-Run) subanimations at the same spot engaged " +
                      "in a race condition - quite fitting for this animation.\n" +
                      "This happens because when an animation such as Pixel Run changes " +
                      "a pixel's temporary color, it puts a lock on that pixel so the " +
                      "pixel's color doesn't change until it's done with that pixel.",
        runCountDefault = -1,
        minimumColors = 1,
        unlimitedColors = true,
        dimensionality = Dimensionality.anyDimensional,
        intParams = listOf(AnimationParameter("interMovementDelay",
                                              "Delay between movements in the pixel run animations",
                                              8),
                           AnimationParameter("maxInterAnimationDelay",
                                              "Maximum time between start of one pixel run and start of the next",
                                              1000)),
        doubleParams = listOf(AnimationParameter("movementPerIteration",
                                                 "How far to move along the X axis during each iteration of the animation",
                                                 1.0),
                              AnimationParameter("maximumInfluence",
                                                 "How far away from the line a pixel can be affected",
                                                 1.0)),
        distanceParams = listOf(AnimationParameter("offset",
                                                   "Offset of the line in the XYZ directions",
                                                   Distance.NO_DISTANCE)),
        rotationParams = listOf(AnimationParameter("rotation",
                                                   "Rotation of the line around the XYZ axes",
                                                   Rotation.NO_ROTATION)),
        equationParams = listOf(AnimationParameter("lineEquation",
                                                   "The equation representing the line the the pixel will follow",
                                                   Equation())),
    )
) { leds, params, scope ->
    val color = params.colors.random()
    val maxInterAnimationDelay = params.intParams.getValue("maxInterAnimationDelay")

    leds.apply {
        if (color.isNotEmpty()) {
            runParallel(SubAnimationToRunParams(params.withModifications(animation = "Pixel Run",
                                                                         colors = mutableListOf(color)),
                                                scope,
                                                sectionManager))
            delay((randomDouble() * maxInterAnimationDelay).toLong())
        }
    }
}
