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

import animatedledstrip.animations.Direction
import animatedledstrip.colors.ColorContainer
import animatedledstrip.leds.animationmanagement.*
import animatedledstrip.leds.colormanagement.setPixelFadeColor
import animatedledstrip.leds.emulation.createNewEmulatedStrip
import io.kotest.core.spec.style.StringSpec
import io.mockk.mockkStatic
import io.mockk.verifyOrder

class MeteorTest : StringSpec(
    {
        mockkStatic("animatedledstrip.leds.animationmanagement.AnimationUtilsKt",
                    "animatedledstrip.leds.animationmanagement.AnimationManagementUtilsKt",
                    "animatedledstrip.leds.colormanagement.SetColorUtilsKt",
                    "animatedledstrip.leds.colormanagement.GetColorUtilsKt")

        val ledStrip = createNewEmulatedStrip(10)

        afterSpec { ledStrip.renderer.close() }

        "Meteor Forward" {
            val section = ledStrip.sectionManager.createSection("met-1", 0, 9)

            val anim = ledStrip.animationManager.startAnimation(AnimationToRunParams()
                                                                    .animation("Meteor")
                                                                    .color(ColorContainer(0xFF, 0xFFFF))
                                                                    .direction(Direction.FORWARD)
                                                                    .runCount(1)
                                                                    .section("met-1"))

            anim.join()

            val pCC = ColorContainer(0xFF, 0xFFFF).prepare(10)

            verifyOrder {
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
        }

        "Meteor Backward" {
            val section = ledStrip.sectionManager.createSection("met-2", 0, 9)

            val anim = ledStrip.animationManager.startAnimation(AnimationToRunParams()
                                                                    .animation("Meteor")
                                                                    .color(ColorContainer(0xFF, 0xFFFF))
                                                                    .direction(Direction.BACKWARD)
                                                                    .runCount(1)
                                                                    .section("met-2"))

            anim.join()

            val pCC = ColorContainer(0xFF, 0xFFFF).prepare(10)

            verifyOrder {
                section.setPixelFadeColor(9, pCC)
                section.setPixelFadeColor(8, pCC)
                section.setPixelFadeColor(7, pCC)
                section.setPixelFadeColor(6, pCC)
                section.setPixelFadeColor(5, pCC)
                section.setPixelFadeColor(4, pCC)
                section.setPixelFadeColor(3, pCC)
                section.setPixelFadeColor(2, pCC)
                section.setPixelFadeColor(1, pCC)
                section.setPixelFadeColor(0, pCC)
            }
        }
    }
)
