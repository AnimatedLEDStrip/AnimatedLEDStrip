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
import org.junit.Test

class AnimatedLEDStripTest {

    @Test
    fun testColor() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(AnimationData().animation(Animation.COLOR).color(0xFF))
        checkAllPixels(testLEDs, 0xFF)
    }

    @Test
    fun testAlternate() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(
            AnimationData()
                .animation(Animation.ALTERNATE)
                .color(0xFF, index = 0)
                .color(0xFFFF, index = 1)
        )
    }

    @Test
    fun testBounce() = runBlocking {
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

        job2.join()
        job1.join()
    }

    @Test
    fun testBounceToColor() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(AnimationData().animation(Animation.BOUNCETOCOLOR).color(0xFF))
        checkAllPixels(testLEDs, 0xFF)
    }

    @Test
    fun testCatToy() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(AnimationData().animation(Animation.CATTOY).color(0xFF))
    }

    @Test
    fun testMeteor() = runBlocking {
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
    fun testMultiPixelRun() = runBlocking {
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
    fun testMultiPixelRunToColor() = runBlocking {
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
    fun testPixelMarathon() = runBlocking {
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
    fun testPixelRun() = runBlocking {
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
    fun testRipple() = runBlocking {
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
    fun testSmoothChase() = runBlocking {
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
    fun testSmoothFade() = runBlocking {
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
    fun testSparkle() = runBlocking {
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
    fun testSparkleFade() = runBlocking {
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
    fun testSparkleToColor() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(
            AnimationData()
                .animation(Animation.SPARKLETOCOLOR)
                .color(0xFF)
        )
        checkAllPixels(testLEDs, 0xFF)
    }

    @Test
    fun testSplat() = runBlocking {
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
    fun testStack() = runBlocking {
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

        job2.join()
        job1.join()

    }

    @Test
    fun testStackOverflow() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(
            AnimationData()
                .animation(Animation.STACKOVERFLOW)
                .color(0xFF)
                .color(0xFF00)
        )
    }

    @Test
    fun testWipe() = runBlocking {
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
    fun testNonAnimation() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(AnimationData().animation(Animation.ENDANIMATION))
    }
}