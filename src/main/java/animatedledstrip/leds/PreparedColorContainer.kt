package animatedledstrip.leds


/**
 * A prepared [ColorContainer] that holds a set of colors that blend from one
 * to the next. This is created by calling the prepare() function on a
 * ColorContainer
 *
 */
class PreparedColorContainer(private val colors: List<Long>): ColorContainerInterface {

    operator fun get(index: Int) = if (colors.indices.contains(index)) colors[index] else 0

    override val color: Long
        get() = 0

    override fun toString(): String {
        var temp = "["
        for (c in colors) {
            temp += c base 16
            temp += ", "
        }
        temp = temp.removeSuffix(", ")
        temp += "]"
        return temp
    }

    operator fun contains(value: Long): Boolean = colors.contains(value)

    override fun prepare(numLEDs: Int, leadingZeros: Int): PreparedColorContainer = this

    val size: Int
        get() = colors.size

}