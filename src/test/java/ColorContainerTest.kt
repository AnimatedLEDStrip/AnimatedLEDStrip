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
        val testCC = ColorContainer(0xFF7B50)

        assertTrue { testCC.color == 0xFF7B50L }
    }

    @Test
    fun testCCConstructor() {
        val cc = ColorContainer(0xFF7B50)
        val testCC = ColorContainer(cc)

        assertTrue { testCC.color == 0xFF7B50L }
    }

    @Test
    fun testRGBConstructor() {
        val testCC = ColorContainer(Triple(255, 123, 80))

        assertTrue { testCC.color == 0xFF7B50L }
    }

    @Test
    fun testListConstructor() {
        val testCC = ColorContainer(listOf<Long>(0xFF2431, 0x5F3C4B))
        assertTrue { testCC.colors == listOf<Long>(0xFF2431, 0x5F3C4B)}
    }

    @Test
    fun testEquals() {
        val testCC = ColorContainer(0xFF7B50)

        assertTrue { testCC == ColorContainer(0xFF7B50) }
        assertFalse { testCC == ColorContainer(0xFF7B51) }
        assertFalse { testCC.equals(10) }

        testCC.hashCode()
    }

    @Test
    fun testContains() {
        val testCC = ColorContainer(0xFF7B50, 0xF0AF29, 0x3C538B, 0x0084AF)
        assertTrue { testCC.contains(0xFF7B50) }
        assertFalse { testCC.contains(0xFF145C) }
    }

    @Test
    fun testGet() {
        val testCC = ColorContainer(0xFF7B50, 0xF0AF29, 0x3C538B, 0x0084AF)

        assertTrue { testCC[0] == 0xFF7B50L }
        assertTrue { testCC[0,2] == listOf<Long>(0xFF7B50, 0x3C538B) }
        assertTrue { testCC[0..2] == listOf<Long>(0xFF7B50, 0xF0AF29, 0x3C538B) }
        assertTrue { testCC.color == 0xFF7B50L }

        assertTrue { testCC[5] == 0L }
        assertTrue { testCC[3, 5] == listOf<Long>(0x0084AF, 0) }
        assertTrue { testCC[3..5] == listOf<Long>(0x0084AF, 0, 0) }


        val testCC2 = ColorContainer(0xFF7B50)
        assertTrue { testCC2.color == 0xFF7B50L }
        assertTrue { testCC2[10] == 0xFF7B50L }
        assertTrue { testCC2[3..5] == listOf<Long>(0xFF7B50) }
        assertTrue { testCC2[5, 8, 10] == listOf<Long>(0xFF7B50) }
    }

    @Test
    fun testSet() {
        val testCC = ColorContainer(0xFF7B50, 0xF0AF29, 0x3C538B, 0x0084AF)

        testCC[2] = 0xFF753C
        assertTrue { testCC.colors == listOf<Long>(0xFF7B50, 0xF0AF29, 0xFF753C, 0x0084AF)}

        testCC[3, 5] = 0xB39247
        assertTrue { testCC.colors == listOf<Long>(0xFF7B50, 0xF0AF29, 0xFF753C, 0xB39247, 0xB39247) }

        testCC[3..7] = 0x526BE2
        assertTrue { testCC.colors == listOf<Long>(0xFF7B50, 0xF0AF29, 0xFF753C, 0x526BE2, 0x526BE2, 0x526BE2, 0x526BE2, 0x526BE2) }
    }

    @Test
    fun testToString() {
        val testCC = ColorContainer(0xFF3B82)
        assertTrue { testCC.toString() == "ff3b82" }
        assertTrue { testCC.hexString == "ff3b82" }

        val testCC2 = ColorContainer(0xFF7B50, 0xFFFFFF)
        assertTrue { testCC2.toString() == "[ff7b50, ffffff]" }

        val testCC3 = ColorContainer()
        assertTrue { testCC3.toString() == "[]" }
    }

