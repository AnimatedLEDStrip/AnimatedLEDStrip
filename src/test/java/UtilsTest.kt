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


import animatedledstrip.ccpresets.CCBlack
import animatedledstrip.ccpresets.CCBlue
import animatedledstrip.leds.blend
import animatedledstrip.leds.parseHex
import org.junit.Ignore
import org.junit.Test
import org.pmw.tinylog.Configurator
import org.pmw.tinylog.Level
import kotlin.test.assertTrue

class UtilsTest {

    @Test
    fun testParseHex() {
        assertTrue { parseHex("0xFF") == 0xFFL }
        assertTrue { parseHex("FFFF") == 0xFFFFL }
        assertTrue { parseHex("FF0000") == 0xFF0000L }
        Configurator.defaultConfig().level(Level.OFF).activate()
        assertTrue { parseHex("0xG") == 0L }
    }

    @Test
    fun testBlend() {
        blend(CCBlack, CCBlue, 0)
        blend(CCBlack, CCBlue, 255)
    }

}