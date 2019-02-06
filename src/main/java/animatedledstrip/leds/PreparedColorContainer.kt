package animatedledstrip.leds

class PreparedColorContainer(private val colors: List<Long>): ColorContainerInterface {

    operator fun get(index: Int) = if (colors.indices.contains(index)) colors[index] else 0

    override val color: Long
        get() = 0

    override fun toString(): String {
        var temp = ""
        for (i in 0 until colors.size) temp += "${colors[i].toString(16)} "

        return temp
    }



}