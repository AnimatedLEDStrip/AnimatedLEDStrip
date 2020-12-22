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
import animatedledstrip.animationutils.ParamUsage
import animatedledstrip.animationutils.PredefinedAnimation
import animatedledstrip.colors.PreparedColorContainer
import animatedledstrip.colors.shuffledWithIndices
import animatedledstrip.utils.delayBlocking

val bubbleSort = PredefinedAnimation(
    Animation.AnimationInfo(
        name = "Bubble Sort",
        abbr = "BST",
        description = "Visualization of bubble sort.\n" +
                      "`pCols[0]` is randomized, then bubble sort is used to resort it.",
        signatureFile = "bubble_sort.png",
        runCountDefault = 1,
        minimumColors = 1,
        unlimitedColors = false,
        center = ParamUsage.NOTUSED,
        delay = ParamUsage.USED,
        delayDefault = 5,
        direction = ParamUsage.NOTUSED,
        distance = ParamUsage.NOTUSED,
        spacing = ParamUsage.NOTUSED,
    )
) { leds, params, _ ->
    val colorMap = params.colors[0].shuffledWithIndices().toMutableList()
    val color = PreparedColorContainer(colorMap.map { it.second })
    val delay = params.delay

    leds.apply {
        setProlongedStripColor(color)

        var keepSearching = true

        while (keepSearching) {
            keepSearching = false
            for (pixel in 0 until numLEDs - 1) {
                if (colorMap[pixel].first > colorMap[pixel + 1].first) {
                    keepSearching = true
                    val temp = colorMap[pixel]
                    colorMap[pixel] = colorMap[pixel + 1]
                    setProlongedPixelColor(pixel, colorMap[pixel].second)
                    colorMap[pixel + 1] = temp
                    setProlongedPixelColor(pixel + 1, colorMap[pixel + 1].second)
                }
                delayBlocking(delay)
            }
        }
    }
}
