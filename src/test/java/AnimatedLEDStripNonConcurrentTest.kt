package animatedledstrip.test

import animatedledstrip.leds.Animation
import animatedledstrip.leds.AnimationData
import animatedledstrip.leds.Direction
import animatedledstrip.leds.EmulatedAnimatedLEDStripNonConcurrent
import org.junit.Ignore
import org.junit.Test
import org.pmw.tinylog.Configurator
import org.pmw.tinylog.Level

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
                .color1(0xFF)
                .color2(0xFFFF))
    }

    @Test
    fun testBounceToColor() {
        val testLEDs = EmulatedAnimatedLEDStripNonConcurrent(50)

        testLEDs.run(AnimationData().animation(Animation.BOUNCETOCOLOR).color(0xFF))
        checkAllPixels(testLEDs, 0xFF)
    }

    @Test
    @Ignore
    fun testMultiColor() {
        val testLEDs = EmulatedAnimatedLEDStripNonConcurrent(50)

        testLEDs.run(AnimationData()
                .animation(Animation.MULTICOLOR)
                .colorList(listOf<Long>(0xFF, 0xFFFF)))

//        val testGradient = colorsFromPalette(listOf(
//                ColorContainer(0xFF),
//                ColorContainer(0xFFFF))
//                , 50)
//
//        for (i in 0 until 50) {
//            assertTrue { testGradient[i] == testLEDs[i] }
//        }
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
                .colorList(listOf(0xFF, 0xFF00))
                .direction(Direction.FORWARD))

        testLEDs.run(AnimationData()
                .animation(Animation.SMOOTHCHASE)
                .colorList(listOf(0xFF00, 0xFF))
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