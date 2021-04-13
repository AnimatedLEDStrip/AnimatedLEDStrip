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

package animatedledstrip.leds.animationmanagement

import kotlin.random.Random

/**
 * The number of LEDs in the section that corresponds with this animation manager
 */
val AnimationManager.numLEDs: Int
    get() = sectionManager.numLEDs

/**
 * A list of all valid indices for the section that corresponds with this animation manager
 */
val AnimationManager.pixels: List<Int>
    get() = sectionManager.pixels

/**
 * Iterate over a range of indices
 */
inline fun iterateOver(
    indices: IntProgression,
    operation: (Int) -> Unit,
) {
    for (q in indices) operation.invoke(q)
}

/**
 * Iterate over indices given in a list
 */
inline fun iterateOver(
    indices: List<Int>,
    operation: (Int) -> Unit,
) {
    for (q in indices) operation.invoke(q)
}

/**
 * Iterate over each pixel, performing [operation] on each index
 */
inline fun AnimationManager.iterateOverPixels(operation: (Int) -> Unit) {
    for (q in pixels) operation.invoke(q)
}

/**
 * Iterate over each pixel in reverse, performing [operation] on each index
 */
inline fun AnimationManager.iterateOverPixelsReverse(operation: (Int) -> Unit) {
    for (q in pixels.reversed()) operation.invoke(q)
}

fun AnimationManager.randomIndex(): Int = pixels.random()

/**
 * @return A shuffled list of all valid indices for the section that corresponds with this animation manager
 */
fun AnimationManager.shuffledIndices(): List<Int> = pixels.shuffled()

/**
 * @return A random `Int`
 */
fun randomInt(): Int = Random.nextInt()

/**
 * @return A random `Double`
 */
fun randomDouble(): Double = Random.nextDouble()

/**
 * Contains lists of pixels so the animation knows what pixels to set and
 * revert during this iteration.
 *
 * @property allSetPixels All pixels that should be set
 * @property allRevertPixels All pixels that should be reverted
 * @property pairedSetRevertPixels A list of paired up pixels from the set and revert lists
 *   (used by animations like Runway lights so pixels are set and reverted at
 *   nearly the exact same time instead of setting all and then reverting all)
 * @property unpairedSetPixels A list with any remaining pixels that should be set that are
 *   not included in [pairedSetRevertPixels] (due to [allRevertPixels] being smaller than [allSetPixels])
 * @property unpairedRevertPixels A list with any remaining pixels that should be reverted that are
 *   not included in [pairedSetRevertPixels] (due to [allSetPixels] being smaller than [allRevertPixels])
 */
data class PixelsToModify(
    val allSetPixels: List<Int>,
    val allRevertPixels: List<Int>,
) {
    val pairedSetRevertPixels: List<Pair<Int, Int>> = allSetPixels.zip(allRevertPixels)
    val unpairedSetPixels: List<Int> = allSetPixels.drop(pairedSetRevertPixels.size)
    val unpairedRevertPixels: List<Int> = allRevertPixels.drop(pairedSetRevertPixels.size)
}

/**
 * A list of [PixelsToModify] for an animation.
 * A separate class was created so it can be saved in a map without worrying about
 * type erasure and so the [PixelsToModify] instances can be created easily.
 *
 * [setLists] and [revertLists] should have the same size.
 */
data class PixelModificationLists(
    private val setLists: List<List<Int>>,
    private val revertLists: List<List<Int>>,
) {
    init {
        require(setLists.size == revertLists.size - 1) { "${setLists.size} set lists and ${revertLists.size} revert lists found" }
    }

    val modLists: List<PixelsToModify> =
        setLists
            .zip(revertLists)
            .map { lists -> PixelsToModify(lists.first, lists.second) }

    val lastRevertList: List<Int> = revertLists.last()
}