//    @Test
//    @Ignore
//    fun testToColor() {
//        val testCC = ColorContainer(0xFF7B50)
//
////        assertTrue { testCC.toColor().red == 1.0 }
////        assertTrue { testCC.toColor().green == 0.48235294222831726 }
////        assertTrue { testCC.toColor().blue == 0.3137255012989044 }
//
//    }

    @Test
    fun testInvert() {
        val testCC = ColorContainer(0xFF7B50, 0xFF99C2)
        assertTrue { testCC.invert(0)[0] == 0x0084AFL }
        assertTrue { testCC[1] == 0xFF99C2L }

        val testCC2 = ColorContainer(0xFF7B50, 0xF0AF29, 0x3C538B)
        assertTrue { testCC2.invert(0, 2, 5)[0, 2] == listOf<Long>(0x0084AF, 0xC3AC74) }
        assertTrue { testCC2.colors == listOf<Long>(0x0084AF, 0xF0AF29, 0xC3AC74) }

        val testCC3 = ColorContainer(0xFF7B50, 0xF0AF29, 0x3C538B)
        assertTrue { testCC3.invert().colors == listOf<Long>(0x0084AF, 0x0F50D6, 0xC3AC74) }
    }

    @Test
    fun testInverse() {
        val testCC = ColorContainer(0xFF7B50, 0xFF99C2)
        val invCC = testCC.inverse(0)
        assertTrue { invCC[0] == 0x0084AFL }
        assertTrue { testCC.colors == listOf<Long>(0xFF7B50, 0xFF99C2) }

        val testCC2 = ColorContainer(0xFF7B50, 0xF0AF29, 0x3C538B)
        val invCC2 = testCC2.inverse(0, 2, 5)
        assertTrue { invCC2.colors == listOf<Long>(0x0084AF, 0xC3AC74) }
        assertTrue { testCC2.colors == listOf<Long>(0xFF7B50, 0xF0AF29, 0x3C538B) }


        val testCC3 = ColorContainer(0xFF7B50, 0xF0AF29, 0x3C538B)
        val invCC3 = testCC3.inverse()
        val invCC4 = -testCC3
        assertTrue { invCC3.colors == listOf<Long>(0x0084AF, 0x0F50D6, 0xC3AC74) }
        assertTrue { invCC4.colors == listOf<Long>(0x0084AF, 0x0F50D6, 0xC3AC74) }
        assertTrue { testCC2.colors == listOf<Long>(0xFF7B50, 0xF0AF29, 0x3C538B) }
    }

    @Test
    fun testGrayscale() {
        val testCC = ColorContainer(0xFF7B51, 0xFF99C2)
        assertTrue { testCC.grayscale(0)[0] == 0x999999L }
        assertTrue { testCC[1] == 0xFF99C2L }

        val testCC2 = ColorContainer(0xFF7B51, 0xF0AF29, 0x3C538B)
        assertTrue { testCC2.grayscale(0, 2, 5)[0, 2] == listOf<Long>(0x999999, 0x5E5E5E) }
        assertTrue { testCC2.colors == listOf<Long>(0x999999, 0xF0AF29, 0x5E5E5E) }

        val testCC3 = ColorContainer(0xFF7B51, 0xF0AF29, 0x3C538B)
        assertTrue { testCC3.grayscale().colors == listOf<Long>(0x999999, 0x989898, 0x5E5E5E) }
    }

    @Test
    fun testGrayscaled() {
        val testCC = ColorContainer(0xFF7B51, 0xFF99C2)
        val grayCC = testCC.grayscaled(0)
        assertTrue { grayCC[0] == 0x999999L }
        assertTrue { testCC.colors == listOf<Long>(0xFF7B51, 0xFF99C2) }

        val testCC2 = ColorContainer(0xFF7B51, 0xF0AF29, 0x3C538B)
        val grayCC2 = testCC2.grayscaled(0, 2, 5)
        assertTrue { grayCC2.colors == listOf<Long>(0x999999, 0x5E5E5E) }
        assertTrue { testCC2.colors == listOf<Long>(0xFF7B51, 0xF0AF29, 0x3C538B) }


        val testCC3 = ColorContainer(0xFF7B51, 0xF0AF29, 0x3C538B)
        val grayCC3 = testCC3.grayscaled()
        assertTrue { grayCC3.colors == listOf<Long>(0x999999, 0x989898, 0X5E5E5E) }
        assertTrue { testCC2.colors == listOf<Long>(0xFF7B51, 0xF0AF29, 0x3C538B) }
    }

    @Test
    fun testPrepare() {
        val testCC = ColorContainer(0xFF7B51, 0xF0AF29, 0x3C538B)

        val p = testCC.prepare(50)
        assertTrue { p[0] == testCC[0] }
        assertTrue { p.contains(testCC[1]) }
        assertTrue { p.contains(testCC[2]) }

        val q = testCC.prepare(30, 20)
        assertTrue { q[0] == 0L }
        assertTrue { q[19] == 0L }
        assertTrue { q[20] == testCC[0] }
        assertTrue { q.contains(testCC[1]) }
        assertTrue { q.contains(testCC[2]) }
    }

    @Test
    fun testToLong() {
        val testCC = ColorContainer(0xFF7B51, 0xF0AF29, 0x3C538B)
        assertTrue { testCC.toLong() == 0xFF7B51L }
    }

    @Test
    fun testToRGB() {
        val testCC = ColorContainer(0xFF7B51, 0xF0AF29, 0x3C538B)
        assertTrue { testCC.toRGB() == Triple(0xFF, 0x7B, 0x51) }
        assertTrue { testCC.toTriple() == Triple(0xFF, 0x7B, 0x51) }
    }
}