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
import animatedledstrip.colors.PreparedColorContainer
import animatedledstrip.colors.offsetBy
import animatedledstrip.utils.delayBlocking
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/* Set pixel colors */

/**
 * Set the temporary color of the specified pixels
 *
 * @param pixels A list of pixel indices to set
 */
fun AnimatedLEDStrip.Section.setTemporaryPixelColors(pixels: List<Int>, color: ColorContainerInterface) {
    for (pixel in pixels) {
        setTemporaryPixelColor(pixel, color)
    }
}

/**
 * Set the temporary color of the specified pixels
 *
 * @param pixels A list of pixel indices to set
 */
fun AnimatedLEDStrip.Section.setTemporaryPixelColors(pixels: List<Int>, color: Long) {
    for (pixel in pixels) {
        setTemporaryPixelColor(pixel, color)
    }
}

/**
 * Set the temporary color of the specified range of pixels
 * (alias for setTemporarySectionColor)
 */
fun AnimatedLEDStrip.Section.setTemporaryPixelColors(pixels: IntRange, color: ColorContainerInterface) =
    setTemporaryPixelColors(pixels.toList(), color)

/**
 * Set the temporary color of the specified range of pixels
 * (alias for setTemporarySectionColor)
 */
fun AnimatedLEDStrip.Section.setTemporaryPixelColors(pixels: IntRange, color: Long) =
    setTemporaryPixelColors(pixels.toList(), color)

/**
 * Set the prolonged color of the specified pixels
 *
 * @param pixels A list of pixel indices to set
 */
fun AnimatedLEDStrip.Section.setProlongedPixelColors(pixels: List<Int>, color: ColorContainerInterface) {
    for (pixel in pixels) {
        setProlongedPixelColor(pixel, color)
    }
}

/**
 * Set the prolonged color of the specified pixels
 *
 * @param pixels A list of pixel indices to set
 */
fun AnimatedLEDStrip.Section.setProlongedPixelColors(pixels: List<Int>, color: Long) {
    for (pixel in pixels) {
        setProlongedPixelColor(pixel, color)
    }
}

/**
 * Set the prolonged color of the specified range of pixels
 * (alias for setProlongedSectionColor)
 */
fun AnimatedLEDStrip.Section.setProlongedPixelColors(pixels: IntRange, color: ColorContainerInterface) =
    setProlongedPixelColors(pixels.toList(), color)

/**
 * Set the prolonged color of the specified range of pixels
 * (alias for setProlongedSectionColor)
 */
fun AnimatedLEDStrip.Section.setProlongedPixelColors(pixels: IntRange, color: Long) =
    setProlongedPixelColors(pixels.toList(), color)


/* Set and fade pixel */

/**
 * Set a pixel to a color and then immediately fade it back to its
 * prolonged color. This will return before the fade is complete
 * because the fade is started in a separate coroutine.
 *
 * @param amountOfOverlay Amount of overlay in the fade (see [animatedledstrip.utils.blend])
 * @param delay Amount of delay in the fade (see [animatedledstrip.utils.blend])
 * @param context The thread pool to create the fading thread in
 */
fun AnimatedLEDStrip.Section.setAndFadePixel(
    pixel: Int,
    color: ColorContainerInterface,
    amountOfOverlay: Int = 25,
    delay: Int = 30,
    context: CoroutineContext = EmptyCoroutineContext,
) {
    setTemporaryPixelColor(pixel, color)
    GlobalScope.launch(context) {
        fadePixel(pixel, amountOfOverlay, delay)
    }
}


/* Revert pixels */

/**
 * Revert pixels based on indices in a list
 */
fun AnimatedLEDStrip.Section.revertPixels(pixels: List<Int>) {
    for (pixel in pixels) {
        revertPixel(pixel)
    }
}

/**
 * Revert pixels based on indices in a range
 */
fun AnimatedLEDStrip.Section.revertPixels(pixels: IntRange) {
    for (pixel in pixels) {
        revertPixel(pixel)
    }
}


/* Set strip color */

/**
 * Set the temporary color of a strip using a ColorContainer offset by `offset`
 * `offset` is the index where the color at index `0` will be located.
 */
fun AnimatedLEDStrip.Section.setTemporaryStripColorWithOffset(colors: PreparedColorContainer, offset: Int) {
    setTemporaryStripColor(colors.offsetBy(offset))
}

/**
 * Set the prolonged color of a strip using a ColorContainer offset by `offset`.
 * `offset` is the index where the color at index `0` will be located.
 */
fun AnimatedLEDStrip.Section.setProlongedStripColorWithOffset(colors: PreparedColorContainer, offset: Int) {
    setProlongedStripColor(colors.offsetBy(offset))
}


/* Get pixel color */

/**
 * Get the temporary color of a pixel or null if the index is invalid.
 */
fun AnimatedLEDStrip.Section.getTemporaryPixelColorOrNull(pixel: Int): Long? = try {
    getTemporaryPixelColor(pixel)
} catch (e: IllegalArgumentException) {
    null
}

/**
 * Get the prolonged color of a pixel or null if the index is invalid.
 */
fun AnimatedLEDStrip.Section.getProlongedPixelColorOrNull(pixel: Int): Long? = try {
    getProlongedPixelColor(pixel)
} catch (e: IllegalArgumentException) {
    null
}


/* Iterate over indices and perform operation */

/**
 * Iterate over the indices from `startPixel` to `endPixel` (inclusive)
 */
inline fun AnimatedLEDStrip.Section.iterateOverPixels(
    operation: (Int) -> Unit,
) {
    for (q in indices) operation.invoke(q)
}

/**
 * Iterate over the indices from `endPixel` down to `startPixel` (inclusive)
 */

inline fun AnimatedLEDStrip.Section.iterateOverPixelsReverse(
    operation: (Int) -> Unit,
) {
    for (q in indices.reversed()) operation.invoke(q)
}

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
    pool: ExecutorCoroutineDispatcher = parallelAnimationThreadPool,
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
    block: suspend CoroutineScope.() -> T,
): T {
    return runBlocking(context) {
        withContext(NonCancellable) {
            block.invoke(this@runBlocking)
        }
    }
}


/* RunningAnimation extensions */

/**
 * Join the `RunningAnimation`'s job
 */
suspend fun AnimatedLEDStrip.RunningAnimation.join() = job.join()

/**
 * End the animation
 */
fun AnimatedLEDStrip.RunningAnimation.endAnimation() = cancel("End of Animation")


/* Run with pixel lock */

fun AnimatedLEDStrip.Section.withPixelLock(pixel: Int, operation: () -> Any?) =
    ledStrip.withPixelLock(pixel, operation)
