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
import animatedledstrip.leds.animationmanagement.*
import animatedledstrip.leds.colormanagement.revertPixel
import animatedledstrip.leds.colormanagement.setPixelFadeColor
import animatedledstrip.leds.colormanagement.setPixelTemporaryColor
import animatedledstrip.leds.emulation.createNewEmulatedStrip
import io.kotest.core.spec.style.StringSpec
import io.mockk.mockkStatic
import io.mockk.verify
import io.mockk.verifyOrder

class BounceTest : StringSpec(
    {
        mockkStatic("animatedledstrip.leds.animationmanagement.AnimationUtilsKt",
                    "animatedledstrip.leds.animationmanagement.AnimationManagementUtilsKt",
                    "animatedledstrip.leds.colormanagement.SetColorUtilsKt",
                    "animatedledstrip.leds.colormanagement.GetColorUtilsKt")

        val ledStrip = createNewEmulatedStrip(11)

        afterSpec { ledStrip.renderer.close() }

        "Bounce even number of pixels" {
            val section = ledStrip.sectionManager.createSection("bnc-1", 0, 9)
            val anim = ledStrip.animationManager.startAnimation(AnimationToRunParams()
                                                                    .animation("Bounce")
                                                                    .color(0xFF)
                                                                    .runCount(1)
                                                                    .section("bnc-1"))

            anim.join()

            val pCC = ColorContainer(0xFF).prepare(10)

            verify(exactly = 1) {
                section.setPixelFadeColor(0, pCC)
                section.setPixelFadeColor(1, pCC)
                section.setPixelFadeColor(2, pCC)
                section.setPixelFadeColor(3, pCC)
                section.setPixelFadeColor(4, pCC)
                section.setPixelFadeColor(5, pCC)
                section.setPixelFadeColor(6, pCC)
                section.setPixelFadeColor(7, pCC)
                section.setPixelFadeColor(8, pCC)
                section.setPixelFadeColor(9, pCC)
            }

            verify(exactly = 1) {
                section.setPixelTemporaryColor(0, pCC)
                section.revertPixel(0)
            }

            verify(exactly = 3) {
                section.setPixelTemporaryColor(1, pCC)
                section.revertPixel(1)
            }

            verify(exactly = 5) {
                section.setPixelTemporaryColor(2, pCC)
                section.revertPixel(2)
            }

            verify(exactly = 7) {
                section.setPixelTemporaryColor(3, pCC)
                section.revertPixel(3)
            }

            verify(exactly = 9) {
                section.setPixelTemporaryColor(4, pCC)
                section.revertPixel(4)
            }

            verify(exactly = 8) {
                section.setPixelTemporaryColor(5, pCC)
                section.revertPixel(5)
            }

            verify(exactly = 6) {
                section.setPixelTemporaryColor(6, pCC)
                section.revertPixel(6)
            }

            verify(exactly = 4) {
                section.setPixelTemporaryColor(7, pCC)
                section.revertPixel(7)
            }

            verify(exactly = 2) {
                section.setPixelTemporaryColor(8, pCC)
                section.revertPixel(8)
            }

            verify(exactly = 0) {
                section.setPixelTemporaryColor(9, pCC)
                section.revertPixel(9)
            }

            verifyOrder {
                section.setPixelTemporaryColor(0, pCC)
                section.revertPixel(0)
                section.setPixelTemporaryColor(1, pCC)
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
                section.setPixelFadeColor(9, pCC)

                section.setPixelTemporaryColor(8, pCC)
                section.revertPixel(8)
                section.setPixelTemporaryColor(7, pCC)
                section.revertPixel(7)
                section.setPixelTemporaryColor(6, pCC)
                section.revertPixel(6)
                section.setPixelTemporaryColor(5, pCC)
                section.revertPixel(5)
                section.setPixelTemporaryColor(4, pCC)
                section.revertPixel(4)
                section.setPixelTemporaryColor(3, pCC)
                section.revertPixel(3)
                section.setPixelTemporaryColor(2, pCC)
                section.revertPixel(2)
                section.setPixelTemporaryColor(1, pCC)
                section.revertPixel(1)
                section.setPixelFadeColor(0, pCC)

                section.setPixelTemporaryColor(1, pCC)
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
                section.setPixelFadeColor(8, pCC)

                section.setPixelTemporaryColor(7, pCC)
                section.revertPixel(7)
                section.setPixelTemporaryColor(6, pCC)
                section.revertPixel(6)
                section.setPixelTemporaryColor(5, pCC)
                section.revertPixel(5)
                section.setPixelTemporaryColor(4, pCC)
                section.revertPixel(4)
                section.setPixelTemporaryColor(3, pCC)
                section.revertPixel(3)
                section.setPixelTemporaryColor(2, pCC)
                section.revertPixel(2)
                section.setPixelFadeColor(1, pCC)

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
                section.setPixelFadeColor(7, pCC)

                section.setPixelTemporaryColor(6, pCC)
                section.revertPixel(6)
                section.setPixelTemporaryColor(5, pCC)
                section.revertPixel(5)
                section.setPixelTemporaryColor(4, pCC)
                section.revertPixel(4)
                section.setPixelTemporaryColor(3, pCC)
                section.revertPixel(3)
                section.setPixelFadeColor(2, pCC)

                section.setPixelTemporaryColor(3, pCC)
                section.revertPixel(3)
                section.setPixelTemporaryColor(4, pCC)
                section.revertPixel(4)
                section.setPixelTemporaryColor(5, pCC)
                section.revertPixel(5)
                section.setPixelFadeColor(6, pCC)

                section.setPixelTemporaryColor(5, pCC)
                section.revertPixel(5)
                section.setPixelTemporaryColor(4, pCC)
                section.revertPixel(4)
                section.setPixelFadeColor(3, pCC)

                section.setPixelTemporaryColor(4, pCC)
                section.revertPixel(4)
                section.setPixelFadeColor(5, pCC)

                section.setPixelFadeColor(4, pCC)
            }
        }

        "Bounce odd number of pixels" {
            val section = ledStrip.sectionManager.createSection("bnc-2", 0, 10)

            val anim = ledStrip.animationManager.startAnimation(AnimationToRunParams()
                                                                    .animation("Bounce")
                                                                    .color(0xFF)
                                                                    .runCount(1)
                                                                    .section("bnc-2"))

            anim.join()

            val pCC = ColorContainer(0xFF).prepare(11)

            verify(exactly = 1) {
                section.setPixelFadeColor(0, pCC)
                section.setPixelFadeColor(1, pCC)
                section.setPixelFadeColor(2, pCC)
                section.setPixelFadeColor(3, pCC)
                section.setPixelFadeColor(4, pCC)
                section.setPixelFadeColor(5, pCC)
                section.setPixelFadeColor(6, pCC)
                section.setPixelFadeColor(7, pCC)
                section.setPixelFadeColor(8, pCC)
                section.setPixelFadeColor(9, pCC)
                section.setPixelFadeColor(10, pCC)
            }

            verify(exactly = 1) {
                section.setPixelTemporaryColor(0, pCC)
                section.revertPixel(0)
            }

            verify(exactly = 3) {
                section.setPixelTemporaryColor(1, pCC)
                section.revertPixel(1)
            }

            verify(exactly = 5) {
                section.setPixelTemporaryColor(2, pCC)
                section.revertPixel(2)
            }

            verify(exactly = 7) {
                section.setPixelTemporaryColor(3, pCC)
                section.revertPixel(3)
            }

            verify(exactly = 9) {
                section.setPixelTemporaryColor(4, pCC)
                section.revertPixel(4)
            }

            verify(exactly = 10) {
                section.setPixelTemporaryColor(5, pCC)
                section.revertPixel(5)
            }

            verify(exactly = 8) {
                section.setPixelTemporaryColor(6, pCC)
                section.revertPixel(6)
            }

            verify(exactly = 6) {
                section.setPixelTemporaryColor(7, pCC)
                section.revertPixel(7)
            }

            verify(exactly = 4) {
                section.setPixelTemporaryColor(8, pCC)
                section.revertPixel(8)
            }

            verify(exactly = 2) {
                section.setPixelTemporaryColor(9, pCC)
                section.revertPixel(9)
            }

            verify(exactly = 0) {
                section.setPixelTemporaryColor(10, pCC)
                section.revertPixel(10)
            }

            verifyOrder {
                section.setPixelTemporaryColor(0, pCC)
                section.revertPixel(0)
                section.setPixelTemporaryColor(1, pCC)
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
                section.setPixelTemporaryColor(9, pCC)
                section.revertPixel(9)
                section.setPixelFadeColor(10, pCC)

                section.setPixelTemporaryColor(9, pCC)
                section.revertPixel(9)
                section.setPixelTemporaryColor(8, pCC)
                section.revertPixel(8)
                section.setPixelTemporaryColor(7, pCC)
                section.revertPixel(7)
                section.setPixelTemporaryColor(6, pCC)
                section.revertPixel(6)
                section.setPixelTemporaryColor(5, pCC)
                section.revertPixel(5)
                section.setPixelTemporaryColor(4, pCC)
                section.revertPixel(4)
                section.setPixelTemporaryColor(3, pCC)
                section.revertPixel(3)
                section.setPixelTemporaryColor(2, pCC)
                section.revertPixel(2)
                section.setPixelTemporaryColor(1, pCC)
                section.revertPixel(1)
                section.setPixelFadeColor(0, pCC)

                section.setPixelTemporaryColor(1, pCC)
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
                section.setPixelFadeColor(9, pCC)

                section.setPixelTemporaryColor(8, pCC)
                section.revertPixel(8)
                section.setPixelTemporaryColor(7, pCC)
                section.revertPixel(7)
                section.setPixelTemporaryColor(6, pCC)
                section.revertPixel(6)
                section.setPixelTemporaryColor(5, pCC)
                section.revertPixel(5)
                section.setPixelTemporaryColor(4, pCC)
                section.revertPixel(4)
                section.setPixelTemporaryColor(3, pCC)
                section.revertPixel(3)
                section.setPixelTemporaryColor(2, pCC)
                section.revertPixel(2)
                section.setPixelFadeColor(1, pCC)

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
                section.setPixelFadeColor(8, pCC)

                section.setPixelTemporaryColor(7, pCC)
                section.revertPixel(7)
                section.setPixelTemporaryColor(6, pCC)
                section.revertPixel(6)
                section.setPixelTemporaryColor(5, pCC)
                section.revertPixel(5)
                section.setPixelTemporaryColor(4, pCC)
                section.revertPixel(4)
                section.setPixelTemporaryColor(3, pCC)
                section.revertPixel(3)
                section.setPixelFadeColor(2, pCC)

                section.setPixelTemporaryColor(3, pCC)
                section.revertPixel(3)
                section.setPixelTemporaryColor(4, pCC)
                section.revertPixel(4)
                section.setPixelTemporaryColor(5, pCC)
                section.revertPixel(5)
                section.setPixelTemporaryColor(6, pCC)
                section.revertPixel(6)
                section.setPixelFadeColor(7, pCC)

                section.setPixelTemporaryColor(6, pCC)
                section.revertPixel(6)
                section.setPixelTemporaryColor(5, pCC)
                section.revertPixel(5)
                section.setPixelTemporaryColor(4, pCC)
                section.revertPixel(4)
                section.setPixelFadeColor(3, pCC)

                section.setPixelTemporaryColor(4, pCC)
                section.revertPixel(4)
                section.setPixelTemporaryColor(5, pCC)
                section.revertPixel(5)
                section.setPixelFadeColor(6, pCC)

                section.setPixelTemporaryColor(5, pCC)
                section.revertPixel(5)
                section.setPixelFadeColor(4, pCC)

                section.setPixelFadeColor(5, pCC)
            }
        }
    }
)
