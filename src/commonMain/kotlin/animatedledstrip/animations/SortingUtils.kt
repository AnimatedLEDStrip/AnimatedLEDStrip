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

package animatedledstrip.animations

import animatedledstrip.colors.PreparedColorContainer
import animatedledstrip.colors.shuffledWithIndices
import animatedledstrip.leds.animationmanagement.AnimationManager
import animatedledstrip.leds.colormanagement.setPixelProlongedColor

/**
 * Tracks the final location, current location, and color of a 'pixel' being sorted
 */
data class SortablePixel(val finalLocation: Int, val currentLocation: Int, val color: Int)

/**
 * Convert a [PreparedColorContainer] into a shuffled list of [SortablePixel]s
 */
fun PreparedColorContainer.toSortableList(): MutableList<SortablePixel> =
    shuffledWithIndices()
        .mapIndexed { index, it -> SortablePixel(it.first, index, it.second) }
        .toMutableList()

/**
 * Convert a list of [SortablePixel]s to a [PreparedColorContainer] that can be
 * used to set the strip color before the sorting begins
 */
fun List<SortablePixel>.toPreparedColorContainer(): PreparedColorContainer =
    PreparedColorContainer(this.map { it.color })

/**
 * Sets the color of the pixel at [index] with the appropriate color in [sortablePixels]
 */
fun AnimationManager.updateColorAtIndex(index: Int, sortablePixels: List<SortablePixel>) {
    setPixelProlongedColor(index, sortablePixels[index].color)
}

/**
 * Shift a pixel in the [sortablePixels] list from the [from] index to the [to] index,
 * showing the movement on the strip
 */
fun AnimationManager.shiftPixel(from: Int, to: Int, sortablePixels: MutableList<SortablePixel>) {
    when {
        from < to -> {
            val tmp = sortablePixels[from]
            for (p in from until to) {
                sortablePixels[p] = sortablePixels[p + 1]
                updateColorAtIndex(p, sortablePixels)
            }
            sortablePixels[to] = tmp
            updateColorAtIndex(to, sortablePixels)
        }
        from > to -> {
            val tmp = sortablePixels[from]
            for (p in from downTo to + 1) {
                sortablePixels[p] = sortablePixels[p - 1]
                updateColorAtIndex(p, sortablePixels)
            }
            sortablePixels[to] = tmp
            updateColorAtIndex(to, sortablePixels)
        }
    }
}
