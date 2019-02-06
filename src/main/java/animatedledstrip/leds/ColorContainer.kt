package animatedledstrip.leds

import kotlin.math.roundToInt

open class ColorContainer(vararg c: Long) : ColorContainerInterface {

    val colors = mutableListOf<Long>()
    override val color: Long
        get() = try { colors[0] } catch (e: IndexOutOfBoundsException) { 0 }

    private val singleColor: Boolean
        get() = colors.size == 1

    /* Constructors */

    init {
        for (i in c) {
            colors += i
        }
    }

    constructor(rgb: Triple<Int, Int, Int>) : this((rgb.first shl 16).toLong() or (rgb.second shl 8).toLong() or rgb.third.toLong())

    constructor(colorList: List<Long>) : this() {
        colorList.forEach {
            colors += it
        }
    }

    constructor(ccIn: ColorContainer) : this() {
        for (c in ccIn) {
            colors += c
        }
    }

    /* Get/set operations */

    operator fun get(index: Int): Long =
            when {
                singleColor -> color
                colors.indices.contains(index) -> colors[index]
                else -> 0
            }

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


    operator fun set(vararg indices: Int, c: Long) {
        for (index in indices.sorted()) {
            if (colors.indices.contains(index)) colors[index] = c
            else colors += c
        }
    }

    operator fun set(indices: IntRange, c: Long) {
        for (index in indices) {
            if (colors.indices.contains(index)) colors[index] = c
            else colors += c
        }
    }

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
     * @return A `PreparedColorContainer` containing all the colors
     */
    override fun prepare(numLEDs: Int): PreparedColorContainer {
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
        return PreparedColorContainer(returnMap.values.toList())
    }


    fun grayscale(): ColorContainer {
        colors.forEachIndexed { index, color ->
            colors[index] = color.grayscale()
        }
        return this
    }

    fun grayscale(vararg indices: Int): ColorContainer  {
        for (index in indices) {
            if (colors.indices.contains(index)) colors[index] = colors[index].grayscale()
        }
        return this
    }

    fun grayscaled(): ColorContainer {
        val temp = ColorContainer()
        for (c in colors) {
            temp += c.grayscale()
        }
        return temp
    }

    fun grayscaled(vararg indices: Int): ColorContainer {
        val temp = ColorContainer()
        for (i in indices) {
            if (colors.indices.contains(i)) temp += colors[i].grayscale()
        }
        return temp
    }


    fun invert(): ColorContainer {
        colors.forEachIndexed { index, color ->
            colors[index] = color.inv() and 0xFFFFFF
        }
        return this
    }

    fun invert(vararg indices: Int): ColorContainer {
        for (index in indices) {
            if (colors.indices.contains(index)) colors[index] = colors[index].inv() and 0xFFFFFF
        }
        return this
    }

    fun inverse(): ColorContainer {
        val temp = ColorContainer()
        for (c in colors) {
            temp += c.inv() and 0xFFFFFF
        }
        return temp
    }

    fun inverse(vararg indices: Int): ColorContainer {
        val temp = ColorContainer()
        for (i in indices) {
            if (colors.indices.contains(i)) temp += colors[i].inv() and 0xFFFFFF
        }
        return temp
    }

    operator fun unaryMinus(): ColorContainer = inverse()


    /* Conversion functions */

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

    fun toLong(): Long = color

    fun toRGB(): Triple<Int, Int, Int> = Triple(
            (color shr 16 and 0xFF).toInt(),
            (color shr 8 and 0xFF).toInt(),
            (color and 0xFF).toInt())

    fun toTriple() = toRGB()



    override fun equals(other: Any?): Boolean {
        return when (other) {
            is ColorContainer -> other.colors == this.colors
            is Long -> singleColor && other == this.color
            else -> super.equals(other)
        }
    }

    operator fun iterator() = colors.iterator()

    operator fun contains(c: Long): Boolean = colors.contains(c)

    override fun hashCode(): Int {
        return colors.hashCode()
    }

    val size: Int
        get() = colors.size


    /* Deprecated properties */

    @Deprecated("Use color property and r extension property", ReplaceWith("color.r"))
    val r: Int
        get() = (color shr 16 and 0xFF).toInt()
    val red = r

    @Deprecated("Use color property and g extension property", ReplaceWith("color.g"))
    val g: Int
        get() = (color shr 16 and 0xFF).toInt()
    val green = g

    @Deprecated("Use color property and b extension property", ReplaceWith("color.b"))
    val b: Int
        get() = (color and 0xFF).toInt()
    val blue = b

    @Deprecated("Use color property", ReplaceWith("color"))
    val hex
        get() = color

    @Deprecated("Use toString()", ReplaceWith("toString()"))
    val hexString
        get() = toString()

}
