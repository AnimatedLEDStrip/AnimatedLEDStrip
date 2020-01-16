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

package animatedledstrip.test

import animatedledstrip.animationutils.*
import animatedledstrip.leds.emulated.EmulatedAnimatedLEDStrip
import animatedledstrip.leds.endAnimation
import animatedledstrip.leds.runParallelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AnimatedLEDStripTest {

    @Test
    fun testEndAnimation() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        // RunningAnimation extension function
        val anim1 = testLEDs.addAnimation(
            AnimationData()
                .animation(Animation.ALTERNATE)
                .delay(100)
        )
        assertNotNull(anim1)
        assertTrue(testLEDs.runningAnimations.map.containsKey(anim1.id))
        delay(500)
        anim1.endAnimation()


        // End with AnimationData instance
        val anim2 = testLEDs.addAnimation(
            AnimationData()
                .animation(Animation.ALTERNATE)
                .delay(100)
        )
        assertNotNull(anim2)
        delay(500)
        assertTrue(testLEDs.runningAnimations.map.containsKey(anim2.id))
        testLEDs.endAnimation(anim2.animation)

        val nullAnim: AnimationData? = null
        testLEDs.endAnimation(nullAnim)


        // End with animation ID
        val anim3 = testLEDs.addAnimation(
            AnimationData()
                .animation(Animation.ALTERNATE)
                .delay(100)
        )
        assertNotNull(anim3)
        delay(500)
        assertTrue(testLEDs.runningAnimations.map.containsKey(anim3.id))
        testLEDs.endAnimation(anim3.id)

        assertFalse(testLEDs.runningAnimations.map.containsKey("TEST"))
        testLEDs.endAnimation("TEST")
        assertFalse(testLEDs.runningAnimations.map.containsKey("TEST"))


        // End with addAnimation and AnimationData instance with ENDANIMATION and ID
        val anim4 = testLEDs.addAnimation(
            AnimationData()
                .animation(Animation.ALTERNATE)
                .delay(100)
        )
        assertNotNull(anim4)
        delay(500)
        assertTrue(testLEDs.runningAnimations.map.containsKey(anim4.id))
        testLEDs.addAnimation(
            AnimationData()
                .animation(Animation.ENDANIMATION)
                .id(anim4.id)
        )


        delay(1000)

        // Confirm that all animations have ended
        assertFalse(testLEDs.runningAnimations.map.containsKey(anim1.id))
        assertFalse(testLEDs.runningAnimations.map.containsKey(anim2.id))
        assertFalse(testLEDs.runningAnimations.map.containsKey(anim3.id))
        assertFalse(testLEDs.runningAnimations.map.containsKey(anim4.id))
        Unit
    }

    @Test
    fun testRunParallel() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50)
        val anim = AnimationData().animation(Animation.COLOR)
        @Suppress("EXPERIMENTAL_API_USAGE")
        val pool = newSingleThreadContext("Test Pool")

        // Default parameters
        // join() not needed because animation is COLOR which doesn't spawn a coroutine
        testLEDs.runParallel(anim, this)

        // Set parameters
        // join() not needed because animation is COLOR which doesn't spawn a coroutine
        // continuous doesn't actually affect anything because animation is COLOR
        testLEDs.runParallel(anim, this, pool = pool, continuous = true)

        // Test runParallelAndJoin with animation that does not return a job
        testLEDs.runParallelAndJoin(this, anim)

        Unit
    }

    @Test
    fun testRunSequential() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)
        val anim1 = AnimationData().animation(Animation.COLOR)
        val anim2 = AnimationData().animation(Animation.ALTERNATE)

        // Continuous false (default)
        // Also tests animation that doesn't spawn a coroutine
        testLEDs.runSequential(anim1)

        // Continuous true
        // continuous doesn't actually affect anything because animation is COLOR
        testLEDs.runSequential(anim1, continuous = true)

        // Test with animation that spawns a coroutine
        testLEDs.runSequential(anim2)
    }

    @Test
    fun testCallbacks() = runBlocking {
        var indicator1 = false
        var indicator2 = false

        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.startAnimationCallback = {
            indicator1 = true
            Unit
        }

        testLEDs.endAnimationCallback = {
            indicator2 = true
            Unit
        }

        testLEDs.addAnimation(AnimationData().animation(Animation.ALTERNATE).continuous(false).delay(10))

        delay(100)

        assertTrue(indicator1)
        assertTrue(indicator2)
    }
}
