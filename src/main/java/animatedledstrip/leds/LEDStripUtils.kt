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

import animatedledstrip.colors.ColorContainerInterface
import animatedledstrip.colors.PreparedColorContainer
import animatedledstrip.colors.offsetBy
import animatedledstrip.utils.tryWithLock
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.pmw.tinylog.Logger
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
 * Set the temporary color of the specified range of pixels
 * (alias for setTemporarySectionColor)
 */
fun AnimatedLEDStrip.Section.setTemporaryPixelColors(pixels: IntRange, color: ColorContainerInterface) =
    setTemporaryPixelColors(pixels.toList(), color)

/**
 * Set the prolonged color of the specified range of pixels
 * (alias for setProlongedSectionColor)
 */
fun AnimatedLEDStrip.Section.setProlongedPixelColors(pixels: IntRange, color: ColorContainerInterface) =
    setProlongedPixelColors(pixels.toList(), color)

/**
 * Set the temporary color of the specified range of pixels
 * (alias for setTemporarySectionColor)
 */
fun AnimatedLEDStrip.Section.setTemporaryPixelColors(pixels: IntRange, color: Long) =
    setTemporaryPixelColors(pixels.toList(), color)

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
    context: CoroutineContext = EmptyCoroutineContext
) {
    setTemporaryPixelColor(pixel, color)
    GlobalScope.launch(context) {
        fadePixel(pixel, amountOfOverlay, delay)
    }
}


/* Revert pixels */

/**
 * Revert pixels based on indices in a list
 *
 * @param pixels The pixels to revert
 */
fun AnimatedLEDStrip.Section.revertPixels(pixels: List<Int>) {
    for (pixel in pixels) {
        revertPixel(pixel)
    }
}


/* Set strip color */

/**
 * Set the temporary color of a strip using a ColorContainer offset by `offset`
 *
 * @param offset The index of the pixel that will be set to the color at
 * index 0
 */
fun AnimatedLEDStrip.Section.setTemporaryStripColorWithOffset(colors: PreparedColorContainer, offset: Int = 0) {
    setTemporaryStripColor(colors.offsetBy(offset))
}

/**
 * Set the prolonged color of a strip using a ColorContainer offset by `offset`
 *
 * @param offset The index of the pixel that will be set to the color at
 * index 0
 */
fun AnimatedLEDStrip.Section.setProlongedStripColorWithOffset(colors: PreparedColorContainer, offset: Int = 0) {
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


/* Run with pixel lock */

/**
 * Try to lock a pixel's mutex before performing the specified operation
 */
fun LEDStrip.withPixelLock(pixel: Int, operation: () -> Any?) {
    pixelLocks[pixel]?.tryWithLock {
        operation.invoke()
    } ?: Logger.warn { "Could not find Mutex for pixel $pixel" }
}

fun AnimatedLEDStrip.Section.withPixelLock(pixel: Int, operation: () -> Any?) =
    ledStrip.withPixelLock(pixel, operation)
