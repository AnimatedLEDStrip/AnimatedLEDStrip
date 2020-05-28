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

import animatedledstrip.animationutils.*
import animatedledstrip.leds.StripInfo
import animatedledstrip.leds.emulated.EmulatedAnimatedLEDStrip
import animatedledstrip.leds.endAnimation
import animatedledstrip.utils.delayBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.pmw.tinylog.Level
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AnimatedLEDStripTest {

    @Test
    fun testThreadCount() {
        val info1 = StripInfo(numLEDs = 50)
        assertTrue { info1.threadCount == 100 }
        val testLEDs1 = EmulatedAnimatedLEDStrip(info1)
        assertTrue { testLEDs1.threadCount == 100 }

        val info2 = StripInfo(numLEDs = 50, threadCount = 10)
        assertTrue { info2.threadCount == 10 }
        val testLEDs2 = EmulatedAnimatedLEDStrip(info2)
        assertTrue { testLEDs2.threadCount == 10 }
    }

    @Test
    fun testCallbacks()  {
        var indicator1 = false
        var indicator2 = false
        val testLEDs = EmulatedAnimatedLEDStrip(50)
        awaitPredefinedAnimationsLoaded()

        testLEDs.startAnimationCallback = {
            indicator1 = true
            Unit
        }

        testLEDs.endAnimationCallback = {
            indicator2 = true
            Unit
        }

        testLEDs.startAnimation(AnimationData().animation("Alternate").continuous(false).delay(10))

        delayBlocking(500)

        assertTrue(indicator1)
        assertTrue(indicator2)
    }

    @Test
    fun testEndAnimation() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50)
        awaitPredefinedAnimationsLoaded()

        // RunningAnimation extension function
        val anim1 = testLEDs.startAnimation(
            AnimationData()
                .animation("Alternate")
                .delay(100)
        )
        assertNotNull(anim1)
        assertTrue { testLEDs.runningAnimations.map.containsKey(anim1.id) }
        delay(500)
        anim1.endAnimation()


        // End with EndAnimation instance
        val anim2 = testLEDs.startAnimation(
            AnimationData()
                .animation("Alternate")
                .delay(100)
        )
        assertNotNull(anim2)
        delay(500)
        assertTrue { testLEDs.runningAnimations.map.containsKey(anim2.id) }
        testLEDs.endAnimation(EndAnimation(anim2.id))


        // Null EndAnimation instance
        val nullAnim: EndAnimation? = null
        testLEDs.endAnimation(nullAnim)


        // End with ID
        val anim3 = testLEDs.startAnimation(
            AnimationData()
                .animation("Alternate")
                .delay(100)
        )
        assertNotNull(anim3)
        delay(500)
        assertTrue { testLEDs.runningAnimations.map.containsKey(anim3.id) }
        testLEDs.endAnimation(anim3.id)

        // End nonexistent animation
        startLogCapture()
        testLEDs.endAnimation("animation_that_is_not_running")
        assertLogs(setOf(Pair(Level.WARNING, "Animation animation_that_is_not_running is not running")))
        stopLogCapture()

        delay(1000)

        // Confirm that all animations have ended
        assertFalse { testLEDs.runningAnimations.map.containsKey(anim1.id) }
        assertFalse { testLEDs.runningAnimations.map.containsKey(anim2.id) }
        assertFalse { testLEDs.runningAnimations.map.containsKey(anim3.id) }
        Unit
    }

    @Test
    fun testCreateSection() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)
        testLEDs.createSection("Test", 5, 20)

        assertTrue { testLEDs.sections.size == 2 }
        assertTrue { testLEDs.sections.containsKey("Test") }
    }

    @Test
    fun testGetSection() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)
        testLEDs.createSection("Test", 5, 20)

        assertTrue { testLEDs.getSection("Test") != testLEDs.wholeStrip }
        assertTrue { testLEDs.getSection("Test").startPixel == 5 }
        assertTrue { testLEDs.getSection("Test").endPixel == 20 }
        assertTrue { testLEDs.wholeStrip.getSection("Test") != testLEDs.wholeStrip }
        assertTrue { testLEDs.wholeStrip.getSection("Test").startPixel == 5 }
        assertTrue { testLEDs.wholeStrip.getSection("Test").endPixel == 20 }

        startLogCapture()

        assertTrue { testLEDs.getSection("Other") === testLEDs.wholeStrip }
        assertLogs(setOf(Pair(Level.WARNING, "Could not find section Other, defaulting to whole strip")))

        assertTrue { testLEDs.wholeStrip.getSection("Other") === testLEDs.wholeStrip }

        stopLogCapture()
    }
}
