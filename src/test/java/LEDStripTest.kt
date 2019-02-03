package animatedledstrip.test

import animatedledstrip.leds.*
import org.junit.Test
import org.pmw.tinylog.Configurator
import org.pmw.tinylog.Level
import kotlin.test.assertTrue

class LEDStripTest {

    init {
        Configurator.defaultConfig().level(Level.OFF).activate()
    }

    @Test
    fun testSetPixelColor() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        assertTrue { testLEDs.getPixelColor(10) == ColorContainer(0) }

        testLEDs.setPixelColor(10, ColorContainer(0xFF))
        assertTrue { testLEDs.getPixelColor(10) == ColorContainer(0xFF) }

        testLEDs.setPixelColor(10, 0, 255, 0)
        assertTrue { testLEDs.getPixelColor(10) == ColorContainer(0xFF00) }

        testLEDs.setPixelColor(10, 0xFF0000)
        assertTrue { testLEDs.getPixelColor(10) == ColorContainer(0xFF0000) }


    }

    @Test
    fun testGetPixelColor() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)
        testLEDs.setPixelColor(15, 0xFF)

        assertTrue { testLEDs.getPixelColor(15) == ColorContainer(0xFF) }
        assertTrue { testLEDs.getPixelLong(15) == 0xFFL }
        assertTrue { testLEDs.getPixelHexString(15) == "ff" }
    }

    @Test
    fun testToggleRender() {
//        val testLEDs = EmulatedAnimatedLEDStrip(50)

//        assertTrue { testLEDs.rendering }
//        testLEDs.toggleRender()
//        Logger.info("Test1")
//        delayBlocking(1000)
//        assertFalse { testLEDs.rendering }
//        Logger.info("Test2")
//        testLEDs.toggleRender()
//        Logger.info("Test3")
//        delayBlocking(1000)
//        assertTrue { testLEDs.rendering }
    }

    @Test
    fun testSectionCreator() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        val testSection = LEDStrip.SectionCreator.new(15, 40, testLEDs)

        assertTrue { testLEDs.getPixelColor(0) == ColorContainer(0) }
        assertTrue { testLEDs.getPixelColor(15) == ColorContainer(0) }
        assertTrue { testLEDs.getPixelColor(30) == ColorContainer(0) }
        assertTrue { testLEDs.getPixelColor(40) == ColorContainer(0) }
        assertTrue { testLEDs.getPixelColor(45) == ColorContainer(0) }

        testSection.run(AnimationData().animation(Animation.COLOR).color(0xFF))

        assertTrue { testLEDs.getPixelColor(0) == ColorContainer(0) }
        assertTrue { testLEDs.getPixelColor(14) == ColorContainer(0) }
        assertTrue { testLEDs.getPixelColor(15) == ColorContainer(0xFF) }
        assertTrue { testLEDs.getPixelColor(30) == ColorContainer(0xFF) }
        assertTrue { testLEDs.getPixelColor(40) == ColorContainer(0xFF) }
        assertTrue { testLEDs.getPixelColor(41) == ColorContainer(0) }
        assertTrue { testLEDs.getPixelColor(45) == ColorContainer(0) }
    }
}