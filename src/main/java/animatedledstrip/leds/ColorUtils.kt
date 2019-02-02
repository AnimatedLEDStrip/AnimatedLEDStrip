package animatedledstrip.leds

/*
 *  Copyright (c) 2019 AnimatedLEDStrip
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


import org.pmw.tinylog.Logger
import java.lang.NumberFormatException
import kotlin.math.roundToInt

/**
 * Blend two [ColorContainer]s together and return a new `ColorContainer`.
 *
 * Adapted from the FastLED library.
 *
 * @param existing The starting `ColorContainer`
 * @param overlay The `ColorContainer` to blend toward
 * @param amountOfOverlay The proportion (0-255) of `b` to blend
 */
fun blend(existing: ColorContainer, overlay: ColorContainer, amountOfOverlay: Int): ColorContainer {
    if (amountOfOverlay == 0) return existing

    if (amountOfOverlay == 255) return overlay

    val r = blend8(existing.r, overlay.r, amountOfOverlay)
    val g = blend8(existing.g, overlay.g, amountOfOverlay)
    val b = blend8(existing.b, overlay.b, amountOfOverlay)

    return ColorContainer(r, g, b)

}

/**
 * Create a collection of colors that blend between multiple colors along a 'strip'.
 *
 * The palette colors are spread out along the strip at approximately equal
 * intervals. All pixels between these 'pure' pixels are a blend between the
 * colors of the two nearest pure pixels. The blend ratio is determined by the
 * location of the pixel relative to the nearest pure pixels.
 *
 * @param palette A list of [ColorContainer]s used to create the collection's colors
 * @param numLEDs The number of LEDs to create colors for
 * @return A `Map<Int, ColorContainer>` with each pixel index mapped to a `ColorContainer`
 */
fun colorsFromPalette(palette: List<ColorContainer>, numLEDs: Int): Map<Int, ColorContainer> {

    val returnMap = mutableMapOf<Int, ColorContainer>()

    val spacing = numLEDs.toDouble() / palette.size.toDouble()

    val purePixels = mutableListOf<Int>()
    for (i in 0 until palette.size) {
        purePixels.add((spacing * i).roundToInt())
    }

    for (i in 0 until numLEDs) {
        for (j in purePixels) {
            if ((i - j) < spacing) {
                if ((i - j) == 0) returnMap[i] = palette[purePixels.indexOf(j)]
                else {
                    returnMap[i] = blend(
                            palette[purePixels.indexOf(j)],
                            palette[(purePixels.indexOf(j) + 1) % purePixels.size],
                            if (purePixels.indexOf(j) < purePixels.size - 1) (((i - j) / ((purePixels[purePixels.indexOf(j) + 1]) - j).toDouble()) * 255).toInt() else (((i - j) / (numLEDs - j).toDouble()) * 255).toInt()
                    )
                }
                break
            }
        }
    }
    return returnMap
}


/**
 * Try to pause the thread for an amount of time in milliseconds.
 *
 * @param wait The time (in milliseconds) to wait for
 */
fun delayBlocking(wait: Int) {
    try {
        Thread.sleep(wait.toLong())
    } catch (e: InterruptedException) {
    }
}


suspend fun delay(wait: Int) {
    kotlinx.coroutines.delay(wait.toLong())
}


/**
 * Returns a `Long` from a hexadecimal String.
 *
 * @param string The hex `String` to decode
 */
fun parseHex(string: String): Long {
    val s = string.removePrefix("0x")     // remove leading 0x if present
    return try {
        java.lang.Long.parseLong(s, 16)
    } catch (e: NumberFormatException) {
        Logger.warn("Format of string \"$string\" is malformed: $e")
        0x0
    }
}
