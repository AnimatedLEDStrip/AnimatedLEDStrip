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

package animatedledstrip.colors

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A set of colors ready for use with a LED strip.
 * Colors blend from one to the next along the length of the strip.
 *
 * @see ColorContainer.prepare
 * @property colors The `List` of colors in this `PreparedColorContainer`
 * @property originalColors The colors that were used to prepare this
 * `PreparedColorContainer`
 */
@Serializable
@SerialName("PreparedColorContainer")
class PreparedColorContainer(
    override val colors: List<Int>,
    val originalColors: List<Int> = listOf(),
) : ColorContainerInterface {

    /**
     * Get the color in [colors] at the specified index.
     * Checks if `index` is a valid index of `colors`
     * and if so, returns the color stored there,
     * if not, returns `0` (black).
     */
    operator fun get(index: Int): Int = colors.getOrElse(index) { 0 }

    /**
     * Override for `color` that only returns `0`
     * @return `0`
     */
    override val color: Int
        get() = 0

    /**
     * If this `PreparedColorContainer` is the correct size, return this
     * instance, otherwise a new instance of the correct size
     */
    override fun prepare(numLEDs: Int): PreparedColorContainer =
        if (numLEDs == size) this
        else ColorContainer(originalColors.toMutableList()).prepare(numLEDs)

    /**
     * Create a string representation of this `PreparedColorContainer`.
     * The hexadecimal representation of each color in [colors] is
     * listed in comma delimited format, between brackets `[` & `]`.
     */
    override fun toString(): String =
        colors.joinToString(separator = ", ", prefix = "[", postfix = "]") { it base 16 }

    /**
     * @return The iterator for [colors]
     */
    operator fun iterator(): Iterator<Int> = colors.iterator()

    /**
     * Checks if the specified color is in [colors]
     */
    operator fun contains(value: Int): Boolean = colors.contains(value)

    /**
     * @return The size of [colors]
     */
    val size: Int
        get() = colors.size

    /**
     * @return A new [ColorContainer] instance with the colors in [colors]
     */
    override fun toColorContainer() = ColorContainer(colors.toMutableList())

    /**
     * @return A new [ColorContainer] instance with the colors in [originalColors]
     */
    fun originalColorContainer() = ColorContainer(originalColors.toMutableList())

    /**
     * Compares this `PreparedColorContainer` against another `ColorContainer` or an `Int`.
     * If `other` is a `ColorContainer`, the [originalColors] parameter is compared
     * to the `originalColors` parameter.
     * If `other` is a `PreparedColorContainer`, the [originalColors] parameters are compared.
     * If `other` is an `Int`, the [color] parameter is compared to the `Int`.
     */
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is ColorContainer -> other.colors == originalColors
            is PreparedColorContainer -> other.originalColors == originalColors
            else -> super.equals(other)
        }
    }

    /**
     * @return A combination of the hash codes of [colors] and [originalColors]
     */
    override fun hashCode(): Int {
        var result = colors.hashCode()
        result = 31 * result + originalColors.hashCode()
        return result
    }
}
