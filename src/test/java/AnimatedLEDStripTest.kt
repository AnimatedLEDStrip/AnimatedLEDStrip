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


import animatedledstrip.animationutils.*
import animatedledstrip.colors.ColorContainer
import animatedledstrip.leds.emulated.EmulatedAnimatedLEDStrip
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Ignore
import org.junit.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class AnimatedLEDStripTest {

    @Test
    fun testColor() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(AnimationData().animation(Animation.COLOR).color(0xFF))
        checkAllPixels(testLEDs, 0xFF)
    }

    @Test
    fun testAlternate() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(
            AnimationData()
                .animation(Animation.ALTERNATE)
                .color(0xFF, index = 0)
                .color(0xFFFF, index = 1)
        )
    }

    @Test
    fun testBounce() {
        val job1 = GlobalScope.launch {
            val testLEDs = EmulatedAnimatedLEDStrip(50)

            testLEDs.run(
                AnimationData()
                    .animation(Animation.BOUNCE)
                    .color(0xFF)
            )
        }

        val job2 = GlobalScope.launch {
            val testLEDs = EmulatedAnimatedLEDStrip(50)

            testLEDs.run(
                AnimationData()
                    .animation(Animation.BOUNCE)
                    .color(0xFF)
                    .delay(-1)
            )
        }

        runBlocking { job2.join(); job1.join() }
    }

    @Test
    fun testBounceToColor() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(AnimationData().animation(Animation.BOUNCETOCOLOR).color(0xFF))
        checkAllPixels(testLEDs, 0xFF)
    }

    @Test
    fun testMeteor() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(
            AnimationData()
                .animation(Animation.METEOR)
                .color(0xFF)
                .direction(Direction.FORWARD)
        )

        testLEDs.run(
            AnimationData()
                .animation(Animation.METEOR)
                .color(0xFF00)
                .direction(Direction.BACKWARD)
        )
    }

    @Test
    fun testMultiColor() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(
            AnimationData()
                .animation(Animation.MULTICOLOR)
                .color(ColorContainer(0xFF, 0xFFFF))
        )

        val testGradient = ColorContainer(0xFF, 0xFFFF).prepare(50)

        for (i in 0 until 50) {
            assertTrue { testGradient[i] == testLEDs[i] }
        }
    }

    @Test
    fun testMultiPixelRun() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(
            AnimationData()
                .animation(Animation.MULTIPIXELRUN)
                .color(0xFF)
                .direction(Direction.FORWARD)
        )

        testLEDs.run(
            AnimationData()
                .animation(Animation.MULTIPIXELRUN)
                .color(0xFF00)
                .direction(Direction.BACKWARD)
        )
    }

    @Test
    fun testMultiPixelRunToColor() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(
            AnimationData()
                .animation(Animation.MULTIPIXELRUNTOCOLOR)
                .color(0xFF)
                .direction(Direction.FORWARD)
        )
        checkAllPixels(testLEDs, 0xFF)

        testLEDs.run(
            AnimationData()
                .animation(Animation.MULTIPIXELRUNTOCOLOR)
                .color(0xFF00)
                .direction(Direction.BACKWARD)
        )
        checkAllPixels(testLEDs, 0xFF00)
    }

    @Test
    fun testPixelMarathon() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(
            AnimationData()
                .animation(Animation.PIXELMARATHON)
                .color(0xFF, index = 0)
                .color(0xFFFF, index = 1)
                .color(0xFF00FF, index = 2)
                .color(0xFF00, index = 3)
                .color(0xFFFF00, index = 4)
        )
    }

    @Test
    fun testPixelRun() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(
            AnimationData()
                .animation(Animation.PIXELRUN)
                .color(0xFF)
                .direction(Direction.FORWARD)
        )

        testLEDs.run(
            AnimationData()
                .animation(Animation.PIXELRUN)
                .color(0xFF00)
                .direction(Direction.BACKWARD)
                .delay(-1)
        )
    }

    @Test
    fun testRipple() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(
            AnimationData()
                .animation(Animation.RIPPLE)
                .color(0xFFFF)
                .center(25)
                .distance(10)
        )

        testLEDs.run(
            AnimationData()
                .animation(Animation.RIPPLE)
                .color(0xFF)
        )
    }

    @Test
    fun testSmoothChase() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(
            AnimationData()
                .animation(Animation.SMOOTHCHASE)
                .color(ColorContainer(0xFF, 0xFF00))
                .direction(Direction.FORWARD)
        )

        testLEDs.run(
            AnimationData()
                .animation(Animation.SMOOTHCHASE)
                .color(ColorContainer(0xFF00, 0xFF))
                .direction(Direction.BACKWARD)
        )
    }

    @Test
    fun testSmoothFade() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(
            AnimationData()
                .animation(Animation.SMOOTHFADE)
                .color(ColorContainer(0xFF, 0xFF00))
                .direction(Direction.FORWARD)
        )

        testLEDs.run(
            AnimationData()
                .animation(Animation.SMOOTHFADE)
                .color(ColorContainer(0xFF00, 0xFF))
                .direction(Direction.BACKWARD)
        )
    }

    @Test
    fun testSparkle() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(
            AnimationData()
                .animation(Animation.SPARKLE)
                .color(0xFF)
        )

        testLEDs.run(
            AnimationData()
                .animation(Animation.SPARKLE)
                .color(0xFF)
                .delay(-1)
        )
    }

    @Test
    fun testSparkleFade() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(
            AnimationData()
                .animation(Animation.SPARKLEFADE)
                .color(0xFF00)
        )

        testLEDs.run(
            AnimationData()
                .animation(Animation.SPARKLEFADE)
                .color(0xFF00)
                .delay(-1)
        )
    }

    @Test
    fun testSparkleToColor() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(
            AnimationData()
                .animation(Animation.SPARKLETOCOLOR)
                .color(0xFF)
        )
        checkAllPixels(testLEDs, 0xFF)
    }

    @Test
    fun testSplat() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(
            AnimationData()
                .animation(Animation.SPLAT)
                .color(0xFFFF)
                .center(15)
                .distance(10)
        )

        checkPixels(6..25, testLEDs, 0xFFFF)

        testLEDs.run(
            AnimationData()
                .animation(Animation.SPLAT)
                .color(0xFF00)
        )

        checkAllPixels(testLEDs, 0xFF00)
    }

    @Test
    fun testStack() {
        val job2 = GlobalScope.launch {
            val testLEDs = EmulatedAnimatedLEDStrip(50)

            testLEDs.run(
                AnimationData()
                    .animation(Animation.STACK)
                    .color(0xFF)
                    .direction(Direction.FORWARD)
            )
            checkAllPixels(testLEDs, 0xFF)
        }

        val job1 = GlobalScope.launch {
            val testLEDs = EmulatedAnimatedLEDStrip(50)

            testLEDs.run(
                AnimationData()
                    .animation(Animation.STACK)
                    .color(0xFF00)
                    .direction(Direction.BACKWARD)
                    .delay(-1)
            )
            checkAllPixels(testLEDs, 0xFF00)
        }

        runBlocking { job2.join(); job1.join() }

    }

    @Test
    fun testStackOverflow() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(
            AnimationData()
                .animation(Animation.STACKOVERFLOW)
                .color(0xFF)
                .color(0xFF00)
        )
    }

    @Test
    fun testWipe() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(
            AnimationData()
                .animation(Animation.WIPE)
                .color(0xFF)
                .direction(Direction.FORWARD)
        )
        checkAllPixels(testLEDs, 0xFF)

        testLEDs.run(
            AnimationData()
                .animation(Animation.WIPE)
                .color(0xFF00)
                .direction(Direction.BACKWARD)
        )
        checkAllPixels(testLEDs, 0xFF00)
    }

    @Test
    fun testNonAnimation() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(AnimationData().animation(Animation.ENDANIMATION))
    }

    @Test
    @Ignore
    fun testRunCustomAnimation() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        assertFailsWith(UninitializedPropertyAccessException::class) {
            testLEDs.run(AnimationData().animation(Animation.CUSTOMANIMATION))
        }
        assertFailsWith(UninitializedPropertyAccessException::class) {
            testLEDs.run(AnimationData().animation(Animation.CUSTOMREPETITIVEANIMATION))
        }
    }
}