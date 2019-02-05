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


import animatedledstrip.leds.ColorContainer
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ColorContainerTest {

    @Test
    fun testPrimaryConstructor() {
        val testCC = ColorContainer(255, 123, 80)

        assertTrue { testCC.r == 255 }
        assertTrue { testCC.g == 123 }
        assertTrue { testCC.b == 80 }
    }

    @Test
    fun testCCConstructor() {
        val cc = ColorContainer(255, 123, 80)
        val testCC = ColorContainer(cc)

        assertTrue { testCC.r == 255 }
        assertTrue { testCC.g == 123 }
        assertTrue { testCC.b == 80 }
    }

    @Test
    fun testLongConstructor() {
        val testCC = ColorContainer(0xFF7B50)

        assertTrue { testCC.r == 255 }
        assertTrue { testCC.g == 123 }
        assertTrue { testCC.b == 80 }
    }

    @Test
    fun testR() {
        val testCC = ColorContainer(0)
        testCC.r = 255

        assertTrue { testCC.r == 255 }
    }

    @Test
    fun testG() {
        val testCC = ColorContainer(0)
        testCC.g = 255

        assertTrue { testCC.g == 255 }
    }

    @Test
    fun testB() {
        val testCC = ColorContainer(0)
        testCC.b = 255

        assertTrue { testCC.b == 255 }
    }

    @Test
    fun testHex() {
        val testCC = ColorContainer(0xFF7B50)

        assertTrue { testCC.hex == 0xFF7B50L }

        testCC.hex = 0x84AF
        assertTrue { testCC.hex == 0x84AFL }
    }

    @Test
    fun testHexString() {
        val testCC = ColorContainer(0xFF7B50)

        assertTrue { testCC.hexString == "FF7B50" }
        assertTrue { testCC.toString() == "FF7B50" }
    }

    @Test
    fun testSetRGB() {
        val testCC = ColorContainer(0)
        testCC.setRGB(255, 123, 80)

        assertTrue { testCC.r == 255 }
        assertTrue { testCC.g == 123 }
        assertTrue { testCC.b == 80 }
    }

    @Test
    fun testToColor() {
        val testCC = ColorContainer(0xFF7B50)

        assertTrue { testCC.toColor().red == 1.0 }
        assertTrue { testCC.toColor().green == 0.48235294222831726 }
        assertTrue { testCC.toColor().blue == 0.3137255012989044 }

    }

    @Test
    fun testInvert() {
        val testCC = ColorContainer(0xFF7B50)

        assertTrue { testCC.invert() == ColorContainer(0x0084AF) }
    }

    @Test
    fun testGrayscale() {
        val testCC = ColorContainer(0xFF7B51)

        assertTrue { testCC.grayscale() == 0x99L }
    }

    @Test
    fun testEquals() {
        val testCC = ColorContainer(0xFF7B50)

        assertTrue { testCC == ColorContainer(0xFF7B50) }
        assertFalse { testCC == ColorContainer(0xFF7B51) }
        assertFalse { testCC.equals(10) }

        testCC.hashCode()
    }
}