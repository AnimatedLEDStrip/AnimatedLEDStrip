package animatedledstrip.leds.emulated

import java.nio.ByteBuffer

actual class EmulatedStripBuffer actual constructor(val numLEDs: Int) {
    private val SIZE_OF_INT = 4
    private val ledArray: ByteBuffer = ByteBuffer.allocate(numLEDs * SIZE_OF_INT)

    private fun validatePixel(pixel: Int) {
        if (pixel < 0 || pixel >= numLEDs) {
            throw IllegalArgumentException("Pixel $pixel not in 0..${numLEDs - 1}")
        }
    }

    actual fun getInt(pixel: Int): Int {
        validatePixel(pixel)
        return ledArray.getInt(pixel * SIZE_OF_INT)
    }

    actual fun putInt(pixel: Int, color: Int) {
        validatePixel(pixel)
        ledArray.putInt(pixel * SIZE_OF_INT, color)
    }
}