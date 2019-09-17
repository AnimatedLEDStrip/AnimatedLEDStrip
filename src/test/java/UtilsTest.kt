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


import animatedledstrip.colors.ccpresets.CCBlack
import animatedledstrip.colors.ccpresets.CCBlue
import animatedledstrip.utils.blend
import animatedledstrip.utils.parseHex
import animatedledstrip.utils.parseHexOrDefault
import animatedledstrip.utils.toARGB
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


}