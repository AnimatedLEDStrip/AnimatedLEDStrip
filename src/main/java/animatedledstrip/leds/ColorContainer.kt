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


import kotlin.math.roundToInt

/**
 * A class for storing colors that can be used in animations. This can store a
 * variable number of colors (stored as 24-bit Longs).
 *
 * @param c The color(s) to store in the ColorContainer
 */
open class ColorContainer(vararg c: Long) : ColorContainerInterface {

    /**
     * The List where the colors are stored.
     */
    val colors = mutableListOf<Long>()

    /**
     * A helper property that returns the first color in [colors]. If `colors`
     * is empty, returns 0 (black).
     */
    override val color: Long
        get() = try {
            colors[0]
        } catch (e: IndexOutOfBoundsException) {
            0
        }

    /**
     * Helper property that tracks if there is only one color in [colors].
     */
    private val singleColor: Boolean
        get() = colors.size == 1

    /* Constructors */

    init {
        for (i in c) {
            colors += i
        }
    }

    /**
     * Create a new ColorContainer with one color represented as a Triple
     * containing r, g, and b.
     */
    constructor(rgb: Triple<Int, Int, Int>) : this((rgb.first shl 16).toLong() or (rgb.second shl 8).toLong() or rgb.third.toLong())

    /**
     * Create a new ColorContainer from a List<Long>.
     */
    constructor(colorList: List<Long>) : this() {
        colorList.forEach {
            colors += it
        }
    }

    /**
     * Copy constructor
     */
    constructor(ccIn: ColorContainer) : this() {
        for (c in ccIn) {
            colors += c
        }
    }

    /* Get/set operations */

    /**
     * Get one color from [colors]. If there is only one color ([singleColor]
     * returns true), this will return that color, regardless of what index
     * was sent. Otherwise, it checks if `index` is a valid index of `colors`
     * and if so, returns the color stored there, if not, returns 0 (black).
     *
     * @param index The index of the color to get
     */
    operator fun get(index: Int): Long =
            when {
                singleColor -> color
                colors.indices.contains(index) -> colors[index]
                else -> 0
            }

    /**
     * Get colors from [colors]. Accepts a variable number of arguments
     * (except one, that is caught by the get() operator above). If 0
     * indices are provided, this will return an empty list.
     * If multiple indices are provided but there is only one color
     * in the list, this will return a list containing only that one
     * color. If an index is not a valid index in `colors`, 0 is added
     * to the list. The returned list contains the colors in the order
     * that the indices were ordered when this was called.
     *
     * @param indices The indices of the colors to retrieve
     */
    operator fun get(vararg indices: Int): List<Long> =
            if (singleColor) listOf(color)
            else {
                val temp = mutableListOf<Long>()
                for (index in indices) {
                    temp += if (colors.indices.contains(index)) colors[index]
                    else 0
                }
                temp
            }

    /**
     * Get colors from [colors]. If there is only one color in `colors`,
     * this will return a list containing only that one color. If an index
     * in the range is not a valid index in `colors`, 0 is added to the list.
     *
     * @param indices A range of indices of the colors to retrieve
     */
    operator fun get(indices: IntRange): List<Long> =
            if (singleColor) listOf(color)
            else {
                val temp = mutableListOf<Long>()
                for (index in indices) {
                    temp += if (colors.indices.contains(index)) colors[index]
                    else 0
                }
                temp
            }

    /**
     * Set some indices of [colors] to [c]. If an index is not a valid index
     * in `colors`, this will add it to the end of `colors`, though not
     * necessarily at the index specified.
     *
     * @param indices The indices to set to `c`
     * @param c The color to store
     */
    operator fun set(vararg indices: Int, c: Long) {
        for (index in indices.sorted()) {
            if (colors.indices.contains(index)) colors[index] = c
            else colors += c
        }
    }

    /**
     * Set a range of indices of [colors] to [c]. If an index is not a valid
     * in [colors], this will add the color to the end of `colors`, though not
     * necessarily at the index specified.
     *
     * @param indices The range of indices to set to `c`
     * @param c The color to store
     */
    operator fun set(indices: IntRange, c: Long) {
        for (index in indices) {
            if (colors.indices.contains(index)) colors[index] = c
            else colors += c
        }
    }

