package animatedledstrip.leds

/*
 *  Copyright (c) 2019 AnimatedLEDStrip
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


import animatedledstrip.animationutils.AnimationData
import animatedledstrip.animationutils.color
import animatedledstrip.colors.ColorContainerInterface
import animatedledstrip.colors.ccpresets.CCBlack
import animatedledstrip.utils.delayBlocking
import animatedledstrip.utils.infoOrNull
import kotlinx.coroutines.*
import java.lang.Math.random

/**
 * Iterate over the indices from startPixel to endPixel (inclusive)
 *
 * @param animation The animation data to use to determine startPixel
 * and endPixel
 * @param operation The operation to perform
 */
inline fun iterateOverPixels(
    animation: AnimationData,
    operation: (Int) -> Unit
) {
    for (q in animation.startPixel..animation.endPixel) operation.invoke(q)
}

/**
 * Iterate over the indices from endPixel down to startPixel (inclusive)
 *
 * @param animation The animation data to use to determine startPixel
 * and endPixel
 * @param operation The operation to perform
 */

inline fun iterateOverPixelsReverse(
    animation: AnimationData,
    operation: (Int) -> Unit
) {
    for (q in animation.endPixel downTo animation.startPixel) operation.invoke(q)
}

/**
 * Iterate over a range of indices
 *
 * @param indices The indices to iterate over
 * @param operation The operation to perform
 */
inline fun iterateOver(
    indices: IntProgression,
    operation: (Int) -> Unit
) {
    for (q in indices) operation.invoke(q)
}

/**
 * Iterate over indices given in a list
 *
 * @param indices The indices to iterate over
 * @param operation The operation to perform
 */
inline fun iterateOver(
    indices: List<Int>,
    operation: (Int) -> Unit
) {
    for (q in indices) operation.invoke(q)
}

/**
 * Helper extension method that sets a pixel, waits a specified time in
 * milliseconds, then reverts the pixel.
 */
fun AnimatedLEDStrip.setPixelAndRevertAfterDelay(pixel: Int, color: ColorContainerInterface, delay: Long) {
    withPixelLock(pixel) {
        setPixelColor(pixel, color)
        delayBlocking(delay)
        revertPixel(pixel)
    }
}

/**
 * Start multiple animations in parallel and wait for all to complete
 * before returning
 *
 * @param animations The animations to run
 * @param pool The pool of threads to start the animations in
 */
fun AnimatedLEDStrip.runParallelAndJoin(
    scope: CoroutineScope,
    vararg animations: AnimationData,
    pool: ExecutorCoroutineDispatcher = parallelAnimationThreadPool
) {
    val jobs = mutableListOf<Job>()
    animations.forEach {
        val job = runParallel(it, scope, pool)
        if (job != null) jobs += job
    }
    runBlocking {
        jobs.joinAll()
    }
}

fun randomPixelIn(start: Int, end: Int): Int = ((end - start) * random() + start).toInt()

fun randomPixelIn(animation: AnimationData): Int =
    ((animation.endPixel - animation.startPixel) * random() + animation.startPixel).toInt()

fun AnimationData.prepare(ledStrip: AnimatedLEDStrip): AnimationData {
    endPixel = when (endPixel) {
        -1 -> ledStrip.numLEDs - 1
        else -> endPixel
    }

    center = when (center) {
        -1 -> ledStrip.numLEDs / 2
        else -> center
    }

    distance = when (distance) {
        -1 -> ledStrip.numLEDs
        else -> distance
    }

    if (colors.isEmpty()) color(CCBlack)

    pCols = mutableListOf()
    colors.forEach {
        pCols.add(
            it.prepare(
                endPixel - startPixel + 1,
                leadingZeros = startPixel
            )
        )
    }

    for (i in colors.size until (animation.infoOrNull()?.numColors ?: 0)) {
        pCols.add(
            CCBlack.prepare(
                endPixel - startPixel + 1,
                leadingZeros = startPixel
            )
        )
    }

    return this
}

suspend fun AnimatedLEDStrip.RunningAnimation.join() = job.join()
fun AnimatedLEDStrip.RunningAnimation.endAnimation() = cancel("End of Animation")