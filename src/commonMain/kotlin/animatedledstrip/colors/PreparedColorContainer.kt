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

import animatedledstrip.utils.base
import kotlinx.serialization.Serializable

/**
 * A prepared [ColorContainer] that holds a set of colors that blend from one
 * to the next (see [ColorContainer.prepare]. This is created by calling
 * [ColorContainer.prepare].
 *
 * @property colors The `List` of colors in this `PreparedColorContainer`
 * @property originalColors The colors that were used to prepare this
 * `PreparedColorContainer`
 */
@Serializable
class PreparedColorContainer(
    override val colors: List<Int>,
    val originalColors: List<Int> = listOf(),
) : ColorContainerInterface {

    /**
     * Get the color in [colors] at the specified index.
     */
    operator fun get(index: Int): Int = colors.getOrElse(index) { 0 }

    /**
     * Override for `color` that only returns 0
     * @return 0
     */
    override val color: Int
        get() = 0

    /**
     * Create a string representation of this `PreparedColorContainer`.
     * The hexadecimal representation of each color in [colors] is
     * listed in comma delimited format, between brackets `[` & `]`
     */
    override fun toString(): String =
        colors.joinToString(separator = ", ", prefix = "[", postfix = "]") { it base 16 }

    /**
     * Checks if the specified value is in [colors].
     */
    operator fun contains(value: Int): Boolean = colors.contains(value)

    /**
     * If this `PreparedColorContainer` is the correct size, return this
     * instance, otherwise a new instance of the correct size
     */
    override fun prepare(numLEDs: Int): PreparedColorContainer =
        if (numLEDs == size) this
        else ColorContainer(originalColors.toMutableList()).prepare(numLEDs)

    /**
     * Returns the size of [colors].
     */
    val size: Int
        get() = colors.size

    /**
     * Returns a new [ColorContainer] instance with the colors in [colors].
     */
    override fun toColorContainer() = ColorContainer(colors.toMutableList())

    fun originalColorContainer() = ColorContainer(originalColors.toMutableList())

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is ColorContainer -> other.colors == originalColors
            is PreparedColorContainer -> other.originalColors == originalColors
            else -> super.equals(other)
        }
    }

    override fun hashCode(): Int {
        var result = colors.hashCode()
        result = 31 * result + originalColors.hashCode()
        return result
    }
}
