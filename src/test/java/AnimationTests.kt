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
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertNotNull

class AnimationTests{

    @Test
    fun testColor() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip

        val anim = testLEDs.startAnimation(AnimationData().animation("Color").color(0xFF))
        assertNotNull(anim)
        delay(500)
        anim.endAnimation()
        anim.join()
        testLEDs.assertAllPixels(0xFF)
    }

    @Test
    fun testAlternate() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip

        val anim = testLEDs.startAnimation(
            AnimationData()
                .animation("Alternate")
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
        val testLEDs1 = EmulatedAnimatedLEDStrip(10).wholeStrip

        val anim1 = testLEDs1.startAnimation(
            AnimationData()
                .animation("Bounce")
                .color(0xFF)
        )

        val testLEDs2 = EmulatedAnimatedLEDStrip(11).wholeStrip

        val anim2 = testLEDs2.startAnimation(
            AnimationData()
                .animation("Bounce")
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
        val testLEDs = EmulatedAnimatedLEDStrip(10).wholeStrip

        val anim = testLEDs.startAnimation(
            AnimationData()
                .animation("Bounce to Color")
                .color(0xFF)
        )

        assertNotNull(anim)
        delay(100)
        anim.endAnimation()
        anim.join()
        testLEDs.assertAllPixels(0xFF)

        val testLEDs2 = EmulatedAnimatedLEDStrip(11).wholeStrip

        val anim2 = testLEDs2.startAnimation(
            AnimationData()
                .animation("Bounce to Color")
                .color(0xFF)
        )

        assertNotNull(anim2)
        delay(100)
        anim2.endAnimation()
        anim2.join()
        testLEDs2.assertAllPixels(0xFF)
    }

    @Test
    fun testBubbleSort() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(10).wholeStrip

        val anim = testLEDs.startAnimation(
            AnimationData()
                .animation("Bubble Sort")
                .color(ColorContainer(0xFF, 0xFFFF))
        )

        assertNotNull(anim)
        delay(100)
        anim.endAnimation()
        anim.join()
        Unit
    }

    @Test
    fun testCatToy() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip

        val anim = testLEDs.startAnimation(
            AnimationData()
                .animation("Cat Toy")
                .color(0xFF)
        )

        assertNotNull(anim)
        delay(100)
        anim.endAnimation()
        anim.join()
        Unit
    }

    @Test
    fun testCatToyToColor() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(10).wholeStrip

        val anim = testLEDs.startAnimation(
            AnimationData()
                .animation("Cat Toy to Color")
                .color(0xFF)
        )

        assertNotNull(anim)
        delay(100)
        anim.endAnimation()
        anim.join()
        testLEDs.assertAllPixels(0xFF)
    }

    @Test
    fun testFadeToColor() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip

        val anim = testLEDs.startAnimation(
            AnimationData()
                .animation("Fade to Color")
                .addColor(0xFF)
        )

        assertNotNull(anim)
        delay(100)
        anim.endAnimation()
        anim.join()
        Unit
    }

    @Test
    fun testFireworks() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip

        val anim1 = testLEDs.startAnimation(
            AnimationData()
                .animation("Fireworks")
                .addColor(0xFF)
                .addColor(0xFF00)
                .addColor(0xFF0000)
                .addColor(0xFFFF)
                .addColor(ColorContainer())
        )

        val anim2 = testLEDs.startAnimation(
            AnimationData()
                .animation("Fireworks")
                .addColor(ColorContainer())
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
    fun testMergeSort() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(25).wholeStrip

        val anim1 = testLEDs.startAnimation(
            AnimationData()
                .animation("Merge Sort Parallel")
                .color(ColorContainer(0xFF, 0xFF00))
        )

        val anim2 = testLEDs.startAnimation(
            AnimationData()
                .animation("Merge Sort Sequential")
                .color(ColorContainer(0xFF, 0xFF00))
        )

        assertNotNull(anim1)
        assertNotNull(anim2)
        delay(100)
        anim1.endAnimation()
        anim2.endAnimation()
        anim1.join()
        anim2.join()
    }

    @Test
    fun testMeteor() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip

        val anim1 = testLEDs.startAnimation(
            AnimationData()
                .animation("Meteor")
                .color(0xFF)
                .direction(Direction.FORWARD)
        )

        val anim2 = testLEDs.startAnimation(
            AnimationData()
                .animation("Meteor")
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
        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip

        val anim1 = testLEDs.startAnimation(
            AnimationData()
                .animation("Multi Pixel Run")
                .color(0xFF)
                .direction(Direction.FORWARD)
        )

        val anim2 = testLEDs.startAnimation(
            AnimationData()
                .animation("Multi-pixel Run")
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
        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip

        val anim1 = testLEDs.startAnimation(
            AnimationData()
                .animation("Multi Pixel Run to Color")
                .color(0xFF)
                .direction(Direction.FORWARD)
        )

        assertNotNull(anim1)
        delay(100)
        anim1.endAnimation()
        anim1.join()
        testLEDs.assertAllPixels(0xFF)

        val anim2 = testLEDs.startAnimation(
            AnimationData()
                .animation("Multi-Pixel Run to Color")
                .color(0xFF00)
                .direction(Direction.BACKWARD)
        )

        assertNotNull(anim2)
        delay(100)
        anim2.endAnimation()
        anim2.join()
        testLEDs.assertAllPixels(0xFF00)
    }

    @Test
    fun testPixelMarathon() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip

        val anim = testLEDs.startAnimation(
            AnimationData()
                .animation("Pixel Marathon")
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
        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip

        val anim1 = testLEDs.startAnimation(
            AnimationData()
                .animation("Pixel Run")
                .color(0xFF)
                .direction(Direction.FORWARD)
        )

        val anim2 = testLEDs.startAnimation(
            AnimationData()
                .animation("Pixel Run")
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
        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip

        val anim1 = testLEDs.startAnimation(
            AnimationData()
                .animation("Ripple")
                .color(0xFFFF)
                .center(25)
                .distance(10)
        )

        val anim2 = testLEDs.startAnimation(
            AnimationData()
                .animation("Ripple")
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
        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip

        val anim1 = testLEDs.startAnimation(
            AnimationData()
                .animation("Smooth Chase")
                .color(ColorContainer(0xFF, 0xFF00))
                .direction(Direction.FORWARD)
        )

        val anim2 = testLEDs.startAnimation(
            AnimationData()
                .animation("Smooth Chase")
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
        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip

        val anim1 = testLEDs.startAnimation(
            AnimationData()
                .animation("Smooth Fade")
                .color(ColorContainer(0xFF, 0xFF00))
                .direction(Direction.FORWARD)
        )

        val anim2 = testLEDs.startAnimation(
            AnimationData()
                .animation("Smooth Fade")
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
        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip

        val anim1 = testLEDs.startAnimation(
            AnimationData()
                .animation("Sparkle")
                .color(0xFF)
        )

        val anim2 = testLEDs.startAnimation(
            AnimationData()
                .animation("Sparkle")
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
        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip

        val anim1 = testLEDs.startAnimation(
            AnimationData()
                .animation("Sparkle Fade")
                .color(0xFF00)
        )

        val anim2 = testLEDs.startAnimation(
            AnimationData()
                .animation("Sparkle Fade")
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
        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip

        val anim = testLEDs.startAnimation(
            AnimationData()
                .animation("Sparkle to Color")
                .color(0xFF)
        )

        delay(100)
        anim?.endAnimation()
        anim?.join()
        testLEDs.assertAllPixels(0xFF)
    }

    @Test
    fun testSplat() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip

        val anim1 = testLEDs.startAnimation(
            AnimationData()
                .animation("Splat")
                .color(0xFFFF)
                .center(15)
                .distance(10)
        )

        assertNotNull(anim1)
        delay(500)
        anim1.endAnimation()
        anim1.join()
        testLEDs.assertPixels(6..25, 0xFFFF)

        val anim2 = testLEDs.startAnimation(
            AnimationData()
                .animation("Splat")
                .color(0xFF00)
        )

        assertNotNull(anim2)
        delay(500)
        anim2.endAnimation()
        anim2.join()
        testLEDs.assertAllPixels(0xFF00)
    }

    @Test
    fun testStack() = runBlocking {
        val testLEDs1 = EmulatedAnimatedLEDStrip(50).wholeStrip

        val anim1 = testLEDs1.startAnimation(
            AnimationData()
                .animation("Stack")
                .color(0xFF)
                .direction(Direction.FORWARD)
        )

        val testLEDs2 = EmulatedAnimatedLEDStrip(50).wholeStrip

        val anim2 = testLEDs2.startAnimation(
            AnimationData()
                .animation("Stack")
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

        testLEDs1.assertAllPixels(0xFF)
        testLEDs2.assertAllPixels(0xFF00)
    }

    @Test
    fun testStackOverflow() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip

        val anim = testLEDs.startAnimation(
            AnimationData()
                .animation("Stack Overflow")
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
        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip

        val anim1 = testLEDs.startAnimation(
            AnimationData()
                .animation("Wipe")
                .color(0xFF)
                .direction(Direction.FORWARD)
        )

        assertNotNull(anim1)
        delay(100)
        anim1.endAnimation()
        anim1.join()
        testLEDs.assertAllPixels(0xFF)

        val anim2 = testLEDs.startAnimation(
            AnimationData()
                .animation("Wipe")
                .color(0xFF00)
                .direction(Direction.BACKWARD)
        )

        assertNotNull(anim2)
        delay(100)
        anim2.endAnimation()
        anim2.join()
        testLEDs.assertAllPixels(0xFF00)
    }

    @Test
    fun testNonAnimation() {
        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip

        testLEDs.run(AnimationData().animation("Im Not an Animation"))
        Unit
    }
}
