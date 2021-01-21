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
import animatedledstrip.leds.emulation.createNewEmulatedStrip
import io.kotest.core.spec.style.StringSpec
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.mockkStatic

class CatToyTest : StringSpec(
    {
        mockkStatic("animatedledstrip.leds.animationmanagement.AnimationUtilsKt",
                    "animatedledstrip.leds.animationmanagement.AnimationManagementUtilsKt",
                    "animatedledstrip.leds.colormanagement.SetColorUtilsKt",
                    "animatedledstrip.leds.colormanagement.GetColorUtilsKt")

        val ledStrip = createNewEmulatedStrip(10)

        afterSpec { ledStrip.renderer.close() }

        "Cat Toy".config(enabled = false) {
            val section = ledStrip.sectionManager.createSection("cat", 0, 9)

            every { any<AnimationManager>().randomIndex() } returns
                    8 andThen 9 andThen 3 andThen 5 andThen
                    1 andThen 3 andThen 2 andThen 5 andThen
                    7 andThen 0 andThen 7 andThen 6 andThen
                    6 andThen 8 andThen 2

            val anim = ledStrip.animationManager.startAnimation(AnimationToRunParams()
                                                                    .animation("Cat Toy")
                                                                    .color(0xFF)
                                                                    .intParam("delay", 1)
                                                                    .runCount(15)
                                                                    .section("cat"))

            anim.join()

            val pCC = ColorContainer(0xFF).prepare(10)

            coVerifyOrder {
//                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
//                                                                 direction = Direction.FORWARD),
//                                   section.getSubSection(0, 8))
//                section.setPixelTemporaryColor(8, pCC)
//                section.revertPixel(8)
//                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
//                                                                 direction = Direction.FORWARD),
//                                   section.getSubSection(8, 9))
//                section.setPixelTemporaryColor(9, pCC)
//                section.revertPixel(9)
//                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
//                                                                 direction = Direction.BACKWARD),
//                                   section.getSubSection(3, 9))
//                section.setPixelTemporaryColor(3, pCC)
//                section.revertPixel(3)
//                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
//                                                                 direction = Direction.FORWARD),
//                                   section.getSubSection(3, 5))
//                section.setPixelTemporaryColor(5, pCC)
//                section.revertPixel(5)
//                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
//                                                                 direction = Direction.BACKWARD),
//                                   section.getSubSection(1, 5))
//                section.setPixelTemporaryColor(1, pCC)
//                section.revertPixel(1)
//                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
//                                                                 direction = Direction.FORWARD),
//                                   section.getSubSection(1, 3))
//                section.setPixelTemporaryColor(3, pCC)
//                section.revertPixel(3)
//                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
//                                                                 direction = Direction.BACKWARD),
//                                   section.getSubSection(2, 3))
//                section.setPixelTemporaryColor(2, pCC)
//                section.revertPixel(2)
//                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
//                                                                 direction = Direction.FORWARD),
//                                   section.getSubSection(2, 5))
//                section.setPixelTemporaryColor(5, pCC)
//                section.revertPixel(5)
//                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
//                                                                 direction = Direction.FORWARD),
//                                   section.getSubSection(5, 7))
//                section.setPixelTemporaryColor(7, pCC)
//                section.revertPixel(7)
//                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
//                                                                 direction = Direction.BACKWARD),
//                                   section.getSubSection(0, 7))
//                section.setPixelTemporaryColor(0, pCC)
//                section.revertPixel(0)
//                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
//                                                                 direction = Direction.FORWARD),
//                                   section.getSubSection(0, 7))
//                section.setPixelTemporaryColor(7, pCC)
//                section.revertPixel(7)
//                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
//                                                                 direction = Direction.BACKWARD),
//                                   section.getSubSection(6, 7))
//                section.setPixelTemporaryColor(6, pCC)
//                section.setPixelTemporaryColor(6, pCC)
//                section.revertPixel(6)
//                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
//                                                                 direction = Direction.FORWARD),
//                                   section.getSubSection(6, 8))
//                section.setPixelTemporaryColor(8, pCC)
//                section.revertPixel(8)
//                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
//                                                                 direction = Direction.BACKWARD),
//                                   section.getSubSection(2, 8))
//                section.setPixelTemporaryColor(2, pCC)
            }
        }
    }
)
