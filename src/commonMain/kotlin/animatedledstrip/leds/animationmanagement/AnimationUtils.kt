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
val AnimationManager.validIndices: List<Int>
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
    for (q in validIndices) operation.invoke(q)
}

/**
 * Iterate over each pixel in reverse, performing [operation] on each index
 */
inline fun AnimationManager.iterateOverPixelsReverse(operation: (Int) -> Unit) {
    for (q in validIndices.reversed()) operation.invoke(q)
}

fun AnimationManager.randomIndex(): Int = validIndices.random()

/**
 * @return A shuffled list of all valid indices for the section that corresponds with this animation manager
 */
fun AnimationManager.shuffledIndices(): List<Int> = validIndices.shuffled()

/**
 * @return A random `Int`
 */
fun randomInt(): Int = Random.nextInt()

/**
 * @return A random `Double`
 */
fun randomDouble(): Double = Random.nextDouble()
