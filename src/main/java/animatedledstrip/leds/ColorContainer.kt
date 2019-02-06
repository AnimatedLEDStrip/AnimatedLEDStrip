package animatedledstrip.leds

import kotlin.math.roundToInt

open class ColorContainer(vararg c: Long) : ColorContainerInterface {

    val colors = mutableListOf<Long>()
    override val color: Long
        get() = colors[0]

    private val singleColor: Boolean
        get() = colors.size == 1


    init {
        for (i in c) {
            colors += i
        }
    }

    constructor(r: Int, g: Int, b: Int) : this((r shl 16).toLong() or (g shl 8).toLong() or b.toLong())

    constructor(colorList: List<Long>) : this(colorList[0]) {
        colorList.forEachIndexed { index, color ->
            colors[index] = color
        }
    }

    constructor(ccIn: ColorContainer) : this()

    fun grayscale() {
        colors.forEachIndexed { index, color ->
            colors[index] = color.grayscale()
        }
    }

    fun grayscale(vararg indices: Int) {
        for (index in indices) {
            if (colors.indices.contains(index)) colors[index] = colors[index].grayscale()
        }
    }


    fun invert() {
        colors.forEachIndexed { index, color ->
            colors[index] = color.inv()
        }
    }

    fun invert(vararg indices: Int) {
        for (index in indices) {
            if (colors.indices.contains(index)) colors[index] = colors[index].inv()
        }
    }


    fun prepare(numLEDs: Int): PreparedColorContainer {
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


    fun toLong(): Long = color

    fun toRGB(): Triple<Int, Int, Int> = Triple(
            (color and 0xFF0000 shr 16).toInt(),
            (color and 0x00FF00 shr 8).toInt(),
            (color and 0x0000FF).toInt())

    fun toTriple() = toRGB()

    @Deprecated("Use color property and r extension property", ReplaceWith("color.r"))
    val r: Int
        get() = (color shr 16 and 0xFF).toInt()
    val red = r

    @Deprecated("Use color property and g extension property", ReplaceWith("color.g"))
    val g: Int
        get() = (color shr 8 and 0xFF).toInt()
    val green = g

    @Deprecated("Use color property and b extension property", ReplaceWith("color.b"))
    val b: Int
        get() = (color and 0xFF).toInt()
    val blue = b

    override fun toString(): String {
        return if (singleColor) color.toString(16)
        else "[${colors.forEachIndexed { index, color ->
            color.toString(16) + if (index != colors.lastIndex) "," else ""
        }}]"
    }

    override fun equals(other: Any?): Boolean {
        return if (other is ColorContainer) {
            if (other.colors.size == this.colors.size) {
                var i = true

                other.colors.forEachIndexed { index, color ->
                    if (i) i = i && (color == this.colors[index])
                }
                i
            } else false
        } else if (other is Long) other == this.color
        else super.equals(other)
    }


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


    operator fun set(vararg indices: Int, c: Long) {
        for (index in indices.sorted()) {
            if (colors.indices.contains(index)) colors[index] = c
            else colors += c
        }
    }

    operator fun unaryMinus() {
        invert()
    }

    operator fun plusAssign(c: Long) {
        colors.add(c)
    }

    operator fun iterator() = colors.iterator()

    operator fun contains(c: Long): Boolean = colors.contains(c)

    override fun hashCode(): Int {
        return colors.hashCode()
    }


    @Deprecated("Use color property", ReplaceWith("color"))
    val hex
        get() = color

    @Deprecated("Use toString()", ReplaceWith("toString()"))
    val hexString
        get() = toString()

}
