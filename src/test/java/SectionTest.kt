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
import org.pmw.tinylog.Level
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
        testLEDs.setTemporaryPixelColor(10, ColorContainer(0xFF))

        assertTrue { testLEDs.getTemporaryPixelColor(10) == 0xFFL }
        assertTrue { testLEDs.getProlongedPixelColor(10) == 0L }

        // setPixelColor with Long
        testLEDs.setTemporaryPixelColor(10, 0xFF0000)

        assertTrue { testLEDs.getTemporaryPixelColor(10) == 0xFF0000L }
        assertTrue { testLEDs.getProlongedPixelColor(10) == 0L }


        // Prolonged

        // setPixelColor with ColorContainer
        testLEDs.setProlongedPixelColor(10, ColorContainer(0xFF))

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
        assertFailsWith<IllegalArgumentException> { testLEDs.setTemporaryPixelColor(50, CCBlack) }
        assertFailsWith<IllegalArgumentException> { testLEDs.setProlongedPixelColor(50, CCBlack) }
    }

    @Test
    fun testSetPixelColors() {
        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip

        testLEDs.assertAllPixels(0)

        // Temporary

        // setTemporaryPixelColors with ColorContainer
        testLEDs.setTemporaryPixelColors(listOf(10, 15, 20), ColorContainer(0xFF))

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
        testLEDs.setTemporaryPixelColors(30..40, ColorContainer(0xFF))

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
        testLEDs.setProlongedPixelColors(listOf(10, 15, 20), ColorContainer(0xFF))

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
        testLEDs.setProlongedPixelColors(30..40, ColorContainer(0xFF))

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
        awaitPredefinedAnimationsLoaded()
        val anim = AnimationData().animation("Alternate")
        testLEDs.startAnimation(anim, "TEST")
        delayBlocking(100)
        assertTrue { testLEDs.runningAnimations.map.containsKey("TEST") }
        assertTrue { testLEDs.runningAnimations["TEST"]?.data == anim }
        testLEDs.endAnimation(EndAnimation("TEST"))
    }

    @Test
    fun testStartAnimationBadAnimation() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)
        assertNull(testLEDs.startAnimation(AnimationData().animation("Im not an animation"), "TEST"))
    }

    @Test
    fun testStartAnimationScriptException() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50)
        awaitPredefinedAnimationsLoaded()
        defineNewAnimation(
            "// ## animation info ##\n// name Bad Animation\n// abbr BAD\n// ## end info ##\nthrow Exception()",
            "BadAnimation"
        )

        startLogCapture()
        testLEDs.startAnimation(AnimationData().animation("BadAnimation"))?.join()

        assertLogs(
            setOf(
                Pair(Level.ERROR, "Error when running Bad Animation:"),
                Pair(Level.ERROR, "javax.script.ScriptException: java.lang.Exception")
            )
        )

        stopLogCapture()
        definedAnimations.remove("badanimation")
        Unit
    }

    @Test
    fun testRunParallel() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip
        awaitPredefinedAnimationsLoaded()
        val anim = AnimationData().animation("Color")

        @Suppress("EXPERIMENTAL_API_USAGE")
        val pool = newSingleThreadContext("Test Pool")

        // Default parameters
        testLEDs.runParallel(anim, this)

        // Set parameters
        val runningAnim = testLEDs.runParallel(anim, this, pool = pool, continuous = true)
        runningAnim?.cancel()

        // Test runParallelAndJoin
        val badAnim = AnimationData().animation("NonexistentAnimation")
        testLEDs.runParallelAndJoin(this, Pair(anim, testLEDs), Pair(badAnim, testLEDs))

        Unit
    }

    @Test
    fun testRunSequential() {
        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip
        awaitPredefinedAnimationsLoaded()

        // Continuous false (default)
        testLEDs.runSequential(AnimationData().animation("Color"))

        // Bad Animation
        testLEDs.runSequential(AnimationData().animation("Im not an animation"))
    }

}