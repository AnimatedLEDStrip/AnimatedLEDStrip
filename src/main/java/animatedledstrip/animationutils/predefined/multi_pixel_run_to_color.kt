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

val multiPixelRunToColor = PredefinedAnimation(
    Animation.AnimationInfo(
        name = "Multi Pixel Run To Color",
        abbr = "MTC",
        repetitive = true,
        minimumColors = 1,
        center = ParamUsage.NOTUSED,
        delay = ParamUsage.USED,
        delayDefault = 150,
        direction = ParamUsage.USED,
        distance = ParamUsage.NOTUSED,
        spacing = ParamUsage.USED,
        spacingDefault = 3
    )
) { leds, data, _ ->
    val color0 = data.pCols[0]
    val delay = data.delay
    val direction = data.direction
    val spacing = data.spacing

    leds.apply {
        when (direction) {
            Direction.FORWARD ->
                iterateOver(spacing - 1 downTo 0) { s ->
                    iterateOver( 0 until numLEDs step spacing) { i ->
                        if (i + s < numLEDs)
                            setProlongedPixelColor(i + s, color0)
                    }
                    delayBlocking(delay)
                }
            Direction.BACKWARD ->
                iterateOver(0 until spacing) { s ->
                    iterateOver(0 until numLEDs step spacing) { i ->
                        if (i + s < numLEDs)
                            setProlongedPixelColor(i + s, color0)
                    }
                    delayBlocking(delay)
                }
        }
    }
}
