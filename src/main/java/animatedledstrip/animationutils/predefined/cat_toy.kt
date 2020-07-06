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
        repetitive = true,
        minimumColors = 1,
        center = ParamUsage.NOTUSED,
        delay = ParamUsage.USED,
        delayDefault = 5,
        direction = ParamUsage.NOTUSED,
        distance = ParamUsage.NOTUSED,
        spacing = ParamUsage.NOTUSED
    )
) { leds, data, _ ->
    val color0 = data.pCols[0]

    leds.apply {
        val pixel1 = randomPixelIn(indices)
        val pixel2 = randomPixelIn(indices.first(), pixel1)
        val pixel3 = randomPixelIn(pixel2, indices.last())
        val pixel4 = randomPixelIn(indices.first(), pixel3)
        val pixel5 = randomPixelIn(pixel4, indices.last())

        runSequential(
            animation = data.copy(
                animation = "Pixel Run",
                direction = Direction.FORWARD
            ),
            section = getSubSection(0, pixel1)
        )
        setTemporaryPixelColor(pixel1, color0)
        delayBlocking((Math.random() * 2500).toLong())
        revertPixel(pixel1)

        runSequential(
            animation = data.copy(
                animation = "Pixel Run",
                direction = Direction.BACKWARD
            ),
            section = getSubSection(pixel2, pixel1)
        )
        setTemporaryPixelColor(pixel2, color0)
        delayBlocking((Math.random() * 2500).toLong())
        revertPixel(pixel2)

        runSequential(
            animation = data.copy(
                animation = "Pixel Run",
                direction = Direction.FORWARD
            ),
            section = getSubSection(pixel2, pixel3)
        )
        setTemporaryPixelColor(pixel3, color0)
        delayBlocking((Math.random() * 2500).toLong())
        revertPixel(pixel3)

        runSequential(
            animation = data.copy(
                animation = "Pixel Run",
                direction = Direction.BACKWARD
            ),
            section = getSubSection(pixel4, pixel3)
        )
        setTemporaryPixelColor(pixel4, color0)
        delayBlocking((Math.random() * 2500).toLong())
        revertPixel(pixel4)

        runSequential(
            animation = data.copy(
                animation = "Pixel Run",
                direction = Direction.FORWARD
            ),
            section = getSubSection(pixel4, pixel5)
        )
        setTemporaryPixelColor(pixel5, color0)
        delayBlocking((Math.random() * 2500).toLong())
        revertPixel(pixel5)

        runSequential(
            animation = data.copy(
                animation = "Pixel Run",
                direction = Direction.BACKWARD
            ),
            section = getSubSection(0, pixel5)
        )
    }
}