package animatedledstrip.leds

interface LEDStripInterface {

    fun close()

    val numLEDs: Int

    fun render()

    fun getPixelColor(pixel: Int): Int

    fun setPixelColor(pixel: Int, color: Int): Unit

    fun setPixelColorRGB(pixel: Int, red: Int, green: Int, blue: Int) {
        setPixelColor(pixel, (red shl 16) or (green shl 8) or blue)
    }

}