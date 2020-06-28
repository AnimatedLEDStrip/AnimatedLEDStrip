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

import animatedledstrip.animationutils.*
import animatedledstrip.utils.delayBlocking

val pixelMarathon = PredefinedAnimation(
    Animation.AnimationInfo(
        name = "Pixel Marathon",
        abbr = "PXM",
        repetitive = true,
        numReqColors = 5,
        center = ParamUsage.NOTUSED,
        delay = ParamUsage.USED,
        delayDefault = 8,
        direction = ParamUsage.USED,
        distance = ParamUsage.NOTUSED,
        spacing = ParamUsage.NOTUSED
    )
) { leds, data, scope ->
    val color0 = data.pCols[0]
    val color1 = data.pCols[1]
    val color2 = data.pCols[2]
    val color3 = data.pCols[3]
    val color4 = data.pCols[4]
    val delay = data.delay
    val direction = data.direction

    leds.apply {
        val baseAnimation = AnimationData().animation("Pixel Run")
            .direction(direction).delay(delay)

        runParallel(baseAnimation.copy().color(color4), scope = scope)
        delayBlocking((Math.random() * 500).toLong())

        runParallel(baseAnimation.copy().color(color3), scope = scope)
        delayBlocking((Math.random() * 500).toLong())

        runParallel(baseAnimation.copy().color(color1), scope = scope)
        delayBlocking((Math.random() * 500).toLong())

        runParallel(baseAnimation.copy().color(color2), scope = scope)
        delayBlocking((Math.random() * 500).toLong())

        runParallel(baseAnimation.copy().color(color0), scope = scope)
        delayBlocking((Math.random() * 500).toLong())

        runParallel(baseAnimation.copy().color(color1), scope = scope)
        delayBlocking((Math.random() * 500).toLong())

        runParallel(baseAnimation.copy().color(color4), scope = scope)
        delayBlocking((Math.random() * 500).toLong())

        runParallel(baseAnimation.copy().color(color2), scope = scope)
        delayBlocking((Math.random() * 500).toLong())

        runParallel(baseAnimation.copy().color(color3), scope = scope)
        delayBlocking((Math.random() * 500).toLong())

        runParallel(baseAnimation.copy().color(color0), scope = scope)
        delayBlocking((Math.random() * 500).toLong())
    }
}
