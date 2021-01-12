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
import animatedledstrip.leds.animationmanagement.randomDouble
import animatedledstrip.leds.animationmanagement.randomIndex
import animatedledstrip.leds.animationmanagement.runSequential
import animatedledstrip.leds.colormanagement.revertPixel
import animatedledstrip.leds.colormanagement.setPixelTemporaryColor
import animatedledstrip.leds.sectionmanagement.getSubSection
import kotlinx.coroutines.delay

val catToy = DefinedAnimation(
    Animation.AnimationInfo(
        name = "Cat Toy",
        abbr = "CAT",
        description = "Entertain your cat with a pixel running back and forth to " +
                      "random locations, waiting for up to `delay * 500` milliseconds between movements.\n" +
                      "Works better on a shorter strip (~100 pixels).",
        runCountDefault = -1,
        minimumColors = 1,
        unlimitedColors = false,
        dimensionality = Dimensionality.oneDimensional,
        directional = false,
        intParams = listOf(AnimationParameter("interMovementDelay",
                                              "Delay between movements in the pixel run animation",
                                              5),
                           AnimationParameter("maximumWait",
                                              "Maximum time spent waiting at a pixel before moving to the next",
                                              1000)),
    )
) { leds, params, _ ->
    val color = params.colors[0]
    val maximumWait = params.intParams.getValue("maximumWait")

    var doRevert = true
    var doDelay = true
    val lastPixel = params.extraData.getOrPut("previousPixel") {
        doRevert = false
        0
    } as Int

    leds.apply {
        val pixel = randomIndex()

        if (doRevert && pixel != lastPixel) revertPixel(lastPixel)

        when {
            pixel > lastPixel ->
                runSequential(
                    animation = params.withModifications(animation = "Pixel Run",
                                                         direction = Direction.FORWARD),
                    section = getSubSection(lastPixel, pixel),
                )
            pixel < lastPixel ->
                runSequential(
                    animation = params.withModifications(animation = "Pixel Run",
                                                         direction = Direction.BACKWARD),
                    section = getSubSection(pixel, lastPixel),
                )
            pixel == lastPixel -> doDelay = false
        }

        setPixelTemporaryColor(pixel, color)

        if (doDelay) delay((randomDouble() * maximumWait).toLong())

        params.extraData["previousPixel"] = pixel

    }
}
