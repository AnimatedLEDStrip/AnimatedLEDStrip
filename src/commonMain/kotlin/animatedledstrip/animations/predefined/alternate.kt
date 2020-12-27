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

package animatedledstrip.animations.predefined

import animatedledstrip.animations.Animation
import animatedledstrip.animations.ParamUsage
import animatedledstrip.animations.PredefinedAnimation
import animatedledstrip.leds.colormanagement.setStripProlongedColor
import kotlinx.coroutines.delay

val alternate = PredefinedAnimation(
    Animation.AnimationInfo(
        name = "Alternate",
        abbr = "ALT",
        description = "Strip alternates* between each color in pCols, " +
                      "delaying delay milliseconds between changes.\n\n" +
                      "\\* alternate may not be the best word because this animation " +
                      "supports more than two animatedledstrip.colors",
        signatureFile = "alternate.png",
        runCountDefault = -1,
        minimumColors = 2,
        unlimitedColors = true,
        center = ParamUsage.NOTUSED,
        delay = ParamUsage.USED,
        delayDefault = 1000,
        direction = ParamUsage.NOTUSED,
        distance = ParamUsage.NOTUSED,
        spacing = ParamUsage.NOTUSED,
    )
) { leds, params, _ ->
    val nextColorIndex = params.extraData.getOrPut("nextColorIndex") { 0 } as Int
    val color = params.colors[nextColorIndex]
    val delay = params.delay

    leds.apply {
        setStripProlongedColor(color)
    }

    delay(delay)

    params.extraData["nextColorIndex"] = if (nextColorIndex == params.colors.lastIndex) 0 else nextColorIndex + 1
}
