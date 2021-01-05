/*
 * Copyright (c) 2018-2021 AnimatedLEDStrip
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package animatedledstrip.colors

/* Grayscale */

/**
 * Returns the 'grayscale' version of a 24-bit color.
 */
fun Int.grayscale(): Int {
    val avg = (((this.r) + (this.g) + (this.b)) / 3) base 16
    return parseHex("$avg$avg$avg")
}

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

fun ColorContainerInterface.shuffledWithIndices(): List<Pair<Int, Int>> =
    colors.mapIndexed { index, it -> Pair(index, it) }.shuffled()


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


/**
 * Returns a new [ColorContainer] with the same colors as this instance,
 * but inverted.
 *
 * @return A new [ColorContainer] instance
 */
fun PreparedColorContainer.inverse(): PreparedColorContainer =
    ColorContainer(originalColors.toMutableList()).invert().prepare(size)

/**
 * Returns a new [ColorContainer] with the same colors as this instance,
 * inverted, but only including the indices specified.
 *
 * @return A new [ColorContainer] instance
 */
fun PreparedColorContainer.inverse(vararg indices: Int): PreparedColorContainer {
    val temp = ColorContainer()
    for (i in indices)
            if (colors.indices.contains(i)) temp += colors[i]
    return temp.invert().prepare(size)
}

/**
 * Returns a new [ColorContainer] with the same colors as this instance,
 * inverted, but only including the ranges of indices specified.
 *
 * @return A new [ColorContainer] instance
 */
fun PreparedColorContainer.inverse(vararg indices: IntRange): PreparedColorContainer {
    val temp = ColorContainer()
    for (r in indices)
        for (i in r)
            if (colors.indices.contains(i)) temp += colors[i]
    return temp.invert().prepare(size)
}

/**
 * Operator overload that returns a new [ColorContainer] containing the
 * inverse of the colors in this [ColorContainer].
 *
 * @return A new [ColorContainer] instance
 */
operator fun PreparedColorContainer.unaryMinus(): PreparedColorContainer = inverse()


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

    val temp = mutableListOf<Int>()
    for (i in colors.indices) {
        temp += colors[(i + n) % size]
    }
    return PreparedColorContainer(temp)
}


/* Check if empty */

/**
 * Report whether `colors` is empty
 */
fun ColorContainerInterface.isEmpty() = colors.isEmpty()

/**
 * Report whether `colors` is not empty
 */
fun ColorContainerInterface.isNotEmpty() = !isEmpty()


/* 24-bit to 32-bit conversion */

/**
 * Convert a 24-bit `Long` to a 32-bit `Int`
 */
fun Long.toARGB(): Int = (this or 0xFF000000).toInt()

/**
 * Convert a 24-bit `Int` to a 32-bit `Int`
 */
fun Int.toARGB(): Int = this or 0xFF000000.toInt()


/* Create a ColorContainer */

/**
 * Create a [ColorContainer] from this `Int`
 */
fun Int.toColorContainer(): ColorContainer = ColorContainer(this)

/**
 * Create a [ColorContainer] from this `Long`
 */
fun Long.toColorContainer(): ColorContainer = ColorContainer(this.toInt())


/* Parse Hex Strings */

/**
 * Returns an `Int` from a hexadecimal string
 */
fun parseHex(string: String): Int = string.remove0xPrefix().toInt(16)

/**
 * Returns an `Int` from a hexadecimal string or `default` on error
 */
fun parseHexOrDefault(string: String, default: Int = 0x0): Int =
    string.remove0xPrefix().toIntOrNull(16) ?: default

/**
 * Helper function for removing the 0x prefix from a hex string
 */
fun String.remove0xPrefix(): String = this.toUpperCase().removePrefix("0X")


/* RGB Components of a 24-bit Color */

/**
 * Returns the 'red' part of a 24-bit color.
 */
val Int.r
    get() = this shr 16 and 0xFF

/**
 * Returns the 'green' part of a 24-bit color.
 */
val Int.g
    get() = this shr 8 and 0xFF

/**
 * Returns the 'blue' part of a 24-bit color.
 */
val Int.b
    get() = this and 0xFF


/**
 * Infix function for easily creating string representations of an `Int` in
 * different bases.
 */
infix fun Int.base(b: Int) = this.toString(b)
