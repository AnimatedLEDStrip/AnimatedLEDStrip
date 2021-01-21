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
import animatedledstrip.leds.animationmanagement.AnimationManager
import animatedledstrip.leds.colormanagement.setStripProlongedColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

suspend fun AnimationManager.mergeSort(
    sortablePixels: MutableList<SortablePixel>,
    scope: CoroutineScope,
    startIndex: Int,
    endIndex: Int,
    interMovementDelay: Long,
    parallel: Boolean,
): Job? {
    if (startIndex == endIndex) return null

    return scope.launch {
        val midpoint = startIndex + ((endIndex - startIndex) / 2)

        val op1 = mergeSort(sortablePixels, scope, startIndex, midpoint, interMovementDelay, parallel)
        if (!parallel) op1?.join()

        val op2 = mergeSort(sortablePixels, scope, midpoint + 1, endIndex, interMovementDelay, parallel)
        if (parallel) op1?.join()
        op2?.join()

        var p1 = startIndex
        var p2 = midpoint + 1
        for (x in 0 until endIndex - startIndex) {
            when {
                sortablePixels[p1].finalLocation < sortablePixels[p2].finalLocation -> {
                    p1++
                }
                sortablePixels[p2].finalLocation < sortablePixels[p1].finalLocation -> {
                    shiftPixel(p2, p1, sortablePixels)
                    p1++
                    if (p2 < endIndex) p2++
                    delay(interMovementDelay)
                }
            }
        }
    }
}

val mergeSortParallel = DefinedAnimation(
    Animation.AnimationInfo(
        name = "Merge Sort (Parallel)",
        abbr = "MSP",
        description = "Visualization of merge sort.\n" +
                      "`colors[0]` is randomized, then a parallelized merge sort is " +
                      "used to re-sort it.",
        runCountDefault = 1,
        minimumColors = 1,
        unlimitedColors = false,
        dimensionality = Dimensionality.oneDimensional,
        intParams = listOf(AnimationParameter("interMovementDelay", "Delay between sorting movements", 50)),
    )
) { leds, params, scope ->
    val interMovementDelay = params.intParams.getValue("interMovementDelay").toLong()

    val sortablePixels = params.colors[0].toSortableList()
    val startColor = sortablePixels.toPreparedColorContainer()

    leds.apply {
        setStripProlongedColor(startColor)

        mergeSort(sortablePixels, scope, 0, sortablePixels.lastIndex, interMovementDelay, true)?.join()
    }
}

val mergeSortSequential = DefinedAnimation(
    Animation.AnimationInfo(
        name = "Merge Sort (Sequential)",
        abbr = "MSS",
        description = "Visualization of merge sort.\n" +
                      "`pCols[0]` is randomized, then merge sort is used to re-sort it.",
        runCountDefault = 1,
        minimumColors = 1,
        unlimitedColors = false,
        dimensionality = Dimensionality.oneDimensional,
        intParams = listOf(AnimationParameter("interMovementDelay", "Delay between sorting movements", 25)),
    )
) { leds, params, scope ->
    val interMovementDelay = params.intParams.getValue("interMovementDelay").toLong()

    val sortablePixels = params.colors[0].toSortableList()
    val startColor = sortablePixels.toPreparedColorContainer()

    leds.apply {
        setStripProlongedColor(startColor)

        mergeSort(sortablePixels, scope, 0, sortablePixels.lastIndex, interMovementDelay, false)?.join()
    }
}
