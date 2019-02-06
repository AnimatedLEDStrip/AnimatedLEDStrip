package animatedledstrip.leds

interface ColorContainerInterface {
    val color: Long

    fun prepare(numLEDs: Int, leadingZeros: Int = 0): PreparedColorContainer
}