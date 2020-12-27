package animatedledstrip.leds.emulation

expect class EmulatedStripBuffer(numLEDs: Int) {
    fun getInt(pixel: Int): Int
    fun putInt(pixel: Int, color: Int)
}