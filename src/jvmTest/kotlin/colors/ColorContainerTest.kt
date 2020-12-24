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
import animatedledstrip.utils.b
import animatedledstrip.utils.g
import animatedledstrip.utils.grayscale
import animatedledstrip.utils.r
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldExistInOrder
import io.kotest.matchers.collections.shouldStartWith
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import io.kotest.property.Arb
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.next
import io.kotest.property.checkAll

class ColorContainerTest : StringSpec(
    {
        "primary constructor" {
            checkAll<Int> { c ->
                ColorContainer(c).color shouldBe c
            }
        }

        "ColorContainer constructor" {
            checkAll(Arb.list(Arb.int(0..0xFFFFFF))) { c ->
                val cc = ColorContainer(c.toMutableList())
                ColorContainer(cc).colors shouldBe cc.colors
            }
        }

        "RGB constructor" {
            checkAll(Arb.int(0..0xFF),
                     Arb.int(0..0xFF),
                     Arb.int(0..0xFF)) { r, g, b ->
                ColorContainer(Triple(r, g, b)).color shouldBe ((r shl 16) or (g shl 8) or b)
            }
        }

        "list constructor" {
            checkAll(Arb.list(Arb.int(0..0xFFFFFF))) { c ->
                ColorContainer(c.toMutableList()).colors shouldBe c.toMutableList()
            }
        }

        "get" {
            checkAll(Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF)) { c1, c2, c3, c4 ->
                val testCC = ColorContainer(c1, c2, c3, c4)

                testCC[0] shouldBe c1
                testCC[0, 2] shouldBe listOf(c1, c3)
                testCC[0..2] shouldBe listOf(c1, c2, c3)
                testCC[IntRange(2, 0)] shouldBe listOf()     // Test with empty IntRange
                testCC.color shouldBe c1

                testCC[5] shouldBe 0
                testCC[3, 5] shouldBe listOf(c4, 0)
                testCC[3..5] shouldBe listOf(c4, 0, 0)
            }

            val testCC = ColorContainer()
            testCC.color shouldBe 0
        }

        "set" {
            checkAll(Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF)) { c1, c2, c3, c4, c5, c6 ->
                val testCC = ColorContainer(c1, c2, c3)

                testCC.colors.shouldContainExactly(c1, c2, c3)

                testCC[2] = c4

                testCC.colors.shouldContainExactly(c1, c2, c4)

                testCC[3, 5] = c5

                testCC.colors.shouldContainExactly(c1, c2, c4, c5, c5)

                testCC[3..7] = c6

                testCC.colors.shouldContainExactly(c1, c2, c4, c6, c6, c6, c6, c6)

                testCC[IntRange(5, 3)] = c1

                testCC.colors.shouldContainExactly(c1, c2, c4, c6, c6, c6, c6, c6)

                testCC += c4

                testCC.colors.shouldContainExactly(c1, c2, c4, c6, c6, c6, c6, c6, c4)
            }
        }

        "prepare" {
            val p = ColorContainer(0xFF7B51, 0xF0AF29, 0x3C538B).prepare(50)
            p.colors.shouldStartWith(0xFF7B51)
            p.colors.shouldExistInOrder({ it == 0xFF7B51 }, { it == 0xF0AF29 }, { it == 0x3C538B })
            p.originalColors.shouldContainExactly(0xFF7B51, 0xF0AF29, 0x3C538B)

            ColorContainer().prepare(50).colors.shouldBeEmpty()

            shouldThrow<IllegalArgumentException> {
                ColorContainer(0xFF).prepare(0)
            }

            checkAll(Arb.list(Arb.int(0..0xFFFFFF), 1..100)) { c ->
                ColorContainer(c.toMutableList()).prepare(500)
            }
        }

        "string" {
            ColorContainer(0xFF3B82).toString() shouldBe "ff3b82"

            ColorContainer(0xFF7B50, 0xFFFFFF).toString() shouldBe "[ff7b50, ffffff]"

            ColorContainer().toString() shouldBe "[]"
        }

        "toInt" {
            checkAll(Arb.list(Arb.int(0..0xFFFFFF), 1..100)) { c ->
                ColorContainer(c.toMutableList()).toInt() shouldBe c.first()
            }
        }

        "toRGB" {
            checkAll(Arb.list(Arb.int(0..0xFFFFFF), 1..100)) { c ->
                ColorContainer(c.toMutableList()).toRGB() shouldBe Triple(c.first().r, c.first().g, c.first().b)
                ColorContainer(c.toMutableList()).toTriple() shouldBe Triple(c.first().r, c.first().g, c.first().b)
            }
        }

        "toColorContainer" {
            checkAll(Arb.list(Arb.int(0..0xFFFFFF))) { c ->
                val testCC = ColorContainer(c.toMutableList())
                testCC.toColorContainer() shouldBeSameInstanceAs testCC
            }
        }

        "equality" {
            checkAll(Arb.int(0..0xFFFFFF)) { c ->
                val testCC = ColorContainer(c)

                testCC shouldBe ColorContainer(c)
                testCC.hashCode() shouldBe ColorContainer(c).hashCode()
                testCC shouldBe c

                checkAll(10, Arb.int(0..0xFFFFFF).filter { it != c }) { c2 ->
                    testCC shouldNotBe ColorContainer(c2)
                    testCC.hashCode() shouldNotBe ColorContainer(c2).hashCode()
                    testCC shouldNotBe c2
                }

                checkAll(10, Arb.list(Arb.int(0..0xFFFFFF), 2..100)) { c3 ->
                    testCC shouldNotBe ColorContainer(c3.toMutableList())
                }
            }

            checkAll(Arb.list(Arb.int(0..0xFFFFFF))) { c ->
                val testCC = ColorContainer(c.toMutableList())

                testCC shouldBe ColorContainer(c.toMutableList())
                testCC.hashCode() shouldBe ColorContainer(c.toMutableList()).hashCode()
                testCC shouldNotBe c
                testCC.hashCode() shouldBe c.hashCode()

                checkAll(50, Arb.int(1..500).filter { it != c.size }) { len ->
                    testCC shouldBe ColorContainer(c.toMutableList()).prepare(len)
                    testCC.hashCode() shouldNotBe ColorContainer(c.toMutableList()).prepare(len).hashCode()
                }

                checkAll(10, Arb.list(Arb.int(0..0xFFFFFF)).filter { it != c }) { c2 ->
                    testCC shouldNotBe ColorContainer(c2.toMutableList())
                    testCC.hashCode() shouldNotBe ColorContainer(c2.toMutableList()).hashCode()
                    testCC shouldNotBe c2
                    testCC.hashCode() shouldNotBe c2.hashCode()
                }
            }
        }

        "contains" {
            checkAll(Arb.list(Arb.int(0..0xFFFFFF))) { c ->
                val testCC = ColorContainer(c.toMutableList())

                for (i in c) {
                    (i in testCC).shouldBeTrue()
                }

                for (i in Arb.list(Arb.int(0..0xFFFFFF).filter { it !in c }, 10..10).next())
                    (i in testCC).shouldBeFalse()
            }
        }

        "size" {
            checkAll(Arb.list(Arb.int(0..0xFFFFFF))) { c ->
                ColorContainer(c.toMutableList()).size shouldBe c.size
            }
        }

        "grayscale" {
            checkAll(Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF)) { c1, c2 ->
                val testCC = ColorContainer(c1, c2)

                testCC.grayscale() shouldBeSameInstanceAs testCC
                testCC.colors.shouldContainExactly(c1.grayscale(),
                                                   c2.grayscale())
            }

            checkAll(Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF)) { c1, c2, c3, c4 ->
                val testCC = ColorContainer(c1, c2, c3, c4)

                testCC.grayscale(0, 3) shouldBeSameInstanceAs testCC
                testCC.colors.shouldContainExactly(c1.grayscale(),
                                                   c2,
                                                   c3,
                                                   c4.grayscale())

                testCC.grayscale(1, 2) shouldBeSameInstanceAs testCC
                testCC.colors.shouldContainExactly(c1.grayscale(),
                                                   c2.grayscale(),
                                                   c3.grayscale(),
                                                   c4.grayscale())

                val testCC2 = ColorContainer(c1, c2, c3, c4)

                testCC2.grayscale(3, 1, 2) shouldBeSameInstanceAs testCC2
                testCC2.colors.shouldContainExactly(c1,
                                                    c2.grayscale(),
                                                    c3.grayscale(),
                                                    c4.grayscale())

                val testCC3 = ColorContainer(c1, c2, c3, c4)

                testCC3.grayscale(8) shouldBeSameInstanceAs testCC3
                testCC3.colors.shouldContainExactly(c1, c2, c3, c4)
            }

            checkAll(Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF)) { c1, c2, c3, c4 ->
                val testCC = ColorContainer(c1, c2, c3, c4)

                testCC.grayscale(0..2) shouldBeSameInstanceAs testCC
                testCC.colors.shouldContainExactly(c1.grayscale(),
                                                   c2.grayscale(),
                                                   c3.grayscale(),
                                                   c4)

                val testCC2 = ColorContainer(c1, c2, c3, c4)

                testCC2.grayscale(0..1, 3..5) shouldBeSameInstanceAs testCC2
                testCC2.colors.shouldContainExactly(c1.grayscale(),
                                                    c2.grayscale(),
                                                    c3,
                                                    c4.grayscale())

                val testCC3 = ColorContainer(c1, c2, c3, c4)

                testCC3.grayscale(IntRange(2, 0)) shouldBeSameInstanceAs testCC3
                testCC3.colors.shouldContainExactly(c1, c2, c3, c4)
            }
        }

        "grayscaled" {
            checkAll(Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF)) { c1, c2 ->
                val testCC = ColorContainer(c1, c2)
                val gTestCC1 = testCC.grayscaled()

                gTestCC1 shouldNotBeSameInstanceAs testCC
                testCC.colors.shouldContainExactly(c1, c2)
                gTestCC1.colors.shouldContainExactly(c1.grayscale(),
                                                     c2.grayscale())
            }

            checkAll(Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF)) { c1, c2, c3, c4 ->
                val testCC = ColorContainer(c1, c2, c3, c4)
                val gTestCC1 = testCC.grayscaled(0, 3)

                gTestCC1 shouldNotBeSameInstanceAs testCC
                testCC.colors.shouldContainExactly(c1, c2, c3, c4)
                gTestCC1.colors.shouldContainExactly(c1.grayscale(),
                                                     c4.grayscale())

                val gTestCC2 = testCC.grayscaled(3, 1, 2)

                gTestCC2 shouldNotBeSameInstanceAs testCC
                testCC.colors.shouldContainExactly(c1, c2, c3, c4)
                gTestCC2.colors.shouldContainExactly(c4.grayscale(),
                                                     c2.grayscale(),
                                                     c3.grayscale())

                val gTestCC3 = testCC.grayscaled(8)

                gTestCC3 shouldNotBeSameInstanceAs testCC
                testCC.colors.shouldContainExactly(c1, c2, c3, c4)
                gTestCC3.colors.shouldBeEmpty()
            }

            checkAll(Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF)) { c1, c2, c3, c4 ->
                val testCC = ColorContainer(c1, c2, c3, c4)
                val gTestCC1 = testCC.grayscaled(0..2)

                gTestCC1 shouldNotBeSameInstanceAs testCC
                testCC.colors.shouldContainExactly(c1, c2, c3, c4)
                gTestCC1.colors.shouldContainExactly(c1.grayscale(),
                                                     c2.grayscale(),
                                                     c3.grayscale())

                val gTestCC2 = testCC.grayscaled(0..1, 3..5)

                gTestCC2 shouldNotBeSameInstanceAs testCC
                testCC.colors.shouldContainExactly(c1, c2, c3, c4)
                gTestCC2.colors.shouldContainExactly(c1.grayscale(),
                                                     c2.grayscale(),
                                                     c4.grayscale())

                val gTestCC3 = testCC.grayscaled(IntRange(2, 0))

                gTestCC3 shouldNotBeSameInstanceAs testCC
                testCC.colors.shouldContainExactly(c1, c2, c3, c4)
                gTestCC3.colors.shouldBeEmpty()
            }
        }

        "invert" {
            checkAll(Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF)) { c1, c2 ->
                val testCC = ColorContainer(c1, c2)

                testCC.invert() shouldBeSameInstanceAs testCC
                testCC.colors.shouldContainExactly(c1.inv() and 0xFFFFFF,
                                                   c2.inv() and 0xFFFFFF)

                testCC.invert() shouldBeSameInstanceAs testCC
                testCC.colors.shouldContainExactly(c1, c2)
            }

            checkAll(Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF)) { c1, c2, c3, c4 ->
                val testCC = ColorContainer(c1, c2, c3, c4)

                testCC.invert(0, 3) shouldBeSameInstanceAs testCC
                testCC.colors.shouldContainExactly(c1.inv() and 0xFFFFFF,
                                                   c2,
                                                   c3,
                                                   c4.inv() and 0xFFFFFF)

                testCC.invert(1, 2) shouldBeSameInstanceAs testCC
                testCC.colors.shouldContainExactly(c1.inv() and 0xFFFFFF,
                                                   c2.inv() and 0xFFFFFF,
                                                   c3.inv() and 0xFFFFFF,
                                                   c4.inv() and 0xFFFFFF)

                testCC.invert(3, 1, 2) shouldBeSameInstanceAs testCC
                testCC.colors.shouldContainExactly(c1.inv() and 0xFFFFFF,
                                                   c2,
                                                   c3,
                                                   c4)

                testCC.invert(8) shouldBeSameInstanceAs testCC
                testCC.colors.shouldContainExactly(c1.inv() and 0xFFFFFF,
                                                   c2,
                                                   c3,
                                                   c4)
            }

            checkAll(Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF)) { c1, c2, c3, c4 ->
                val testCC = ColorContainer(c1, c2, c3, c4)

                testCC.invert(0..2) shouldBeSameInstanceAs testCC
                testCC.colors.shouldContainExactly(c1.inv() and 0xFFFFFF,
                                                   c2.inv() and 0xFFFFFF,
                                                   c3.inv() and 0xFFFFFF,
                                                   c4)

                testCC.invert(1..2) shouldBeSameInstanceAs testCC
                testCC.colors.shouldContainExactly(c1.inv() and 0xFFFFFF,
                                                   c2,
                                                   c3,
                                                   c4)

                testCC.invert(0..1, 3..5) shouldBeSameInstanceAs testCC
                testCC.colors.shouldContainExactly(c1,
                                                   c2.inv() and 0xFFFFFF,
                                                   c3,
                                                   c4.inv() and 0xFFFFFF)

                testCC.invert(IntRange(2, 0)) shouldBeSameInstanceAs testCC
                testCC.colors.shouldContainExactly(c1,
                                                   c2.inv() and 0xFFFFFF,
                                                   c3,
                                                   c4.inv() and 0xFFFFFF)
            }
        }

        "inverse" {
            checkAll(Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF)) { c1, c2 ->
                val testCC = ColorContainer(c1, c2)
                val iTestCC1 = testCC.inverse()

                iTestCC1 shouldNotBeSameInstanceAs testCC
                testCC.colors.shouldContainExactly(c1, c2)
                iTestCC1.colors.shouldContainExactly(c1.inv() and 0xFFFFFF,
                                                     c2.inv() and 0xFFFFFF)

                val iTestCC2 = -testCC

                iTestCC2 shouldNotBeSameInstanceAs testCC
                testCC.colors.shouldContainExactly(c1, c2)
                iTestCC2.colors.shouldContainExactly(c1.inv() and 0xFFFFFF,
                                                     c2.inv() and 0xFFFFFF)
            }

            checkAll(Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF)) { c1, c2, c3, c4 ->
                val testCC = ColorContainer(c1, c2, c3, c4)
                val iTestCC1 = testCC.inverse(0, 3)

                iTestCC1 shouldNotBeSameInstanceAs testCC
                testCC.colors.shouldContainExactly(c1, c2, c3, c4)
                iTestCC1.colors.shouldContainExactly(c1.inv() and 0xFFFFFF,
                                                     c4.inv() and 0xFFFFFF)

                val iTestCC2 = testCC.inverse(3, 1, 2)

                iTestCC2 shouldNotBeSameInstanceAs testCC
                testCC.colors.shouldContainExactly(c1, c2, c3, c4)
                iTestCC2.colors.shouldContainExactly(c4.inv() and 0xFFFFFF,
                                                     c2.inv() and 0xFFFFFF,
                                                     c3.inv() and 0xFFFFFF)

                val iTestCC3 = testCC.inverse(8)

                iTestCC3 shouldNotBeSameInstanceAs testCC
                testCC.colors.shouldContainExactly(c1, c2, c3, c4)
                iTestCC3.colors.shouldBeEmpty()
            }

            checkAll(Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF)) { c1, c2, c3, c4 ->
                val testCC = ColorContainer(c1, c2, c3, c4)
                val iTestCC1 = testCC.inverse(0..2)

                iTestCC1 shouldNotBeSameInstanceAs testCC
                testCC.colors.shouldContainExactly(c1, c2, c3, c4)
                iTestCC1.colors.shouldContainExactly(c1.inv() and 0xFFFFFF,
                                                     c2.inv() and 0xFFFFFF,
                                                     c3.inv() and 0xFFFFFF)

                val iTestCC2 = testCC.inverse(0..1, 3..5)

                iTestCC2 shouldNotBeSameInstanceAs testCC
                testCC.colors.shouldContainExactly(c1, c2, c3, c4)
                iTestCC2.colors.shouldContainExactly(c1.inv() and 0xFFFFFF,
                                                     c2.inv() and 0xFFFFFF,
                                                     c4.inv() and 0xFFFFFF)

                val iTestCC3 = testCC.inverse(IntRange(2, 0))

                iTestCC3 shouldNotBeSameInstanceAs testCC
                testCC.colors.shouldContainExactly(c1, c2, c3, c4)
                iTestCC3.colors.shouldBeEmpty()
            }
        }

        "is empty" {
            val testCC1 = ColorContainer()
            testCC1.isEmpty().shouldBeTrue()
            testCC1.isNotEmpty().shouldBeFalse()

            checkAll(Arb.list(Arb.int(0..0xFFFFFF), 1..100)) { c ->
                val testCC2 = ColorContainer(c.toMutableList())
                testCC2.isEmpty().shouldBeFalse()
                testCC2.isNotEmpty().shouldBeTrue()
            }
        }
    })
