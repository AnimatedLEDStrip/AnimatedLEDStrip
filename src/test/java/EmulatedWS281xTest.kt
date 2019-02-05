package animatedledstrip.test

import animatedledstrip.leds.EmulatedAnimatedLEDStrip
import org.junit.Test
import org.pmw.tinylog.Configurator
import org.pmw.tinylog.Level
import java.lang.IllegalArgumentException
import kotlin.test.assertFailsWith

class EmulatedWS281xTest {

    init {
        Configurator.defaultConfig().level(Level.OFF).activate()
    }

    @Test
    fun testClose() {
        EmulatedAnimatedLEDStrip(50).ledStrip.close()
    }

    @Test
    fun testValidatePixel() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        assertFailsWith(IllegalArgumentException::class){
            testLEDs.ledStrip.getPixelColor(50)
        }
        assertFailsWith(IllegalArgumentException::class){
            testLEDs.ledStrip.getPixelColor(-1)
        }
    }

}