    /**
     * Adds a color at the end of [colors].
     *
     * @param c The color to add
     */
    operator fun plusAssign(c: Long) {
        colors.add(c)
    }


    /* Utility functions */

    /**
     * Create a collection of colors that blend between multiple colors along a 'strip'.
     *
     * The palette colors are spread out along the strip at approximately equal
     * intervals. All pixels between these 'pure' pixels are a blend between the
     * colors of the two nearest pure pixels. The blend ratio is determined by the
     * location of the pixel relative to the nearest pure pixels.
     *
     * If this is not used prior to sending this ColorContainer to setPixelColor (or other
     * set methods), then it will be called before any LEDs are set.
     *
     * @param numLEDs The number of LEDs to create colors for
     * @return A [PreparedColorContainer] containing all the colors
     */
    override fun prepare(numLEDs: Int, leadingZeros: Int): PreparedColorContainer {
        val returnMap = mutableMapOf<Int, Long>()

        val spacing = numLEDs.toDouble() / colors.size.toDouble()

        val purePixels = mutableListOf<Int>()
        for (i in 0 until colors.size) {
            purePixels.add((spacing * i).roundToInt())
        }

        for (i in 0 until numLEDs) {
            for (j in purePixels) {
                if ((i - j) < spacing) {
                    if ((i - j) == 0) returnMap[i] = colors[purePixels.indexOf(j)]
                    else {
                        returnMap[i] = blend(
                                colors[purePixels.indexOf(j)],
                                colors[(purePixels.indexOf(j) + 1) % purePixels.size],
                                if (purePixels.indexOf(j) < purePixels.size - 1) (((i - j) / ((purePixels[purePixels.indexOf(j) + 1]) - j).toDouble()) * 255).toInt() else (((i - j) / (numLEDs - j).toDouble()) * 255).toInt()
                        )
                    }
                    break
                }
            }
        }

        val returnList = returnMap.values.toMutableList()

        for (i in 1..leadingZeros) returnList.add(0, 0)

        return PreparedColorContainer(returnList)
    }


    /**
     * Replaces all colors in [colors] with a grayscaled version of themselves.
     *
     * @return This ColorContainer instance
     */
    fun grayscale(): ColorContainer {
        colors.forEachIndexed { index, color ->
            colors[index] = color.grayscale()
        }
        return this
    }

    /**
     * Replaces the colors at the specified indices with a grayscaled version
     * of themselves.
     *
     * @param indices The indices to grayscale
     * @return This ColorContainer instance
     */
    fun grayscale(vararg indices: Int): ColorContainer {
        for (index in indices) {
            if (colors.indices.contains(index)) colors[index] = colors[index].grayscale()
        }
        return this
    }

    /**
     * Returns a new ColorContainer with the same colors as this instance,
     * but grayscaled.
     *
     * @return A new ColorContainer
     */
    fun grayscaled(): ColorContainer {
        val temp = ColorContainer()
        for (c in colors) {
            temp += c.grayscale()
        }
        return temp
    }

    /**
     * Returns a new ColorContainer with the same colors as this instance,
     * grayscaled, but only including the indices specified.
     *
     * @param indices The indices to include in the new ColorContainer
     * @return A new ColorContainer
     */
    fun grayscaled(vararg indices: Int): ColorContainer {
        val temp = ColorContainer()
        for (i in indices) {
            if (colors.indices.contains(i)) temp += colors[i].grayscale()
        }
        return temp
    }

    /**
     * Replaces all colors in [colors] with their inverse.
     *
     *@return This ColorContainer instance
     */
    fun invert(): ColorContainer {
        colors.forEachIndexed { index, color ->
            colors[index] = color.inv() and 0xFFFFFF
        }
        return this
    }

    /**
     * Replaces the colors at the specified indices with their inverse.
     *
     * @param indices The indices to invert
     * @return This ColorContainer instance
     */
    fun invert(vararg indices: Int): ColorContainer {
        for (index in indices) {
            if (colors.indices.contains(index)) colors[index] = colors[index].inv() and 0xFFFFFF
        }
        return this
    }

