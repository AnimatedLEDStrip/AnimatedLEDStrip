package animatedledstrip.leds.stripmanagement

import animatedledstrip.leds.colormanagement.LEDStripColorLogger
import animatedledstrip.leds.colormanagement.LEDStripColorManager

expect class LEDStripRenderer(
    ledStrip: NativeLEDStrip,
    stripColorManager: LEDStripColorManager,
) {


    var isRendering: Boolean

    val stripColorLogger: LEDStripColorLogger

    fun startRendering()

    fun stopRendering()
}