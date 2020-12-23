package animatedledstrip.leds

import animatedledstrip.utils.blend

data class PixelColor(val pixelNumber: Int) {
    var actualColor: Int = 0
        private set
    var fadeColor: Int = -1
        private set
    var prolongedColor: Int = 0
        private set
    var temporaryColor: Int = -1
        private set

    fun setColor(color: Int, colorType: PixelColorType) {
        when (colorType) {
            PixelColorType.ACTUAL -> throw IllegalArgumentException("Cannot set actual color directly")
            PixelColorType.FADE -> fadeColor = color
            PixelColorType.PROLONGED -> prolongedColor = color
            PixelColorType.TEMPORARY -> temporaryColor = color
        }
    }

    fun getColor(colorType: PixelColorType): Int {
        return when (colorType) {
            PixelColorType.ACTUAL -> actualColor
            PixelColorType.FADE -> fadeColor
            PixelColorType.PROLONGED -> prolongedColor
            PixelColorType.TEMPORARY -> temporaryColor
        }
    }

    fun revertColor() {
        temporaryColor = -1
    }

    fun sendColorToStrip(ledStrip: NativeLEDStrip, doFade: Boolean) {
        val colorToSet: Int = when {
            temporaryColor != -1 -> temporaryColor
            fadeColor != -1 -> fadeColor
            else -> prolongedColor
        }

        ledStrip.setPixelColor(pixelNumber, colorToSet)
        actualColor = colorToSet

        if (doFade && fadeColor != -1) {
            fadeColor = blend(fadeColor, prolongedColor, 25)
            if (fadeColor == prolongedColor) fadeColor = -1
        }
    }
}
