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


import animatedledstrip.colors.ColorContainer
import animatedledstrip.colors.ColorContainerInterface
import animatedledstrip.colors.PreparedColorContainer
import animatedledstrip.colors.offsetBy


operator fun LEDStripNonConcurrent.set(vararg pixels: Int, color: ColorContainerInterface) {
    for (pixel in pixels) {
        setPixelColor(pixel, color)
    }
}

operator fun LEDStripNonConcurrent.set(pixels: IntRange, color: ColorContainerInterface) {
    for (pixel in pixels) {
        setPixelColor(pixel, color)
    }
}

operator fun LEDStripNonConcurrent.set(vararg pixels: Int, color: Long) {
    for (pixel in pixels) {
        setPixelColor(pixel, ColorContainer(color))
    }
}

operator fun LEDStripNonConcurrent.set(pixels: IntRange, color: Long) {
    for (pixel in pixels) {
        setPixelColor(pixel, ColorContainer(color))
    }
}


/**
 * Set the color of a strip using a ColorContainer offset by `offset`
 *
 * @param colors The colors
 * @param offset The index of the pixel that will be set to the color at
 * index 0
 */
fun LEDStripNonConcurrent.setStripColorWithOffset(colors: PreparedColorContainer, offset: Int = 0) {
    setStripColor(colors.offsetBy(offset))
}

/**
 * Get the color of a pixel as a hexadecimal string.
 *
 * @param pixel The pixel to find the color of
 * @return A `String` containing the color of the pixel in hexadecimal
 */
fun LEDStripNonConcurrent.getPixelHexString(pixel: Int): String {
    return getPixelColor(pixel).toString(16)
}

/**
 * Get the color of a pixel.
 *
 * @param pixel The pixel to find the color of
 * @return The color of the pixel or null if the index is invalid
 */
fun LEDStripNonConcurrent.getPixelColorOrNull(pixel: Int): Long? = try {
    getPixelColor(pixel)
} catch (e: IllegalArgumentException) {
    null
}

/**
 * Get the actual color of a pixel.
 *
 * @param pixel The pixel to find the color of
 * @return The color of the pixel or null if the index is invalid
 */
fun LEDStrip.getActualPixelColorOrNull(pixel: Int): Long? = try {
    getActualPixelColor(pixel)
} catch (e: IllegalArgumentException) {
    null
}