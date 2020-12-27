///*
// *  Copyright (c) 2018-2020 AnimatedLEDStrip
// *
// *  Permission is hereby granted, free of charge, to any person obtaining a copy
// *  of this software and associated documentation files (the "Software"), to deal
// *  in the Software without restriction, including without limitation the rights
// *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// *  copies of the Software, and to permit persons to whom the Software is
// *  furnished to do so, subject to the following conditions:
// *
// *  The above copyright notice and this permission notice shall be included in
// *  all copies or substantial portions of the Software.
// *
// *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHAL THE
// *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// *  THE SOFTWARE.
// */
//
//package animatedledstrip.test.leds
//
//import animatedledstrip.animationutils.*
//import animatedledstrip.colors.ColorContainer
//import animatedledstrip.colors.ccpresets.CCBlack
//import animatedledstrip.leds.*
//import animatedledstrip.leds.emulated.EmulatedAnimatedLEDStrip
//import animatedledstrip.test.assertAllPixels
//import animatedledstrip.test.assertAllProlongedPixels
//import animatedledstrip.test.assertAllTemporaryPixels
//import animatedledstrip.test.assertPixels
//import io.kotest.core.spec.style.StringSpec
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.newSingleThreadContext
//import kotlin.test.assertFailsWith
//import kotlin.test.assertNull
//import kotlin.test.assertTrue
//
//class SectionTest : StringSpec(
//    {
//        "set pixel color".config(enabled = false) {
//            val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip
//
//            testLEDs.assertAllPixels(-1, 0)
//
//            // Temporary
//
//            // setPixelColor with ColorContainer
//            testLEDs.setTemporaryPixelColor(10, ColorContainer(0xFF).prepare(testLEDs.numLEDs))
//
//            assertTrue { testLEDs.getTemporaryPixelColor(10) == 0xFF }
//            assertTrue { testLEDs.getProlongedPixelColor(10) == 0 }
//
//            // setPixelColor with Long
//            testLEDs.setTemporaryPixelColor(10, 0xFF0000)
//
//            assertTrue { testLEDs.getTemporaryPixelColor(10) == 0xFF0000 }
//            assertTrue { testLEDs.getProlongedPixelColor(10) == 0 }
//
//
//            // Prolonged
//
//            // setPixelColor with ColorContainer
//            testLEDs.setProlongedPixelColor(10, ColorContainer(0xFF).prepare(testLEDs.numLEDs))
//
//            assertTrue { testLEDs.getTemporaryPixelColor(10) == 0xFF }
//            assertTrue { testLEDs.getProlongedPixelColor(10) == 0xFF }
//
//            // setPixelColor with Long
//            testLEDs.setProlongedPixelColor(10, 0xFF0000)
//
//            assertTrue { testLEDs.getTemporaryPixelColor(10) == 0xFF0000 }
//            assertTrue { testLEDs.getProlongedPixelColor(10) == 0xFF0000 }
//
//
//            testLEDs.setProlongedPixelColors(listOf(10, 11, 12), 0)   // reset pixels
//
//            // Confirm successful reset
//            testLEDs.assertPixels(10..12, -1, 0)
//
//            // Test bad pixel index
//            assertFailsWith<IllegalArgumentException> { testLEDs.setTemporaryPixelColor(50, CCBlack.prepare(testLEDs.numLEDs)) }
//            assertFailsWith<IllegalArgumentException> { testLEDs.setProlongedPixelColor(50, CCBlack.prepare(testLEDs.numLEDs)) }
//        }
//
//        "set pixel colors".config(enabled = false) {
//            val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip
//
//            testLEDs.assertAllPixels(-1, 0)
//
//            // Temporary
//
//            // setTemporaryPixelColors with ColorContainer
//            testLEDs.setTemporaryPixelColors(listOf(10, 15, 20), ColorContainer(0xFF).prepare(testLEDs.numLEDs))
//
//            assertTrue { testLEDs.getTemporaryPixelColor(10) == 0xFF }
//            assertTrue { testLEDs.getTemporaryPixelColor(15) == 0xFF }
//            assertTrue { testLEDs.getTemporaryPixelColor(20) == 0xFF }
//            assertTrue { testLEDs.getTemporaryPixelColor(18) == 0 }
//            assertTrue { testLEDs.getProlongedPixelColor(10) == 0 }
//            assertTrue { testLEDs.getProlongedPixelColor(15) == 0 }
//            assertTrue { testLEDs.getProlongedPixelColor(20) == 0 }
//            assertTrue { testLEDs.getProlongedPixelColor(18) == 0 }
//
//            // setTemporaryPixelColors with Long
//            testLEDs.setTemporaryPixelColors(listOf(12, 17, 24), 0xFF00)
//
//            assertTrue { testLEDs.getTemporaryPixelColor(12) == 0xFF00 }
//            assertTrue { testLEDs.getTemporaryPixelColor(17) == 0xFF00 }
//            assertTrue { testLEDs.getTemporaryPixelColor(24) == 0xFF00 }
//            assertTrue { testLEDs.getTemporaryPixelColor(21) == 0 }
//            assertTrue { testLEDs.getProlongedPixelColor(12) == 0 }
//            assertTrue { testLEDs.getProlongedPixelColor(17) == 0 }
//            assertTrue { testLEDs.getProlongedPixelColor(24) == 0 }
//            assertTrue { testLEDs.getProlongedPixelColor(21) == 0 }
//
//            // setTemporaryPixelColors with IntRange and ColorContainer
//            testLEDs.setTemporaryPixelColors(30..40, ColorContainer(0xFF).prepare(testLEDs.numLEDs))
//
//            assertTrue { testLEDs.getTemporaryPixelColor(0) == 0 }
//            assertTrue { testLEDs.getTemporaryPixelColor(29) == 0 }
//            assertTrue { testLEDs.getTemporaryPixelColor(30) == 0xFF }
//            assertTrue { testLEDs.getTemporaryPixelColor(40) == 0xFF }
//            assertTrue { testLEDs.getTemporaryPixelColor(41) == 0 }
//            assertTrue { testLEDs.getTemporaryPixelColor(45) == 0 }
//            assertTrue { testLEDs.getProlongedPixelColor(0) == 0 }
//            assertTrue { testLEDs.getProlongedPixelColor(29) == 0 }
//            assertTrue { testLEDs.getProlongedPixelColor(30) == 0 }
//            assertTrue { testLEDs.getProlongedPixelColor(40) == 0 }
//            assertTrue { testLEDs.getProlongedPixelColor(41) == 0 }
//            assertTrue { testLEDs.getProlongedPixelColor(45) == 0 }
//
//            // setTemporaryPixelColors with IntRange and Long
//            testLEDs.setTemporaryPixelColors(30..40, 0xFF00)
//
//            assertTrue { testLEDs.getTemporaryPixelColor(0) == 0 }
//            assertTrue { testLEDs.getTemporaryPixelColor(29) == 0 }
//            assertTrue { testLEDs.getTemporaryPixelColor(30) == 0xFF00 }
//            assertTrue { testLEDs.getTemporaryPixelColor(40) == 0xFF00 }
//            assertTrue { testLEDs.getTemporaryPixelColor(41) == 0 }
//            assertTrue { testLEDs.getTemporaryPixelColor(45) == 0 }
//            assertTrue { testLEDs.getProlongedPixelColor(0) == 0 }
//            assertTrue { testLEDs.getProlongedPixelColor(29) == 0 }
//            assertTrue { testLEDs.getProlongedPixelColor(30) == 0 }
//            assertTrue { testLEDs.getProlongedPixelColor(40) == 0 }
//            assertTrue { testLEDs.getProlongedPixelColor(41) == 0 }
//            assertTrue { testLEDs.getProlongedPixelColor(45) == 0 }
//
//
//            // Prolonged
//
//            // setProlongedPixelColors with ColorContainer
//            testLEDs.setProlongedPixelColors(listOf(10, 15, 20), ColorContainer(0xFF).prepare(testLEDs.numLEDs))
//
//            assertTrue { testLEDs.getTemporaryPixelColor(10) == 0xFF }
//            assertTrue { testLEDs.getTemporaryPixelColor(15) == 0xFF }
//            assertTrue { testLEDs.getTemporaryPixelColor(20) == 0xFF }
//            assertTrue { testLEDs.getTemporaryPixelColor(18) == 0 }
//            assertTrue { testLEDs.getProlongedPixelColor(10) == 0xFF }
//            assertTrue { testLEDs.getProlongedPixelColor(15) == 0xFF }
//            assertTrue { testLEDs.getProlongedPixelColor(20) == 0xFF }
//            assertTrue { testLEDs.getProlongedPixelColor(18) == 0 }
//
//            // setProlongedPixelColors with Long
//            testLEDs.setProlongedPixelColors(listOf(12, 17, 24), 0xFF00)
//
//            assertTrue { testLEDs.getTemporaryPixelColor(12) == 0xFF00 }
//            assertTrue { testLEDs.getTemporaryPixelColor(17) == 0xFF00 }
//            assertTrue { testLEDs.getTemporaryPixelColor(24) == 0xFF00 }
//            assertTrue { testLEDs.getTemporaryPixelColor(21) == 0 }
//            assertTrue { testLEDs.getProlongedPixelColor(12) == 0xFF00 }
//            assertTrue { testLEDs.getProlongedPixelColor(17) == 0xFF00 }
//            assertTrue { testLEDs.getProlongedPixelColor(24) == 0xFF00 }
//            assertTrue { testLEDs.getProlongedPixelColor(21) == 0 }
//
//            // setProlongedPixelColors with IntRange and ColorContainer
//            testLEDs.setProlongedPixelColors(30..40, ColorContainer(0xFF).prepare(testLEDs.numLEDs))
//
//            assertTrue { testLEDs.getTemporaryPixelColor(0) == 0 }
//            assertTrue { testLEDs.getTemporaryPixelColor(29) == 0 }
//            assertTrue { testLEDs.getTemporaryPixelColor(30) == 0xFF }
//            assertTrue { testLEDs.getTemporaryPixelColor(40) == 0xFF }
//            assertTrue { testLEDs.getTemporaryPixelColor(41) == 0 }
//            assertTrue { testLEDs.getTemporaryPixelColor(45) == 0 }
//            assertTrue { testLEDs.getProlongedPixelColor(0) == 0 }
//            assertTrue { testLEDs.getProlongedPixelColor(29) == 0 }
//            assertTrue { testLEDs.getProlongedPixelColor(30) == 0xFF }
//            assertTrue { testLEDs.getProlongedPixelColor(40) == 0xFF }
//            assertTrue { testLEDs.getProlongedPixelColor(41) == 0 }
//            assertTrue { testLEDs.getProlongedPixelColor(45) == 0 }
//
//            // setProlongedPixelColors with IntRange and Long
//            testLEDs.setProlongedPixelColors(30..40, 0xFF00)
//
//            assertTrue { testLEDs.getTemporaryPixelColor(0) == 0 }
//            assertTrue { testLEDs.getTemporaryPixelColor(29) == 0 }
//            assertTrue { testLEDs.getTemporaryPixelColor(30) == 0xFF00 }
//            assertTrue { testLEDs.getTemporaryPixelColor(40) == 0xFF00 }
//            assertTrue { testLEDs.getTemporaryPixelColor(41) == 0 }
//            assertTrue { testLEDs.getTemporaryPixelColor(45) == 0 }
//            assertTrue { testLEDs.getProlongedPixelColor(0) == 0 }
//            assertTrue { testLEDs.getProlongedPixelColor(29) == 0 }
//            assertTrue { testLEDs.getProlongedPixelColor(30) == 0xFF00 }
//            assertTrue { testLEDs.getProlongedPixelColor(40) == 0xFF00 }
//            assertTrue { testLEDs.getProlongedPixelColor(41) == 0 }
//            assertTrue { testLEDs.getProlongedPixelColor(45) == 0 }
//        }
//
//        "revert pixel".config(enabled = false) {
//            val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip
//
//            testLEDs.assertAllPixels(-1, 0)
//
//            testLEDs.setTemporaryStripColor(0xFF)
//
//            testLEDs.assertAllProlongedPixels(0)
//            testLEDs.assertAllTemporaryPixels(0xFF)
//
//
//            @Suppress("EmptyRange")
//            testLEDs.revertPixels(5..2)
//
//            testLEDs.assertAllProlongedPixels(0)
//            testLEDs.assertAllTemporaryPixels(0xFF)
//
//
//            testLEDs.revertPixel(40)
//
//            assertTrue { testLEDs.getTemporaryPixelColor(0) == 0xFF }
//            assertTrue { testLEDs.getTemporaryPixelColor(30) == 0xFF }
//            assertTrue { testLEDs.getTemporaryPixelColor(39) == 0xFF }
//            assertTrue { testLEDs.getTemporaryPixelColor(40) == 0 }
//            assertTrue { testLEDs.getTemporaryPixelColor(41) == 0xFF }
//            assertTrue { testLEDs.getTemporaryPixelColor(45) == 0xFF }
//            testLEDs.assertAllProlongedPixels(0)
//
//
//            testLEDs.revertPixels(listOf(10, 15, 30))
//
//            assertTrue { testLEDs.getTemporaryPixelColor(0) == 0xFF }
//            assertTrue { testLEDs.getTemporaryPixelColor(9) == 0xFF }
//            assertTrue { testLEDs.getTemporaryPixelColor(10) == 0 }
//            assertTrue { testLEDs.getTemporaryPixelColor(11) == 0xFF }
//            assertTrue { testLEDs.getTemporaryPixelColor(14) == 0xFF }
//            assertTrue { testLEDs.getTemporaryPixelColor(15) == 0 }
//            assertTrue { testLEDs.getTemporaryPixelColor(16) == 0xFF }
//            assertTrue { testLEDs.getTemporaryPixelColor(29) == 0xFF }
//            assertTrue { testLEDs.getTemporaryPixelColor(30) == 0 }
//            assertTrue { testLEDs.getTemporaryPixelColor(31) == 0xFF }
//            assertTrue { testLEDs.getTemporaryPixelColor(39) == 0xFF }
//            assertTrue { testLEDs.getTemporaryPixelColor(40) == 0 }
//            assertTrue { testLEDs.getTemporaryPixelColor(41) == 0xFF }
//            assertTrue { testLEDs.getTemporaryPixelColor(45) == 0xFF }
//            testLEDs.assertAllProlongedPixels(0)
//
//
//            testLEDs.revertPixels(5..7)
//
//            assertTrue { testLEDs.getTemporaryPixelColor(0) == 0xFF }
//            assertTrue { testLEDs.getTemporaryPixelColor(4) == 0xFF }
//            assertTrue { testLEDs.getTemporaryPixelColor(5) == 0 }
//            assertTrue { testLEDs.getTemporaryPixelColor(6) == 0 }
//            assertTrue { testLEDs.getTemporaryPixelColor(7) == 0 }
//            assertTrue { testLEDs.getTemporaryPixelColor(8) == 0xFF }
//            assertTrue { testLEDs.getTemporaryPixelColor(9) == 0xFF }
//            assertTrue { testLEDs.getTemporaryPixelColor(10) == 0 }
//            assertTrue { testLEDs.getTemporaryPixelColor(11) == 0xFF }
//            assertTrue { testLEDs.getTemporaryPixelColor(14) == 0xFF }
//            assertTrue { testLEDs.getTemporaryPixelColor(15) == 0 }
//            assertTrue { testLEDs.getTemporaryPixelColor(16) == 0xFF }
//            assertTrue { testLEDs.getTemporaryPixelColor(29) == 0xFF }
//            assertTrue { testLEDs.getTemporaryPixelColor(30) == 0 }
//            assertTrue { testLEDs.getTemporaryPixelColor(31) == 0xFF }
//            assertTrue { testLEDs.getTemporaryPixelColor(39) == 0xFF }
//            assertTrue { testLEDs.getTemporaryPixelColor(40) == 0 }
//            assertTrue { testLEDs.getTemporaryPixelColor(41) == 0xFF }
//            assertTrue { testLEDs.getTemporaryPixelColor(45) == 0xFF }
//            testLEDs.assertAllProlongedPixels(0)
//        }
//
//        "set strip color".config(enabled = false) {
//            val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip
//
//            testLEDs.assertAllPixels(-1, 0)
//
//            // Temporary
//
//            // setTemporaryStripColor with ColorContainer
//            testLEDs.setTemporaryStripColor(ColorContainer(0xFF).prepare(testLEDs.numLEDs))
//
//            testLEDs.assertAllTemporaryPixels(0xFF)
//            testLEDs.assertAllProlongedPixels(0)
//
//
//            // setTemporaryStripColor with Long
//            testLEDs.setTemporaryStripColor(0xFFFF)
//
//            testLEDs.assertAllTemporaryPixels(0xFFFF)
//            testLEDs.assertAllProlongedPixels(0)
//
//
//            // Prolonged
//
//            // setProlongedStripColor with ColorContainer
//            testLEDs.setProlongedStripColor(ColorContainer(0xFF).prepare(testLEDs.numLEDs))
//
//            testLEDs.assertAllPixels(-1, 0xFF)
//
//
//            // setProlongedStripColor with Long
//            testLEDs.setProlongedStripColor(0xFFFF)
//
//            testLEDs.assertAllPixels(-1, 0xFFFF)
//        }
//
//        "set strip color with offset".config(enabled = false) {
//            val testLEDs = EmulatedAnimatedLEDStrip(5).wholeStrip
//
//            testLEDs.setTemporaryStripColorWithOffset(ColorContainer(0xFF, 0x0, 0xFF00, 0xFF00FF, 0xFFFFFF).prepare(5), 2)
//
//            assertTrue { testLEDs.getTemporaryPixelColor(0) == 0xFF00FF }
//            assertTrue { testLEDs.getTemporaryPixelColor(1) == 0xFFFFFF }
//            assertTrue { testLEDs.getTemporaryPixelColor(2) == 0xFF }
//            assertTrue { testLEDs.getTemporaryPixelColor(3) == 0 }
//            assertTrue { testLEDs.getTemporaryPixelColor(4) == 0xFF00 }
//            testLEDs.assertAllProlongedPixels(0)
//
//            testLEDs.setProlongedStripColorWithOffset(ColorContainer(0xFF, 0x0, 0xFF00, 0xFF00FF, 0xFFFFFF).prepare(5), 3)
//            assertTrue { testLEDs.getTemporaryPixelColor(0) == 0xFF00 }
//            assertTrue { testLEDs.getTemporaryPixelColor(1) == 0xFF00FF }
//            assertTrue { testLEDs.getTemporaryPixelColor(2) == 0xFFFFFF }
//            assertTrue { testLEDs.getTemporaryPixelColor(3) == 0xFF }
//            assertTrue { testLEDs.getTemporaryPixelColor(4) == 0 }
//            assertTrue { testLEDs.getProlongedPixelColor(0) == 0xFF00 }
//            assertTrue { testLEDs.getProlongedPixelColor(1) == 0xFF00FF }
//            assertTrue { testLEDs.getProlongedPixelColor(2) == 0xFFFFFF }
//            assertTrue { testLEDs.getProlongedPixelColor(3) == 0xFF }
//            assertTrue { testLEDs.getProlongedPixelColor(4) == 0 }
//        }
//
//        "clear".config(enabled = false) {
//            val testLEDs = EmulatedAnimatedLEDStrip(50)
//
//            testLEDs.wholeStrip.setProlongedStripColor(0xFF)
//            testLEDs.wholeStrip.assertAllPixels(-1, 0xFF)
//
//            testLEDs.clear()
//            testLEDs.wholeStrip.assertAllPixels(-1, 0)
//
//            testLEDs.wholeStrip.setProlongedStripColor(0xFF)
//            testLEDs.wholeStrip.assertAllPixels(-1, 0xFF)
//
//            testLEDs.wholeStrip.clear()
//            testLEDs.wholeStrip.assertAllPixels(-1, 0)
//        }
//
//        "get pixel color".config(enabled = false) {
//            val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip
//
//            testLEDs.setTemporaryPixelColor(15, 0xFF)
//
//            assertTrue { testLEDs.getTemporaryPixelColor(15) == 0xFF }
//            assertTrue { testLEDs.getProlongedPixelColor(15) == 0 }
//
//            testLEDs.setProlongedPixelColor(15, 0xFF00)
//
//            assertTrue { testLEDs.getTemporaryPixelColor(15) == 0xFF00 }
//            assertTrue { testLEDs.getProlongedPixelColor(15) == 0xFF00 }
//
//            assertFailsWith<IllegalArgumentException> { testLEDs.getTemporaryPixelColor(50) }
//            assertFailsWith<IllegalArgumentException> { testLEDs.getProlongedPixelColor(50) }
//        }
//
//        "get pixel color or null".config(enabled = false) {
//            val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip
//
//            testLEDs.setTemporaryPixelColor(15, 0xFF)
//
//            assertTrue { testLEDs.getTemporaryPixelColorOrNull(15) == 0xFF }
//            assertTrue { testLEDs.getProlongedPixelColorOrNull(15) == 0 }
//
//
//            testLEDs.setProlongedPixelColor(15, 0xFF)
//
//            assertTrue { testLEDs.getTemporaryPixelColorOrNull(15) == 0xFF }
//            assertTrue { testLEDs.getProlongedPixelColorOrNull(15) == 0xFF }
//
//            assertNull(testLEDs.getTemporaryPixelColorOrNull(50))
//            assertNull(testLEDs.getProlongedPixelColorOrNull(50))
//        }
//
////        "fade pixel" {
////            val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip
////
////            testLEDs.fadePixel(50, CCBlue.color.toInt())
////        }
//
//        "get subsection" {
//            val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip
//
//            val section1 = testLEDs.getSubSection(5, 10)
//            val section2 = testLEDs.getSubSection(5, 10)
//
//            assertTrue { section1 === section2 }
//        }
//
//        "start animation" {
//            val testLEDs = EmulatedAnimatedLEDStrip(50)
//            val anim1 = AnimationToRunParams().animation("Alternate")
//            testLEDs.startAnimation(anim1, "TEST1")
//            delay(100)
//            assertTrue { testLEDs.runningAnimations.map.containsKey("TEST1") }
////            assertTrue { testLEDs.runningAnimations["TEST1"]?.data == anim1 }
//            testLEDs.endAnimation(EndAnimation("TEST1"))
//            testLEDs.runningAnimations["TEST1"]?.job?.join()
//
//            val anim2 = AnimationToRunParams().animation("Wipe").addColor(0xFF)
//            testLEDs.startAnimation(anim2, "TEST2")
//            delay(100)
//            testLEDs.runningAnimations["TEST2"]?.job?.join()
//            testLEDs.wholeStrip.assertAllPixels(-1, 0xFF)
//
//            testLEDs.createSection("Sect", 15, 40)
//            val anim3 = AnimationToRunParams().animation("Wipe").addColor(0xFFFF).section("Sect")
//            testLEDs.startAnimation(anim3, "TEST3")
//            delay(100)
//            testLEDs.runningAnimations["TEST3"]?.job?.join()
//            testLEDs.wholeStrip.assertPixels(0..14, -1, 0xFF)
//            testLEDs.wholeStrip.assertPixels(15..40, -1, 0xFFFF)
//            testLEDs.wholeStrip.assertPixels(41..49, -1, 0xFF)
//        }
//
//        "start animation bad animation" {
//            val testLEDs = EmulatedAnimatedLEDStrip(50)
//            assertNull(testLEDs.startAnimation(AnimationToRunParams().animation("Im not an animation"), "TEST"))
//        }
//
//        "run parallel".config(enabled = false) {
//            val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip
//            val anim = AnimationToRunParams().animation("Color")
//
//            @Suppress("EXPERIMENTAL_API_USAGE")
//            val pool = newSingleThreadContext("Test Pool")
//
//            // Default parameters
//            testLEDs.runParallel(anim, this)
//
//            // Set parameters
//            val runningAnim = testLEDs.runParallel(anim, this, runCount = -1)
//            runningAnim?.job?.cancel()
//
//            // Test runParallelAndJoin
//            val badAnim = AnimationToRunParams().animation("NonexistentAnimation")
//            testLEDs.runParallelAndJoin(this, Pair(anim, testLEDs), Pair(badAnim, testLEDs))
//
//            Unit
//        }
//
//        "run sequential".config(enabled = false) {
//            val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip
//
//            // Continuous false (default)
//            testLEDs.runSequential(AnimationToRunParams().animation("Color"))
//
//            // Bad Animation
//            testLEDs.runSequential(AnimationToRunParams().animation("Im not an animation"))
//        }
//    })
