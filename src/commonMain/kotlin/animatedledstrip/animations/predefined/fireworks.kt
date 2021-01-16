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
import animatedledstrip.colors.isNotEmpty
import animatedledstrip.leds.animationmanagement.runParallel
import animatedledstrip.leds.colormanagement.randomColor
import animatedledstrip.leds.locationmanagement.pixelLocationManager
import kotlinx.coroutines.delay

val fireworks = DefinedAnimation(
    Animation.AnimationInfo(
        name = "Fireworks",
        abbr = "FWK",
        description = "Runs [Ripple](Ripple) animations from random center points within the defined " +
                      "locations of all pixels, travelling a predefined distance.\n" +
                      "Color is chosen randomly from `colors`.",
        runCountDefault = -1,
        minimumColors = 1,
        unlimitedColors = true,
        dimensionality = Dimensionality.oneDimensional,
        directional = false,
        intParams = listOf(AnimationParameter("interMovementDelay",
                                              "Delay between movements in the ripple animation",
                                              30),
                           AnimationParameter("interAnimationDelay",
                                              "Time between start of one animation and start of the next",
                                              500)),
        doubleParams = listOf(AnimationParameter("movementPerIteration",
                                                 "How far to move during each iteration of the animation",
                                                 1.0)),
        distanceParams = listOf(AnimationParameter("distance",
                                                   "Distance each firework should travel",
                                                   PercentDistance(0.1, 0.1, 0.1))),
    )
) { leds, params, _ ->
    val color = params.randomColor()
    val interAnimationDelay = params.intParams.getValue("interAnimationDelay").toLong()

    leds.apply {
        if (color.isNotEmpty()) {
            runParallel(params.withModifications(
                animation = "Ripple",
                colors = listOf(color),
                locationParamMods = mapOf("center" to pixelLocationManager.randomLocation()),
                distanceParamMods = mapOf("distance" to params.distanceParams.getValue("distance") / 2)
            ))
            delay(interAnimationDelay)
        }
    }
}
