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
import animatedledstrip.colors.ccpresets.Black
import animatedledstrip.leds.animationmanagement.*
import animatedledstrip.leds.colormanagement.setStripFadeColor
import animatedledstrip.leds.colormanagement.setStripProlongedColor
import animatedledstrip.leds.emulation.createNewEmulatedStrip
import io.kotest.core.spec.style.StringSpec
import io.mockk.mockkStatic
import io.mockk.verifyOrder

class FadeToColorTest : StringSpec(
    {
        mockkStatic("animatedledstrip.leds.animationmanagement.AnimationUtilsKt",
                    "animatedledstrip.leds.animationmanagement.AnimationManagementUtilsKt",
                    "animatedledstrip.leds.colormanagement.SetColorUtilsKt",
                    "animatedledstrip.leds.colormanagement.GetColorUtilsKt")

        val ledStrip = createNewEmulatedStrip(10)

        afterSpec { ledStrip.close() }

        "Fade to Color".config(enabled = false) {
            val section = ledStrip.sectionManager.createSection("ftc", 0, 9)

            val anim = ledStrip.animationManager.startAnimation(AnimationToRunParams()
                                                                    .animation("Fade to Color")
                                                                    .addColor(ColorContainer(0xFF, 0xFFFF))
                                                                    .section("ftc"))

            anim.join()

            val pCC = ColorContainer(0xFF, 0xFFFF).prepare(10)

            verifyOrder {
                section.setStripProlongedColor(pCC)
                section.setStripFadeColor(ColorContainer.Black.prepare(10).toColorContainer().prepare(10))
            }
        }
    }
)
