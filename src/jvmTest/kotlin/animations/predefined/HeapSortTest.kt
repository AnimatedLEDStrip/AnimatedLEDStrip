/*
 * Copyright (c) 2018-2022 AnimatedLEDStrip
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

package animatedledstrip.test.animations.predefined

import animatedledstrip.colors.ColorContainer
import animatedledstrip.colors.ColorContainerInterface
import animatedledstrip.colors.shuffledWithIndices
import animatedledstrip.leds.animationmanagement.*
import animatedledstrip.leds.colormanagement.setPixelProlongedColor
import animatedledstrip.leds.emulation.createNewEmulatedStrip
import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.verifyOrder

class HeapSortTest : StringSpec(
    {
        mockkStatic("animatedledstrip.leds.animationmanagement.AnimationUtilsKt",
                    "animatedledstrip.leds.animationmanagement.AnimationManagementUtilsKt",
                    "animatedledstrip.leds.colormanagement.SetColorUtilsKt",
                    "animatedledstrip.leds.colormanagement.GetColorUtilsKt")

        val ledStrip = createNewEmulatedStrip(10)

        afterSpec { ledStrip.close() }

        "Heap Sort" {
            val section = ledStrip.sectionManager.createSection("hps", 0, 9)

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

            val anim = ledStrip.animationManager.startAnimation(AnimationToRunParams()
                                                                    .animation("Heap Sort")
                                                                    .color(ColorContainer(0xFF, 0xFFFF))
                                                                    .section("hps"))

            anim.join()

            verifyOrder {
                // siftDown(4, 9) [1942703685]
                // swap < child: root = 4(7), child = 9(5), swap = 4(7)
                // swap < ch +1: ch +1 DNE
                // swap == root: true
                //
                // siftDown(3, 9) [1942703685]
                // swap < child: root = 3(2), child = 7(6), swap = 3(2) -> 7(6)
                // swap < ch +1: root = 3(2), ch +1 = 8(8), swap = 7(6) -> 8(8)
                // swap == root: false
                // root [3(2)] <> swap [8(8)]
                section.setPixelProlongedColor(3, 0x66FF)
                section.setPixelProlongedColor(8, 0x67FF)
                // root = 8(2)
                // [1948703625]
                // 2 * root + 1 <= 9: false
                //
                // siftDown(2, 9) [1948703625]
                // swap < child: root = 2(4), child = 5(0), swap = 2(4)
                // swap < ch +1: root = 2(4), ch +1 = 6(3), swap = 2(4)
                // swap == root: true
                //
                // siftDown(1, 9) [1948703625]
                // swap < child: root = 1(9), child = 3(8), swap = 1(9)
                // swap < ch +1: root = 1(9), ch +1 = 4(7), swap = 1(9)
                // swap == root: true
                //
                // siftDown(0, 9) [1948703625]
                // swap < child: root = 0(1), child = 1(9), swap = 0(1) -> 1(9)
                // swap < ch +1: root = 0(1), ch +1 = 2(4), swap = 1(9)
                // swap == root: false
                // root [0(1)] <> swap [1(9)]
                section.setPixelProlongedColor(0, 0x33FF)
                section.setPixelProlongedColor(1, 0x34FF)
                // [9148703625]
                // root = 1(1)
                // 2 * root + 1 <= 9: true
                // swap < child: root = 1(1), child = 3(8), swap = 1(1) -> 3(8)
                // swap < ch +1: root = 1(1), ch +1 = 4(7), swap = 3(8)
                // swap == root: false
                // root [1(1)] <> swap [3(8)]
                section.setPixelProlongedColor(1, 0x66FF)
                section.setPixelProlongedColor(3, 0x34FF)
                // [9841703625]
                // root = 3(1)
                // 2 * root + 1 <= 9: true
                // swap < child: root = 3(1), child = 7(6), swap = 3(1) -> 7(6)
                // swap < ch +1: root = 3(1), ch +1 = 8(2), swap = 7(6)
                // swap == root: false
                // root [3(1)] <> swap [7(6)]
                section.setPixelProlongedColor(3, 0xCCFF)
                section.setPixelProlongedColor(7, 0x34FF)
                // [9846703125]
                // root = 7(1)
                // 2 * root + 1 <= 9: false
                //
                // [9846703125]
                // end = 9
                // [0(9)] <> end [9(5)]
                section.setPixelProlongedColor(0, 0xFFFF)
                section.setPixelProlongedColor(9, 0x33FF)
                // [5846703129]
                // siftDown(0, 8)
                // swap < child: root = 0(5), child = 1(8), swap = 0(5) -> 1(8)
                // swap < ch +1: root = 0(5), child = 2(4), swap = 1(8)
                // swap == root: false
                // root [0(5)] <> swap [1(8)]
                section.setPixelProlongedColor(0, 0x66FF)
                section.setPixelProlongedColor(1, 0xFFFF)
                // [8546703129]
                // root = 1(5)
                // 2 * root + 1 <= 8: true
                // swap < child: root = 1(5), child = 3(6), swap = 1(5) -> 3(6)
                // swap < ch +1: root = 1(5), ch +1 = 4(7), swap = 3(6) -> 4(7)
                // swap == root: false
                // root [1(5)] <> swap [4(7)]
                section.setPixelProlongedColor(1, 0x99FF)
                section.setPixelProlongedColor(4, 0xFFFF)
                // [8746503129]
                // root = 4(5)
                // 2 * root + 1 <= 8: false
                //
                // [8746503129]
                // end = 8
                // [0(8)] <> end [8(2)]
                section.setPixelProlongedColor(0, 0x67FF)
                section.setPixelProlongedColor(8, 0x66FF)
                // [2746503189]
                // siftDown(0, 7)
                // swap < child: root = 0(2), child = 1(7), swap = 0(2) -> 1(7)
                // swap < ch +1: root = 0(2), ch +1 = 2(4), swap = 1(7)
                // swap == root: false
                // root [0(2)] <> swap [1(7)]
                section.setPixelProlongedColor(0, 0x99FF)
                section.setPixelProlongedColor(1, 0x67FF)
                // [7246503189]
                // root = 1(2)
                // 2 * root + 1 <= 7: true
                // swap < child: root = 1(2), child = 3(6), swap = 1(2) -> 3(6)
                // swap < ch +1: root = 1(2), ch +1 = 4(5), swap = 3(6)
                // swap == root: false
                // root [1(2)] <> swap [3(6)]
                section.setPixelProlongedColor(1, 0xCCFF)
                section.setPixelProlongedColor(3, 0x67FF)
                // [7642503189]
                // root = 3(2)
                // 2 * root + 1 <= 7: true
                // swap < child: root = 3(2), child = 7(1), swap = 3(2)
                // swap < ch +1: ch +1 DNE
                // swap == root: true
                //
                // [7642503189]
                // end = 7
                // [0(7)] <> end [7(1)]
                section.setPixelProlongedColor(0, 0x34FF)
                section.setPixelProlongedColor(7, 0x99FF)
                // [1642503789]
                // siftDown(0, 6)
                // swap < child: root = 0(1), child = 1(6), swap = 0(1) -> 1(6)
                // swap < ch +1: root = 0(1), ch +1 = 2(4), swap = 1(6)
                // swap == root: false
                // root [0(1)] <> swap [1(6)]
                section.setPixelProlongedColor(0, 0xCCFF)
                section.setPixelProlongedColor(1, 0x34FF)
                // [6142503789]
                // root = 1(1)
                // 2 * root + 1 <= 6: true
                // swap < child: root = 1(1), child = 3(2), swap = 1(1) -> 3(2)
                // swap < ch +1: root = 1(1), ch +1 = 4(5), swap = 3(2) -> 4(5)
                // swap == root: false
                // root [1(1)] <> swap [4(5)]
                section.setPixelProlongedColor(1, 0xFFFF)
                section.setPixelProlongedColor(4, 0x34FF)
                // [6542103789]
                // root = 4(1)
                // 2 * root + 1 <= 6: false
                //
                // [6542103789]
                // end = 6
                // [0(6)] <> [6(3)]
                section.setPixelProlongedColor(0, 0x9AFF)
                section.setPixelProlongedColor(6, 0xCCFF)
                // [3542106789]
                // siftDown(0, 5)
                // swap < child: root = 0(3), child = 1(5), swap = 0(3) -> 1(5)
                // swap < ch +1: root = 0(3), ch +1 = 2(4), swap = 1(5)
                // swap == root: false
                // root [0(3)] <> swap [1(5)]
                section.setPixelProlongedColor(0, 0xFFFF)
                section.setPixelProlongedColor(1, 0x9AFF)
                // [5342106789]
                // root = 1(3)
                // 2 * root + 1 <= 5: true
                // swap < child: root = 1(3), child = 3(2), swap = 1(3)
                // swap < ch +1: root = 1(3), ch +1 = 4(1), swap = 1(3)
                // swap == root: true
                //
                // [5342106789]
                // end = 5
                // [0(5)] <> end [5(0)]
                section.setPixelProlongedColor(0, 0x00FF)
                section.setPixelProlongedColor(5, 0xFFFF)
                // [0342156789]
                // siftDown(0, 4)
                // swap < child: root = 0(0), child = 1(3), swap = 0(0) -> 1(3)
                // swap < ch +1: root = 0(0), ch +1 = 2(4), swap = 1(3) -> 2(4)
                // swap == root: false
                // root [0(0)] <> swap [2(4)]
                section.setPixelProlongedColor(0, 0xCDFF)
                section.setPixelProlongedColor(2, 0x00FF)
                // [4302156789]
                // root = 2(0)
                // 2 * root + 1 <= 4: false
                //
                // [4302156789]
                // end = 4
                // [0(4)] <> end [4(1)]
                section.setPixelProlongedColor(0, 0x34FF)
                section.setPixelProlongedColor(4, 0xCDFF)
                // [1302456789]
                // siftDown(0, 3)
                // swap < child: root = 0(1), child = 1(3), swap = 0(1) -> 1(3)
                // swap < ch +1: root = 0(1), ch +1 = 2(0), swap = 1(3)
                // swap == child: false
                // root [0(1)] <> swap [1(3)]
                section.setPixelProlongedColor(0, 0x9AFF)
                section.setPixelProlongedColor(1, 0x34FF)
                // [3102456789]
                // root = 1(1)
                // 2 * root + 1 <= 3: true
                // swap < child: root = 1(1), child = 3(2), swap = 1(1) -> 3(2)
                // swap < ch +1: ch +1 DNE
                // swap == root: false
                // root [1(1)] <> swap [3(2)]
                section.setPixelProlongedColor(1, 0x67FF)
                section.setPixelProlongedColor(3, 0x34FF)
                // [3201456789]
                // root = 3(2)
                // 2 * root + 1 <= 3: false
                //
                // [3201456789]
                // end = 3
                // [0(3)] <> end [3(1)]
                section.setPixelProlongedColor(0, 0x34FF)
                section.setPixelProlongedColor(3, 0x9AFF)
                // [1203456789]
                // siftDown(0, 2)
                // swap < child: root = 0(1), child = 1(2), swap = 0(1) -> 1(2)
                // swap < ch +1: root = 0(1), ch +1 = 2(0), swap = 1(2)
                // swap == root: false
                // root [0(1)] <> swap [1(2)]
                section.setPixelProlongedColor(0, 0x67FF)
                section.setPixelProlongedColor(1, 0x34FF)
                // [2103456789]
                // root = 1(1)
                // 2 * root + 1 <= 2: false
                //
                // [2103456789]
                // end = 2
                // [0(2)] <> end [2(0)]
                section.setPixelProlongedColor(0, 0x00FF)
                section.setPixelProlongedColor(2, 0x67FF)
                // [0123456789]
                // siftDown(0, 1)
                // swap < child: root = 0(0), child = 1(1), swap = 0(0) -> 1(1)
                // swap < ch +1: ch +1 DNE
                // swap == child: false
                // root [0(0)] <> swap [1(1)]
                section.setPixelProlongedColor(0, 0x34FF)
                section.setPixelProlongedColor(1, 0x00FF)
                // [1023456789]
                // root = 1(1)
                // 2 * root + 1 <= 1: false
                //
                // [1023456789]
                // end = 1
                // [0(1)] <> end [1(0)]
                section.setPixelProlongedColor(0, 0x00FF)
                section.setPixelProlongedColor(1, 0x34FF)
                // [0123456789]
                // siftDown(0, 0)
                // 2 * root + 1 <= 0: false
                //
                // [0123456789]
                // end = -1
                // end >= 0: false
            }
        }
    }
)
