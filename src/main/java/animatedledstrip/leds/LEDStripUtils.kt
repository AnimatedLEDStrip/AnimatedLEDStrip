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

/**
 * Set the colors of pixels
 *
 * @param pixels The pixels to set
 * @param color The color to use
 */
operator fun LEDStrip.set(vararg pixels: Int, color: ColorContainerInterface) {
    for (pixel in pixels) {
        setPixelColor(pixel, color, prolonged = false)
    }
}

/**
 * Set the colors of pixels
 *
 * @param pixels The range of pixels to set
 * @param color The color to use
 */
operator fun LEDStrip.set(pixels: IntRange, color: ColorContainerInterface) {
    for (pixel in pixels) {
        setPixelColor(pixel, color, prolonged = false)
    }
}

/**
 * Set the colors of pixels
 *
 * @param pixels The pixels to set
 * @param color The color to use
 */
operator fun LEDStrip.set(vararg pixels: Int, color: Long) {
    for (pixel in pixels) {
        setPixelColor(pixel, color, prolonged = false)
    }
}

/**
 * Set the colors of pixels
 *
 * @param pixels The range of pixels to set
 * @param color The color to use
 */
operator fun LEDStrip.set(pixels: IntRange, color: Long) {
    for (pixel in pixels) {
        setPixelColor(pixel, color, prolonged = false)
    }
}

/**
 * Set the color of a strip using a ColorContainer offset by `offset`
 *
 * @param colors The colors
 * @param offset The index of the pixel that will be set to the color at
 * index 0
 */
fun LEDStrip.setStripColorWithOffset(colors: PreparedColorContainer, offset: Int = 0, prolonged: Boolean = false) {
    setStripColor(colors.offsetBy(offset), prolonged)
}

/**
 * Get the color of a pixel as a hexadecimal string.
 *
 * @param pixel The pixel to find the color of
 * @return A `String` containing the color of the pixel in hexadecimal
 */
fun LEDStrip.getPixelHexString(pixel: Int, prolonged: Boolean = false): String {
    return getPixelColor(pixel, prolonged).toString(16)
}

/**
 * Get the color of a pixel.
 *
 * @param pixel The pixel to find the color of
 * @return The color of the pixel or null if the index is invalid
 */
fun LEDStrip.getPixelColorOrNull(pixel: Int, prolonged: Boolean = false): Long? = try {
    getPixelColor(pixel, prolonged)
} catch (e: IllegalArgumentException) {
    null
}

/**
 * Try to lock a pixel's lock before performing the operation
 *
 * @param pixel
 * @param operation
 */
fun LEDStrip.withPixelLock(pixel: Int, operation: () -> Any?) {
    pixelLocks[pixel]?.tryWithLock {
        operation.invoke()
    } ?: Logger.warn { "Could not find Mutex for pixel $pixel" }
}

/**
 * Revert pixels based on indices in a list
 *
 * @param pixels The pixels to revert
 */
fun LEDStrip.revertPixels(pixels: List<Int>) {
    for (pixel in pixels) {
        revertPixel(pixel)
    }
}

/**
 * Set a pixel to a color and then immediately fade it back to its
 * prolonged color. This will return before the fade is complete
 * because the fade is started in a separate coroutine.
 *
 * @param pixel The pixel to set and fade
 * @param color The color to set the pixel to
 * @param amountOfOverlay Amount of overlay in the fade
 * @param delay Amount of delay in the fade
 * @param context The thread pool to create the fading thread in
 */
fun LEDStrip.setAndFadePixel(
    pixel: Int,
    color: ColorContainerInterface,
    amountOfOverlay: Int = 25,
    delay: Int = 30,
    prolonged: Boolean = false,
    context: CoroutineContext = EmptyCoroutineContext
) {
    setPixelColor(pixel, color, prolonged)
    GlobalScope.launch(context) {
        fadePixel(pixel, amountOfOverlay, delay)
    }
}

/**
 * Set pixels based on indices in a list
 *
 * @param pixels The pixels to set
 * @param color The color to use
 */
fun LEDStrip.setPixelColors(pixels: List<Int>, color: ColorContainerInterface, prolonged: Boolean = false) {
    for (pixel in pixels) {
        setPixelColor(pixel, color, prolonged)
    }
}

/**
 * Set pixels based on indices in a list
 *
 * @param pixels The pixels to set
 * @param color The color to use
 */
fun LEDStrip.setPixelColors(pixels: List<Int>, color: Long, prolonged: Boolean = false) {
    for (pixel in pixels) {
        setPixelColor(pixel, color, prolonged)
    }
}

/**
 * Alias for setSectionColor
 */
fun LEDStrip.setPixelColors(pixels: IntRange, color: ColorContainerInterface, prolonged: Boolean = false) =
    setSectionColor(pixels, color, prolonged)

/**
 * Alias for setSectionColor
 */
fun LEDStrip.setPixelColors(pixels: IntRange, color: Long, prolonged: Boolean = false) =
    setSectionColor(pixels, color, prolonged)
