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

class MergeSortSequentialTest : StringSpec(
    {
        mockkStatic("animatedledstrip.leds.animationmanagement.AnimationUtilsKt",
                    "animatedledstrip.leds.animationmanagement.AnimationManagementUtilsKt",
                    "animatedledstrip.leds.colormanagement.SetColorUtilsKt",
                    "animatedledstrip.leds.colormanagement.GetColorUtilsKt")

        val ledStrip = createNewEmulatedStrip(11)

        afterSpec { ledStrip.renderer.close() }

        "Merge Sort Sequential" {
            val section = ledStrip.sectionManager.createSection("mss", 0, 9)

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
                                                                    .animation("Merge Sort Sequential")
                                                                    .color(ColorContainer(0xFF, 0xFFFF))
                                                                    .section("mss"))

            anim.join()

            verifyOrder {
                // 1942703685
                // 19427-03685
                // 194-27 03685
                // 19-4 27 03685
                // 1-9 4 27 03685
                // |1 9| 4 27 03685
                // |1(9)| 4 27 03685
                // 19 4 27 03685
                // |19 4| 27 03685
                section.setPixelProlongedColor(2, 0x33FF)
                // |1(4)9| 27 03685
                section.setPixelProlongedColor(1, 0xCDFF)
                // (149) 27 03685
                // 149 2-7 03685
                // 149 |2 7| 03685
                // 149 |2(7)| 03685
                // 149 (27) 03685
                // |149 27| 03685
                section.setPixelProlongedColor(3, 0x33FF)
                // |14[2]9 7| 03685
                section.setPixelProlongedColor(2, 0xCDFF)
                section.setPixelProlongedColor(1, 0x67FF)
                // |1(2)49 7| 03685
                section.setPixelProlongedColor(4, 0x33FF)
                section.setPixelProlongedColor(3, 0x99FF)
                // |124(7)9| 03685
                // (12479) 03685
                // 12479 036-85
                // 12479 03-6 85
                // 12479 0-3 6 85
                // 12479 |0 3| 6 85
                // 12479 |0(3)| 6 85
                // 12479 (03) 6 85
                // 12479 |03 6| 85
                // 12479 |03(6)| 85
                // 12479 (036) 85
                // 12479 036 8-5
                // 12479 036 |8 5|
                section.setPixelProlongedColor(9, 0x66FF)
                // 12479 036 |(5)8|
                section.setPixelProlongedColor(8, 0xFFFF)
                // 12479 036 (58)
                // 12479 |036 58|
                section.setPixelProlongedColor(8, 0xCCFF)
                // 12479 |03(5)6 8|
                section.setPixelProlongedColor(7, 0xFFFF)
                // 12479 |0356(8)|
                // 12479 (03568)
                // |12479 03568|
                section.setPixelProlongedColor(5, 0x33FF)
                // |1247[0]9 3568|
                section.setPixelProlongedColor(4, 0x99FF)
                // |124[0]79 3568|
                section.setPixelProlongedColor(3, 0xCDFF)
                // |12[0]479 3568|
                section.setPixelProlongedColor(2, 0x67FF)
                // |1[0]2479 3568|
                section.setPixelProlongedColor(1, 0x34FF)
                section.setPixelProlongedColor(0, 0x00FF)
                // |(0)12479 3568|
                section.setPixelProlongedColor(6, 0x33FF)
                // |01247[3]9 568|
                section.setPixelProlongedColor(5, 0x99FF)
                // |0124[3]79 568|
                section.setPixelProlongedColor(4, 0xCDFF)
                section.setPixelProlongedColor(3, 0x9AFF)
                // |012(3)479 568|
                section.setPixelProlongedColor(7, 0x33FF)
                // |012347[5]9 68|
                section.setPixelProlongedColor(6, 0x99FF)
                section.setPixelProlongedColor(5, 0xFFFF)
                // |01234(5)79 68|
                section.setPixelProlongedColor(8, 0x33FF)
                // |0123457[6]9 8|
                section.setPixelProlongedColor(7, 0x99FF)
                section.setPixelProlongedColor(6, 0xCCFF)
                // |012345(6)79 8|
                section.setPixelProlongedColor(9, 0x33FF)
                section.setPixelProlongedColor(8, 0x66FF)
                // |01234567(8)9|
                // (0123456789)
            }
        }
    }
)
