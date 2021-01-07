/*
 * Copyright (c) 2018-2021 AnimatedLEDStrip
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package animatedledstrip.animations.predefined

import animatedledstrip.animations.*
import animatedledstrip.leds.colormanagement.setPixelProlongedColor
import animatedledstrip.leds.colormanagement.setStripProlongedColor
import kotlinx.coroutines.delay

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
        dimensionality = Dimensionality.oneDimensional,
        directional = false,
        intParams = listOf(AnimationParameter("delay", "Delay used during animation", 25)),
    )
) { leds, params, _ ->
    val delay = params.intParams.getValue("delay").toLong()

    val sortablePixels = params.colors[0].toSortableList()
    val startColor = sortablePixels.toPreparedColorContainer()

    leds.apply {
        setStripProlongedColor(startColor)

        fun updateColorAtLocation(location: Int) {
            setPixelProlongedColor(location, sortablePixels[location].color)
        }

        @Suppress("DuplicatedCode")
        suspend fun sort(startIndex: Int, endIndex: Int) {
            if (startIndex == endIndex) return

            val midpoint = startIndex + ((endIndex - startIndex) / 2)
            sort(startIndex, midpoint)
            sort(midpoint + 1, endIndex)

            var p1 = startIndex
            var p2 = midpoint + 1
            for (x in 0 until endIndex - startIndex) {
                when {
                    sortablePixels[p1].finalLocation < sortablePixels[p2].finalLocation -> {
                        p1++
                    }
                    sortablePixels[p2].finalLocation < sortablePixels[p1].finalLocation -> {
                        val temp = sortablePixels[p2]
                        for (i in p2 downTo p1 + 1) {
                            sortablePixels[i] = sortablePixels[i - 1]
                            updateColorAtLocation(i)
                        }
                        sortablePixels[p1] = temp
                        updateColorAtLocation(p1)
                        p1++
                        if (p2 < endIndex) p2++
                        delay(delay)
                    }
                }
            }
        }
        sort(0, sortablePixels.lastIndex)
    }
}
