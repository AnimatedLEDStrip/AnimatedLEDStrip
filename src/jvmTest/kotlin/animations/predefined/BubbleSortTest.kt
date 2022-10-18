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
import animatedledstrip.test.haveProlongedColors
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.verifyOrder

class BubbleSortTest : StringSpec(
    {
        mockkStatic("animatedledstrip.leds.animationmanagement.AnimationUtilsKt",
                    "animatedledstrip.leds.animationmanagement.AnimationManagementUtilsKt",
                    "animatedledstrip.leds.colormanagement.SetColorUtilsKt",
                    "animatedledstrip.leds.colormanagement.GetColorUtilsKt")

        val ledStrip = createNewEmulatedStrip(10)

        afterSpec { ledStrip.close() }

        "Bubble Sort" {
            val section = ledStrip.sectionManager.createSection("bst", 0, 9)

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
                                                                    .animation("Bubble Sort")
                                                                    .color(ColorContainer(0xFF, 0xFFFF))
                                                                    .section("bst"))

            anim.join()

            verifyOrder {
                // |19|42703685
                // 1|94|2703685
                section.setPixelProlongedColor(1, 0xCDFF)
                section.setPixelProlongedColor(2, 0x33FF)
                // 14|92|703685
                section.setPixelProlongedColor(2, 0x67FF)
                section.setPixelProlongedColor(3, 0x33FF)
                // 142|97|03685
                section.setPixelProlongedColor(3, 0x99FF)
                section.setPixelProlongedColor(4, 0x33FF)
                // 1427|90|3685
                section.setPixelProlongedColor(4, 0x00FF)
                section.setPixelProlongedColor(5, 0x33FF)
                // 14270|93|685
                section.setPixelProlongedColor(5, 0x9AFF)
                section.setPixelProlongedColor(6, 0x33FF)
                // 142703|96|85
                section.setPixelProlongedColor(6, 0xCCFF)
                section.setPixelProlongedColor(7, 0x33FF)
                // 1427036|98|5
                section.setPixelProlongedColor(7, 0x66FF)
                section.setPixelProlongedColor(8, 0x33FF)
                // 14270368|95|
                section.setPixelProlongedColor(8, 0xFFFF)
                section.setPixelProlongedColor(9, 0x33FF)
                // =1427036859=
                // |14|27036859
                // 1|42|7036859
                section.setPixelProlongedColor(1, 0x67FF)
                section.setPixelProlongedColor(2, 0xCDFF)
                // 12|47|036859
                // 124|70|36859
                section.setPixelProlongedColor(3, 0x00FF)
                section.setPixelProlongedColor(4, 0x99FF)
                // 1240|73|6859
                section.setPixelProlongedColor(4, 0x9AFF)
                section.setPixelProlongedColor(5, 0x99FF)
                // 12403|76|859
                section.setPixelProlongedColor(5, 0xCCFF)
                section.setPixelProlongedColor(6, 0x99FF)
                // 124036|78|59
                // 1240367|85|9
                section.setPixelProlongedColor(7, 0xFFFF)
                section.setPixelProlongedColor(8, 0x66FF)
                // 12403675|89|
                // =1240367589=
                // |12|40367589
                // 1|24|0367589
                // 12|40|367589
                section.setPixelProlongedColor(2, 0x00FF)
                section.setPixelProlongedColor(3, 0xCDFF)
                // 120|43|67589
                section.setPixelProlongedColor(3, 0x9AFF)
                section.setPixelProlongedColor(4, 0xCDFF)
                // 1203|46|7589
                // 12034|67|589
                // 120346|75|89
                section.setPixelProlongedColor(6, 0xFFFF)
                section.setPixelProlongedColor(7, 0x99FF)
                // 1203465|78|9
                // 12034657|89|
                // =1203465789=
                // |12|03465789
                // 1|20|3465789
                section.setPixelProlongedColor(1, 0x00FF)
                section.setPixelProlongedColor(2, 0x67FF)
                // 10|23|465789
                // 102|34|65789
                // 1023|46|5789
                // 10234|65|789
                section.setPixelProlongedColor(5, 0xFFFF)
                section.setPixelProlongedColor(6, 0xCCFF)
                // 102345|67|89
                // 1023456|78|9
                // 10234567|89|
                // =1203456789=
                // |10|23456789
                section.setPixelProlongedColor(0, 0x00FF)
                section.setPixelProlongedColor(1, 0x34FF)
                // 0|12|3456789
                // 01|23|456789
                // 012|34|56789
                // 0123|45|6789
                // 01234|56|789
                // 012345|67|89
                // 0123456|78|9
                // 01234567|89|
                // =0123456789=
            }

            section should haveProlongedColors(ColorContainer(0xFF, 0xFFFF).prepare(10))
        }
    }
)
