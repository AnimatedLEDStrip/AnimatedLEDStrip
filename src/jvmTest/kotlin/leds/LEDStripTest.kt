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
//import animatedledstrip.leds.stripmanagement.StripInfo
//import animatedledstrip.leds.emulated.EmulatedAnimatedLEDStrip
//import animatedledstrip.leds.withPixelLock
//import kotlinx.coroutines.delay
//import io.kotest.core.spec.style.StringSpec
//import java.nio.file.Files
//import java.nio.file.Paths
//import kotlin.test.assertFalse
//import kotlin.test.assertTrue
//
//class LEDStripTest : StringSpec(
//    {
//        "toggle render" {
//            val testLEDs = EmulatedAnimatedLEDStrip(50)
//
//            delay(1000)
//            assertTrue { testLEDs.rendering }
//            testLEDs.toggleRender()
//            delay(1000)
//            assertFalse { testLEDs.rendering }
//            testLEDs.toggleRender()
//            delay(1000)
//            assertTrue { testLEDs.rendering }
//        }
//
//        "with pixel lock" {
//            val testLEDs = EmulatedAnimatedLEDStrip(50)
//
//            var testVal1 = false
//            var testVal2 = true
//
//            testLEDs.withPixelLock(10) { testVal1 = true; Unit }
//
//            // Test trying to get a lock on a non-existent pixel
//            testLEDs.withPixelLock(50) { testVal2 = false; Unit }
//
//            assertTrue(testVal1)
//            assertTrue(testVal2)
//        }
//
//        "image debugging" {
//            // Test file name
//            val info1 = StripInfo(numLEDs = 50, imageDebugging = true, fileName = "test1.csv")
//            val leds1 = EmulatedAnimatedLEDStrip(info1)
//            delay(5000)
//            leds1.toggleRender()
//            delay(1000)
//            assertTrue { Files.exists(Paths.get("test1.csv")) }
////            Files.delete(Paths.get("test1.csv"))
//
//            // Test file name without .csv extension
//            val info2 = StripInfo(numLEDs = 50, imageDebugging = true, fileName = "test2")
//            val leds2 = EmulatedAnimatedLEDStrip(info2)
//            delay(5000)
//            leds2.toggleRender()
//            delay(1000)
//            assertTrue { Files.exists(Paths.get("test2.csv")) }
////            Files.delete(Paths.get("test2.csv"))
//
//            // Test saving renders to file
//            val info3 = StripInfo(numLEDs = 50, imageDebugging = true, fileName = "test3.csv", rendersBeforeSave = 100)
//            val leds3 = EmulatedAnimatedLEDStrip(info3)
//            delay(10000)
//            leds3.toggleRender()
//            delay(1000)
////            Files.delete(Paths.get("test3.csv"))
//        }
//    })
