package animatedledstrip.test

import animatedledstrip.colors.ColorContainer
import animatedledstrip.colors.ccpresets.CCBlack
import animatedledstrip.colors.ccpresets.CCBlue
import animatedledstrip.leds.emulated.EmulatedAnimatedLEDStrip
import animatedledstrip.leds.getProlongedPixelColorOrNull
import animatedledstrip.leds.getTemporaryPixelColorOrNull
import animatedledstrip.leds.setProlongedPixelColors
import animatedledstrip.leds.setTemporaryPixelColors
import org.junit.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SectionTest {

    @Test
    fun testSetPixelColor() {
        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip

        checkAllPixels(testLEDs, 0)
        checkAllProlongedPixels(testLEDs, 0)

        // Temporary

        // setPixelColor with ColorContainer
        testLEDs.setTemporaryPixelColor(10, ColorContainer(0xFF))

        assertTrue { testLEDs.getTemporaryPixelColor(10) == 0xFFL }
        assertTrue { testLEDs.getProlongedPixelColor(10) == 0L }

        // setPixelColor with Long
        testLEDs.setTemporaryPixelColor(10, 0xFF0000)

        assertTrue { testLEDs.getTemporaryPixelColor(10) == 0xFF0000L }
        assertTrue { testLEDs.getProlongedPixelColor(10) == 0L }


        // Prolonged

        // setPixelColor with ColorContainer
        testLEDs.setProlongedPixelColor(10, ColorContainer(0xFF))

        assertTrue { testLEDs.getTemporaryPixelColor(10) == 0xFFL }
        assertTrue { testLEDs.getProlongedPixelColor(10) == 0xFFL }

        // setPixelColor with Long
        testLEDs.setProlongedPixelColor(10, 0xFF0000)

        assertTrue { testLEDs.getTemporaryPixelColor(10) == 0xFF0000L }
        assertTrue { testLEDs.getProlongedPixelColor(10) == 0xFF0000L }


        testLEDs.setProlongedPixelColors(listOf(10, 11, 12), 0L)   // reset pixels

        // Confirm successful reset
        checkPixels(10..12, testLEDs, 0)
        checkProlongedPixels(10..12, testLEDs, 0)

        // Test bad pixel index
        assertFailsWith<IllegalArgumentException> { testLEDs.setTemporaryPixelColor(50, CCBlack) }
        assertFailsWith<IllegalArgumentException> { testLEDs.setProlongedPixelColor(50, CCBlack) }
    }

    @Test
    fun testSetPixelColors() {
        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip

        checkAllPixels(testLEDs, 0)
        checkAllProlongedPixels(testLEDs, 0)

        // Temporary

        // setTemporaryPixelColors with ColorContainer
        testLEDs.setTemporaryPixelColors(listOf(10, 15, 20), ColorContainer(0xFF))

        assertTrue { testLEDs.getTemporaryPixelColor(10) == 0xFFL }
        assertTrue { testLEDs.getTemporaryPixelColor(15) == 0xFFL }
        assertTrue { testLEDs.getTemporaryPixelColor(20) == 0xFFL }
        assertTrue { testLEDs.getTemporaryPixelColor(18) == 0L }
        assertTrue { testLEDs.getProlongedPixelColor(10) == 0L }
        assertTrue { testLEDs.getProlongedPixelColor(15) == 0L }
        assertTrue { testLEDs.getProlongedPixelColor(20) == 0L }
        assertTrue { testLEDs.getProlongedPixelColor(18) == 0L }

        // setTemporaryPixelColors with Long
        testLEDs.setTemporaryPixelColors(listOf(12, 17, 24), 0xFF00)

        assertTrue { testLEDs.getTemporaryPixelColor(12) == 0xFF00L }
        assertTrue { testLEDs.getTemporaryPixelColor(17) == 0xFF00L }
        assertTrue { testLEDs.getTemporaryPixelColor(24) == 0xFF00L }
        assertTrue { testLEDs.getTemporaryPixelColor(21) == 0L }
        assertTrue { testLEDs.getProlongedPixelColor(12) == 0L }
        assertTrue { testLEDs.getProlongedPixelColor(17) == 0L }
        assertTrue { testLEDs.getProlongedPixelColor(24) == 0L }
        assertTrue { testLEDs.getProlongedPixelColor(21) == 0L }

        // setTemporaryPixelColors with IntRange and ColorContainer
        testLEDs.setTemporaryPixelColors(30..40, ColorContainer(0xFF))

        assertTrue { testLEDs.getTemporaryPixelColor(0) == 0L }
        assertTrue { testLEDs.getTemporaryPixelColor(29) == 0L }
        assertTrue { testLEDs.getTemporaryPixelColor(30) == 0xFFL }
        assertTrue { testLEDs.getTemporaryPixelColor(40) == 0xFFL }
        assertTrue { testLEDs.getTemporaryPixelColor(41) == 0L }
        assertTrue { testLEDs.getTemporaryPixelColor(45) == 0L }
        assertTrue { testLEDs.getProlongedPixelColor(0) == 0L }
        assertTrue { testLEDs.getProlongedPixelColor(29) == 0L }
        assertTrue { testLEDs.getProlongedPixelColor(30) == 0L }
        assertTrue { testLEDs.getProlongedPixelColor(40) == 0L }
        assertTrue { testLEDs.getProlongedPixelColor(41) == 0L }
        assertTrue { testLEDs.getProlongedPixelColor(45) == 0L }

        // setTemporaryPixelColors with IntRange and Long
        testLEDs.setTemporaryPixelColors(30..40, 0xFF00)

        assertTrue { testLEDs.getTemporaryPixelColor(0) == 0L }
        assertTrue { testLEDs.getTemporaryPixelColor(29) == 0L }
        assertTrue { testLEDs.getTemporaryPixelColor(30) == 0xFF00L }
        assertTrue { testLEDs.getTemporaryPixelColor(40) == 0xFF00L }
        assertTrue { testLEDs.getTemporaryPixelColor(41) == 0L }
        assertTrue { testLEDs.getTemporaryPixelColor(45) == 0L }
        assertTrue { testLEDs.getProlongedPixelColor(0) == 0L }
        assertTrue { testLEDs.getProlongedPixelColor(29) == 0L }
        assertTrue { testLEDs.getProlongedPixelColor(30) == 0L }
        assertTrue { testLEDs.getProlongedPixelColor(40) == 0L }
        assertTrue { testLEDs.getProlongedPixelColor(41) == 0L }
        assertTrue { testLEDs.getProlongedPixelColor(45) == 0L }


        // Prolonged

        // setProlongedPixelColors with ColorContainer
        testLEDs.setProlongedPixelColors(listOf(10, 15, 20), ColorContainer(0xFF))

        assertTrue { testLEDs.getTemporaryPixelColor(10) == 0xFFL }
        assertTrue { testLEDs.getTemporaryPixelColor(15) == 0xFFL }
        assertTrue { testLEDs.getTemporaryPixelColor(20) == 0xFFL }
        assertTrue { testLEDs.getTemporaryPixelColor(18) == 0L }
        assertTrue { testLEDs.getProlongedPixelColor(10) == 0xFFL }
        assertTrue { testLEDs.getProlongedPixelColor(15) == 0xFFL }
        assertTrue { testLEDs.getProlongedPixelColor(20) == 0xFFL }
        assertTrue { testLEDs.getProlongedPixelColor(18) == 0L }

        // setProlongedPixelColors with Long
        testLEDs.setProlongedPixelColors(listOf(12, 17, 24), 0xFF00)

        assertTrue { testLEDs.getTemporaryPixelColor(12) == 0xFF00L }
        assertTrue { testLEDs.getTemporaryPixelColor(17) == 0xFF00L }
        assertTrue { testLEDs.getTemporaryPixelColor(24) == 0xFF00L }
        assertTrue { testLEDs.getTemporaryPixelColor(21) == 0L }
        assertTrue { testLEDs.getProlongedPixelColor(12) == 0xFF00L }
        assertTrue { testLEDs.getProlongedPixelColor(17) == 0xFF00L }
        assertTrue { testLEDs.getProlongedPixelColor(24) == 0xFF00L }
        assertTrue { testLEDs.getProlongedPixelColor(21) == 0L }

        // setProlongedPixelColors with IntRange and ColorContainer
        testLEDs.setProlongedPixelColors(30..40, ColorContainer(0xFF))

        assertTrue { testLEDs.getTemporaryPixelColor(0) == 0L }
        assertTrue { testLEDs.getTemporaryPixelColor(29) == 0L }
        assertTrue { testLEDs.getTemporaryPixelColor(30) == 0xFFL }
        assertTrue { testLEDs.getTemporaryPixelColor(40) == 0xFFL }
        assertTrue { testLEDs.getTemporaryPixelColor(41) == 0L }
        assertTrue { testLEDs.getTemporaryPixelColor(45) == 0L }
        assertTrue { testLEDs.getProlongedPixelColor(0) == 0L }
        assertTrue { testLEDs.getProlongedPixelColor(29) == 0L }
        assertTrue { testLEDs.getProlongedPixelColor(30) == 0xFFL }
        assertTrue { testLEDs.getProlongedPixelColor(40) == 0xFFL }
        assertTrue { testLEDs.getProlongedPixelColor(41) == 0L }
        assertTrue { testLEDs.getProlongedPixelColor(45) == 0L }

        // setProlongedPixelColors with IntRange and Long
        testLEDs.setProlongedPixelColors(30..40, 0xFF00)

        assertTrue { testLEDs.getTemporaryPixelColor(0) == 0L }
        assertTrue { testLEDs.getTemporaryPixelColor(29) == 0L }
        assertTrue { testLEDs.getTemporaryPixelColor(30) == 0xFF00L }
        assertTrue { testLEDs.getTemporaryPixelColor(40) == 0xFF00L }
        assertTrue { testLEDs.getTemporaryPixelColor(41) == 0L }
        assertTrue { testLEDs.getTemporaryPixelColor(45) == 0L }
        assertTrue { testLEDs.getProlongedPixelColor(0) == 0L }
        assertTrue { testLEDs.getProlongedPixelColor(29) == 0L }
        assertTrue { testLEDs.getProlongedPixelColor(30) == 0xFF00L }
        assertTrue { testLEDs.getProlongedPixelColor(40) == 0xFF00L }
        assertTrue { testLEDs.getProlongedPixelColor(41) == 0L }
        assertTrue { testLEDs.getProlongedPixelColor(45) == 0L }
    }

    @Test
    fun testSetStripColor() {
        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip

        checkAllPixels(testLEDs, 0)
        checkAllProlongedPixels(testLEDs, 0)

        // Temporary

        // setTemporaryStripColor with ColorContainer
        testLEDs.setTemporaryStripColor(ColorContainer(0xFF))

        checkAllPixels(testLEDs, 0xFF)
        checkAllProlongedPixels(testLEDs, 0)


        // setTemporaryStripColor with Long
        testLEDs.setTemporaryStripColor(0xFFFF)

        checkAllPixels(testLEDs, 0xFFFF)
        checkAllProlongedPixels(testLEDs, 0)


        // Prolonged

        // setProlongedStripColor with ColorContainer
        testLEDs.setProlongedStripColor(ColorContainer(0xFF))

        checkAllPixels(testLEDs, 0xFF)
        checkAllProlongedPixels(testLEDs, 0xFFL)


        // setProlongedStripColor with Long
        testLEDs.setProlongedStripColor(0xFFFF)

        checkAllPixels(testLEDs, 0xFFFF)
        checkAllProlongedPixels(testLEDs, 0xFFFFL)
    }

    @Test
    fun testGetPixelColor() {
        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip

        testLEDs.setTemporaryPixelColor(15, 0xFF)

        assertTrue { testLEDs.getTemporaryPixelColor(15) == 0xFFL }
        assertTrue { testLEDs.getProlongedPixelColor(15) == 0L }

        testLEDs.setProlongedPixelColor(15, 0xFF00)

        assertTrue { testLEDs.getTemporaryPixelColor(15) == 0xFF00L }
        assertTrue { testLEDs.getProlongedPixelColor(15) == 0xFF00L }

        assertFailsWith<IllegalArgumentException> { testLEDs.getTemporaryPixelColor(50) }
        assertFailsWith<IllegalArgumentException> { testLEDs.getProlongedPixelColor(50) }
    }

    @Test
    fun testGetPixelColorOrNull() {
        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip

        testLEDs.setTemporaryPixelColor(15, 0xFF)

        assertTrue { testLEDs.getTemporaryPixelColorOrNull(15) == 0xFFL }
        assertTrue { testLEDs.getProlongedPixelColorOrNull(15) == 0L }


        testLEDs.setProlongedPixelColor(15, 0xFF)

        assertTrue { testLEDs.getTemporaryPixelColorOrNull(15) == 0xFFL }
        assertTrue { testLEDs.getProlongedPixelColorOrNull(15) == 0xFFL }

        assertNull(testLEDs.getTemporaryPixelColorOrNull(50))
        assertNull(testLEDs.getProlongedPixelColorOrNull(50))
    }

    @Test
    fun testFadePixel() {
        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip

        testLEDs.fadePixel(50, CCBlue.color.toInt())
    }

}