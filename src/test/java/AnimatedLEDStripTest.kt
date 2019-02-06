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


import animatedledstrip.ccpresets.CCBlue
import animatedledstrip.leds.*
import org.junit.Test
import org.pmw.tinylog.Configurator
import org.pmw.tinylog.Level
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue


class AnimatedLEDStripTest {

    init {
        Configurator.defaultConfig().level(Level.OFF).activate()
    }

    @Test
    fun testColor() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(AnimationData().animation(Animation.COLOR).color(0xFF))
        checkAllPixels(testLEDs, 0xFF)
    }

    @Test
    fun testAlternate() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(AnimationData()
                .animation(Animation.ALTERNATE)
                .color1(0xFF)
                .color2(0xFFFF))
    }

    @Test
    fun testBounce() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(AnimationData()
                .animation(Animation.BOUNCE)
                .color(0xFF))
    }

    @Test
    fun testBounceToColor() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(AnimationData().animation(Animation.BOUNCETOCOLOR).color(0xFF))
        checkAllPixels(testLEDs, 0xFF)
    }

    @Test
    fun testMultiColor() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(AnimationData()
                .animation(Animation.MULTICOLOR)
                .color(ColorContainer(0xFF, 0xFFFF)))

        val testGradient = ColorContainer(0xFF, 0xFFFF).prepare(50)

        for (i in 0 until 50) {
            assertTrue { testGradient[i] == testLEDs[i] }
        }
    }

    @Test
    fun testMultiPixelRun() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(AnimationData()
                .animation(Animation.MULTIPIXELRUN)
                .color(0xFF)
                .direction(Direction.FORWARD))

        testLEDs.run(AnimationData()
                .animation(Animation.MULTIPIXELRUN)
                .color(0xFF00)
                .direction(Direction.BACKWARD))
    }

    @Test
    fun testMultiPixelRunToColor() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(AnimationData()
                .animation(Animation.MULTIPIXELRUNTOCOLOR)
                .color(0xFF)
                .direction(Direction.FORWARD))
        checkAllPixels(testLEDs, 0xFF)

        testLEDs.run(AnimationData()
                .animation(Animation.MULTIPIXELRUNTOCOLOR)
                .color(0xFF00)
                .direction(Direction.BACKWARD))
        checkAllPixels(testLEDs, 0xFF00)
    }

    @Test
    fun testPixelMarathon() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(AnimationData()
                .animation(Animation.PIXELMARATHON)
                .color1(0xFF)
                .color2(0xFFFF)
                .color3(0xFF00FF)
                .color4(0xFF00)
                .color5(0xFFFF00))
    }

    @Test
    fun testPixelRun() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(AnimationData()
                .animation(Animation.PIXELRUN)
                .color(0xFF)
                .direction(Direction.FORWARD))

        testLEDs.run(AnimationData()
                .animation(Animation.PIXELRUN)
                .color(0xFF00)
                .direction(Direction.BACKWARD))
    }

    @Test
    fun testPixelRunWithTrail() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(AnimationData()
                .animation(Animation.PIXELRUNWITHTRAIL)
                .color(0xFF)
                .direction(Direction.FORWARD))

        testLEDs.run(AnimationData()
                .animation(Animation.PIXELRUNWITHTRAIL)
                .color(0xFF00)
                .direction(Direction.BACKWARD))
    }

    @Test
    fun testSmoothChase() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(AnimationData()
                .animation(Animation.SMOOTHCHASE)
                .color1(ColorContainer(0xFF, 0xFF00))
                .direction(Direction.FORWARD))

        testLEDs.run(AnimationData()
                .animation(Animation.SMOOTHCHASE)
                .color1(ColorContainer(0xFF00, 0xFF))
                .direction(Direction.BACKWARD))
    }

    @Test
    fun testSmoothFade() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(AnimationData()
                .animation(Animation.SMOOTHFADE)
                .color(ColorContainer(0xFF, 0xFF00))
                .direction(Direction.FORWARD))

        testLEDs.run(AnimationData()
                .animation(Animation.SMOOTHFADE)
                .color(ColorContainer(0xFF00, 0xFF))
                .direction(Direction.BACKWARD))
    }

    @Test
    fun testSparkle() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(AnimationData()
                .animation(Animation.SPARKLE)
                .color(0xFF))
    }

    @Test
    fun testSparkleFade() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(AnimationData()
                .animation(Animation.SPARKLEFADE)
                .color(0xFF00))
    }

    @Test
    fun testSparkleToColor() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(AnimationData()
                .animation(Animation.SPARKLETOCOLOR)
                .color(0xFF))
        checkAllPixels(testLEDs, 0xFF)
    }

    @Test
    fun testStack() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(AnimationData().animation(Animation.STACK)
                .color(0xFF)
                .direction(Direction.FORWARD))
        checkAllPixels(testLEDs, 0xFF)

        testLEDs.run(AnimationData()
                .animation(Animation.STACK)
                .color(0xFF00)
                .direction(Direction.BACKWARD))
        checkAllPixels(testLEDs, 0xFF00)
    }

    @Test
    fun testStackOverflow() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(AnimationData()
                .animation(Animation.STACKOVERFLOW)
                .color1(0xFF)
                .color2(0xFF00))
    }

    @Test
    fun testWipe() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(AnimationData()
                .animation(Animation.WIPE)
                .color(0xFF)
                .direction(Direction.FORWARD))
        checkAllPixels(testLEDs, 0xFF)

        testLEDs.run(AnimationData()
                .animation(Animation.WIPE)
                .color(0xFF00)
                .direction(Direction.BACKWARD))
        checkAllPixels(testLEDs, 0xFF00)
    }

    @Test
    fun testNonAnimation() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.run(AnimationData().animation(Animation.ENDANIMATION))
    }

    @Test
    fun testCustomAnimationCompiler() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        assertFailsWith(UninitializedPropertyAccessException::class) {
            testLEDs.customAnimationCompiler
        }

        assertFailsWith(UninitializedPropertyAccessException::class) {
            testLEDs.run(AnimationData().animation(Animation.CUSTOMANIMATION))
        }

        assertFailsWith(UninitializedPropertyAccessException::class) {
            testLEDs.run(AnimationData().animation(Animation.CUSTOMREPETITIVEANIMATION))
        }
    }

    @Test
    fun testFadePixel() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.fadePixel(50, CCBlue.color)
    }

}