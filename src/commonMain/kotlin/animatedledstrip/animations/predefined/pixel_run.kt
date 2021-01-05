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
import animatedledstrip.leds.animationmanagement.iterateOverPixels
import animatedledstrip.leds.animationmanagement.iterateOverPixelsReverse
import animatedledstrip.leds.colormanagement.setPixelAndRevertAfterDelay

val pixelRun = PredefinedAnimation(
    Animation.AnimationInfo(
        name = "Pixel Run",
        abbr = "PXR",
        description = "A pixel colored from `pCols[0]` runs along the strip.\n" +
                      "Similar to [Multi Pixel Run](Multi-Pixel-Run) but with only " +
                      "one pixel.",
        signatureFile = "pixel_run.png",
        runCountDefault = -1,
        minimumColors = 1,
        unlimitedColors = false,
        dimensionality = Dimensionality.oneDimensional,
        directional = true,
        intParams = listOf(AnimationParameter("delay", "Delay used during animation", 10)),
//        center = ParamUsage.NOTUSED,
//        delay = ParamUsage.USED,
//        delayDefault = 10,
//        direction = ParamUsage.USED,
//        distance = ParamUsage.NOTUSED,
//        spacing = ParamUsage.NOTUSED,
    )
) { leds, params, _ ->
    val color = params.colors[0]
    val delay = params.intParams.getValue("delay").toLong()
    val direction = params.direction

    leds.apply {
        when (direction) {
            Direction.FORWARD ->
                iterateOverPixels {
                    setPixelAndRevertAfterDelay(it, color, delay)
                }
            Direction.BACKWARD ->
                iterateOverPixelsReverse {
                    setPixelAndRevertAfterDelay(it, color, delay)
                }
        }
    }
}
