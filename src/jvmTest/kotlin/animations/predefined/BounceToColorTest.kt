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
import animatedledstrip.leds.colormanagement.setPixelProlongedColor
import animatedledstrip.leds.emulation.createNewEmulatedStrip
import animatedledstrip.test.haveProlongedColors
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.mockk.coVerifyOrder
import io.mockk.mockkStatic
import io.mockk.verify

class BounceToColorTest : StringSpec(
    {
        mockkStatic("animatedledstrip.leds.animationmanagement.AnimationUtilsKt",
                    "animatedledstrip.leds.animationmanagement.AnimationManagementUtilsKt",
                    "animatedledstrip.leds.colormanagement.SetColorUtilsKt",
                    "animatedledstrip.leds.colormanagement.GetColorUtilsKt")

        val ledStrip = createNewEmulatedStrip(11)

        afterSpec { ledStrip.renderer.close() }

        "Bounce to Color even number of pixels".config(enabled = false) {
            val section = ledStrip.sectionManager.createSection("btc-1", 0, 9)

            val anim = ledStrip.animationManager.startAnimation(AnimationToRunParams()
                                                                    .animation("Bounce to Color")
                                                                    .color(0xFF)
                                                                    .runCount(1)
                                                                    .section("btc-1"))

            anim.join()

            val pCC = ColorContainer(0xFF).prepare(10)

            section should haveProlongedColors(pCC)

            verify {
                section.setPixelProlongedColor(0, pCC)
                section.setPixelProlongedColor(1, pCC)
                section.setPixelProlongedColor(2, pCC)
                section.setPixelProlongedColor(3, pCC)
                section.setPixelProlongedColor(4, pCC)
                section.setPixelProlongedColor(5, pCC)
                section.setPixelProlongedColor(6, pCC)
                section.setPixelProlongedColor(7, pCC)
                section.setPixelProlongedColor(8, pCC)
                section.setPixelProlongedColor(9, pCC)
            }

            coVerifyOrder {
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.FORWARD),
                                   section.getSubSection(0, 9))
                section.setPixelProlongedColor(9, pCC)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.BACKWARD),
                                   section.getSubSection(0, 8))
                section.setPixelProlongedColor(0, pCC)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.FORWARD),
                                   section.getSubSection(1, 8))
                section.setPixelProlongedColor(8, pCC)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.BACKWARD),
                                   section.getSubSection(1, 7))
                section.setPixelProlongedColor(1, pCC)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.FORWARD),
                                   section.getSubSection(2, 7))
                section.setPixelProlongedColor(7, pCC)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.BACKWARD),
                                   section.getSubSection(2, 6))
                section.setPixelProlongedColor(2, pCC)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.FORWARD),
                                   section.getSubSection(3, 6))
                section.setPixelProlongedColor(6, pCC)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.BACKWARD),
                                   section.getSubSection(3, 5))
                section.setPixelProlongedColor(3, pCC)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.FORWARD),
                                   section.getSubSection(4, 5))
                section.setPixelProlongedColor(5, pCC)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.BACKWARD),
                                   section.getSubSection(4, 4))
                section.setPixelProlongedColor(4, pCC)
            }
        }

        "Bounce to Color odd number of pixels".config(enabled = false) {
            val section = ledStrip.sectionManager.createSection("btc-2", 0, 10)

            val anim = ledStrip.animationManager.startAnimation(AnimationToRunParams()
                                                                    .animation("Bounce to Color")
                                                                    .color(0xFF)
                                                                    .runCount(1)
                                                                    .section("btc-2"))

            anim.join()

            val pCC = ColorContainer(0xFF).prepare(11)

            section should haveProlongedColors(pCC)

            verify {
                section.setPixelProlongedColor(0, pCC)
                section.setPixelProlongedColor(1, pCC)
                section.setPixelProlongedColor(2, pCC)
                section.setPixelProlongedColor(3, pCC)
                section.setPixelProlongedColor(4, pCC)
                section.setPixelProlongedColor(5, pCC)
                section.setPixelProlongedColor(6, pCC)
                section.setPixelProlongedColor(7, pCC)
                section.setPixelProlongedColor(8, pCC)
                section.setPixelProlongedColor(9, pCC)
                section.setPixelProlongedColor(10, pCC)
            }

            coVerifyOrder {
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.FORWARD),
                                   section.getSubSection(0, 10))
                section.setPixelProlongedColor(10, pCC)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.BACKWARD),
                                   section.getSubSection(0, 9))
                section.setPixelProlongedColor(0, pCC)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.FORWARD),
                                   section.getSubSection(1, 9))
                section.setPixelProlongedColor(9, pCC)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.BACKWARD),
                                   section.getSubSection(1, 8))
                section.setPixelProlongedColor(1, pCC)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.FORWARD),
                                   section.getSubSection(2, 8))
                section.setPixelProlongedColor(8, pCC)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.BACKWARD),
                                   section.getSubSection(2, 7))
                section.setPixelProlongedColor(2, pCC)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.FORWARD),
                                   section.getSubSection(3, 7))
                section.setPixelProlongedColor(7, pCC)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.BACKWARD),
                                   section.getSubSection(3, 6))
                section.setPixelProlongedColor(3, pCC)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.FORWARD),
                                   section.getSubSection(4, 6))
                section.setPixelProlongedColor(6, pCC)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.BACKWARD),
                                   section.getSubSection(4, 5))
                section.setPixelProlongedColor(4, pCC)
                section.setPixelProlongedColor(5, pCC)
            }
        }
    }
)
