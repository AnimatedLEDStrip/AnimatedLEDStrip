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

import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

/**
 * A class for storing colors that can be used in animations. This can store a
 * variable number of colors (stored as 24-bit `Int`s).
 *
 * Behavior when `Int`s outside the range `0..0xFFFFFF` are added is undefined.
 */
@Serializable
open class ColorContainer(
    final override val colors: MutableList<Int> = mutableListOf(),
) : ColorContainerInterface {

    /**
     * A helper property that tries to return the first color in [colors]
     */
    override val color: Int
        get() = this[0]

    /**
     * Tracks if there is only one color in [colors].
     */
    private val singleColor: Boolean
        get() = colors.size == 1


    /* Construction */

    /**
     * Create a `ColorContainer` from 0 or more `Int`s
     */
    constructor(vararg c: Int) : this() {
        for (i in c) colors += i
    }

    /**
     * Create a new `ColorContainer` with one color, passed as a Triple
     * containing r, g, and b.
     */
    constructor(rgb: Triple<Int, Int, Int>)
            : this((rgb.first shl 16) or (rgb.second shl 8) or rgb.third)

    /**
     * Copy constructor
     */
    constructor(vararg ccs: ColorContainer) : this() {
        for (cc in ccs)
            colors.addAll(cc.colors)
    }


    /* Get color */

    /**
     * Get the color in [colors] at the specified index.
     * Checks if `index` is a valid index of `colors`
     * and if so, returns the color stored there,
     * if not, returns `0` (black).
     */
    operator fun get(index: Int): Int =
        colors.getOrElse(index) { 0 }


    /**
     * Get multiple colors from [colors]. Accepts a variable number of arguments
     * (a single argument will be caught by the `get()` operator above). If no
     * indices are provided, this will return an empty list.
     * If an index is not a valid index in `colors`, `0` is added
     * to the list. The returned list contains the colors in the order
     * specified.
     */
    operator fun get(vararg indices: Int): List<Int> =
        indices.map { colors.getOrElse(it) { 0 } }

    /**
     * Get multiple colors from [colors]. If an index in the range is not a
     * valid index in `colors`, `0` is added to the list.
     */
    operator fun get(indices: IntRange): List<Int> =
        indices.map { colors.getOrElse(it) { 0 } }


    /* Set color */

    /**
     * Set some indices of [colors] to [c]. If an index is not a valid index
     * in `colors`, this will add it to the end of `colors`, though not
     * necessarily at the index specified.
     */
    operator fun set(vararg indices: Int, c: Int) {
        for (index in indices.sorted())
            if (colors.indices.contains(index)) colors[index] = c
            else colors += c
    }

    /**
     * Set a range of indices of [colors] to [c]. If an index is not a valid
     * in [colors], this will add the color to the end of `colors`, though not
     * necessarily at the index specified.
     */
    operator fun set(indices: IntRange, c: Int) {
        for (index in indices)
            if (colors.indices.contains(index)) colors[index] = c
            else colors += c
    }

    /**
     * Adds a color at the end of [colors].
     */
    operator fun plusAssign(c: Int) {
        colors.add(c)
    }


    /* Preparation */

    /**
     * Prepare these colors for use with a LED strip by creating a collection of
     * colors that blend between multiple colors along the 'strip'.
     *
     * The palette colors are spread out along the strip at approximately equal
     * intervals. All pixels between these 'pure' pixels are a blend between the
     * colors of the two nearest pure pixels. The blend ratio is determined by the
     * location of the pixel relative to the nearest pure pixels.
     *
     * If the `ColorContainer` has more colors than there are pixels in the strip,
     * some colors will be dropped.
     *
     * If the `ColorContainer` is empty, the resulting `PreparedColorContainer`
     * will contain 0s.
     *
     * @param numLEDs The number of LEDs to create colors for
     * @return A [PreparedColorContainer] containing all the calculated colors
     */
    override fun prepare(numLEDs: Int): PreparedColorContainer {
        require(numLEDs > 0)
        if (colors.isEmpty()) colors.add(0)

        val returnMap = mutableMapOf<Int, Int>()
        val spacing = numLEDs.toDouble() / colors.size.toDouble()
        val purePixels = (0 until colors.size).map { (spacing * it).roundToInt() }

        for (i in 0 until numLEDs) {
            for (p in purePixels) {
                if ((i - p) < spacing) {
                    val pIndex = purePixels.indexOf(p)
                    val d =
                        if (pIndex < purePixels.size - 1) purePixels[pIndex + 1] - p
                        else numLEDs - p

                    if ((i - p) == 0) // We are on a pure pixel
                        returnMap[i] = colors[pIndex]
                    else
                        returnMap[i] = blend(
                            colors[pIndex],
                            colors[(pIndex + 1) % purePixels.size],
                            (((i - p) / d.toDouble()) * 255).toInt(),
                        )
                    break
                }
            }
        }

        return PreparedColorContainer(returnMap.values.toMutableList(), colors)
    }


    /* Conversion */

    /**
     * Create a string representation of this `ColorContainer`.
     * The hexadecimal representation of each color in [colors] is
     * listed in comma delimited format, between brackets `[` & `]`.
     */
    override fun toString(): String = colors.joinToString(separator = ", ", prefix = "[", postfix = "]") { it base 16 }


    /**
     * @return The first color in [colors].
     */
    fun toInt(): Int = color

    /**
     * @return The first color in [colors] as a Triple containing r, g, b
     */
    fun toRGB(): Triple<Int, Int, Int> = Triple(
        color shr 16 and 0xFF,
        color shr 8 and 0xFF,
        color and 0xFF
    )

    /**
     * Calls toRGB()
     */
    fun toTriple(): Triple<Int, Int, Int> = toRGB()

    /**
     * @return This ColorContainer instance
     */
    override fun toColorContainer(): ColorContainer = this


    /* Operators/Other */

    /**
     * @return The iterator for [colors]
     */
    operator fun iterator(): MutableIterator<Int> = colors.iterator()

    /**
     * Checks if the specified color is in [colors]
     */
    operator fun contains(c: Int): Boolean = colors.contains(c)

    /**
     * @return The size of [colors]
     */
    val size: Int
        get() = colors.size

    /**
     * Compares this `ColorContainer` against another `ColorContainer` or an `Int`.
     * If `other` is a `ColorContainer`, the [colors] parameters are compared.
     * If `other` is a `PreparedColorContainer`, the [colors] parameter is compared
     * to the `originalColors` parameter.
     * If `other` is an `Int`, the [color] parameter is compared to the `Int`.
     */
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is ColorContainer -> other.colors == this.colors
            is PreparedColorContainer -> other.originalColors == this.colors
            is Int -> singleColor && other == this.color
            else -> super.equals(other)
        }
    }

    /**
     * @return The hashCode of [colors]
     */
    override fun hashCode(): Int = colors.hashCode()
}
