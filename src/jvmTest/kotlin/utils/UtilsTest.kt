/*
 *  Copyright (c) 2018-2020 AnimatedLEDStrip
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

package animatedledstrip.test.utils

import animatedledstrip.leds.animationmanagement.AnimationToRunParams
import animatedledstrip.leds.animationmanagement.endAnimation
import animatedledstrip.leds.animationmanagement.iterateOver
import animatedledstrip.leds.stripmanagement.StripInfo
import io.kotest.core.spec.style.StringSpec
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UtilsTest : StringSpec(
    {

        "strip info" {
            val info = StripInfo(
                numLEDs = 10,
                pin = 15,
                imageDebugging = true,
                fileName = "test.csv",
                rendersBeforeSave = 100,
                threadCount = 200,
            )

            assertTrue { info.numLEDs == 10 }
            assertTrue { info.pin == 15 }
            assertTrue { info.imageDebugging }
            assertTrue { info.fileName == "test.csv" }
            assertTrue { info.rendersBeforeSave == 100 }
            assertTrue { info.threadCount == 200 }
        }

        "animation data to end animation" {
            val data = AnimationToRunParams(id = "Test")
            val end = data.endAnimation()
            assertTrue { data.id == end.id }
        }

        "iterate over" {
            val testVals1 = mutableListOf(false, false, false, false)
            iterateOver(0..3) {
                testVals1[it] = true
            }

            assertTrue(testVals1[0])
            assertTrue(testVals1[1])
            assertTrue(testVals1[2])
            assertTrue(testVals1[3])

            val testVals2 = mutableListOf(false, false, false, false)
            iterateOver(listOf(3, 1, 2)) {
                testVals2[it] = true
            }

            assertFalse(testVals2[0])
            assertTrue(testVals2[1])
            assertTrue(testVals2[2])
            assertTrue(testVals2[3])
        }
    })