    /**
     * Returns a new ColorContainer with the same colors as this instance,
     * but inverted.
     *
     * @return A new ColorContainer
     */
    fun inverse(): ColorContainer {
        val temp = ColorContainer()
        for (c in colors) {
            temp += c.inv() and 0xFFFFFF
        }
        return temp
    }

    /**
     * Returns a new ColorContainer with the same colors as this instance,
     * inverted, but only including the indices specified.
     *
     * @param indices The indices to include in the new ColorContainer
     * @return A new ColorContainer
     */
    fun inverse(vararg indices: Int): ColorContainer {
        val temp = ColorContainer()
        for (i in indices) {
            if (colors.indices.contains(i)) temp += colors[i].inv() and 0xFFFFFF
        }
        return temp
    }

    /**
     * Operator overload that returns a new ColorContainer containing the
     * inverse of the colors in this ColorContainer.
     *
     * @return A new ColorContainer
     */
    operator fun unaryMinus(): ColorContainer = inverse()


    /* Conversion functions */

    /**
     * Create a string representation of this ColorContainer.
     * The hexadecimal representation of each color in [colors] is
     * listed in comma delimited format, between brackets `[` & `]`
     * If there is only one color in this ColorContainer, the brackets
     * are dropped.
     */
    override fun toString(): String {
        return if (singleColor) color.toString(16)
        else {
            var temp = "["
            for (c in colors) {
                temp += c base 16
                temp += ", "
            }
            temp = temp.removeSuffix(", ")
            temp += "]"
            temp
        }
    }

    /**
     * Returns the first color in [colors].
     */
    fun toLong(): Long = color

    /**
     * Returns the first color in [colors] a Triple containing r, g, b.
     */
    fun toRGB(): Triple<Int, Int, Int> = Triple(
            (color shr 16 and 0xFF).toInt(),
            (color shr 8 and 0xFF).toInt(),
            (color and 0xFF).toInt())

    /**
     * Calls toRGB()
     */
    fun toTriple() = toRGB()


    /**
     * Compares this ColorContainer against another ColorContainer or a Long.
     * If [other] is a ColorContainer, the [colors] parameters are compared.
     * If [other] is a Long, the [color] parameter is compared to the Long.
     *
     * @param other The value to compare
     */
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is ColorContainer -> other.colors == this.colors
            is Long -> singleColor && other == this.color
            else -> super.equals(other)
        }
    }

    /**
     * @return The iterator for [colors]
     */
    operator fun iterator() = colors.iterator()

    /**
     * Checks if the specified color (Long) is in [colors].
     *
     * @param c The color to search for
     */
    operator fun contains(c: Long): Boolean = colors.contains(c)

    /**
     * @return The hashCode of [colors]
     */
    override fun hashCode(): Int {
        return colors.hashCode()
    }

    /**
     * @return This ColorContainer instance
     */
    override fun toColorContainer(): ColorContainer = this

    /**
     * Returns the size of [colors].
     */
    val size: Int
        get() = colors.size


    /* Deprecated properties */

    @Deprecated("Use color property and r extension property", ReplaceWith("color.r"))
    val r: Int
        get() = color.r
    @Deprecated("Use color property and r extension property", ReplaceWith("color.r"))
    val red: Int
        get() = color.r

    @Deprecated("Use color property and g extension property", ReplaceWith("color.g"))
    val g: Int
        get() = color.g
    @Deprecated("Use color property and g extension property", ReplaceWith("color.g"))
    val green: Int
        get() = color.g

    @Deprecated("Use color property and b extension property", ReplaceWith("color.b"))
    val b: Int
        get() = color.b
    @Deprecated("Use color property and b extension property", ReplaceWith("color.b"))
    val blue: Int
        get() = color.b

    @Deprecated("Use color property", ReplaceWith("color"))
    val hex
        get() = color

    @Deprecated("Use toString()", ReplaceWith("toString()"))
    val hexString
        get() = toString()

}
