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
import animatedledstrip.utils.delayBlocking
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Suppress("DuplicatedCode")
val mergeSortParallel = PredefinedAnimation(
    Animation.AnimationInfo(
        name = "Merge Sort (Parallel)",
        abbr = "MSP",
        description = "Visualization of merge sort.\n" +
                "`pCols[0]` is randomized, then a parallelized merge sort is " +
                "used to re-sort it.",
        signatureFile = "merge_sort_parallel.png",
        repetitive = false,
        minimumColors = 1,
        unlimitedColors = false,
        center = ParamUsage.NOTUSED,
        delay = ParamUsage.USED,
        delayDefault = 50,
        direction = ParamUsage.NOTUSED,
        distance = ParamUsage.NOTUSED,
        spacing = ParamUsage.NOTUSED
    )
) { leds, data, scope ->

    data class SortablePixel(val finalLocation: Int, val currentLocation: Int, val color: Long)

    val colorMap = data.pCols[0].colors.mapIndexed { index, it -> Pair(index, it) }.shuffled()
        .mapIndexed { index, it -> SortablePixel(it.first, index, it.second) }.toMutableList()
    val color = PreparedColorContainer(colorMap.map { it.color })

    leds.apply {
        setProlongedStripColor(color)

        fun updateColorAtLocation(location: Int) {
            setProlongedPixelColor(location, colorMap[location].color)
        }

        runBlocking {
            fun sort(startIndex: Int, endIndex: Int): Job? {
                if (startIndex == endIndex) return null

                return scope.launch {
                    val midpoint = startIndex + ((endIndex - startIndex) / 2)
                    val op1 = sort(startIndex, midpoint)
                    val op2 = sort(midpoint + 1, endIndex)

                    op1?.join()
                    op2?.join()

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
                                delayBlocking(data.delay)
                            }
                        }
                    }
                }
            }
            sort(0, colorMap.lastIndex)
        }
    }
}
