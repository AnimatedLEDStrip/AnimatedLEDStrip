/*
 *  Copyright (c) 2018-2020 AnimatedLEDStrip
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

package animatedledstrip.leds

import animatedledstrip.animationutils.AnimationData
import animatedledstrip.colors.ColorContainerInterface
import animatedledstrip.utils.delayBlocking
import kotlinx.coroutines.*
import java.lang.Math.random
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.math.roundToInt

/* Iterate over indices and perform operation */

/**
 * Iterate over the indices from `startPixel` to `endPixel` (inclusive)
 */
inline fun AnimatedLEDStrip.Section.iterateOverPixels(
    operation: (Int) -> Unit
) {
    for (q in indices) operation.invoke(q)
}

/**
 * Iterate over the indices from `endPixel` down to `startPixel` (inclusive)
 */

inline fun AnimatedLEDStrip.Section.iterateOverPixelsReverse(
    operation: (Int) -> Unit
) {
    for (q in indices.reversed()) operation.invoke(q)
}

/**
 * Iterate over a range of indices
 */
inline fun iterateOver(
    indices: IntProgression,
    operation: (Int) -> Unit
) {
    for (q in indices) operation.invoke(q)
}

/**
 * Iterate over indices given in a list
 */
inline fun iterateOver(
    indices: List<Int>,
    operation: (Int) -> Unit
) {
    for (q in indices) operation.invoke(q)
}


/* Set and revert pixel */

/**
 * Set a pixel, wait the specified time in milliseconds, then revert the pixel
 */
fun AnimatedLEDStrip.Section.setPixelAndRevertAfterDelay(pixel: Int, color: ColorContainerInterface, delay: Long) {
    withPixelLock(pixel) {
        setTemporaryPixelColor(pixel, color)
        delayBlocking(delay)
        revertPixel(pixel)
    }
}


/* Run parallel animations */

/**
 * Start multiple animations in parallel and wait for all to complete
 * before returning
 *
 * @param pool The pool of threads to start the animations in
 */
fun AnimatedLEDStrip.Section.runParallelAndJoin(
    scope: CoroutineScope,
    vararg animations: Pair<AnimationData, AnimatedLEDStrip.Section>,
    pool: ExecutorCoroutineDispatcher = parallelAnimationThreadPool
) {
    val jobs = mutableListOf<Job>()
    animations.forEach {
        val job = runParallel(it.first, scope, it.second, pool)
        if (job != null) jobs += job
    }
    runBlocking {
        jobs.joinAll()
    }
}

fun <T> runBlockingNonCancellable(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> T
): T {
    return runBlocking(context) {
        withContext(NonCancellable) {
            block.invoke(this@runBlocking)
        }
    }
}


/* Random pixel index generators */

/**
 * Return a random index between `start` and `end` (inclusive)
 */
fun randomPixelIn(start: Int, end: Int): Int = ((end - start) * random() + start).roundToInt()

/**
 * Return a random index from indices
 */
fun randomPixelIn(indices: List<Int>): Int = indices.random()


/* RunningAnimation extensions */

/**
 * Join the `RunningAnimation`'s job
 */
suspend fun AnimatedLEDStrip.RunningAnimation.join() = job.join()

/**
 * End the animation
 */
fun AnimatedLEDStrip.RunningAnimation.endAnimation() = cancel("End of Animation")
