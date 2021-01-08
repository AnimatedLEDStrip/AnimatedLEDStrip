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
import kotlin.random.Random

val quickSortSequential = PredefinedAnimation(
    Animation.AnimationInfo(
        name = "Quick Sort (Sequential)",
        abbr = "QKS",
        description = "Visualization of quick sort.\n" +
                      "`pCols[0]` is randomized, then a quick sort is " +
                      "used to re-sort it. Pivot locations are chosen randomly.",
        signatureFile = "quick_sort_sequential.png",
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

    @Suppress("DuplicatedCode")
    leds.apply {
        setStripProlongedColor(startColor)

        fun updateColorAtLocation(location: Int) {
            setPixelProlongedColor(location, sortablePixels[location].color)
        }

        suspend fun partition(startIndex: Int, endIndex: Int): Int {
            var pivotLocation = Random.nextInt(startIndex, endIndex)
            var i = startIndex
            var j = pivotLocation + 1

            while (i < pivotLocation) {
                if (sortablePixels[i].finalLocation > sortablePixels[pivotLocation].finalLocation) {
                    val tmp = sortablePixels[i]
                    for (p in i until pivotLocation) {
                        sortablePixels[p] = sortablePixels[p + 1]
                        updateColorAtLocation(p)
                    }
                    sortablePixels[pivotLocation] = tmp
                    updateColorAtLocation(pivotLocation)
                    pivotLocation--
                    delay(delay)
                } else i++
            }

            while (j <= endIndex) {
                if (sortablePixels[j].finalLocation < sortablePixels[pivotLocation].finalLocation) {
                    val tmp = sortablePixels[j]
                    for (p in j downTo pivotLocation + 1) {
                        sortablePixels[p] = sortablePixels[p - 1]
                        updateColorAtLocation(p)
                    }
                    sortablePixels[pivotLocation] = tmp
                    updateColorAtLocation(pivotLocation)
                    pivotLocation++
                    delay(delay)
                }
                j++
            }
            return pivotLocation
        }

        suspend fun sort(startIndex: Int, endIndex: Int) {
            if (startIndex >= endIndex) return

            val pivot = partition(startIndex, endIndex)
            sort(startIndex, pivot - 1)
            sort(pivot + 1, endIndex)
        }
        sort(0, sortablePixels.lastIndex)
    }
}
