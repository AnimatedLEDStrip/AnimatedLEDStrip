package animatedledstrip.leds.colormanagement

import animatedledstrip.colors.ColorContainer
import animatedledstrip.colors.ColorContainerInterface
import animatedledstrip.colors.PreparedColorContainer
import animatedledstrip.leds.stripmanagement.LEDStrip

class LEDStripColorManager(val stripManager: LEDStrip) {

    val pixelColors: List<PixelColor> = mutableListOf<PixelColor>().apply {
        for (i in IntRange(0, stripManager.numLEDs - 1)) add(PixelColor(i))
    }.toList()

    fun setPixelColor(pixel: Int, color: ColorContainerInterface, colorType: PixelColorType) {
        when (color) {
            is ColorContainer -> error("")
            is PreparedColorContainer -> setPixelColor(pixel, color[pixel], colorType)
        }
    }

    fun setPixelColor(pixel: Int, color: Int, colorType: PixelColorType) {
//        require(pixel in ledStripManager.validIndices) { "$pixel not in indices (${ledStripManager.validIndices.first()}..${ledStripManager.validIndices.last()})" }
        pixelColors[pixel].setColor(color, colorType)
    }

    fun revertPixel(pixel: Int) {
        pixelColors[pixel].revertColor()
    }

    fun getPixelColor(pixel: Int, colorType: PixelColorType): Int =
        pixelColors[pixel].getColor(colorType)
}
