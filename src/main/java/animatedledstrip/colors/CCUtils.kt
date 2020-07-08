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

import animatedledstrip.utils.grayscale

/* Grayscale */

/**
 * Replaces all colors in [ColorContainer.colors] with a grayscaled version of themselves.
 *
 * @return This [ColorContainer] instance
 */
fun ColorContainer.grayscale(): ColorContainer {
    colors.forEachIndexed { index, color ->
        colors[index] = color.grayscale()
    }
    return this
}

/**
 * Replaces the colors at the specified indices with a grayscaled version
 * of themselves.
 *
 * @return This [ColorContainer] instance
 */
fun ColorContainer.grayscale(vararg indices: Int): ColorContainer {
    for (index in indices)
        if (colors.indices.contains(index)) colors[index] = colors[index].grayscale()
    return this
}

/**
 * Replaces the colors at the indices in the specified ranges with a
 * grayscaled version of themselves.
 *
 * @return This [ColorContainer] instance
 */
fun ColorContainer.grayscale(vararg indices: IntRange): ColorContainer {
    for (range in indices)
        for (index in range)
            if (colors.indices.contains(index)) colors[index] = colors[index].grayscale()
    return this
}

/**
 * Returns a new [ColorContainer] with the same colors as this instance,
 * but grayscaled.
 *
 * @return A new [ColorContainer] instance
 */
fun ColorContainer.grayscaled(): ColorContainer {
    val temp = ColorContainer()
    for (c in colors)
        temp += c.grayscale()
    return temp
}

/**
 * Returns a new [ColorContainer] with the same colors as this instance,
 * grayscaled, but only including the indices specified.
 *
 * @return A new [ColorContainer] instance
 */
fun ColorContainer.grayscaled(vararg indices: Int): ColorContainer {
    val temp = ColorContainer()
    for (i in indices)
        if (colors.indices.contains(i)) temp += colors[i].grayscale()
    return temp
}

/**
 * Returns a new [ColorContainer] with the same colors as this instance,
 * grayscaled, but only including the ranges of indices specified.
 *
 * @return A new [ColorContainer] instance
 */
fun ColorContainer.grayscaled(vararg indices: IntRange): ColorContainer {
    val temp = ColorContainer()
    for (r in indices)
        for (i in r)
            if (colors.indices.contains(i)) temp += colors[i].grayscale()
    return temp
}


/* Invert */

/**
 * Replaces all colors in [ColorContainer.colors] with their inverse.
 *
 *@return This [ColorContainer] instance
 */
fun ColorContainer.invert(): ColorContainer {
    colors.forEachIndexed { index, color ->
        colors[index] = color.inv() and 0xFFFFFF
    }
    return this
}

/**
 * Replaces the colors at the specified indices with their inverse.
 *
 * @return This [ColorContainer] instance
 */
fun ColorContainer.invert(vararg indices: Int): ColorContainer {
    for (index in indices)
        if (colors.indices.contains(index)) colors[index] = colors[index].inv() and 0xFFFFFF
    return this
}

/**
 * Replaces the colors at the indices in the specified ranges with
 * their inverse
 *
 * @return This [ColorContainer] instance
 */
fun ColorContainer.invert(vararg indices: IntRange): ColorContainer {
    for (range in indices)
        for (index in range)
            if (colors.indices.contains(index)) colors[index] = colors[index].inv() and 0xFFFFFF
    return this
}

/**
 * Returns a new [ColorContainer] with the same colors as this instance,
 * but inverted.
 *
 * @return A new [ColorContainer] instance
 */
fun ColorContainer.inverse(): ColorContainer {
    val temp = ColorContainer()
    for (c in colors)
        temp += c.inv() and 0xFFFFFF
    return temp
}

/**
 * Returns a new [ColorContainer] with the same colors as this instance,
 * inverted, but only including the indices specified.
 *
 * @return A new [ColorContainer] instance
 */
fun ColorContainer.inverse(vararg indices: Int): ColorContainer {
    val temp = ColorContainer()
    for (i in indices)
        if (colors.indices.contains(i)) temp += colors[i].inv() and 0xFFFFFF
    return temp
}

/**
 * Returns a new [ColorContainer] with the same colors as this instance,
 * inverted, but only including the ranges of indices specified.
 *
 * @return A new [ColorContainer] instance
 */
fun ColorContainer.inverse(vararg indices: IntRange): ColorContainer {
    val temp = ColorContainer()
    for (r in indices)
        for (i in r)
            if (colors.indices.contains(i)) temp += colors[i].inv() and 0xFFFFFF
    return temp
}

/**
 * Operator overload that returns a new [ColorContainer] containing the
 * inverse of the colors in this [ColorContainer].
 *
 * @return A new [ColorContainer] instance
 */
operator fun ColorContainer.unaryMinus(): ColorContainer = inverse()


/* Add offset */

/**
 * Create a [PreparedColorContainer] with the same colors as this
 * [PreparedColorContainer], only offset by `offset`.
 * The color at index `0` will be moved to index `offset`.
 * Negative offsets and offsets that are larger than the size of
 * `colors` are supported.
 */
fun PreparedColorContainer.offsetBy(offset: Int): PreparedColorContainer {
    val n = when {
        offset > colors.lastIndex -> size - (offset % size)
        -offset > colors.lastIndex -> -offset % size
        offset < 0 -> -offset
        offset > 0 -> size - offset
        else -> 0
    }

    val temp = mutableListOf<Long>()
    for (i in colors.indices) {
        temp += colors[(i + n) % size]
    }
    return PreparedColorContainer(temp)
}


/* Check if empty */

/**
 * Report whether `colors` is empty
 */
fun ColorContainer.isEmpty() = colors.isEmpty()

/**
 * Report whether `colors` is empty
 */
fun PreparedColorContainer.isEmpty() = colors.isEmpty()

/**
 * Report whether `colors` is not empty
 */
fun ColorContainer.isNotEmpty() = !isEmpty()

/**
 * Report whether `colors` is not empty
 */
fun PreparedColorContainer.isNotEmpty() = !isEmpty()
