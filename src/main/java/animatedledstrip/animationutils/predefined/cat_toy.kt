/*
 *  Copyright (c) 2020 AnimatedLEDStrip
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

package animatedledstrip.animationutils.predefined

import animatedledstrip.animationutils.Animation
import animatedledstrip.animationutils.Direction
import animatedledstrip.animationutils.ParamUsage
import animatedledstrip.animationutils.PredefinedAnimation
import animatedledstrip.leds.randomPixelIn
import animatedledstrip.utils.delayBlocking

val catToy = PredefinedAnimation(
    Animation.AnimationInfo(
        name = "Cat Toy",
        abbr = "CAT",
        description = "Entertain your cat with a pixel running back and forth to " +
                "random locations, waiting for up to `delay * 500` milliseconds between movements.\n" +
                "Works better on a shorter strip (~100 pixels).",
        signatureFile = "cat_toy.png",
        repetitive = true,
        minimumColors = 1,
        unlimitedColors = false,
        center = ParamUsage.NOTUSED,
        delay = ParamUsage.USED,
        delayDefault = 5,
        direction = ParamUsage.NOTUSED,
        distance = ParamUsage.NOTUSED,
        spacing = ParamUsage.NOTUSED
    )
) { leds, data, _ ->
    val color0 = data.pCols[0]
    val delay = data.delay
    var doRevert = true
    var doDelay = true
    val lastPixel = data.extraData.getOrPut("lastPixel") {
        doRevert = false
        0
    } as Int

    leds.apply {
        val pixel = randomPixelIn(indices)

        if (doRevert) revertPixel(lastPixel)

        when {
            pixel > lastPixel ->
                runSequential(
                    animation = data.copy(
                        animation = "Pixel Run",
                        direction = Direction.FORWARD
                    ),
                    section = getSubSection(lastPixel, pixel)
                )
            pixel < lastPixel ->
                runSequential(
                    animation = data.copy(
                        animation = "Pixel Run",
                        direction = Direction.BACKWARD
                    ),
                    section = getSubSection(pixel, lastPixel)
                )
            pixel == lastPixel -> doDelay = false
        }

        setTemporaryPixelColor(pixel, color0)

        if (doDelay) delayBlocking((Math.random() * delay * 500).toLong())

        data.extraData["lastPixel"] = pixel

    }
}
