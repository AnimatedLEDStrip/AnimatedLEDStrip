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
import animatedledstrip.colors.inverse
import animatedledstrip.leds.animationmanagement.*
import animatedledstrip.leds.colormanagement.revertPixel
import animatedledstrip.leds.colormanagement.setPixelProlongedColor
import animatedledstrip.leds.colormanagement.setPixelTemporaryColor
import animatedledstrip.leds.emulation.createNewEmulatedStrip
import animatedledstrip.test.haveProlongedColors
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.verifyOrder

class CatToyToColorTest : StringSpec(
    {
        mockkStatic("animatedledstrip.leds.animationmanagement.AnimationUtilsKt",
                    "animatedledstrip.leds.animationmanagement.AnimationManagementUtilsKt",
                    "animatedledstrip.leds.colormanagement.SetColorUtilsKt",
                    "animatedledstrip.leds.colormanagement.GetColorUtilsKt")

        val ledStrip = createNewEmulatedStrip(10)

        afterSpec { ledStrip.close() }

        "Cat Toy to Color".config(enabled = false) {
            val section = ledStrip.sectionManager.createSection("ctc", 0, 9)

            every { any<AnimationManager>().shuffledIndices() } returns listOf(1, 9, 4, 2, 7, 0, 3, 6, 8, 5)

            val anim = ledStrip.animationManager.startAnimation(AnimationToRunParams()
                                                                    .animation("Cat Toy to Color")
                                                                    .color(ColorContainer(0xFF, 0xFFFF))
                                                                    .intParam("delay", 1)
                                                                    .section("ctc"))

            anim.join()

            val pCC = ColorContainer(0xFF, 0xFFFF).prepare(10)
            val iPCC = pCC.inverse()

            verifyOrder {
                // set:
                section.setPixelTemporaryColor(0, pCC)
                section.revertPixel(0)
                section.setPixelProlongedColor(1, pCC)
                // set: 1
                section.setPixelTemporaryColor(1, iPCC)
                section.revertPixel(1)
                section.setPixelTemporaryColor(2, pCC)
                section.revertPixel(2)
                section.setPixelTemporaryColor(3, pCC)
                section.revertPixel(3)
                section.setPixelTemporaryColor(4, pCC)
                section.revertPixel(4)
                section.setPixelTemporaryColor(5, pCC)
                section.revertPixel(5)
                section.setPixelTemporaryColor(6, pCC)
                section.revertPixel(6)
                section.setPixelTemporaryColor(7, pCC)
                section.revertPixel(7)
                section.setPixelTemporaryColor(8, pCC)
                section.revertPixel(8)
                section.setPixelProlongedColor(9, pCC)
                // set: 1 9
                section.setPixelTemporaryColor(9, iPCC)
                section.revertPixel(9)
                section.setPixelTemporaryColor(8, pCC)
                section.revertPixel(8)
                section.setPixelTemporaryColor(7, pCC)
                section.revertPixel(7)
                section.setPixelTemporaryColor(6, pCC)
                section.revertPixel(6)
                section.setPixelTemporaryColor(5, pCC)
                section.revertPixel(5)
                section.setPixelProlongedColor(4, pCC)
                // set: 1 4 9
                section.setPixelTemporaryColor(4, iPCC)
                section.revertPixel(4)
                section.setPixelTemporaryColor(3, pCC)
                section.revertPixel(3)
                section.setPixelProlongedColor(2, pCC)
                // set: 1 2 4 9
                section.setPixelTemporaryColor(2, iPCC)
                section.revertPixel(2)
                section.setPixelTemporaryColor(3, pCC)
                section.revertPixel(3)
                section.setPixelTemporaryColor(4, iPCC)
                section.revertPixel(4)
                section.setPixelTemporaryColor(5, pCC)
                section.revertPixel(5)
                section.setPixelTemporaryColor(6, pCC)
                section.revertPixel(6)
                section.setPixelProlongedColor(7, pCC)
                // set: 1 2 4 7 9
                section.setPixelTemporaryColor(7, iPCC)
                section.revertPixel(7)
                section.setPixelTemporaryColor(6, pCC)
                section.revertPixel(6)
                section.setPixelTemporaryColor(5, pCC)
                section.revertPixel(5)
                section.setPixelTemporaryColor(4, iPCC)
                section.revertPixel(4)
                section.setPixelTemporaryColor(3, pCC)
                section.revertPixel(3)
                section.setPixelTemporaryColor(2, iPCC)
                section.revertPixel(2)
                section.setPixelTemporaryColor(1, iPCC)
                section.revertPixel(1)
                section.setPixelProlongedColor(0, pCC)
                // set: 0 1 2 4 7 9
                section.setPixelTemporaryColor(0, iPCC)
                section.revertPixel(0)
                section.setPixelTemporaryColor(1, iPCC)
                section.revertPixel(1)
                section.setPixelTemporaryColor(2, iPCC)
                section.revertPixel(2)
                section.setPixelProlongedColor(3, pCC)
                // set: 0 1 2 3 4 7 9
                section.setPixelTemporaryColor(3, iPCC)
                section.revertPixel(3)
                section.setPixelTemporaryColor(4, iPCC)
                section.revertPixel(4)
                section.setPixelTemporaryColor(5, pCC)
                section.revertPixel(5)
                section.setPixelProlongedColor(6, pCC)
                // set: 0 1 2 3 4 6 7 9
                section.setPixelTemporaryColor(6, iPCC)
                section.revertPixel(6)
                section.setPixelTemporaryColor(7, iPCC)
                section.revertPixel(7)
                section.setPixelProlongedColor(8, pCC)
                // set: 0 1 2 3 4 6 7 8 9
                section.setPixelTemporaryColor(8, iPCC)
                section.revertPixel(8)
                section.setPixelTemporaryColor(7, iPCC)
                section.revertPixel(7)
                section.setPixelTemporaryColor(6, iPCC)
                section.revertPixel(6)
                section.setPixelProlongedColor(5, pCC)
                // set: 0 1 2 3 4 5 6 7 8 9
            }

            section should haveProlongedColors(pCC)
        }
    }
)
