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
import animatedledstrip.colors.PreparedColorContainer
import animatedledstrip.colors.shuffledWithIndices
import animatedledstrip.leds.colormanagement.setPixelProlongedColor
import animatedledstrip.leds.colormanagement.setStripProlongedColor
import kotlinx.coroutines.delay

@Suppress("DuplicatedCode")
val mergeSortSequential = PredefinedAnimation(
    Animation.AnimationInfo(
        name = "Merge Sort (Sequential)",
        abbr = "MSS",
        description = "Visualization of merge sort.\n" +
                      "`pCols[0]` is randomized, then merge sort is used to re-sort it.",
        signatureFile = "merge_sort_sequential.png",
        runCountDefault = 1,
        minimumColors = 1,
        unlimitedColors = false,
        center = ParamUsage.NOTUSED,
        delay = ParamUsage.USED,
        delayDefault = 25,
        direction = ParamUsage.NOTUSED,
        distance = ParamUsage.NOTUSED,
        spacing = ParamUsage.NOTUSED,
    )
) { leds, params, _ ->

    data class SortablePixel(val finalLocation: Int, val currentLocation: Int, val color: Int)

    val colorMap = params.colors[0].shuffledWithIndices()
        .mapIndexed { index, it -> SortablePixel(it.first, index, it.second) }.toMutableList()
    val color = PreparedColorContainer(colorMap.map { it.color })

    leds.apply {
        setStripProlongedColor(color)

        fun updateColorAtLocation(location: Int) {
            setPixelProlongedColor(location, colorMap[location].color)
        }

        suspend fun sort(startIndex: Int, endIndex: Int) {
            if (startIndex == endIndex) return

            val midpoint = startIndex + ((endIndex - startIndex) / 2)
            sort(startIndex, midpoint)
            sort(midpoint + 1, endIndex)

            var p1 = startIndex
            var p2 = midpoint + 1
            for (x in 0 until endIndex - startIndex) {
                when {
                    colorMap[p1].finalLocation < colorMap[p2].finalLocation -> {
                        p1++
                    }
                    colorMap[p2].finalLocation < colorMap[p1].finalLocation -> {
                        val temp = colorMap[p2]
                        for (i in p2 downTo p1 + 1) {
                            colorMap[i] = colorMap[i - 1]
                            updateColorAtLocation(i)
                        }
                        colorMap[p1] = temp
                        updateColorAtLocation(p1)
                        p1++
                        if (p2 < endIndex) p2++
                        delay(params.delay)
                    }
                }
            }
        }
        sort(0, colorMap.lastIndex)
    }
}
