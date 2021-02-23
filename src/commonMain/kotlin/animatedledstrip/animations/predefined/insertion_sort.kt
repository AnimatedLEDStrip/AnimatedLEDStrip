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

val insertionSort = DefinedAnimation(
    Animation.AnimationInfo(
        name = "Insertion Sort",
        abbr = "INS",
        description = "Visualization of insertion sort.\n" +
                      "`colors[0]` is randomized, then an insertion sort is" +
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
        for (i in 1..sortablePixels.lastIndex) {
            inner@ for (j in i - 1 downTo 0) {
                if (sortablePixels[i].finalLocation < sortablePixels[j].finalLocation &&
                    (j == 0 || sortablePixels[i].finalLocation > sortablePixels[j - 1].finalLocation)
                ) {
                    shiftPixel(i, j, sortablePixels)
                    delay(interMovementDelay)
                    break@inner
                }
            }
        }
    }
}
