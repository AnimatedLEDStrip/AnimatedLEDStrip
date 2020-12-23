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

package animatedledstrip.test.colors

import animatedledstrip.colors.*
import io.kotest.core.spec.style.StringSpec
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ColorContainerTest : StringSpec(
    {
        "primary constructor" {
            val testCC = ColorContainer(0xFF7B50)

            assertTrue { testCC.color == 0xFF7B50 }
        }

        "ColorContainer constructor" {
            val cc = ColorContainer(0xFF7B50)
            val testCC = ColorContainer(cc)

            assertTrue { testCC.color == 0xFF7B50 }
        }

        "RGB constructor" {
            val testCC = ColorContainer(Triple(255, 123, 80))

            assertTrue { testCC.color == 0xFF7B50 }
        }

        "list constructor" {
            @Suppress("RemoveExplicitTypeArguments")
            val testCC = ColorContainer(mutableListOf<Int>(0xFF2431, 0x5F3C4B))
            assertTrue { testCC.colors == mutableListOf<Int>(0xFF2431, 0x5F3C4B) }
        }

        "equality" {
            val testCC = ColorContainer(0xFF7B50)

            assertTrue { testCC == ColorContainer(0xFF7B50) }
            assertTrue { testCC.equals(0xFF7B50) }
            assertFalse { testCC == ColorContainer(0xFF7B51) }
            assertFalse { testCC.equals(10) }
            assertFalse { ColorContainer(0xFF, 0xFFFF).equals(10) }
            assertFalse { testCC.equals("X") }

            testCC.hashCode()

            val testPCC = testCC.prepare(50)

            assertTrue { testPCC.equals(ColorContainer(0xFF7B50)) }
            assertTrue { testPCC == ColorContainer(0xFF7B50).prepare(50) }
            assertTrue { testCC.equals(testPCC) }
            assertFalse { testPCC.equals(ColorContainer(0xFF7B51)) }
            assertFalse { testPCC == ColorContainer(0xFF7B51).prepare(50) }
            assertFalse { ColorContainer(0xFF7B51).equals(testPCC) }
            assertFalse { testPCC.equals("X") }

            testPCC.hashCode()
        }

        "contains" {
            val testCC = ColorContainer(0xFF7B50, 0xF0AF29, 0x3C538B, 0x0084AF)
            assertTrue { testCC.contains(0xFF7B50) }
            assertFalse { testCC.contains(0xFF145C) }
        }

        "get" {
            val testCC = ColorContainer(0xFF7B50, 0xF0AF29, 0x3C538B, 0x0084AF)

            assertTrue { testCC[0] == 0xFF7B50 }
            assertTrue { testCC[0, 2] == listOf(0xFF7B50, 0x3C538B) }
            assertTrue { testCC[0..2] == listOf(0xFF7B50, 0xF0AF29, 0x3C538B) }
            assertTrue { testCC[IntRange(2, 0)] == listOf<Int>() }     // Test with empty IntRange
            assertTrue { testCC.color == 0xFF7B50 }

            assertTrue { testCC[5] == 0 }
            assertTrue { testCC[3, 5] == listOf(0x0084AF, 0) }
            assertTrue { testCC[3..5] == listOf(0x0084AF, 0, 0) }


            val testCC2 = ColorContainer(0xFF7B50)
            assertTrue { testCC2.color == 0xFF7B50 }
            assertTrue { testCC2[10] == 0xFF7B50 }
            assertTrue { testCC2[3..5] == listOf(0xFF7B50) }
            assertTrue { testCC2[5, 8, 10] == listOf(0xFF7B50) }

            val testCC3 = ColorContainer()
            assertTrue { testCC3.color == 0 }

            val testPCC = testCC.prepare(50)
            assertTrue { testPCC[0] == 0xFF7B50 }
            assertTrue { testPCC[50] == 0 }
            assertTrue { testPCC.color == 0 }
        }

        "set" {
            val testCC = ColorContainer(0xFF7B50, 0xF0AF29, 0x3C538B, 0x0084AF)

            testCC[2] = 0xFF753C
            assertTrue { testCC.colors == listOf(0xFF7B50, 0xF0AF29, 0xFF753C, 0x0084AF) }

            testCC[3, 5] = 0xB39247
            assertTrue { testCC.colors == listOf(0xFF7B50, 0xF0AF29, 0xFF753C, 0xB39247, 0xB39247) }

            testCC[3..7] = 0x526BE2
            assertTrue {
                testCC.colors == listOf(
                    0xFF7B50,
                    0xF0AF29,
                    0xFF753C,
                    0x526BE2,
                    0x526BE2,
                    0x526BE2,
                    0x526BE2,
                    0x526BE2,
                )
            }

            // Test empty IntRange
            testCC[IntRange(5, 3)] = 0xFF
            assertTrue {
                testCC.colors == listOf(
                    0xFF7B50,
                    0xF0AF29,
                    0xFF753C,
                    0x526BE2,
                    0x526BE2,
                    0x526BE2,
                    0x526BE2,
                    0x526BE2,
                )
            }
        }

        "size" {
            val testCC = ColorContainer(0xFF7B50, 0xF0AF29, 0x3C538B, 0x0084AF)

            assertTrue { testCC.size == 4 }
        }

        "string" {
            val testCC = ColorContainer(0xFF3B82)
            assertTrue { testCC.toString() == "ff3b82" }

            val testCC2 = ColorContainer(0xFF7B50, 0xFFFFFF)
            assertTrue { testCC2.toString() == "[ff7b50, ffffff]" }

            val testCC3 = ColorContainer()
            assertTrue { testCC3.toString() == "[]" }
        }

        "invert" {
            val testCC = ColorContainer(0xFF7B50, 0xFF99C2)
            assertTrue { testCC.invert(0)[0] == 0x0084AF }
            assertTrue { testCC[1] == 0xFF99C2 }

            val testCC2 = ColorContainer(0xFF7B50, 0xF0AF29, 0x3C538B)
            assertTrue { testCC2.invert(0, 2, 5)[0, 2] == listOf(0x0084AF, 0xC3AC74) }
            assertTrue { testCC2.colors == listOf(0x0084AF, 0xF0AF29, 0xC3AC74) }

            val testCC3 = ColorContainer(0xFF7B50, 0xF0AF29, 0x3C538B)
            assertTrue { testCC3.invert().colors == listOf(0x0084AF, 0x0F50D6, 0xC3AC74) }

            val testCC4 = ColorContainer(0xFF7B50, 0xF0AF29, 0x3C538B)
            assertTrue { testCC4.invert(0..1, 5..5)[0, 1] == listOf(0x0084AF, 0x0F50D6) }
            assertTrue { testCC4.colors == listOf(0x0084AF, 0x0F50D6, 0x3C538B) }

            // Test with empty IntRange
            val testCC5 = ColorContainer(0xFF7B50, 0xF0AF29, 0x3C538B)
            assertTrue { testCC5.invert(IntRange(10, 5)).colors == listOf(0xFF7B50, 0xF0AF29, 0x3C538B) }
        }

        "inverse" {
            val testCC = ColorContainer(0xFF7B50, 0xFF99C2)
            val invCC = testCC.inverse(0)
            assertTrue { invCC[0] == 0x0084AF }
            assertTrue { testCC.colors == listOf(0xFF7B50, 0xFF99C2) }

            val testCC2 = ColorContainer(0xFF7B50, 0xF0AF29, 0x3C538B)
            val invCC2 = testCC2.inverse(0, 2, 5)
            assertTrue { invCC2.colors == listOf(0x0084AF, 0xC3AC74) }
            assertTrue { testCC2.colors == listOf(0xFF7B50, 0xF0AF29, 0x3C538B) }

            val testCC3 = ColorContainer(0xFF7B50, 0xF0AF29, 0x3C538B)
            val invCC3 = testCC2.inverse(0..1, 5..5)
            assertTrue { invCC3.colors == listOf(0x0084AF, 0x0F50D6) }
            assertTrue { testCC3.colors == listOf(0xFF7B50, 0xF0AF29, 0x3C538B) }

            val testCC4 = ColorContainer(0xFF7B50, 0xF0AF29, 0x3C538B)
            val invCC4 = testCC4.inverse()
            val invCC5 = -testCC4
            assertTrue { invCC4.colors == listOf(0x0084AF, 0x0F50D6, 0xC3AC74) }
            assertTrue { invCC5.colors == listOf(0x0084AF, 0x0F50D6, 0xC3AC74) }
            assertTrue { testCC4.colors == listOf(0xFF7B50, 0xF0AF29, 0x3C538B) }

            // Test with empty IntRange
            val testCC5 = ColorContainer(0xFF7B50, 0xF0AF29, 0x3C538B)
            assertTrue { testCC5.inverse(IntRange(10, 5)).colors == listOf<Long>() }
        }

        "grayscale" {
            val testCC = ColorContainer(0xFF7B51, 0xFF99C2)
            assertTrue { testCC.grayscale(0)[0] == 0x999999 }
            assertTrue { testCC[1] == 0xFF99C2 }

            val testCC2 = ColorContainer(0xFF7B51, 0xF0AF29, 0x3C538B)
            assertTrue { testCC2.grayscale(0, 2, 5)[0, 2] == listOf(0x999999, 0x5E5E5E) }
            assertTrue { testCC2.colors == listOf(0x999999, 0xF0AF29, 0x5E5E5E) }

            val testCC3 = ColorContainer(0xFF7B51, 0xF0AF29, 0x3C538B)
            assertTrue { testCC3.grayscale().colors == listOf(0x999999, 0x989898, 0x5E5E5E) }

            val testCC4 = ColorContainer(0xFF7B51, 0x3C538B, 0xF0AF29)
            assertTrue { testCC4.grayscale(0..1, 5..5)[0, 1] == listOf(0x999999, 0x5E5E5E) }
            assertTrue { testCC4.colors == listOf(0x999999, 0x5E5E5E, 0xF0AF29) }

            // Test with empty IntRange
            val testCC5 = ColorContainer(0xFF7B50, 0xF0AF29, 0x3C538B)
            assertTrue { testCC5.grayscale(IntRange(10, 5)).colors == listOf(0xFF7B50, 0xF0AF29, 0x3C538B) }
        }

        "grayscaled" {
            val testCC = ColorContainer(0xFF7B51, 0xFF99C2)
            val grayCC = testCC.grayscaled(0)
            assertTrue { grayCC[0] == 0x999999 }
            assertTrue { testCC.colors == listOf(0xFF7B51, 0xFF99C2) }

            val testCC2 = ColorContainer(0xFF7B51, 0xF0AF29, 0x3C538B)
            val grayCC2 = testCC2.grayscaled(0, 2, 5)
            assertTrue { grayCC2.colors == listOf(0x999999, 0x5E5E5E) }
            assertTrue { testCC2.colors == listOf(0xFF7B51, 0xF0AF29, 0x3C538B) }


            val testCC3 = ColorContainer(0xFF7B51, 0xF0AF29, 0x3C538B)
            val grayCC3 = testCC3.grayscaled()
            assertTrue { grayCC3.colors == listOf(0x999999, 0x989898, 0X5E5E5E) }
            assertTrue { testCC2.colors == listOf(0xFF7B51, 0xF0AF29, 0x3C538B) }

            val testCC4 = ColorContainer(0xFF7B51, 0x3C538B, 0xF0AF29)
            val grayCC4 = testCC4.grayscaled(0..1, 5..5)
            assertTrue { grayCC4.colors == listOf(0x999999, 0x5E5E5E) }
            assertTrue { testCC4.colors == listOf(0xFF7B51, 0x3C538B, 0xF0AF29) }

            // Test with empty IntRange
            val testCC5 = ColorContainer(0xFF7B50, 0xF0AF29, 0x3C538B)
            assertTrue { testCC5.grayscaled(IntRange(10, 5)).colors == listOf<Int>() }
        }

        "prepare" {
            val testCC = ColorContainer(0xFF7B51, 0xF0AF29, 0x3C538B)

            val p = testCC.prepare(50)
            assertTrue { p[0] == testCC[0] }
            assertTrue { p.contains(testCC[1]) }
            assertTrue { p.contains(testCC[2]) }

            val testCC2 = ColorContainer()
            val r = testCC2.prepare(50)
            assertTrue { r.size == 0 }
        }

        "toLong" {
            val testCC = ColorContainer(0xFF7B51, 0xF0AF29, 0x3C538B)
            assertTrue { testCC.toInt() == 0xFF7B51 }
        }

        "toRGB" {
            val testCC = ColorContainer(0xFF7B51, 0xF0AF29, 0x3C538B)
            assertTrue { testCC.toRGB() == Triple(0xFF, 0x7B, 0x51) }
            assertTrue { testCC.toTriple() == Triple(0xFF, 0x7B, 0x51) }
        }

        "toPreparedColorContainer" {
            val testPCC = PreparedColorContainer(listOf(0xFF, 0xFFFF))
            assertTrue { testPCC.prepare(2) === testPCC }
            assertTrue { testPCC.toString() == "[ff, ffff]" }
        }

        "is empty" {
            val testCC1 = ColorContainer()
            assertTrue { testCC1.isEmpty() }
            assertFalse { testCC1.isNotEmpty() }

            val testCC2 = ColorContainer(0xFF)
            assertFalse { testCC2.isEmpty() }
            assertTrue { testCC2.isNotEmpty() }

            val testCC3 = ColorContainer(0xFF, 0xFFFF)
            assertFalse { testCC3.isEmpty() }
            assertTrue { testCC3.isNotEmpty() }

            val testPCC1 = testCC1.prepare(50)
            assertTrue { testPCC1.isEmpty() }
            assertFalse { testPCC1.isNotEmpty() }

            val testPCC2 = testCC2.prepare(50)
            assertFalse { testPCC2.isEmpty() }
            assertTrue { testPCC2.isNotEmpty() }

            val testPCC3 = testCC3.prepare(50)
            assertFalse { testPCC3.isEmpty() }
            assertTrue { testPCC3.isNotEmpty() }
        }

        "offset by" {
            val c = ColorContainer(
                0x0000FF, 0x00FFFF, 0xFF00FF,
                0x00FF00, 0xFF0000, 0xFFFFFF,
            ).prepare(6)

            assertTrue {
                c.colors ==
                        listOf<Long>(
                            0x0000FF, 0x00FFFF, 0xFF00FF,
                            0x00FF00, 0xFF0000, 0xFFFFFF,
                        )
            }
            assertTrue {
                c.offsetBy(0).colors ==
                        listOf<Long>(
                            0x0000FF, 0x00FFFF, 0xFF00FF,
                            0x00FF00, 0xFF0000, 0xFFFFFF,
                        )
            }
            assertTrue {
                c.offsetBy(4).colors ==
                        listOf<Long>(
                            0xFF00FF, 0x00FF00, 0xFF0000,
                            0xFFFFFF, 0x0000FF, 0x00FFFF,
                        )
            }
            assertTrue {
                c.offsetBy(-4).colors ==
                        listOf<Long>(
                            0xFF0000, 0xFFFFFF, 0x0000FF,
                            0x00FFFF, 0xFF00FF, 0x00FF00,
                        )
            }
            assertTrue {
                c.offsetBy(10).colors ==
                        c.offsetBy(4).colors
            }
            assertTrue {
                c.offsetBy(-10).colors ==
                        c.offsetBy(-4).colors
            }
        }
    })
