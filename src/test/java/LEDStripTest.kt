package animatedledstrip.test

/*
 *  Copyright (c) 2019 AnimatedLEDStrip
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */


import animatedledstrip.animationutils.Animation
import animatedledstrip.animationutils.AnimationData
import animatedledstrip.animationutils.animation
import animatedledstrip.animationutils.color
import animatedledstrip.colors.ColorContainer
import animatedledstrip.colors.ccpresets.CCBlack
import animatedledstrip.colors.ccpresets.CCBlue
import animatedledstrip.leds.*
import animatedledstrip.leds.emulated.EmulatedAnimatedLEDStrip
import animatedledstrip.leds.emulated.EmulatedAnimatedLEDStripNonConcurrent
import animatedledstrip.utils.delayBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LEDStripTest {

    @Test
    fun testSetPixelColor() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        assertTrue { testLEDs.getActualPixelColor(10) == 0L }
        assertTrue { testLEDs.getActualPixelColor(11) == 0L }
        assertTrue { testLEDs.getActualPixelColor(12) == 0L }

        testLEDs.setPixelColor(10, ColorContainer(0xFF))
        assertTrue { testLEDs.getActualPixelColor(10) == 0xFFL }

        testLEDs.setPixelColor(10, 0xFF0000)
        assertTrue { testLEDs.getActualPixelColor(10) == 0xFF0000L }

        testLEDs[10] = ColorContainer(0xFF)
        assertTrue { testLEDs.getActualPixelColor(10) == 0xFFL }

        testLEDs[10, 11, 12] = ColorContainer(0xFFFF)
        assertTrue { testLEDs.getActualPixelColor(10) == 0xFFFFL }
        assertTrue { testLEDs.getActualPixelColor(11) == 0xFFFFL }
        assertTrue { testLEDs.getActualPixelColor(12) == 0xFFFFL }

        testLEDs[10..12] = ColorContainer(0xFF00FF)
        assertTrue { testLEDs.getActualPixelColor(10) == 0xFF00FFL }
        assertTrue { testLEDs.getActualPixelColor(11) == 0xFF00FFL }
        assertTrue { testLEDs.getActualPixelColor(12) == 0xFF00FFL }

        testLEDs[10, 11, 12] = 0xFF00
        assertTrue { testLEDs.getActualPixelColor(10) == 0xFF00L }
        assertTrue { testLEDs.getActualPixelColor(11) == 0xFF00L }
        assertTrue { testLEDs.getActualPixelColor(12) == 0xFF00L }

        testLEDs[10..12] = 0xFF0000
        assertTrue { testLEDs.getActualPixelColor(10) == 0xFF0000L }
        assertTrue { testLEDs.getActualPixelColor(11) == 0xFF0000L }
        assertTrue { testLEDs.getActualPixelColor(12) == 0xFF0000L }

        testLEDs.setPixelColor(50, CCBlack)
    }

    @Test
    fun testSetPixelColors() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.setPixelColors(listOf(10, 15, 20), ColorContainer(0xFF))
        assertTrue { testLEDs.getActualPixelColor(10) == 0xFFL }
        assertTrue { testLEDs.getActualPixelColor(15) == 0xFFL }
        assertTrue { testLEDs.getActualPixelColor(20) == 0xFFL }
        assertTrue { testLEDs.getActualPixelColor(18) == 0L }

        testLEDs.setPixelColors(listOf(12, 17, 24), 0xFF00)
        assertTrue { testLEDs.getActualPixelColor(12) == 0xFF00L }
        assertTrue { testLEDs.getActualPixelColor(17) == 0xFF00L }
        assertTrue { testLEDs.getActualPixelColor(24) == 0xFF00L }
        assertTrue { testLEDs.getActualPixelColor(21) == 0L }
    }

    @Test
    fun testSetProlongedPixelColors() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.setProlongedPixelColors(listOf(10, 15, 20), ColorContainer(0xFF))
        assertTrue { testLEDs.getPixelColor(10) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(15) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(20) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(18) == 0L }

        testLEDs.setProlongedPixelColors(listOf(12, 17, 24), 0xFF00)
        assertTrue { testLEDs.getPixelColor(12) == 0xFF00L }
        assertTrue { testLEDs.getPixelColor(17) == 0xFF00L }
        assertTrue { testLEDs.getPixelColor(24) == 0xFF00L }
        assertTrue { testLEDs.getPixelColor(21) == 0L }
    }

    @Test
    fun testSetProlongedPixelColor() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        assertTrue { testLEDs[10] == 0L }
        assertTrue { testLEDs[11] == 0L }
        assertTrue { testLEDs[12] == 0L }

        testLEDs.setProlongedPixelColor(10, ColorContainer(0xFF))
        assertTrue { testLEDs[10] == 0xFFL }

        testLEDs.setProlongedPixelColor(10, 0xFF0000)
        assertTrue { testLEDs[10] == 0xFF0000L }
    }


    @Test
    fun testSetSectionColor() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        checkAllPixels(testLEDs, 0)

        testLEDs.setSectionColor(15, 30, ColorContainer(0xFF))
        assertTrue { testLEDs[0] == 0L }
        assertTrue { testLEDs[14] == 0L }
        assertTrue { testLEDs[15] == 0xFFL }
        assertTrue { testLEDs[30] == 0xFFL }
        assertTrue { testLEDs[31] == 0L }
        assertTrue { testLEDs[45] == 0L }

        testLEDs.setSectionColor(10, 20, 0xFFFF)
        assertTrue { testLEDs[0] == 0L }
        assertTrue { testLEDs[9] == 0L }
        assertTrue { testLEDs[10] == 0xFFFFL }
        assertTrue { testLEDs[20] == 0xFFFFL }
        assertTrue { testLEDs[21] == 0xFFL }
        assertTrue { testLEDs[30] == 0xFFL }
        assertTrue { testLEDs[31] == 0L }
        assertTrue { testLEDs[45] == 0L }
    }

    @Test
    fun testSetSectionColorNonConcurrent() {
        val testLEDs = EmulatedAnimatedLEDStripNonConcurrent(50)

        checkAllPixels(testLEDs, 0)

        testLEDs.setSectionColor(15, 30, ColorContainer(0xFF))
        assertTrue { testLEDs[0] == 0L }
        assertTrue { testLEDs[14] == 0L }
        assertTrue { testLEDs[15] == 0xFFL }
        assertTrue { testLEDs[30] == 0xFFL }
        assertTrue { testLEDs[31] == 0L }
        assertTrue { testLEDs[45] == 0L }

        testLEDs.setSectionColor(10, 20, 0xFFFF)
        assertTrue { testLEDs[0] == 0L }
        assertTrue { testLEDs[9] == 0L }
        assertTrue { testLEDs[10] == 0xFFFFL }
        assertTrue { testLEDs[20] == 0xFFFFL }
        assertTrue { testLEDs[21] == 0xFFL }
        assertTrue { testLEDs[30] == 0xFFL }
        assertTrue { testLEDs[31] == 0L }
        assertTrue { testLEDs[45] == 0L }
    }

    @Test
    fun testSetStripColor() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        checkAllPixels(testLEDs, 0)

        testLEDs.setStripColor(ColorContainer(0xFF))
        checkAllPixels(testLEDs, 0xFF)

        testLEDs.setStripColor(0xFFFF)
        checkAllPixels(testLEDs, 0xFFFF)

        testLEDs.color = ColorContainer(0xFF0000)
        checkAllPixels(testLEDs, 0xFF0000)

        assertFailsWith(IllegalStateException::class) {
            testLEDs.color
        }
    }

    @Test
    fun testSetStripColorNonConcurrent() {
        val testLEDs = EmulatedAnimatedLEDStripNonConcurrent(50)

        checkAllPixels(testLEDs, 0)

        testLEDs.setStripColor(ColorContainer(0xFF))
        checkAllPixels(testLEDs, 0xFF)

        testLEDs.setStripColor(0xFFFF)
        checkAllPixels(testLEDs, 0xFFFF)

        testLEDs.color = ColorContainer(0xFF0000)
        checkAllPixels(testLEDs, 0xFF0000)

        assertFailsWith(IllegalStateException::class) {
            testLEDs.color
        }
    }


    @Test
    fun testGetPixelColor() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)
        testLEDs.setProlongedPixelColor(15, 0xFF)

        assertTrue { testLEDs.getPixelColor(15) == 0xFFL }
        assertTrue { testLEDs.getPixelHexString(15) == "ff" }
        assertTrue { testLEDs[15] == 0xFFL }
        assertFailsWith<IllegalArgumentException> { testLEDs.getPixelColor(50) }
    }

    @Test
    fun testGetPixelColorOrNull() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)
        testLEDs.setProlongedPixelColor(15, 0xFF)

        assertTrue { testLEDs.getPixelColorOrNull(15) == 0xFFL }
        assertNull(testLEDs.getPixelColorOrNull(50))
    }

    @Test
    fun testGetPixelColorOrNullNonConcurrent() {
        val testLEDs = EmulatedAnimatedLEDStripNonConcurrent(50)
        testLEDs.setPixelColor(15, 0xFF)

        assertTrue { testLEDs.getPixelColorOrNull(15) == 0xFFL }
        assertNull(testLEDs.getPixelColorOrNull(50))
    }

    @Test
    fun testGetActualPixelColor() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)
        testLEDs.setPixelColor(15, 0xFF)

        assertTrue { testLEDs.getActualPixelColor(15) == 0xFFL }
        assertFailsWith<IllegalArgumentException> { testLEDs.getActualPixelColor(50) }
    }

    @Test
    fun testGetActualPixelColorOrNull() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)
        testLEDs.setPixelColor(15, 0xFF)

        assertTrue { testLEDs.getActualPixelColorOrNull(15) == 0xFFL }
        assertNull(testLEDs.getActualPixelColorOrNull(50))
    }

    @Test
    fun testSectionCreator() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        val testSection = LEDStrip.SectionCreator.new(15, 40, testLEDs)

        assertTrue { testLEDs[0] == 0L }
        assertTrue { testLEDs[15] == 0L }
        assertTrue { testLEDs[30] == 0L }
        assertTrue { testLEDs[40] == 0L }
        assertTrue { testLEDs[45] == 0L }

        testSection.run(AnimationData().animation(Animation.COLOR).color(0xFF))

        assertTrue { testLEDs[0] == 0L }
        assertTrue { testLEDs[14] == 0L }
        assertTrue { testLEDs[15] == 0xFFL }
        assertTrue { testLEDs[30] == 0xFFL }
        assertTrue { testLEDs[40] == 0xFFL }
        assertTrue { testLEDs[41] == 0L }
        assertTrue { testLEDs[45] == 0L }

        testLEDs.setStripColor(0)

        val testSection2 = LEDStrip.SectionCreator.new(10..25, testLEDs)

        assertTrue { testLEDs[0] == 0L }
        assertTrue { testLEDs[10] == 0L }
        assertTrue { testLEDs[20] == 0L }
        assertTrue { testLEDs[25] == 0L }
        assertTrue { testLEDs[45] == 0L }

        testSection2.run(AnimationData().animation(Animation.COLOR).color(0xFF))

        assertTrue { testLEDs[0] == 0L }
        assertTrue { testLEDs[9] == 0L }
        assertTrue { testLEDs[10] == 0xFFL }
        assertTrue { testLEDs[15] == 0xFFL }
        assertTrue { testLEDs[25] == 0xFFL }
        assertTrue { testLEDs[26] == 0L }
        assertTrue { testLEDs[45] == 0L }
    }

    @Test
    fun testSectionRun() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)
        val testSection = LEDStrip.SectionCreator.new(10..25, testLEDs)

        testSection.run(AnimationData().animation(Animation.COLOR).color(0xFF))

        assertTrue { testLEDs[0] == 0L }
        assertTrue { testLEDs[9] == 0L }
        assertTrue { testLEDs[10] == 0xFFL }
        assertTrue { testLEDs[15] == 0xFFL }
        assertTrue { testLEDs[25] == 0xFFL }
        assertTrue { testLEDs[26] == 0L }
        assertTrue { testLEDs[45] == 0L }

        testSection.run(AnimationData().animation(Animation.STACK).color(0xFFFF))

        assertTrue { testLEDs[0] == 0L }
        assertTrue { testLEDs[9] == 0L }
        assertTrue { testLEDs[10] == 0xFFFFL }
        assertTrue { testLEDs[15] == 0xFFFFL }
        assertTrue { testLEDs[25] == 0xFFFFL }
        assertTrue { testLEDs[26] == 0L }
        assertTrue { testLEDs[45] == 0L }

        testSection.run(AnimationData().animation(Animation.WIPE).color(0xFF00))

        assertTrue { testLEDs[0] == 0L }
        assertTrue { testLEDs[9] == 0L }
        assertTrue { testLEDs[10] == 0xFF00L }
        assertTrue { testLEDs[15] == 0xFF00L }
        assertTrue { testLEDs[25] == 0xFF00L }
        assertTrue { testLEDs[26] == 0L }
        assertTrue { testLEDs[45] == 0L }

        testSection.run(AnimationData().animation(Animation.BOUNCETOCOLOR).color(0xFF00FF))

        assertTrue { testLEDs[0] == 0L }
        assertTrue { testLEDs[9] == 0L }
        assertTrue { testLEDs[10] == 0xFF00FFL }
        assertTrue { testLEDs[15] == 0xFF00FFL }
        assertTrue { testLEDs[25] == 0xFF00FFL }
        assertTrue { testLEDs[26] == 0L }
        assertTrue { testLEDs[45] == 0L }

        testSection.run(AnimationData().animation(Animation.MULTIPIXELRUNTOCOLOR).color(0xFF0000))

        assertTrue { testLEDs[0] == 0L }
        assertTrue { testLEDs[9] == 0L }
        assertTrue { testLEDs[10] == 0xFF0000L }
        assertTrue { testLEDs[15] == 0xFF0000L }
        assertTrue { testLEDs[25] == 0xFF0000L }
        assertTrue { testLEDs[26] == 0L }
        assertTrue { testLEDs[45] == 0L }

        testSection.run(AnimationData().animation(Animation.SPARKLETOCOLOR).color(0xFFFFFF))

        assertTrue { testLEDs[0] == 0L }
        assertTrue { testLEDs[9] == 0L }
        assertTrue { testLEDs[10] == 0xFFFFFFL }
        assertTrue { testLEDs[15] == 0xFFFFFFL }
        assertTrue { testLEDs[25] == 0xFFFFFFL }
        assertTrue { testLEDs[26] == 0L }
        assertTrue { testLEDs[45] == 0L }
    }


    @Test
    fun testFadePixel() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.fadePixel(50, CCBlue.color.toInt())
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

    @Test
    fun testImageDebugging() {
        val leds = EmulatedAnimatedLEDStrip(50, true, fileName = "test.csv")
        runBlocking { delay(5000) }
        leds.toggleRender()
        runBlocking { delay(1000) }
        assertTrue { Files.exists(Paths.get("test.csv")) }
        Files.delete(Paths.get("test.csv"))
    }
}