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
import animatedledstrip.animationutils.Direction
import animatedledstrip.colors.ColorContainer
import animatedledstrip.leds.emulated.EmulatedAnimatedLEDStripNonConcurrent
import org.junit.Test
import org.pmw.tinylog.Configurator
import org.pmw.tinylog.Level
import kotlin.test.assertTrue

class AnimatedLEDStripNonConcurrentTest {

    init {
        Configurator.defaultConfig().level(Level.OFF).activate()
    }

    @Test
    fun testColor() {
        val testLEDs = EmulatedAnimatedLEDStripNonConcurrent(50)

        testLEDs.run(AnimationData().animation(Animation.COLOR).color(0xFF))
        checkAllPixels(testLEDs, 0xFF)
    }

    @Test
    fun testAlternate() {
        val testLEDs = EmulatedAnimatedLEDStripNonConcurrent(50)

        testLEDs.run(AnimationData()
                .animation(Animation.ALTERNATE)
                .color(0xFF, index = 0)
                .color(0xFFFF, index = 1))
    }

    @Test
    fun testBounceToColor() {
        val testLEDs = EmulatedAnimatedLEDStripNonConcurrent(50)

        testLEDs.run(AnimationData().animation(Animation.BOUNCETOCOLOR).color(0xFF))
        checkAllPixels(testLEDs, 0xFF)
    }

    @Test
    fun testMultiColor() {
        val testLEDs = EmulatedAnimatedLEDStripNonConcurrent(50)

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
        val testLEDs = EmulatedAnimatedLEDStripNonConcurrent(50)

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
        val testLEDs = EmulatedAnimatedLEDStripNonConcurrent(50)

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
    fun testPixelRun() {
        val testLEDs = EmulatedAnimatedLEDStripNonConcurrent(50)

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
        val testLEDs = EmulatedAnimatedLEDStripNonConcurrent(50)

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
        val testLEDs = EmulatedAnimatedLEDStripNonConcurrent(50)

        testLEDs.run(AnimationData()
                .animation(Animation.SMOOTHCHASE)
                .color(ColorContainer(0xFF, 0xFF00))
                .direction(Direction.FORWARD))

        testLEDs.run(AnimationData()
                .animation(Animation.SMOOTHCHASE)
                .color(ColorContainer(0xFF00, 0xFF))
                .direction(Direction.BACKWARD))
    }

    @Test
    fun testSparkle() {
        val testLEDs = EmulatedAnimatedLEDStripNonConcurrent(50)

        testLEDs.run(AnimationData()
                .animation(Animation.SPARKLE)
                .color(0xFF))
    }

    @Test
    fun testSparkleToColor() {
        val testLEDs = EmulatedAnimatedLEDStripNonConcurrent(50)

        testLEDs.run(AnimationData().animation(Animation.SPARKLETOCOLOR).color(0xFF))
        checkAllPixels(testLEDs, 0xFF)
    }

    @Test
    fun testStack() {
        val testLEDs = EmulatedAnimatedLEDStripNonConcurrent(50)

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
    fun testWipe() {
        val testLEDs = EmulatedAnimatedLEDStripNonConcurrent(50)

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
        val testLEDs = EmulatedAnimatedLEDStripNonConcurrent(50)

        testLEDs.run(AnimationData().animation(Animation.ENDANIMATION))
    }

}