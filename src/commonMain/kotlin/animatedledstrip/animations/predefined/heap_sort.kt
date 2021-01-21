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
import animatedledstrip.colors.PreparedColorContainer
import animatedledstrip.colors.shuffledWithIndices
import animatedledstrip.leds.colormanagement.setStripProlongedColor
import kotlinx.coroutines.delay

val heapSort = DefinedAnimation(
    Animation.AnimationInfo(
        name = "Heap Sort",
        abbr = "HPS",
        description = "Visualization of heap sort.\n" +
                      "`colors[0]` is randomized, then a heap sort is " +
                      "used to re-sort it.",
        runCountDefault = 1,
        minimumColors = 1,
        unlimitedColors = false,
        dimensionality = Dimensionality.oneDimensional,
        intParams = listOf(AnimationParameter("interMovementDelay", "Delay between sorting movements", 25)),
    )
) { leds, params, _ ->
    val interMovementDelay = params.intParams.getValue("interMovementDelay").toLong()

    val sortablePixels =
        params.colors[0]
            .shuffledWithIndices()
            .mapIndexed { index, it -> SortablePixel(it.first, index, it.second) }
            .toMutableList()
    val startColor = PreparedColorContainer(sortablePixels.map { it.color })

    leds.apply {
        setStripProlongedColor(startColor)

        suspend fun siftDown(start: Int, end: Int) {
            var root = start

            while (2 * root + 1 <= end) {
                val child = 2 * root + 1
                var swap = root

                if (sortablePixels[swap].finalLocation < sortablePixels[child].finalLocation)
                    swap = child

                if (child + 1 <= end && sortablePixels[swap].finalLocation < sortablePixels[child + 1].finalLocation)
                    swap = child + 1

                if (swap == root)
                    return
                else {
                    val tmp = sortablePixels[root]

                    sortablePixels[root] = sortablePixels[swap]
                    updateColorAtIndex(root, sortablePixels)

                    sortablePixels[swap] = tmp
                    updateColorAtIndex(swap, sortablePixels)

                    root = swap

                    delay(interMovementDelay)
                }
            }
        }

        var start = (sortablePixels.lastIndex - 1) / 2

        while (start >= 0) {
            siftDown(start, sortablePixels.lastIndex)
            start--
        }

        var end = sortablePixels.lastIndex
        while (end > 0) {
            val tmp = sortablePixels[0]

            sortablePixels[0] = sortablePixels[end]
            updateColorAtIndex(0, sortablePixels)

            sortablePixels[end] = tmp
            updateColorAtIndex(end, sortablePixels)

            delay(interMovementDelay)

            end--

            siftDown(0, end)
        }
    }
}
