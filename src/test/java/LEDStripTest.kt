/*
 *  Copyright (c) 2018-2020 AnimatedLEDStrip
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

package animatedledstrip.test

import animatedledstrip.colors.ColorContainer
import animatedledstrip.colors.ccpresets.CCBlack
import animatedledstrip.leds.*
import animatedledstrip.leds.emulated.EmulatedAnimatedLEDStrip
import animatedledstrip.utils.delayBlocking
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

//    @Test
//    fun testSetSectionColor() {
//        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip
//
//        checkAllPixels(testLEDs, 0)
//        checkAllProlongedPixels(testLEDs, 0)
//
//        // Temporary
//
//        // setTemporarySectionColor with ColorContainer
//        testLEDs.setTemporarySectionColor(15, 30, ColorContainer(0xFF))
//
//        assertTrue { testLEDs.getTemporaryPixelColor(0) == 0L }
//        assertTrue { testLEDs.getTemporaryPixelColor(14) == 0L }
//        assertTrue { testLEDs.getTemporaryPixelColor(15) == 0xFFL }
//        assertTrue { testLEDs.getTemporaryPixelColor(30) == 0xFFL }
//        assertTrue { testLEDs.getTemporaryPixelColor(31) == 0L }
//        assertTrue { testLEDs.getTemporaryPixelColor(45) == 0L }
//        assertTrue { testLEDs.getProlongedPixelColor(0) == 0L }
//        assertTrue { testLEDs.getProlongedPixelColor(14) == 0L }
//        assertTrue { testLEDs.getProlongedPixelColor(15) == 0L }
//        assertTrue { testLEDs.getProlongedPixelColor(30) == 0L }
//        assertTrue { testLEDs.getProlongedPixelColor(31) == 0L }
//        assertTrue { testLEDs.getProlongedPixelColor(45) == 0L }
//
//
//        // setTemporarySectionColor with Long
//        testLEDs.setTemporarySectionColor(15, 30, 0xFFFF)
//
//        assertTrue { testLEDs.getTemporaryPixelColor(0) == 0L }
//        assertTrue { testLEDs.getTemporaryPixelColor(14) == 0L }
//        assertTrue { testLEDs.getTemporaryPixelColor(15) == 0xFFFFL }
//        assertTrue { testLEDs.getTemporaryPixelColor(30) == 0xFFFFL }
//        assertTrue { testLEDs.getTemporaryPixelColor(31) == 0L }
//        assertTrue { testLEDs.getTemporaryPixelColor(45) == 0L }
//        assertTrue { testLEDs.getProlongedPixelColor(0) == 0L }
//        assertTrue { testLEDs.getProlongedPixelColor(14) == 0L }
//        assertTrue { testLEDs.getProlongedPixelColor(15) == 0L }
//        assertTrue { testLEDs.getProlongedPixelColor(30) == 0L }
//        assertTrue { testLEDs.getProlongedPixelColor(31) == 0L }
//        assertTrue { testLEDs.getProlongedPixelColor(45) == 0L }
//
//
//        // Prolonged
//
//        // setProlongedSectionColor with ColorContainer
//        testLEDs.setProlongedSectionColor(15, 30, ColorContainer(0xFF))
//
//        assertTrue { testLEDs.getTemporaryPixelColor(0) == 0L }
//        assertTrue { testLEDs.getTemporaryPixelColor(14) == 0L }
//        assertTrue { testLEDs.getTemporaryPixelColor(15) == 0xFFL }
//        assertTrue { testLEDs.getTemporaryPixelColor(30) == 0xFFL }
//        assertTrue { testLEDs.getTemporaryPixelColor(31) == 0L }
//        assertTrue { testLEDs.getTemporaryPixelColor(45) == 0L }
//        assertTrue { testLEDs.getProlongedPixelColor(0) == 0L }
//        assertTrue { testLEDs.getProlongedPixelColor(14) == 0L }
//        assertTrue { testLEDs.getProlongedPixelColor(15) == 0xFFL }
//        assertTrue { testLEDs.getProlongedPixelColor(30) == 0xFFL }
//        assertTrue { testLEDs.getProlongedPixelColor(31) == 0L }
//        assertTrue { testLEDs.getProlongedPixelColor(45) == 0L }
//
//
//        // setProlongedSectionColor with Long
//        testLEDs.setProlongedSectionColor(15, 30, 0xFFFF)
//
//        assertTrue { testLEDs.getTemporaryPixelColor(0) == 0L }
//        assertTrue { testLEDs.getTemporaryPixelColor(14) == 0L }
//        assertTrue { testLEDs.getTemporaryPixelColor(15) == 0xFFFFL }
//        assertTrue { testLEDs.getTemporaryPixelColor(30) == 0xFFFFL }
//        assertTrue { testLEDs.getTemporaryPixelColor(31) == 0L }
//        assertTrue { testLEDs.getTemporaryPixelColor(45) == 0L }
//        assertTrue { testLEDs.getProlongedPixelColor(0) == 0L }
//        assertTrue { testLEDs.getProlongedPixelColor(14) == 0L }
//        assertTrue { testLEDs.getProlongedPixelColor(15) == 0xFFFFL }
//        assertTrue { testLEDs.getProlongedPixelColor(30) == 0xFFFFL }
//        assertTrue { testLEDs.getProlongedPixelColor(31) == 0L }
//        assertTrue { testLEDs.getProlongedPixelColor(45) == 0L }
//
//
//        // Test bad pixel ranges
//        assertFailsWith<IllegalArgumentException> { testLEDs.setTemporarySectionColor(20, 15, 0xFF) }
//        assertFailsWith<IllegalArgumentException> { testLEDs.setTemporarySectionColor(20, 15, ColorContainer(0xFF)) }
//        assertFailsWith<IllegalArgumentException> { testLEDs.setTemporarySectionColor(-1..10, ColorContainer(0xFF)) }
//        assertFailsWith<IllegalArgumentException> { testLEDs.setTemporarySectionColor(40, 50, ColorContainer(0xFF)) }
//        assertFailsWith<IllegalArgumentException> { testLEDs.setTemporarySectionColor(-1..10, 0xFF) }
//        assertFailsWith<IllegalArgumentException> { testLEDs.setTemporarySectionColor(40, 50, 0xFF) }
//
//        assertFailsWith<IllegalArgumentException> { testLEDs.setProlongedSectionColor(20, 15, 0xFF) }
//        assertFailsWith<IllegalArgumentException> { testLEDs.setProlongedSectionColor(20, 15, ColorContainer(0xFF)) }
//        assertFailsWith<IllegalArgumentException> { testLEDs.setProlongedSectionColor(-1..10, ColorContainer(0xFF)) }
//        assertFailsWith<IllegalArgumentException> { testLEDs.setProlongedSectionColor(40, 50, ColorContainer(0xFF)) }
//        assertFailsWith<IllegalArgumentException> { testLEDs.setProlongedSectionColor(-1..10, 0xFF) }
//        assertFailsWith<IllegalArgumentException> { testLEDs.setProlongedSectionColor(40, 50, 0xFF) }
//
//        testLEDs.setProlongedStripColor(0)
//
//        checkAllPixels(testLEDs, 0)
//        checkAllProlongedPixels(testLEDs, 0)
//
//        // Test with empty IntRange
//        testLEDs.setTemporarySectionColor(IntRange(20, 15), ColorContainer(0xFF))
//        checkAllPixels(testLEDs, 0)
//        checkAllProlongedPixels(testLEDs, 0)
//
//        testLEDs.setTemporarySectionColor(IntRange(20, 15), 0xFF)
//        checkAllPixels(testLEDs, 0)
//        checkAllProlongedPixels(testLEDs, 0)
//
//        testLEDs.setProlongedSectionColor(IntRange(20, 15), ColorContainer(0xFF))
//        checkAllPixels(testLEDs, 0)
//        checkAllProlongedPixels(testLEDs, 0)
//
//        testLEDs.setProlongedSectionColor(IntRange(20, 15), 0xFF)
//        checkAllPixels(testLEDs, 0)
//        checkAllProlongedPixels(testLEDs, 0)
//
//
//    }

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

//    @Test
//    fun testSectionCreator() = runBlocking {
//        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip
//
//        val testSection = LEDStrip.SectionCreator.new(15, 40, testLEDs)
//
//        checkAllPixels(testLEDs, 0)
//        checkAllProlongedPixels(testLEDs, 0)
//
//        testSection.addAnimation(AnimationData().animation("Color").color(0xFF))
//
//        assertTrue { testLEDs.getTemporaryPixelColor(0) == 0L }
//        assertTrue { testLEDs.getTemporaryPixelColor(14) == 0L }
//        assertTrue { testLEDs.getTemporaryPixelColor(15) == 0xFFL }
//        assertTrue { testLEDs.getTemporaryPixelColor(30) == 0xFFL }
//        assertTrue { testLEDs.getTemporaryPixelColor(40) == 0xFFL }
//        assertTrue { testLEDs.getTemporaryPixelColor(41) == 0L }
//        assertTrue { testLEDs.getTemporaryPixelColor(45) == 0L }
//        assertTrue { testLEDs.getProlongedPixelColor(0) == 0L }
//        assertTrue { testLEDs.getProlongedPixelColor(14) == 0L }
//        assertTrue { testLEDs.getProlongedPixelColor(15) == 0xFFL }
//        assertTrue { testLEDs.getProlongedPixelColor(30) == 0xFFL }
//        assertTrue { testLEDs.getProlongedPixelColor(40) == 0xFFL }
//        assertTrue { testLEDs.getProlongedPixelColor(41) == 0L }
//        assertTrue { testLEDs.getProlongedPixelColor(45) == 0L }
//
//        testLEDs.setProlongedStripColor(0)
//
//        val testSection2 = LEDStrip.SectionCreator.new(10..25, testLEDs)
//
//        checkAllPixels(testLEDs, 0)
//        checkAllProlongedPixels(testLEDs, 0)
//
//        testSection2.addAnimation(AnimationData().animation("Color").color(0xFF))
//
//        assertTrue { testLEDs.getTemporaryPixelColor(0) == 0L }
//        assertTrue { testLEDs.getTemporaryPixelColor(9) == 0L }
//        assertTrue { testLEDs.getTemporaryPixelColor(10) == 0xFFL }
//        assertTrue { testLEDs.getTemporaryPixelColor(20) == 0xFFL }
//        assertTrue { testLEDs.getTemporaryPixelColor(25) == 0xFFL }
//        assertTrue { testLEDs.getTemporaryPixelColor(26) == 0L }
//        assertTrue { testLEDs.getTemporaryPixelColor(45) == 0L }
//        assertTrue { testLEDs.getProlongedPixelColor(0) == 0L }
//        assertTrue { testLEDs.getProlongedPixelColor(9) == 0L }
//        assertTrue { testLEDs.getProlongedPixelColor(10) == 0xFFL }
//        assertTrue { testLEDs.getProlongedPixelColor(20) == 0xFFL }
//        assertTrue { testLEDs.getProlongedPixelColor(25) == 0xFFL }
//        assertTrue { testLEDs.getProlongedPixelColor(26) == 0L }
//        assertTrue { testLEDs.getProlongedPixelColor(45) == 0L }
//    }

//    @Test
//    fun testSectionRun() = runBlocking {
//        val testLEDs = EmulatedAnimatedLEDStrip(50)
//        val testSection = LEDStrip.SectionCreator.new(10..25, testLEDs)
//
//        testSection.addAnimation(AnimationData().animation(Animation.COLOR).color(0xFF))
//
//        assertTrue { testLEDs[0] == 0L }
//        assertTrue { testLEDs[9] == 0L }
//        assertTrue { testLEDs[10] == 0xFFL }
//        assertTrue { testLEDs[15] == 0xFFL }
//        assertTrue { testLEDs[25] == 0xFFL }
//        assertTrue { testLEDs[26] == 0L }
//        assertTrue { testLEDs[45] == 0L }
//
//        testSection.addAnimation(AnimationData().animation(Animation.STACK).color(0xFFFF))?.join()
//
//        assertTrue { testLEDs[0] == 0L }
//        assertTrue { testLEDs[9] == 0L }
//        assertTrue { testLEDs[10] == 0xFFFFL }
//        assertTrue { testLEDs[15] == 0xFFFFL }
//        assertTrue { testLEDs[25] == 0xFFFFL }
//        assertTrue { testLEDs[26] == 0L }
//        assertTrue { testLEDs[45] == 0L }
//
//        testSection.addAnimation(AnimationData().animation(Animation.WIPE).color(0xFF00))?.join()
//
//        assertTrue { testLEDs[0] == 0L }
//        assertTrue { testLEDs[9] == 0L }
//        assertTrue { testLEDs[10] == 0xFF00L }
//        assertTrue { testLEDs[15] == 0xFF00L }
//        assertTrue { testLEDs[25] == 0xFF00L }
//        assertTrue { testLEDs[26] == 0L }
//        assertTrue { testLEDs[45] == 0L }
//
//        testSection.addAnimation(AnimationData().animation(Animation.BOUNCETOCOLOR).color(0xFF00FF))?.join()
//
//        assertTrue { testLEDs[0] == 0L }
//        assertTrue { testLEDs[9] == 0L }
//        assertTrue { testLEDs[10] == 0xFF00FFL }
//        assertTrue { testLEDs[15] == 0xFF00FFL }
//        assertTrue { testLEDs[25] == 0xFF00FFL }
//        assertTrue { testLEDs[26] == 0L }
//        assertTrue { testLEDs[45] == 0L }
//
//        testSection.addAnimation(AnimationData().animation(Animation.MULTIPIXELRUNTOCOLOR).color(0xFF0000))?.join()
//
//        assertTrue { testLEDs[0] == 0L }
//        assertTrue { testLEDs[9] == 0L }
//        assertTrue { testLEDs[10] == 0xFF0000L }
//        assertTrue { testLEDs[15] == 0xFF0000L }
//        assertTrue { testLEDs[25] == 0xFF0000L }
//        assertTrue { testLEDs[26] == 0L }
//        assertTrue { testLEDs[45] == 0L }
//
//        testSection.addAnimation(AnimationData().animation(Animation.SPARKLETOCOLOR).color(0xFFFFFF))?.join()
//
//        assertTrue { testLEDs[0] == 0L }
//        assertTrue { testLEDs[9] == 0L }
//        assertTrue { testLEDs[10] == 0xFFFFFFL }
//        assertTrue { testLEDs[15] == 0xFFFFFFL }
//        assertTrue { testLEDs[25] == 0xFFFFFFL }
//        assertTrue { testLEDs[26] == 0L }
//        assertTrue { testLEDs[45] == 0L }
//    }


//    @Test
//    fun testFadePixel() {
//        val testLEDs = EmulatedAnimatedLEDStrip(50)
//
//        testLEDs.fadePixel(50, CCBlue.color.toInt())
//    }

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
    fun testWithPixelLock() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        var testVal1 = false
        var testVal2 = true

        testLEDs.withPixelLock(10) { testVal1 = true; Unit }

        // Test trying to get a lock on a non-existent pixel
        testLEDs.withPixelLock(50) { testVal2 = false; Unit }

        assertTrue(testVal1)
        assertTrue(testVal2)
    }

    @Test
    fun testImageDebugging() {
        // Test file name
        val info1 = StripInfo(numLEDs = 50, imageDebugging = true, fileName = "test1.csv")
        val leds1 = EmulatedAnimatedLEDStrip(info1)
        delayBlocking(5000)
        leds1.toggleRender()
        delayBlocking(1000)
        assertTrue { Files.exists(Paths.get("test1.csv")) }
        Files.delete(Paths.get("test1.csv"))

        // Test file name without .csv extension
        val info2 = StripInfo(numLEDs = 50, imageDebugging = true, fileName = "test2")
        val leds2 = EmulatedAnimatedLEDStrip(info2)
        delayBlocking(5000)
        leds2.toggleRender()
        delayBlocking(1000)
        assertTrue { Files.exists(Paths.get("test2.csv")) }
        Files.delete(Paths.get("test2.csv"))

        // Test saving renders to file
        val info3 = StripInfo(numLEDs = 50, imageDebugging = true, fileName = "test3.csv", rendersBeforeSave = 100)
        val leds3 = EmulatedAnimatedLEDStrip(info3)
        delayBlocking(10000)
        leds3.toggleRender()
        delayBlocking(1000)
        Files.delete(Paths.get("test3.csv"))
    }
}