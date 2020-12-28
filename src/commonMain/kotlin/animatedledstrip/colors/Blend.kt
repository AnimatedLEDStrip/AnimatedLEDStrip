/*
 *  Copyright (c) 2018-2020 AnimatedLEDStrip
 *  Copyright (c) 2013 FastLED
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

package animatedledstrip.colors

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

/**
 * Blend two colors together and return a new color.
 *
 * Adapted from the FastLED library.
 *
 * @param existing The starting color
 * @param overlay The color to blend toward
 * @param amountOfOverlay The proportion (0-255) of `overlay` to blend into `existing`
 */
fun blend(existing: Int, overlay: Int, amountOfOverlay: Int): Int {
    if (amountOfOverlay == 0) return existing
    if (amountOfOverlay == 255) return overlay
    if (existing == overlay) return existing

    val r = blend8(existing.r, overlay.r, amountOfOverlay)
    val g = blend8(existing.g, overlay.g, amountOfOverlay)
    val b = blend8(existing.b, overlay.b, amountOfOverlay)

    return (r shl 16) or (g shl 8) or b
}

/**
 * Blend a variable proportion (0-255) of one byte to another.
 *
 * Adapted from the FastLED Library.
 *
 * @param a The starting byte value
 * @param b The byte value to blend toward
 * @param amountOfB The proportion (0-255) of b to blend
 * @return A byte value between `a` and `b`, inclusive
 */
fun blend8(a: Int, b: Int, amountOfB: Int): Int {
    var partial: Int
    val amountOfA = 255 - amountOfB
    partial = a * amountOfA
    partial += a
    partial += (b * amountOfB)
    partial += b
    return if (b > a) min(ceil(partial / 256.0).toInt(), b)
    else max(floor(partial / 256.0).toInt(), b)
}
