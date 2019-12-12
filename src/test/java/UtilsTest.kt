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

package animatedledstrip.test

import animatedledstrip.animationutils.*
import animatedledstrip.animationutils.animationinfo.Meteor
import animatedledstrip.colors.ColorContainer
import animatedledstrip.colors.ccpresets.CCBlack
import animatedledstrip.colors.ccpresets.CCBlue
import animatedledstrip.leds.StripInfo
import animatedledstrip.leds.iterateOver
import animatedledstrip.leds.iterateOverPixels
import animatedledstrip.leds.iterateOverPixelsReverse
import animatedledstrip.utils.*
import org.junit.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
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

        val testAnimation2 = testBytes.toUTF8(testBytes.size).jsonToAnimationData()

        assertTrue { testAnimation == testAnimation2 }

        println(gson.toJson(testAnimation))

        assertFailsWith<IllegalStateException> {
            val nullBytes: String? = null
            nullBytes.jsonToAnimationData()
        }
    }

    @Test
    fun testStripInfoJson() {
        val info1 = StripInfo(
            numLEDs = 10,
            pin = 15,
            imageDebugging = true,
            fileName = "test.csv",
            rendersBeforeSave = 100,
            threadCount = 200
        )
        val infoBytes = info1.json()

        val info2 = infoBytes.toUTF8(infoBytes.size).jsonToStripInfo()

        assertTrue { info1 == info2 }

        assertFailsWith<IllegalStateException> {
            val nullBytes: String? = null
            nullBytes.jsonToStripInfo()
        }
    }

    @Test
    fun testStripInfo() {
        val info = StripInfo(
            numLEDs = 10,
            pin = 15,
            imageDebugging = true,
            fileName = "test.csv",
            rendersBeforeSave = 100,
            threadCount = 200
        )

        assertTrue { info.numLEDs == 10 }
        assertTrue { info.pin == 15 }
        assertTrue { info.imageDebugging }
        assertTrue { info.fileName == "test.csv" }
        assertTrue { info.rendersBeforeSave == 100 }
        assertTrue { info.threadCount == 200 }
    }

    @Test
    fun testGetDataTypePrefix() {
        val info1 = StripInfo()
        val infoBytes = info1.jsonString()
        assertTrue { infoBytes.getDataTypePrefix() == "INFO" }

        val animTest = AnimationData()
        val animBytes = animTest.jsonString()
        assertTrue { animBytes.getDataTypePrefix() == "DATA" }

        assertFailsWith<IllegalStateException> {
            val nullBytes: String? = null
            nullBytes.getDataTypePrefix()
        }
    }

    @Test
    fun testGetAnimation() {
        val testStr = "Sparkle"
        assertTrue { testStr.getAnimation() == Animation.SPARKLE }
        assertTrue { testStr.getAnimationOrNull() == Animation.SPARKLE }


        val badTestStr = "Test"

        assertFailsWith<KotlinNullPointerException> { badTestStr.getAnimation() }
        assertNull(badTestStr.getAnimationOrNull())

        val testSpacedStr = "sparkle TO color"

        assertTrue { testSpacedStr.getAnimation() == Animation.SPARKLETOCOLOR }
        assertTrue { testSpacedStr.getAnimationOrNull() == Animation.SPARKLETOCOLOR }

        val testSpacedStr2 = "s p a r k l e T O c o l o r"

        assertTrue { testSpacedStr2.getAnimation() == Animation.SPARKLETOCOLOR }
        assertTrue { testSpacedStr2.getAnimationOrNull() == Animation.SPARKLETOCOLOR }
    }

    @Test
    fun testAnimationInfo() {
        assertTrue { Animation.METEOR.info() == Meteor }
        assertTrue { Animation.METEOR.infoOrNull() == Meteor }

        assertFailsWith<KotlinNullPointerException> { Animation.ENDANIMATION.info() }
        assertNull(Animation.ENDANIMATION.infoOrNull())
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

    @Test
    fun testIterateOverPixels() {
        val testVals1 = mutableListOf(false, false, false, false)
        val anim = AnimationData().startPixel(0).endPixel(3)
        iterateOverPixels(anim) {
            testVals1[it] = true
        }

        assertTrue(testVals1[0])
        assertTrue(testVals1[1])
        assertTrue(testVals1[2])
        assertTrue(testVals1[3])

        val testVals2 = mutableListOf(false, false, false, false)
        iterateOverPixelsReverse(anim) {
            testVals2[it] = true
        }

        assertTrue(testVals2[0])
        assertTrue(testVals2[1])
        assertTrue(testVals2[2])
        assertTrue(testVals2[3])
    }
}