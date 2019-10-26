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


import animatedledstrip.animationutils.*
import animatedledstrip.colors.ColorContainer
import animatedledstrip.colors.ccpresets.CCBlack
import animatedledstrip.colors.ccpresets.CCBlue
import animatedledstrip.leds.StripInfo
import animatedledstrip.utils.*
import org.junit.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class UtilsTest {

    @Test
    fun testParseHex() {
        assertTrue { parseHex("0xFF") == 0xFFL }
        assertTrue { parseHex("FFFF") == 0xFFFFL }
        assertTrue { parseHex("FF0000") == 0xFF0000L }
        assertFailsWith<NumberFormatException> { parseHex("0xG") }
    }

    @Test
    fun testParseHexOrDefault() {
        assertTrue { parseHexOrDefault("0xFF") == 0xFFL }
        assertTrue { parseHexOrDefault("FFFF") == 0xFFFFL }
        assertTrue { parseHexOrDefault("FF0000") == 0xFF0000L }
        assertTrue { parseHexOrDefault("0xFF", 10) == 0xFFL }
        assertTrue { parseHexOrDefault("FFFF", 20) == 0xFFFFL }
        assertTrue { parseHexOrDefault("FF0000", 15) == 0xFF0000L }
        assertTrue { parseHexOrDefault("0xG") == 0L }
        assertTrue { parseHexOrDefault("0xG", 15) == 15L }
    }


    @Test
    fun testBlend() {
        blend(CCBlack.color, CCBlue.color, 0)
        blend(CCBlack.color, CCBlue.color, 255)
    }

    @Test
    fun testToARGB() {
        assertTrue { CCBlack.color.toARGB() == 0xFF000000.toInt() }
        assertTrue { 0xFF00FF.toARGB() == 0xFFFF00FF.toInt() }
    }

    @Test
    fun testToColorContainer() {
        assertTrue { 0xFF.toColorContainer() == ColorContainer(0xFF) }
        assertTrue { 0xFFFFFFL.toColorContainer() == ColorContainer(0xFFFFFF) }
    }

    @Test
    fun testAnimationDataJson() {
        val testAnimation = AnimationData().animation(Animation.STACK)
            .color(ColorContainer(0xFF, 0xFFFF).prepare(5), index = 0)
            .color(0xFF, index = 1)
            .color(0xFF, index = 2)
            .color(0xFF, index = 3)
            .color(0xFF, index = 4)
            .continuous(true)
            .delay(50)
            .direction(Direction.FORWARD)
            .id("TEST")
            .spacing(5)

        val testBytes = testAnimation.json()

        val testAnimation2 = testBytes.jsonToAnimationData(testBytes.size)

        assertTrue { testAnimation == testAnimation2 }

        assertFailsWith<IllegalStateException> {
            val nullBytes: ByteArray? = null
            nullBytes.jsonToAnimationData(0)
        }
    }

    @Test
    fun testStripInfoJson() {
        val info1 = StripInfo()
        val infoBytes = info1.json()

        val info2 = infoBytes.jsonToStripInfo(infoBytes.size)

        assertTrue { info1 == info2 }

        assertFailsWith<IllegalStateException> {
            val nullBytes: ByteArray? = null
            nullBytes.jsonToStripInfo(0)
        }
    }

    @Test
    fun testGetDataTypePrefix() {
        val info1 = StripInfo()
        val infoBytes = info1.json()
        assertTrue { infoBytes.getDataTypePrefix() == "INFO" }

        val animTest = AnimationData()
        val animBytes = animTest.json()
        assertTrue { animBytes.getDataTypePrefix() == "DATA" }

    }
}