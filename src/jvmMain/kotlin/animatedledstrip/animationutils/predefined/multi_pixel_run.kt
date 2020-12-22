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
import animatedledstrip.leds.iterateOver
import animatedledstrip.utils.delayBlocking

val multiPixelRun = PredefinedAnimation(
    Animation.AnimationInfo(
        name = "Multi Pixel Run",
        abbr = "MPR",
        description = "Similar to [Pixel Run](Pixel-Run) but with multiple LEDs " +
                      "at a specified spacing.",
        signatureFile = "multi_pixel_run.png",
        runCountDefault = -1,
        minimumColors = 1,
        unlimitedColors = false,
        center = ParamUsage.NOTUSED,
        delay = ParamUsage.USED,
        delayDefault = 100,
        direction = ParamUsage.USED,
        distance = ParamUsage.NOTUSED,
        spacing = ParamUsage.USED,
        spacingDefault = 3,
    )
) { leds, params, _ ->
    val color0 = params.colors[0]
    val delay = params.delay
    val direction = params.direction
    val spacing = params.spacing

    leds.apply {
        when (direction) {
            Direction.FORWARD ->
                iterateOver(0 until spacing) { s ->
                    val s2 = s - 1 % spacing
                    iterateOver(0 until numLEDs step spacing) { i ->
                        if (i + s2 < numLEDs) revertPixel(i + s2)
                        if (i + s < numLEDs)
                            setTemporaryPixelColor(i + s, color0)
                    }
                    delayBlocking(delay)
                }
            Direction.BACKWARD ->
                iterateOver(spacing - 1 downTo 0) { s ->
                    val s2 = if (s + 1 == spacing) 0 else s + 1
                    iterateOver(0 until numLEDs step spacing) { i ->
                        if (i + s2 < numLEDs) revertPixel(i + s2)
                        if (i + s < numLEDs)
                            setTemporaryPixelColor(i + s, color0)
                    }
                    delayBlocking(delay)
                }
        }
    }
}
