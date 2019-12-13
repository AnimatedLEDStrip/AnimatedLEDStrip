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
import animatedledstrip.colors.ColorContainer
import animatedledstrip.leds.emulated.EmulatedAnimatedLEDStrip
import animatedledstrip.leds.endAnimation
import animatedledstrip.leds.join
import animatedledstrip.leds.runParallelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AnimatedLEDStripTest {

    @Test
    fun testColor() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        val anim = testLEDs.addAnimation(AnimationData().animation(Animation.COLOR).color(0xFF))
        assertNull(anim)
        checkAllPixels(testLEDs, 0xFF)
    }

    @Test
    fun testAlternate() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        val anim = testLEDs.addAnimation(
            AnimationData()
                .animation(Animation.ALTERNATE)
                .color(0xFF, index = 0)
                .color(0xFFFF, index = 1)
                .delay(100)
        )

        assertNotNull(anim)
        delay(500)
        anim.endAnimation()
        anim.join()
        Unit
    }

    @Test
    fun testBounce() = runBlocking {
        val testLEDs1 = EmulatedAnimatedLEDStrip(50)

        val anim1 = testLEDs1.addAnimation(
            AnimationData()
                .animation(Animation.BOUNCE)
                .color(0xFF)
        )

        val testLEDs2 = EmulatedAnimatedLEDStrip(50)

        val anim2 = testLEDs2.addAnimation(
            AnimationData()
                .animation(Animation.BOUNCE)
                .color(0xFF)
                .delay(-1)
        )

        assertNotNull(anim1)
        assertNotNull(anim2)

        anim1.endAnimation()
        anim2.endAnimation()

        anim1.join()
        anim2.join()
        Unit
    }

    @Test
    fun testBounceToColor() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        val anim = testLEDs.addAnimation(
            AnimationData()
                .animation(Animation.BOUNCETOCOLOR)
                .color(0xFF)
        )

        assertNotNull(anim)
        delay(100)
        anim.endAnimation()
        anim.join()
        checkAllPixels(testLEDs, 0xFF)
    }

    @Test
    fun testCatToy() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        val anim = testLEDs.addAnimation(
            AnimationData()
                .animation(Animation.CATTOY)
                .color(0xFF)
        )

        assertNotNull(anim)
        delay(100)
        anim.endAnimation()
        anim.join()
        Unit
    }

    @Test
    fun testMeteor() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        val anim1 = testLEDs.addAnimation(
            AnimationData()
                .animation(Animation.METEOR)
                .color(0xFF)
                .direction(Direction.FORWARD)
        )

        val anim2 = testLEDs.addAnimation(
            AnimationData()
                .animation(Animation.METEOR)
                .color(0xFF00)
                .direction(Direction.BACKWARD)
        )

        assertNotNull(anim1)
        assertNotNull(anim2)

        delay(100)
        anim1.endAnimation()
        anim2.endAnimation()

        anim1.join()
        anim2.join()
        Unit
    }

    @Test
    fun testMultiPixelRun() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        val anim1 = testLEDs.addAnimation(
            AnimationData()
                .animation(Animation.MULTIPIXELRUN)
                .color(0xFF)
                .direction(Direction.FORWARD)
        )

        val anim2 = testLEDs.addAnimation(
            AnimationData()
                .animation(Animation.MULTIPIXELRUN)
                .color(0xFF00)
                .direction(Direction.BACKWARD)
        )

        assertNotNull(anim1)
        assertNotNull(anim2)

        delay(100)
        anim1.endAnimation()
        anim2.endAnimation()

        anim1.join()
        anim2.join()
        Unit
    }

    @Test
    fun testMultiPixelRunToColor() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        val anim1 = testLEDs.addAnimation(
            AnimationData()
                .animation(Animation.MULTIPIXELRUNTOCOLOR)
                .color(0xFF)
                .direction(Direction.FORWARD)
        )

        assertNotNull(anim1)
        delay(100)
        anim1.endAnimation()
        anim1.join()
        checkAllPixels(testLEDs, 0xFF)

        val anim2 = testLEDs.addAnimation(
            AnimationData()
                .animation(Animation.MULTIPIXELRUNTOCOLOR)
                .color(0xFF00)
                .direction(Direction.BACKWARD)
        )

        assertNotNull(anim2)
        delay(100)
        anim2.endAnimation()
        anim2.join()
        checkAllPixels(testLEDs, 0xFF00)
    }

    @Test
    fun testPixelMarathon() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        val anim = testLEDs.addAnimation(
            AnimationData()
                .animation(Animation.PIXELMARATHON)
                .color(0xFF, index = 0)
                .color(0xFFFF, index = 1)
                .color(0xFF00FF, index = 2)
                .color(0xFF00, index = 3)
                .color(0xFFFF00, index = 4)
        )

        assertNotNull(anim)
        delay(100)
        anim.endAnimation()
        anim.join()
        Unit
    }

    @Test
    fun testPixelRun() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        val anim1 = testLEDs.addAnimation(
            AnimationData()
                .animation(Animation.PIXELRUN)
                .color(0xFF)
                .direction(Direction.FORWARD)
        )

        val anim2 = testLEDs.addAnimation(
            AnimationData()
                .animation(Animation.PIXELRUN)
                .color(0xFF00)
                .direction(Direction.BACKWARD)
                .delay(-1)
        )

        assertNotNull(anim1)
        assertNotNull(anim2)

        delay(100)
        anim1.endAnimation()
        anim2.endAnimation()

        anim1.join()
        anim2.join()
        Unit
    }

    @Test
    fun testRipple() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        val anim1 = testLEDs.addAnimation(
            AnimationData()
                .animation(Animation.RIPPLE)
                .color(0xFFFF)
                .center(25)
                .distance(10)
        )

        val anim2 = testLEDs.addAnimation(
            AnimationData()
                .animation(Animation.RIPPLE)
                .color(0xFF)
        )

        assertNotNull(anim1)
        assertNotNull(anim2)

        delay(100)
        anim1.endAnimation()
        anim2.endAnimation()

        anim1.join()
        anim2.join()
        Unit
    }

    @Test
    fun testSmoothChase() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        val anim1 = testLEDs.addAnimation(
            AnimationData()
                .animation(Animation.SMOOTHCHASE)
                .color(ColorContainer(0xFF, 0xFF00))
                .direction(Direction.FORWARD)
        )

        val anim2 = testLEDs.addAnimation(
            AnimationData()
                .animation(Animation.SMOOTHCHASE)
                .color(ColorContainer(0xFF00, 0xFF))
                .direction(Direction.BACKWARD)
        )

        assertNotNull(anim1)
        assertNotNull(anim2)

        delay(100)
        anim1.endAnimation()
        anim2.endAnimation()

        anim1.join()
        anim2.join()
        Unit
    }

    @Test
    fun testSmoothFade() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        val anim1 = testLEDs.addAnimation(
            AnimationData()
                .animation(Animation.SMOOTHFADE)
                .color(ColorContainer(0xFF, 0xFF00))
                .direction(Direction.FORWARD)
        )

        val anim2 = testLEDs.addAnimation(
            AnimationData()
                .animation(Animation.SMOOTHFADE)
                .color(ColorContainer(0xFF00, 0xFF))
                .direction(Direction.BACKWARD)
        )

        assertNotNull(anim1)
        assertNotNull(anim2)

        delay(100)
        anim1.endAnimation()
        anim2.endAnimation()

        anim1.join()
        anim2.join()
        Unit
    }

    @Test
    fun testSparkle() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        val anim1 = testLEDs.addAnimation(
            AnimationData()
                .animation(Animation.SPARKLE)
                .color(0xFF)
        )

        val anim2 = testLEDs.addAnimation(
            AnimationData()
                .animation(Animation.SPARKLE)
                .color(0xFF)
                .delay(-1)
        )

        assertNotNull(anim1)
        assertNotNull(anim2)

        delay(100)
        anim1.endAnimation()
        anim2.endAnimation()

        anim1.join()
        anim2.join()
        Unit
    }

    @Test
    fun testSparkleFade() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        val anim1 = testLEDs.addAnimation(
            AnimationData()
                .animation(Animation.SPARKLEFADE)
                .color(0xFF00)
        )

        val anim2 = testLEDs.addAnimation(
            AnimationData()
                .animation(Animation.SPARKLEFADE)
                .color(0xFF00)
                .delay(-1)
        )

        assertNotNull(anim1)
        assertNotNull(anim2)

        delay(100)
        anim1.endAnimation()
        anim2.endAnimation()

        anim1.join()
        anim2.join()
        Unit
    }

    @Test
    fun testSparkleToColor() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        val anim = testLEDs.addAnimation(
            AnimationData()
                .animation(Animation.SPARKLETOCOLOR)
                .color(0xFF)
        )

        delay(100)
        anim?.endAnimation()
        anim?.join()
        checkAllPixels(testLEDs, 0xFF)
    }

    @Test
    fun testSplat() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        val anim1 = testLEDs.addAnimation(
            AnimationData()
                .animation(Animation.SPLAT)
                .color(0xFFFF)
                .center(15)
                .distance(10)
        )

        assertNotNull(anim1)
        delay(500)
        anim1.endAnimation()
        anim1.join()
        checkPixels(6..25, testLEDs, 0xFFFF)

        val anim2 = testLEDs.addAnimation(
            AnimationData()
                .animation(Animation.SPLAT)
                .color(0xFF00)
        )

        assertNotNull(anim2)
        delay(500)
        anim2.endAnimation()
        anim2.join()
        checkAllPixels(testLEDs, 0xFF00)
    }

    @Test
    fun testStack() = runBlocking {
        val testLEDs1 = EmulatedAnimatedLEDStrip(50)

        val anim1 = testLEDs1.addAnimation(
            AnimationData()
                .animation(Animation.STACK)
                .color(0xFF)
                .direction(Direction.FORWARD)
        )

        val testLEDs2 = EmulatedAnimatedLEDStrip(50)

        val anim2 = testLEDs2.addAnimation(
            AnimationData()
                .animation(Animation.STACK)
                .color(0xFF00)
                .direction(Direction.BACKWARD)
                .delay(-1)
        )

        assertNotNull(anim1)
        assertNotNull(anim2)

        delay(500)
        anim1.endAnimation()
        anim2.endAnimation()

        anim1.join()
        anim2.join()

        checkAllPixels(testLEDs1, 0xFF)
        checkAllPixels(testLEDs2, 0xFF00)
    }

    @Test
    fun testStackOverflow() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        val anim = testLEDs.addAnimation(
            AnimationData()
                .animation(Animation.STACKOVERFLOW)
                .color(0xFF)
                .color(0xFF00)
        )

        assertNotNull(anim)
        delay(100)
        anim.endAnimation()
        anim.join()
        Unit
    }

    @Test
    fun testWipe() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        val anim1 = testLEDs.addAnimation(
            AnimationData()
                .animation(Animation.WIPE)
                .color(0xFF)
                .direction(Direction.FORWARD)
        )

        assertNotNull(anim1)
        delay(100)
        anim1.endAnimation()
        anim1.join()
        checkAllPixels(testLEDs, 0xFF)

        val anim2 = testLEDs.addAnimation(
            AnimationData()
                .animation(Animation.WIPE)
                .color(0xFF00)
                .direction(Direction.BACKWARD)
        )

        assertNotNull(anim2)
        delay(100)
        anim2.endAnimation()
        anim2.join()
        checkAllPixels(testLEDs, 0xFF00)
    }

    @Test
    fun testNonAnimation() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(AnimationData().animation(Animation.ENDANIMATION))
        Unit
    }

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
}