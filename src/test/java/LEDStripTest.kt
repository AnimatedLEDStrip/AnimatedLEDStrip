package animatedledstrip.test

import animatedledstrip.leds.*
import org.junit.Test
import org.pmw.tinylog.Configurator
import org.pmw.tinylog.Level
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LEDStripTest {

    init {
        Configurator.defaultConfig().level(Level.OFF).activate()
    }

    @Test
    fun testSetPixelColor() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        assertTrue { testLEDs[10] == ColorContainer(0) }
        assertTrue { testLEDs[11] == ColorContainer(0) }
        assertTrue { testLEDs[12] == ColorContainer(0) }

        testLEDs.setPixelColor(10, ColorContainer(0xFF))
        assertTrue { testLEDs[10] == ColorContainer(0xFF) }

        testLEDs.setPixelColor(10, 0, 255, 0)
        assertTrue { testLEDs[10] == ColorContainer(0xFF00) }

        testLEDs.setPixelColor(10, 0xFF0000)
        assertTrue { testLEDs[10] == ColorContainer(0xFF0000) }

        testLEDs[10] = ColorContainer(0xFF)
        assertTrue { testLEDs[10] == ColorContainer(0xFF) }

        testLEDs[10, 11, 12] = ColorContainer(0xFFFF)
        assertTrue { testLEDs[10] == ColorContainer(0xFFFF) }
        assertTrue { testLEDs[11] == ColorContainer(0xFFFF) }
        assertTrue { testLEDs[12] == ColorContainer(0xFFFF) }

        testLEDs[10..12] = ColorContainer(0xFF00FF)
        assertTrue { testLEDs[10] == ColorContainer(0xFF00FF) }
        assertTrue { testLEDs[11] == ColorContainer(0xFF00FF) }
        assertTrue { testLEDs[12] == ColorContainer(0xFF00FF) }

        testLEDs[10, 11, 12] = 0xFF00
        assertTrue { testLEDs[10] == ColorContainer(0xFF00) }
        assertTrue { testLEDs[11] == ColorContainer(0xFF00) }
        assertTrue { testLEDs[12] == ColorContainer(0xFF00) }

        testLEDs[10..12] = 0xFF0000
        assertTrue { testLEDs[10] == ColorContainer(0xFF0000) }
        assertTrue { testLEDs[11] == ColorContainer(0xFF0000) }
        assertTrue { testLEDs[12] == ColorContainer(0xFF0000) }
    }

    @Test
    fun testSetSectionColor() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        checkAllPixels(testLEDs, 0)

        testLEDs.setSectionColor(15, 30, ColorContainer(0xFF))
        assertTrue { testLEDs[0] == ColorContainer(0) }
        assertTrue { testLEDs[14] == ColorContainer(0) }
        assertTrue { testLEDs[15] == ColorContainer(0xFF) }
        assertTrue { testLEDs[30] == ColorContainer(0xFF) }
        assertTrue { testLEDs[31] == ColorContainer(0) }
        assertTrue { testLEDs[45] == ColorContainer(0) }

        testLEDs.setSectionColor(10, 20, 0xFFFF)
        assertTrue { testLEDs[0] == ColorContainer(0) }
        assertTrue { testLEDs[9] == ColorContainer(0) }
        assertTrue { testLEDs[10] == ColorContainer(0xFFFF) }
        assertTrue { testLEDs[20] == ColorContainer(0xFFFF) }
        assertTrue { testLEDs[21] == ColorContainer(0xFF) }
        assertTrue { testLEDs[30] == ColorContainer(0xFF) }
        assertTrue { testLEDs[31] == ColorContainer(0) }
        assertTrue { testLEDs[45] == ColorContainer(0) }

        testLEDs.setSectionColor(25, 40, 255, 0, 0)
        assertTrue { testLEDs[0] == ColorContainer(0) }
        assertTrue { testLEDs[9] == ColorContainer(0) }
        assertTrue { testLEDs[10] == ColorContainer(0xFFFF) }
        assertTrue { testLEDs[20] == ColorContainer(0xFFFF) }
        assertTrue { testLEDs[21] == ColorContainer(0xFF) }
        assertTrue { testLEDs[24] == ColorContainer(0xFF) }
        assertTrue { testLEDs[25] == ColorContainer(0xFF0000) }
        assertTrue { testLEDs[40] == ColorContainer(0xFF0000) }
        assertTrue { testLEDs[41] == ColorContainer(0) }
        assertTrue { testLEDs[45] == ColorContainer(0) }
    }

    @Test
    fun testSetStripColor() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        checkAllPixels(testLEDs, 0)

        testLEDs.setStripColor(ColorContainer(0xFF))
        checkAllPixels(testLEDs, 0xFF)

        testLEDs.setStripColor(0xFFFF)
        checkAllPixels(testLEDs, 0xFFFF)

        testLEDs.setStripColor(255, 0, 0)
        checkAllPixels(testLEDs, 0xFF0000)

        testLEDs.color = 0xFF
        checkAllPixels(testLEDs, 0xFF)

        testLEDs.color = 0xFFFFL
        checkAllPixels(testLEDs, 0xFFFF)

        testLEDs.color = ColorContainer(0xFF0000)
        checkAllPixels(testLEDs, 0xFF0000)

        assertFailsWith(Exception::class) {
            testLEDs.color
        }

        assertFailsWith(Exception::class) {
            testLEDs.color = 1.0
        }
    }

    @Test
    fun testGetPixelColor() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)
        testLEDs.setPixelColor(15, 0xFF)

        assertTrue { testLEDs.getPixelColor(15) == ColorContainer(0xFF) }
        assertTrue { testLEDs.getPixelLong(15) == 0xFFL }
        assertTrue { testLEDs.getPixelHexString(15) == "ff" }
        assertTrue { testLEDs[15] == ColorContainer(0xFF) }
    }

    @Test
    fun testSectionCreator() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        val testSection = LEDStrip.SectionCreator.new(15, 40, testLEDs)

        assertTrue { testLEDs[0] == ColorContainer(0) }
        assertTrue { testLEDs[15] == ColorContainer(0) }
        assertTrue { testLEDs[30] == ColorContainer(0) }
        assertTrue { testLEDs[40] == ColorContainer(0) }
        assertTrue { testLEDs[45] == ColorContainer(0) }

        testSection.run(AnimationData().animation(Animation.COLOR).color(0xFF))

        assertTrue { testLEDs[0] == ColorContainer(0) }
        assertTrue { testLEDs[14] == ColorContainer(0) }
        assertTrue { testLEDs[15] == ColorContainer(0xFF) }
        assertTrue { testLEDs[30] == ColorContainer(0xFF) }
        assertTrue { testLEDs[40] == ColorContainer(0xFF) }
        assertTrue { testLEDs[41] == ColorContainer(0) }
        assertTrue { testLEDs[45] == ColorContainer(0) }

        testLEDs.setStripColor(0)

        val testSection2 = LEDStrip.SectionCreator.new(10..25, testLEDs)

        assertTrue { testLEDs[0] == ColorContainer(0) }
        assertTrue { testLEDs[10] == ColorContainer(0) }
        assertTrue { testLEDs[20] == ColorContainer(0) }
        assertTrue { testLEDs[25] == ColorContainer(0) }
        assertTrue { testLEDs[45] == ColorContainer(0) }

        testSection2.run(AnimationData().animation(Animation.COLOR).color(0xFF))

        assertTrue { testLEDs[0] == ColorContainer(0) }
        assertTrue { testLEDs[9] == ColorContainer(0) }
        assertTrue { testLEDs[10] == ColorContainer(0xFF) }
        assertTrue { testLEDs[15] == ColorContainer(0xFF) }
        assertTrue { testLEDs[25] == ColorContainer(0xFF) }
        assertTrue { testLEDs[26] == ColorContainer(0) }
        assertTrue { testLEDs[45] == ColorContainer(0) }
    }

    @Test
    fun testToggleRender() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        delayBlocking(1000)
        assertTrue { testLEDs.rendering }
        testLEDs.toggleRender()
        delayBlocking(1000)
        assertFalse { testLEDs.rendering }
        testLEDs.toggleRender()
        delayBlocking(1000)
        assertTrue { testLEDs.rendering }
    }
}