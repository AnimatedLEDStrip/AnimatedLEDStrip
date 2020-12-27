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
import animatedledstrip.leds.stripmanagement.StripInfo
import animatedledstrip.utils.*
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UtilsTest : StringSpec(
    {
        "blend no overlay returns existing" {
            blend(0x0, 0xFF, 0) shouldBe 0x0
        }

        "full overlay returns overlay" {
            blend(0x0, 0xFF, 255) shouldBe 0xFF
        }

        "blend blue with yellow" {
            blend(0xFF, 0xFFFF, 51) shouldBe 0x34FF
        }

        "parse hex" {
            parseHex("FFFF") shouldBe 0xFFFF
            parseHex("fFFf") shouldBe 0xFFFF
            parseHex("FED432") shouldBe 0xFED432
            parseHex("CBA987") shouldBe 0xCBA987
        }

        "parse hex with 0x" {
            parseHex("0xFF") shouldBe 0xFF
        }

        "parse hex with capitalization" {
            parseHex("FE4d") shouldBe 0xFE4D
        }

        "parse hex bad input" {
            shouldThrow<NumberFormatException> { parseHex("0xG") }
        }

        "parse hex or default" {
            parseHexOrDefault("FFFF") shouldBe 0xFFFF
            parseHexOrDefault("fFFf") shouldBe 0xFFFF
            parseHexOrDefault("FED432") shouldBe 0xFED432
            parseHexOrDefault("CBA987") shouldBe 0xCBA987
        }

        "parse hex or default with 0x" {
            parseHexOrDefault("0xAA") shouldBe 0xAA
        }

        "parse hex or default with capitalization" {
            parseHexOrDefault("aeC5") shouldBe 0xAEC5
        }

        "parse hex or default bad input" {
            parseHexOrDefault("0xABT") shouldBe 0
            parseHexOrDefault("0xGF3", 0xF1E) shouldBe 0xF1E
        }

        "remove 0x prefix" {
            "0x55".remove0xPrefix() shouldBe "55"
            "0x".remove0xPrefix() shouldBe ""
        }

        "remove 0x with capitalization" {
            "0X55".remove0xPrefix() shouldBe "55"
        }

        "don't remove 0x if not at beginning" {
            "340x55".remove0xPrefix() shouldBe "340X55"
        }

        "to utf8" {
            assertFailsWith<IllegalArgumentException> {
                val arr: ByteArray? = null
                arr.toUTF8()
            }

            assertTrue { ByteArray(5).toUTF8().length == 5 }
        }

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
