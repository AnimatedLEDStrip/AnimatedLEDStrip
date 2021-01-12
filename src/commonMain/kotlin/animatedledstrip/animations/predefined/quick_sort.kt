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
import kotlin.random.Random

suspend fun AnimationManager.partition(
    sortablePixels: MutableList<SortablePixel>,
    startIndex: Int,
    endIndex: Int,
    interMovementDelay: Long,
): Int {
    var pivotLocation = Random.nextInt(startIndex, endIndex)
    var i = startIndex
    var j = pivotLocation + 1

    while (i < pivotLocation) {
        if (sortablePixels[i].finalLocation > sortablePixels[pivotLocation].finalLocation) {
            shiftPixel(i, pivotLocation, sortablePixels)
            pivotLocation--
            delay(interMovementDelay)
        } else i++
    }

    while (j <= endIndex) {
        if (sortablePixels[j].finalLocation < sortablePixels[pivotLocation].finalLocation) {
            shiftPixel(j, pivotLocation, sortablePixels)
            pivotLocation++
            delay(interMovementDelay)
        }
        j++
    }
    return pivotLocation
}

suspend fun AnimationManager.quickSort(
    sortablePixels: MutableList<SortablePixel>,
    scope: CoroutineScope,
    startIndex: Int,
    endIndex: Int,
    interMovementDelay: Long,
    parallel: Boolean,
): Job? {
    if (startIndex >= endIndex) return null

    return scope.launch {
        val pivot = partition(sortablePixels, startIndex, endIndex, interMovementDelay)

        val op1 = quickSort(sortablePixels, scope, startIndex, pivot - 1, interMovementDelay, parallel)
        if (!parallel) op1?.join()

        val op2 = quickSort(sortablePixels, scope, pivot + 1, endIndex, interMovementDelay, parallel)
        if (parallel) op1?.join()
        op2?.join()
    }
}

val quickSortParallel = DefinedAnimation(
    Animation.AnimationInfo(
        name = "Quick Sort (Parallel)",
        abbr = "QKP",
        description = "Visualization of quick sort.\n" +
                      "`colors[0]` is randomized, then a parallelized quick sort is " +
                      "used to re-sort it. Pivot locations are chosen randomly.",
        runCountDefault = 1,
        minimumColors = 1,
        unlimitedColors = false,
        dimensionality = Dimensionality.oneDimensional,
        directional = false,
        intParams = listOf(AnimationParameter("interMovementDelay", "Delay between sorting movements", 50)),
    )
) { leds, params, scope ->
    val interMovementDelay = params.intParams.getValue("interMovementDelay").toLong()

    val sortablePixels = params.colors[0].toSortableList()
    val color = sortablePixels.toPreparedColorContainer()

    leds.apply {
        setStripProlongedColor(color)

        quickSort(sortablePixels, scope, 0, sortablePixels.lastIndex, interMovementDelay, true)?.join()
    }
}

val quickSortSequential = DefinedAnimation(
    Animation.AnimationInfo(
        name = "Quick Sort (Sequential)",
        abbr = "QKS",
        description = "Visualization of quick sort.\n" +
                      "`colors[0]` is randomized, then a quick sort is " +
                      "used to re-sort it. Pivot locations are chosen randomly.",
        runCountDefault = 1,
        minimumColors = 1,
        unlimitedColors = false,
        dimensionality = Dimensionality.oneDimensional,
        directional = false,
        intParams = listOf(AnimationParameter("interMovementDelay", "Delay between sorting movements", 25)),
    )
) { leds, params, scope ->
    val interMovementDelay = params.intParams.getValue("interMovementDelay").toLong()

    val sortablePixels = params.colors[0].toSortableList()
    val color = sortablePixels.toPreparedColorContainer()

    leds.apply {
        setStripProlongedColor(color)

        quickSort(sortablePixels, scope, 0, sortablePixels.lastIndex, interMovementDelay, false)?.join()
    }
}
