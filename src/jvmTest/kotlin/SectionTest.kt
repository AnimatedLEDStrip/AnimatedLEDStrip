/*
 *  Copyright (c) 2020 AnimatedLEDStrip
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

import animatedledstrip.animationutils.*
import animatedledstrip.colors.ColorContainer
import animatedledstrip.colors.ccpresets.CCBlack
import animatedledstrip.colors.ccpresets.CCBlue
import animatedledstrip.leds.*
import animatedledstrip.leds.emulated.EmulatedAnimatedLEDStrip
import animatedledstrip.utils.delayBlocking
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SectionTest {

    @Test
    fun testSetPixelColor() {
        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip

        testLEDs.assertAllPixels(0)

        // Temporary

        // setPixelColor with ColorContainer
        testLEDs.setTemporaryPixelColor(10, ColorContainer(0xFF).prepare(testLEDs.numLEDs))

        assertTrue { testLEDs.getTemporaryPixelColor(10) == 0xFFL }
        assertTrue { testLEDs.getProlongedPixelColor(10) == 0L }

        // setPixelColor with Long
        testLEDs.setTemporaryPixelColor(10, 0xFF0000)

        assertTrue { testLEDs.getTemporaryPixelColor(10) == 0xFF0000L }
        assertTrue { testLEDs.getProlongedPixelColor(10) == 0L }


        // Prolonged

        // setPixelColor with ColorContainer
        testLEDs.setProlongedPixelColor(10, ColorContainer(0xFF).prepare(testLEDs.numLEDs))

        assertTrue { testLEDs.getTemporaryPixelColor(10) == 0xFFL }
        assertTrue { testLEDs.getProlongedPixelColor(10) == 0xFFL }

        // setPixelColor with Long
        testLEDs.setProlongedPixelColor(10, 0xFF0000)

        assertTrue { testLEDs.getTemporaryPixelColor(10) == 0xFF0000L }
        assertTrue { testLEDs.getProlongedPixelColor(10) == 0xFF0000L }


        testLEDs.setProlongedPixelColors(listOf(10, 11, 12), 0L)   // reset pixels

        // Confirm successful reset
        testLEDs.assertPixels(10..12, 0)

        // Test bad pixel index
        assertFailsWith<IllegalArgumentException> { testLEDs.setTemporaryPixelColor(50, CCBlack.prepare(testLEDs.numLEDs)) }
        assertFailsWith<IllegalArgumentException> { testLEDs.setProlongedPixelColor(50, CCBlack.prepare(testLEDs.numLEDs)) }
    }

    @Test
    fun testSetPixelColors() {
        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip

        testLEDs.assertAllPixels(0)

        // Temporary

        // setTemporaryPixelColors with ColorContainer
        testLEDs.setTemporaryPixelColors(listOf(10, 15, 20), ColorContainer(0xFF).prepare(testLEDs.numLEDs))

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
        testLEDs.setTemporaryPixelColors(30..40, ColorContainer(0xFF).prepare(testLEDs.numLEDs))

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
        testLEDs.setProlongedPixelColors(listOf(10, 15, 20), ColorContainer(0xFF).prepare(testLEDs.numLEDs))

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
        testLEDs.setProlongedPixelColors(30..40, ColorContainer(0xFF).prepare(testLEDs.numLEDs))

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
    fun testRevertPixel() {
        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip

        testLEDs.assertAllPixels(0)

        testLEDs.setTemporaryStripColor(0xFF)

        testLEDs.assertAllProlongedPixels(0)
        testLEDs.assertAllTemporaryPixels(0xFF)


        @Suppress("EmptyRange")
        testLEDs.revertPixels(5..2)

        testLEDs.assertAllProlongedPixels(0)
        testLEDs.assertAllTemporaryPixels(0xFF)


        testLEDs.revertPixel(40)

        assertTrue { testLEDs.getTemporaryPixelColor(0) == 0xFFL }
        assertTrue { testLEDs.getTemporaryPixelColor(30) == 0xFFL }
        assertTrue { testLEDs.getTemporaryPixelColor(39) == 0xFFL }
        assertTrue { testLEDs.getTemporaryPixelColor(40) == 0L }
        assertTrue { testLEDs.getTemporaryPixelColor(41) == 0xFFL }
        assertTrue { testLEDs.getTemporaryPixelColor(45) == 0xFFL }
        testLEDs.assertAllProlongedPixels(0)


        testLEDs.revertPixels(listOf(10, 15, 30))

        assertTrue { testLEDs.getTemporaryPixelColor(0) == 0xFFL }
        assertTrue { testLEDs.getTemporaryPixelColor(9) == 0xFFL }
        assertTrue { testLEDs.getTemporaryPixelColor(10) == 0L }
        assertTrue { testLEDs.getTemporaryPixelColor(11) == 0xFFL }
        assertTrue { testLEDs.getTemporaryPixelColor(14) == 0xFFL }
        assertTrue { testLEDs.getTemporaryPixelColor(15) == 0L }
        assertTrue { testLEDs.getTemporaryPixelColor(16) == 0xFFL }
        assertTrue { testLEDs.getTemporaryPixelColor(29) == 0xFFL }
        assertTrue { testLEDs.getTemporaryPixelColor(30) == 0L }
        assertTrue { testLEDs.getTemporaryPixelColor(31) == 0xFFL }
        assertTrue { testLEDs.getTemporaryPixelColor(39) == 0xFFL }
        assertTrue { testLEDs.getTemporaryPixelColor(40) == 0L }
        assertTrue { testLEDs.getTemporaryPixelColor(41) == 0xFFL }
        assertTrue { testLEDs.getTemporaryPixelColor(45) == 0xFFL }
        testLEDs.assertAllProlongedPixels(0)


        testLEDs.revertPixels(5..7)

        assertTrue { testLEDs.getTemporaryPixelColor(0) == 0xFFL }
        assertTrue { testLEDs.getTemporaryPixelColor(4) == 0xFFL }
        assertTrue { testLEDs.getTemporaryPixelColor(5) == 0L }
        assertTrue { testLEDs.getTemporaryPixelColor(6) == 0L }
        assertTrue { testLEDs.getTemporaryPixelColor(7) == 0L }
        assertTrue { testLEDs.getTemporaryPixelColor(8) == 0xFFL }
        assertTrue { testLEDs.getTemporaryPixelColor(9) == 0xFFL }
        assertTrue { testLEDs.getTemporaryPixelColor(10) == 0L }
        assertTrue { testLEDs.getTemporaryPixelColor(11) == 0xFFL }
        assertTrue { testLEDs.getTemporaryPixelColor(14) == 0xFFL }
        assertTrue { testLEDs.getTemporaryPixelColor(15) == 0L }
        assertTrue { testLEDs.getTemporaryPixelColor(16) == 0xFFL }
        assertTrue { testLEDs.getTemporaryPixelColor(29) == 0xFFL }
        assertTrue { testLEDs.getTemporaryPixelColor(30) == 0L }
        assertTrue { testLEDs.getTemporaryPixelColor(31) == 0xFFL }
        assertTrue { testLEDs.getTemporaryPixelColor(39) == 0xFFL }
        assertTrue { testLEDs.getTemporaryPixelColor(40) == 0L }
        assertTrue { testLEDs.getTemporaryPixelColor(41) == 0xFFL }
        assertTrue { testLEDs.getTemporaryPixelColor(45) == 0xFFL }
        testLEDs.assertAllProlongedPixels(0)

    }

    @Test
    fun testSetStripColor() {
        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip

        testLEDs.assertAllPixels(0)

        // Temporary

        // setTemporaryStripColor with ColorContainer
        testLEDs.setTemporaryStripColor(ColorContainer(0xFF))

        testLEDs.assertAllTemporaryPixels(0xFF)
        testLEDs.assertAllProlongedPixels(0)


        // setTemporaryStripColor with Long
        testLEDs.setTemporaryStripColor(0xFFFF)

        testLEDs.assertAllTemporaryPixels(0xFFFF)
        testLEDs.assertAllProlongedPixels(0)


        // Prolonged

        // setProlongedStripColor with ColorContainer
        testLEDs.setProlongedStripColor(ColorContainer(0xFF))

        testLEDs.assertAllPixels(0xFF)


        // setProlongedStripColor with Long
        testLEDs.setProlongedStripColor(0xFFFF)

        testLEDs.assertAllPixels(0xFFFF)

    }

    @Test
    fun testSetStripColorWithOffset() {
        val testLEDs = EmulatedAnimatedLEDStrip(5).wholeStrip

        testLEDs.setTemporaryStripColorWithOffset(ColorContainer(0xFF, 0x0, 0xFF00, 0xFF00FF, 0xFFFFFF).prepare(5), 2)

        assertTrue { testLEDs.getTemporaryPixelColor(0) == 0xFF00FFL }
        assertTrue { testLEDs.getTemporaryPixelColor(1) == 0xFFFFFFL }
        assertTrue { testLEDs.getTemporaryPixelColor(2) == 0xFFL }
        assertTrue { testLEDs.getTemporaryPixelColor(3) == 0L }
        assertTrue { testLEDs.getTemporaryPixelColor(4) == 0xFF00L }
        testLEDs.assertAllProlongedPixels(0)

        testLEDs.setProlongedStripColorWithOffset(ColorContainer(0xFF, 0x0, 0xFF00, 0xFF00FF, 0xFFFFFF).prepare(5), 3)
        assertTrue { testLEDs.getTemporaryPixelColor(0) == 0xFF00L }
        assertTrue { testLEDs.getTemporaryPixelColor(1) == 0xFF00FFL }
        assertTrue { testLEDs.getTemporaryPixelColor(2) == 0xFFFFFFL }
        assertTrue { testLEDs.getTemporaryPixelColor(3) == 0xFFL }
        assertTrue { testLEDs.getTemporaryPixelColor(4) == 0L }
        assertTrue { testLEDs.getProlongedPixelColor(0) == 0xFF00L }
        assertTrue { testLEDs.getProlongedPixelColor(1) == 0xFF00FFL }
        assertTrue { testLEDs.getProlongedPixelColor(2) == 0xFFFFFFL }
        assertTrue { testLEDs.getProlongedPixelColor(3) == 0xFFL }
        assertTrue { testLEDs.getProlongedPixelColor(4) == 0L }
    }

    @Test
    fun testClear() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.wholeStrip.setProlongedStripColor(0xFF)
        testLEDs.wholeStrip.assertAllPixels(0xFF)

        testLEDs.clear()
        testLEDs.wholeStrip.assertAllPixels(0)

        testLEDs.wholeStrip.setProlongedStripColor(0xFF)
        testLEDs.wholeStrip.assertAllPixels(0xFF)

        testLEDs.wholeStrip.clear()
        testLEDs.wholeStrip.assertAllPixels(0)
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

    @Test
    fun testGetSubSection() {
        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip

        val section1 = testLEDs.getSubSection(5, 10)
        val section2 = testLEDs.getSubSection(5, 10)

        assertTrue { section1 === section2 }
    }

    @Test
    fun testStartAnimation() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)
        val anim1 = AnimationData().animation("Alternate")
        testLEDs.startAnimation(anim1, "TEST1")
        delayBlocking(100)
        assertTrue { testLEDs.runningAnimations.map.containsKey("TEST1") }
        assertTrue { testLEDs.runningAnimations["TEST1"]?.data == anim1 }
        testLEDs.endAnimation(EndAnimation("TEST1"))
        runBlocking {
            testLEDs.runningAnimations["TEST1"]?.job?.join()
        }

        val anim2 = AnimationData().animation("Wipe").addColor(0xFF)
        testLEDs.startAnimation(anim2, "TEST2")
        delayBlocking(100)
        runBlocking {
            testLEDs.runningAnimations["TEST2"]?.job?.join()
        }
        testLEDs.wholeStrip.assertAllPixels(0xFF)

        testLEDs.createSection("Sect", 15, 40)
        val anim3 = AnimationData().animation("Wipe").addColor(0xFFFF).section("Sect")
        testLEDs.startAnimation(anim3, "TEST3")
        delayBlocking(100)
        runBlocking {
            testLEDs.runningAnimations["TEST3"]?.job?.join()
        }
        testLEDs.wholeStrip.assertPixels(0..14, 0xFF)
        testLEDs.wholeStrip.assertPixels(15..40, 0xFFFF)
        testLEDs.wholeStrip.assertPixels(41..49, 0xFF)
    }

    @Test
    fun testStartAnimationBadAnimation() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)
        assertNull(testLEDs.startAnimation(AnimationData().animation("Im not an animation"), "TEST"))
    }

    @Test
    fun testRunParallel() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip
        val anim = AnimationData().animation("Color")

        @Suppress("EXPERIMENTAL_API_USAGE")
        val pool = newSingleThreadContext("Test Pool")

        // Default parameters
        testLEDs.runParallel(anim, this)

        // Set parameters
        val runningAnim = testLEDs.runParallel(anim, this, pool = pool, runCount = -1)
        runningAnim?.cancel()

        // Test runParallelAndJoin
        val badAnim = AnimationData().animation("NonexistentAnimation")
        testLEDs.runParallelAndJoin(this, Pair(anim, testLEDs), Pair(badAnim, testLEDs))

        Unit
    }

    @Test
    fun testRunSequential() {
        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip

        // Continuous false (default)
        testLEDs.runSequential(AnimationData().animation("Color"))

        // Bad Animation
        testLEDs.runSequential(AnimationData().animation("Im not an animation"))
    }

}