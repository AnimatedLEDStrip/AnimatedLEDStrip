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


import animatedledstrip.colors.ccpresets.CCPresets
import org.junit.Test
import kotlin.test.assertTrue

class ColorPresetsTest {

    @Test
    fun checkColors() {
        val colorList = listOf<Long>(
            0xF0F8FF,
            0x9966CC,
            0xFAEBD7,
            0x00FFFF,
            0x7FFFD4,
            0xF0FFFF,
            0xF5F5DC,
            0xFFE4C4,
            0x000000,
            0xFFEBCD,
            0x0000FF,
            0x8A2BE2,
            0xA52A2A,
            0xDEB887,
            0x5F9EA0,
            0x7FFF00,
            0xD2691E,
            0xFF7F50,
            0x6495ED,
            0xFFF8DC,
            0xDC143C,
            0x00FFFF,
            0x00008B,
            0x008B8B,
            0xB8860B,
            0xA9A9A9,
            0xA9A9A9,
            0x006400,
            0xBDB76B,
            0x8B008B,
            0x556B2F,
            0xFF8C00,
            0x9932CC,
            0x8B0000,
            0xE9967A,
            0x8FBC8F,
            0x483D8B,
            0x2F4F4F,
            0x2F4F4F,
            0x00CED1,
            0x9400D3,
            0xFF1493,
            0x00BFFF,
            0x696969,
            0x696969,
            0x1E90FF,
            0xFFE42D,
            0xFF9D2A,
            0xB22222,
            0xFFFAF0,
            0x228B22,
            0xFF00FF,
            0xDCDCDC,
            0xF8F8FF,
            0xFFD700,
            0xDAA520,
            0x808080,
            0x808080,
            0x008000,
            0xADFF2F,
            0xF0FFF0,
            0xFF69B4,
            0xCD5C5C,
            0x4B0082,
            0xFFFFF0,
            0xF0E68C,
            0xE6E6FA,
            0xFFF0F5,
            0x7CFC00,
            0xFFFACD,
            0xADD8E6,
            0xF08080,
            0xE0FFFF,
            0xFAFAD2,
            0x90EE90,
            0xD3D3D3,
            0xFFB6C1,
            0xFFA07A,
            0x20B2AA,
            0x87CEFA,
            0x778899,
            0x778899,
            0xB0C4DE,
            0xFFFFE0,
            0x00FF00,
            0x32CD32,
            0xFAF0E6,
            0xFF00FF,
            0x800000,
            0x66CDAA,
            0x0000CD,
            0xBA55D3,
            0x9370DB,
            0x3CB371,
            0x7B68EE,
            0x00FA9A,
            0x48D1CC,
            0xC71585,
            0x191970,
            0xF5FFFA,
            0xFFE4E1,
            0xFFE4B5,
            0xFFDEAD,
            0x000080,
            0xFDF5E6,
            0x808000,
            0x6B8E23,
            0xFFA500,
            0xFF4500,
            0xDA70D6,
            0xEEE8AA,
            0x98FB98,
            0xAFEEEE,
            0xDB7093,
            0xFFEFD5,
            0xFFDAB9,
            0xCD853F,
            0xFFC0CB,
            0xCC5533,
            0xDDA0DD,
            0xB0E0E6,
            0x800080,
            0xFF0000,
            0xBC8F8F,
            0x4169E1,
            0x8B4513,
            0xFA8072,
            0xF4A460,
            0x2E8B57,
            0xFFF5EE,
            0xA0522D,
            0xC0C0C0,
            0x87CEEB,
            0x6A5ACD,
            0x708090,
            0x708090,
            0xFFFAFA,
            0x00FF7F,
            0x4682B4,
            0xD2B48C,
            0x008080,
            0xD8BFD8,
            0xFF6347,
            0x40E0D0,
            0xEE82EE,
            0xF5DEB3,
            0xFFFFFF,
            0xF5F5F5,
            0xFFFF00,
            0x9ACD32
        )

        for (i in 0 until colorList.size) {
            assertTrue { CCPresets[i].color == colorList[i] }
        }

    }
}