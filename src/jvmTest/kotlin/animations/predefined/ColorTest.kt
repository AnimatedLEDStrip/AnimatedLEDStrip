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
import animatedledstrip.leds.animationmanagement.*
import animatedledstrip.leds.colormanagement.setStripProlongedColor
import animatedledstrip.leds.emulation.createNewEmulatedStrip
import animatedledstrip.test.haveProlongedColors
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.mockk.mockkStatic
import io.mockk.verify

class ColorTest : StringSpec(
    {
        mockkStatic("animatedledstrip.leds.animationmanagement.AnimationUtilsKt",
                    "animatedledstrip.leds.animationmanagement.AnimationManagementUtilsKt",
                    "animatedledstrip.leds.colormanagement.SetColorUtilsKt",
                    "animatedledstrip.leds.colormanagement.GetColorUtilsKt")

        val ledStrip = createNewEmulatedStrip(50)

        afterSpec { ledStrip.close() }

        "Color" {
            val section = ledStrip.sectionManager.createSection("col", 0, 49)

            val anim = ledStrip.animationManager.startAnimation(AnimationToRunParams()
                                                                    .animation("Color")
                                                                    .color(0xFF)
                                                                    .section("col"))

            anim.join()

            verify {
                section.setStripProlongedColor(ColorContainer(0xFF).prepare(50))
            }

            section should haveProlongedColors(ColorContainer(0xFF).prepare(50))
        }
    }
)
