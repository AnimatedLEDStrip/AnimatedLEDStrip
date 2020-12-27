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
// *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// *  THE SOFTWARE.
// */
//
//package animatedledstrip.test.leds
//
//import animatedledstrip.leds.animationmanagement.AnimationToRunParams
//import animatedledstrip.leds.animationmanagement.animation
//import animatedledstrip.leds.animationmanagement.delay
//import animatedledstrip.leds.animationmanagement.runCount
//import animatedledstrip.leds.emulated.EmulatedAnimatedLEDStrip
//import animatedledstrip.test.assertLogs
//import animatedledstrip.test.startLogCapture
//import animatedledstrip.test.stopLogCapture
//import io.kotest.core.spec.style.FunSpec
//import io.kotest.matchers.booleans.shouldBeTrue
//import io.kotest.matchers.maps.shouldContainKey
//import io.kotest.matchers.shouldBe
//import io.kotest.matchers.shouldNotBe
//import kotlinx.coroutines.delay
//import org.pmw.tinylog.Level
//import kotlin.test.assertTrue
//
//class AnimatedLEDStripTest : FunSpec({
//    test("start and end animation callbacks") {
//        var indicator1 = false
//        var indicator2 = false
//        val testLEDs = EmulatedAnimatedLEDStrip(50)
//
//        testLEDs.startAnimationCallback = {
//            indicator1 = true
//            Unit
//        }
//
//        testLEDs.endAnimationCallback = {
//            indicator2 = true
//            Unit
//        }
//
//        testLEDs.startAnimation(AnimationToRunParams().animation("Alternate").delay(10).runCount(1))
//
//        delay(500)
//
//        indicator1.shouldBeTrue()
//        indicator2.shouldBeTrue()
//
//        delay(10000)
//    }
//
//    test("create section") {
//        val testLEDs = EmulatedAnimatedLEDStrip(50)
//        var indicator = false
//        testLEDs.createSection("Test", 5, 20)
//
//        testLEDs.sections.size shouldBe 2
//        testLEDs.sections.shouldContainKey("Test")
//
//        testLEDs.newSectionCallback = {
//            indicator = true
//            Unit
//        }
//
//        val newSection = testLEDs.Section("Sect", 5, 20)
//        testLEDs.createSection(newSection)
//
//        indicator.shouldBeTrue()
//        testLEDs.sections.size shouldBe 3
//        testLEDs.sections.shouldContainKey("Sect")
//        assertTrue { testLEDs.sections["Sect"] !== newSection }
//    }
//
//    test("get section") {
//        val testLEDs = EmulatedAnimatedLEDStrip(50)
//        testLEDs.createSection("Test", 5, 20)
//
//        testLEDs.getSection("Test") shouldNotBe  testLEDs.wholeStrip
//        testLEDs.getSection("Test").startPixel shouldBe  5
//        testLEDs.getSection("Test").endPixel shouldBe  20
//        testLEDs.wholeStrip.getSection("Test") shouldNotBe testLEDs.wholeStrip
//        testLEDs.wholeStrip.getSection("Test").startPixel shouldBe 5
//        testLEDs.wholeStrip.getSection("Test").endPixel shouldBe 20
//
//        startLogCapture()
//
//        assertTrue { testLEDs.getSection("Other") === testLEDs.wholeStrip }
//        assertLogs(setOf(Pair(Level.WARNING, "Could not find section Other, defaulting to whole strip")))
//
//        assertTrue { testLEDs.wholeStrip.getSection("Other") === testLEDs.wholeStrip }
//
//        stopLogCapture()
//    }
//
//    test("end animations that are not running") {
//        val testLEDs = EmulatedAnimatedLEDStrip(50)
//
//        testLEDs.endAnimation(null)
//
//        startLogCapture()
//
//        testLEDs.endAnimation("not_running")
//
//        assertLogs(setOf(Pair(Level.WARNING, "Animation not_running is not running")))
//
//        stopLogCapture()
//    }
//})