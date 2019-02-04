package animatedledstrip.test

import animatedledstrip.leds.EmulatedAnimatedLEDStrip
import kotlin.test.assertTrue

fun checkAllPixels(testLEDs: EmulatedAnimatedLEDStrip, color: Long) {
    testLEDs.pixelColorList.forEach {
        assertTrue { it == color }
    }
}