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

package animatedledstrip.test

import animatedledstrip.animationutils.AnimationData
import animatedledstrip.leds.StripInfo
import animatedledstrip.leds.iterateOver
import animatedledstrip.utils.*
import com.google.gson.JsonSyntaxException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Suppress("RedundantInnerClassModifier", "ClassName")
class UtilsTest {

    @Nested
    inner class `test blend` {
        @Test
        fun `no overlay returns existing`() {
            blend(0x0, 0xFF, 0) shouldBe 0x0
        }

        @Test
        fun `full overlay returns overlay`() {
            blend(0x0, 0xFF, 255) shouldBe 0xFF
        }

        @Test
        fun `blend blue with yellow`() {
            blend(0xFF, 0xFFFF, 51) shouldBe 0x34FF
        }
    }

    @Nested
    inner class `test parseHex` {

        @Test
        fun `parses to correct hex`() {
            parseHex("FFFF") shouldBe 0xFFFF
            parseHex("fFFf") shouldBe 0xFFFF
            parseHex("FED43210") shouldBe 0xFED43210
            parseHex("CBA98765") shouldBe 0xCBA98765
        }

        @Test
        fun `0x doesn't matter`() {
            parseHex("0xFF") shouldBe 0xFF
        }

        @Test
        fun `capitalization doesn't matter`() {
            parseHex("FE4d") shouldBe 0xFE4D
        }

        @Test
        fun `throws exception on bad input`() {
            shouldThrow<NumberFormatException> { parseHex("0xG") }
        }
    }

    @Nested
    inner class `test parseHexOrDefault` {

        @Test
        fun `parses to correct hex`() {
            parseHexOrDefault("FFFF") shouldBe 0xFFFF
            parseHexOrDefault("fFFf") shouldBe 0xFFFF
            parseHexOrDefault("FED43210") shouldBe 0xFED43210
            parseHexOrDefault("CBA98765") shouldBe 0xCBA98765
        }

        @Test
        fun `0x doesn't matter`() {
            parseHexOrDefault("0xAA") shouldBe 0xAA
        }

        @Test
        fun `capitalization doesn't matter`() {
            parseHex("aeC5") shouldBe 0xAEC5
        }

        @Test
        fun `return default on bad input`() {
            parseHexOrDefault("0xABT") shouldBe 0
            parseHexOrDefault("0xGF3", 0xF1E) shouldBe 0xF1E
        }
    }

    @Nested
    inner class `test remove0xPrefix` {
        @Test
        fun `prefix is removed`() {
            "0x55".remove0xPrefix() shouldBe "55"
        }

        @Test
        fun `capitalization doesn't matter`() {
            "0X55".remove0xPrefix() shouldBe "55"
        }

        @Test
        fun `0x not at beginning is not removed`() {
            "340x55".remove0xPrefix() shouldBe "340X55"
        }

        @Test
        fun `string containing only 0x returns empty string`() {
            "0x".remove0xPrefix() shouldBe ""
        }
    }

    @Test
    fun testToUTF8() {
        assertFailsWith<IllegalStateException> {
            val arr: ByteArray? = null
            arr.toUTF8()
        }

        assertTrue { ByteArray(5).toUTF8().length == 5 }
    }

    @Test
    fun testStripInfo() {
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

    @Test
    fun testAnimationDataToEndAnimation() {
        val data = AnimationData(id = "Test")
        val end = data.endAnimation()
        assertTrue { data.id == end.id }
    }

    @Test
    fun testGetDataTypePrefix() {
        val info1 = StripInfo()
        val infoBytes = info1.jsonString()
        assertTrue { infoBytes.getDataTypePrefix() == "SINF" }

        val animTest = AnimationData()
        val animBytes = animTest.jsonString()
        assertTrue { animBytes.getDataTypePrefix() == "DATA" }

        assertFailsWith<IllegalStateException> {
            val nullBytes: String? = null
            nullBytes.getDataTypePrefix()
        }
    }

    @Test
    fun testBadJson() {
        assertFailsWith<IllegalStateException> {
            val nullBytes: String? = null
            nullBytes.jsonToAnimationData()
        }

        assertFailsWith<JsonSyntaxException> {
            val incompleteJson = "DATA:{test:5"
            incompleteJson.jsonToAnimationData()
        }
    }

    @Test
    fun testIterateOver() {
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
}

