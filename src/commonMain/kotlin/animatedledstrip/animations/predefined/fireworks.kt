/*
 *  Copyright (c) 2018-2020 AnimatedLEDStrip
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package animatedledstrip.animations.predefined

import animatedledstrip.animations.*
import animatedledstrip.colors.isNotEmpty
import animatedledstrip.leds.animationmanagement.randomIndex
import animatedledstrip.leds.animationmanagement.runParallel
import animatedledstrip.leds.colormanagement.randomColor
import animatedledstrip.leds.stripmanagement.Location
import kotlinx.coroutines.delay

val fireworks = PredefinedAnimation(
    Animation.AnimationInfo(
        name = "Fireworks",
        abbr = "FWK",
        description = "Runs [Ripple](Ripple) animations from random centers in " +
                      "the section, with a predefined distance.\n" +
                      "Color is chosen randomly from `pCols`.",
        signatureFile = "fireworks.png",
        runCountDefault = -1,
        minimumColors = 1,
        unlimitedColors = true,
        dimensionality = Dimensionality.oneDimensional,
        directional = false,
        intParams = listOf(AnimationParameter("delay", "Delay used during ripple animation", 30),
                           AnimationParameter("interAnimationDelay",
                                              "Time between start of one animation and start of the next",
                                              500)),
        distanceParams = listOf(AnimationParameter("distance",
                                                   "Distance each firework should travel",
                                                   Distance(0.2, 0.2, 0.2))),
//        center = ParamUsage.NOTUSED,
//        delay = ParamUsage.USED,
//        delayDefault = 30,
//        direction = ParamUsage.NOTUSED,
//        distance = ParamUsage.USED,
//        distanceDefault = 20,
//        spacing = ParamUsage.NOTUSED,
    )
) { leds, params, _ ->
    val interAnimationDelay = params.intParams.getValue("interAnimationDelay").toLong()


    leds.apply {
        val color = params.randomColor()
        if (color.isNotEmpty()) {
            runParallel(params.withModifications(
                colors = mutableListOf(color),
                animation = "Ripple",
                locationParamMods = mapOf("center" to Location(randomIndex().toDouble(), 0.0, 0.0))
            ))
            delay(interAnimationDelay)
        }
    }
}
