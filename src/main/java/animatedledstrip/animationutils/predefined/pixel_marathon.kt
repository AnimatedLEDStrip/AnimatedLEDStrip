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
import animatedledstrip.colors.ccpresets.EmptyColorContainer
import animatedledstrip.utils.delayBlocking

val pixelMarathon = PredefinedAnimation(
    Animation.AnimationInfo(
        name = "Pixel Marathon",
        abbr = "PXM",
        repetitive = true,
        minimumColors = 1,
        unlimitedColors = true,
        center = ParamUsage.NOTUSED,
        delay = ParamUsage.USED,
        delayDefault = 8,
        direction = ParamUsage.USED,
        distance = ParamUsage.NOTUSED,
        spacing = ParamUsage.NOTUSED
    )
) { leds, data, scope ->
    val color = data.colors.random()
    val delay = data.delay
    val direction = data.direction

    leds.apply {
        if (color != EmptyColorContainer) {
            runParallel(
                AnimationData()
                    .animation("Pixel Run")
                    .color(color)
                    .direction(direction)
                    .delay(delay),
                scope = scope
            )
            delayBlocking((Math.random() * delay * 100).toLong())
        }
    }
}
