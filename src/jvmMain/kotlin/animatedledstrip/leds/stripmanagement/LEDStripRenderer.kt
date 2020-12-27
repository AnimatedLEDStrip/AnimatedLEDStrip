package animatedledstrip.leds.stripmanagement

import animatedledstrip.leds.colormanagement.LEDStripColorLogger
import animatedledstrip.leds.colormanagement.LEDStripColorManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import org.pmw.tinylog.Logger

actual class LEDStripRenderer actual constructor(
    val ledStrip: NativeLEDStrip,
    val stripColorManager: LEDStripColorManager,
) {

    actual val stripColorLogger = LEDStripColorLogger(
        stripColorManager,
        500,
    )

    @Suppress("EXPERIMENTAL_API_USAGE")
    private val renderThread = newSingleThreadContext("Render Thread")

    actual var isRendering: Boolean = true

    actual fun startRendering() {
        isRendering = true
    }

    actual fun stopRendering() {
        isRendering = false
    }

    init {
        GlobalScope.launch(renderThread) {
            var renderNum = 0
            while (true) {
                if (isRendering)
                    try {
                        stripColorManager.pixelColors.forEach {
                            it.sendColorToStrip(ledStrip,
                                                doFade = renderNum % 6 == 0) // 6 iterations * 5 ms delay = 30 ms between fades
                        }
                        ledStrip.render()
                        stripColorLogger.saveStripState()
                        renderNum++
                        if (renderNum >= 60000000) renderNum = 0
                    } catch (e: NullPointerException) {
                        Logger.error("LEDStrip NullPointerException when rendering")
                        delay(1000)
                    }
                delay(5)
            }
        }
    }
}