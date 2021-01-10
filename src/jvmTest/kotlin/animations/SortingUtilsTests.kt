/*
 * Copyright (c) 2018-2021 AnimatedLEDStrip
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package animatedledstrip.test.animations

import animatedledstrip.animations.SortablePixel
import animatedledstrip.animations.toPreparedColorContainer
import animatedledstrip.animations.toSortableList
import animatedledstrip.colors.ColorContainer
import animatedledstrip.colors.ColorContainerInterface
import animatedledstrip.colors.PreparedColorContainer
import animatedledstrip.colors.shuffledWithIndices
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import io.mockk.every
import io.mockk.mockkStatic

class SortingUtilsTests : StringSpec(
    {
        "sortable pixel" {
            checkAll<Int, Int, Int> { f, c, col ->
                val sp = SortablePixel(f, c, col)
                sp.finalLocation shouldBe f
                sp.currentLocation shouldBe c
                sp.color shouldBe col
            }
        }

        "to sortable list" {
            mockkStatic("animatedledstrip.colors.ColorUtilsKt")
            every { any<ColorContainerInterface>().shuffledWithIndices() } returns
                    listOf(
                        Pair(1, 0x34FF),
                        Pair(9, 0x33FF),
                        Pair(4, 0xCDFF),
                        Pair(2, 0x67FF),
                        Pair(7, 0x99FF),
                        Pair(0, 0x00FF),
                        Pair(3, 0x9AFF),
                        Pair(6, 0xCCFF),
                        Pair(8, 0x66FF),
                        Pair(5, 0xFFFF),
                    )

            ColorContainer(0xFF, 0xFFFF).prepare(10).toSortableList()
                .shouldContainExactly(SortablePixel(1, 0, 0x34FF),
                                      SortablePixel(9, 1, 0x33FF),
                                      SortablePixel(4, 2, 0xCDFF),
                                      SortablePixel(2, 3, 0x67FF),
                                      SortablePixel(7, 4, 0x99FF),
                                      SortablePixel(0, 5, 0x00FF),
                                      SortablePixel(3, 6, 0x9AFF),
                                      SortablePixel(6, 7, 0xCCFF),
                                      SortablePixel(8, 8, 0x66FF),
                                      SortablePixel(5, 9, 0xFFFF))
        }

        "to PreparedColorContainer" {
            mockkStatic("animatedledstrip.colors.ColorUtilsKt")
            every { any<ColorContainerInterface>().shuffledWithIndices() } returns
                    listOf(
                        Pair(1, 0x34FF),
                        Pair(9, 0x33FF),
                        Pair(4, 0xCDFF),
                        Pair(2, 0x67FF),
                        Pair(7, 0x99FF),
                        Pair(0, 0x00FF),
                        Pair(3, 0x9AFF),
                        Pair(6, 0xCCFF),
                        Pair(8, 0x66FF),
                        Pair(5, 0xFFFF),
                    )

            val original = ColorContainer(0xFF, 0xFFFF).prepare(10)
            original.toSortableList().toPreparedColorContainer() shouldBe
                    PreparedColorContainer(listOf(0x34FF, 0x33FF, 0xCDFF, 0x67FF, 0x99FF,
                                                  0x00FF, 0x9AFF, 0xCCFF, 0x66FF, 0xFFFF),
                                           listOf())
        }
    }
)
