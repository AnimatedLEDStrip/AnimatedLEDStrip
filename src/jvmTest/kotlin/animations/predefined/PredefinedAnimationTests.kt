/*
 *  Copyright (c) 2019-2020 AnimatedLEDStrip
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package animatedledstrip.test.animations.predefined

import animatedledstrip.animations.Direction
import animatedledstrip.colors.ColorContainer
import animatedledstrip.colors.ColorContainerInterface
import animatedledstrip.colors.ccpresets.CCBlack
import animatedledstrip.colors.inverse
import animatedledstrip.colors.shuffledWithIndices
import animatedledstrip.leds.animationmanagement.*
import animatedledstrip.leds.colormanagement.*
import animatedledstrip.leds.emulation.createNewEmulatedStrip
import animatedledstrip.test.haveProlongedColors
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.mockk.*

@Suppress("RedundantInnerClassModifier", "ClassName")
class PredefinedAnimationTests : StringSpec(
    {

        mockkStatic("animatedledstrip.leds.colormanagement.StripColorUtilsKt",
                    "animatedledstrip.leds.animationmanagement.StripAnimationUtilsKt")

        val ledStrip = createNewEmulatedStrip(50)

        beforeEach {
            ledStrip.clear()
        }

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

        "Alternate" {
            val section = ledStrip.sectionManager.createSection("alt", 0, 49)

            val anim = ledStrip.animationManager.startAnimation(AnimationToRunParams()
                                                                    .animation("Alternate")
                                                                    .color(0xFF, index = 0)
                                                                    .color(0xFFFF, index = 1)
                                                                    .color(0xFFFFFF, index = 2)
                                                                    .delay(100)
                                                                    .runCount(3)
                                                                    .section("alt"))

            anim.join()

            verifyOrder {
                section.setStripProlongedColor(ColorContainer(0xFF).prepare(50))
                section.setStripProlongedColor(ColorContainer(0xFFFF).prepare(50))
                section.setStripProlongedColor(ColorContainer(0xFFFFFF).prepare(50))
            }
        }

        "Bounce even number of pixels" {
            val section = ledStrip.sectionManager.createSection("bnc-1", 0, 9)
            val anim = ledStrip.animationManager.startAnimation(AnimationToRunParams()
                                                                    .animation("Bounce")
                                                                    .color(0xFF)
                                                                    .runCount(1)
                                                                    .section("bnc-1"))

            anim.join()

            val pCC = ColorContainer(0xFF).prepare(10)

            verify {
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

            coVerifyOrder {
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.FORWARD),
                                   section.getSubSection(0, 9))
                section.setPixelFadeColor(9, pCC)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.BACKWARD),
                                   section.getSubSection(0, 8))
                section.setPixelFadeColor(0, pCC)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.FORWARD),
                                   section.getSubSection(1, 8))
                section.setPixelFadeColor(8, pCC)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.BACKWARD),
                                   section.getSubSection(1, 7))
                section.setPixelFadeColor(1, pCC)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.FORWARD),
                                   section.getSubSection(2, 7))
                section.setPixelFadeColor(7, pCC)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.BACKWARD),
                                   section.getSubSection(2, 6))
                section.setPixelFadeColor(2, pCC)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.FORWARD),
                                   section.getSubSection(3, 6))
                section.setPixelFadeColor(6, pCC)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.BACKWARD),
                                   section.getSubSection(3, 5))
                section.setPixelFadeColor(3, pCC)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.FORWARD),
                                   section.getSubSection(4, 5))
                section.setPixelFadeColor(5, pCC)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.BACKWARD),
                                   section.getSubSection(4, 4))
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

            verify {
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

            coVerifyOrder {
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.FORWARD),
                                   section.getSubSection(0, 10))
                section.setPixelFadeColor(10, pCC)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.BACKWARD),
                                   section.getSubSection(0, 9))
                section.setPixelFadeColor(0, pCC)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.FORWARD),
                                   section.getSubSection(1, 9))
                section.setPixelFadeColor(9, pCC)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.BACKWARD),
                                   section.getSubSection(1, 8))
                section.setPixelFadeColor(1, pCC)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.FORWARD),
                                   section.getSubSection(2, 8))
                section.setPixelFadeColor(8, pCC)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.BACKWARD),
                                   section.getSubSection(2, 7))
                section.setPixelFadeColor(2, pCC)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.FORWARD),
                                   section.getSubSection(3, 7))
                section.setPixelFadeColor(7, pCC)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.BACKWARD),
                                   section.getSubSection(3, 6))
                section.setPixelFadeColor(3, pCC)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.FORWARD),
                                   section.getSubSection(4, 6))
                section.setPixelFadeColor(6, pCC)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.BACKWARD),
                                   section.getSubSection(4, 5))
                section.setPixelFadeColor(4, pCC)
                section.setPixelFadeColor(5, pCC)
            }
        }

        "Bounce to Color even number of pixels" {
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

        "Bounce to Color odd number of pixels" {
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


        "Bubble Sort" {
            val section = ledStrip.sectionManager.createSection("bst", 0, 9)

            mockkStatic("animatedledstrip.colors.CCUtilsKt")
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

        "Cat Toy" {
            val section = ledStrip.sectionManager.createSection("cat", 0, 9)

            every { any<AnimationManager>().randomPixel() } returns
                    8 andThen 9 andThen 3 andThen 5 andThen
                    1 andThen 3 andThen 2 andThen 5 andThen
                    7 andThen 0 andThen 7 andThen 6 andThen
                    6 andThen 8 andThen 2

            val anim = ledStrip.animationManager.startAnimation(AnimationToRunParams()
                                                                    .animation("Cat Toy")
                                                                    .color(0xFF)
                                                                    .delay(1)
                                                                    .runCount(15)
                                                                    .section("cat"))

            anim.join()

            val pCC = ColorContainer(0xFF).prepare(10)

            coVerifyOrder {
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.FORWARD),
                                   section.getSubSection(0, 8))
                section.setPixelTemporaryColor(8, pCC)
                section.revertPixel(8)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.FORWARD),
                                   section.getSubSection(8, 9))
                section.setPixelTemporaryColor(9, pCC)
                section.revertPixel(9)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.BACKWARD),
                                   section.getSubSection(3, 9))
                section.setPixelTemporaryColor(3, pCC)
                section.revertPixel(3)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.FORWARD),
                                   section.getSubSection(3, 5))
                section.setPixelTemporaryColor(5, pCC)
                section.revertPixel(5)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.BACKWARD),
                                   section.getSubSection(1, 5))
                section.setPixelTemporaryColor(1, pCC)
                section.revertPixel(1)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.FORWARD),
                                   section.getSubSection(1, 3))
                section.setPixelTemporaryColor(3, pCC)
                section.revertPixel(3)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.BACKWARD),
                                   section.getSubSection(2, 3))
                section.setPixelTemporaryColor(2, pCC)
                section.revertPixel(2)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.FORWARD),
                                   section.getSubSection(2, 5))
                section.setPixelTemporaryColor(5, pCC)
                section.revertPixel(5)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.FORWARD),
                                   section.getSubSection(5, 7))
                section.setPixelTemporaryColor(7, pCC)
                section.revertPixel(7)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.BACKWARD),
                                   section.getSubSection(0, 7))
                section.setPixelTemporaryColor(0, pCC)
                section.revertPixel(0)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.FORWARD),
                                   section.getSubSection(0, 7))
                section.setPixelTemporaryColor(7, pCC)
                section.revertPixel(7)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.BACKWARD),
                                   section.getSubSection(6, 7))
                section.setPixelTemporaryColor(6, pCC)
                section.setPixelTemporaryColor(6, pCC)
                section.revertPixel(6)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.FORWARD),
                                   section.getSubSection(6, 8))
                section.setPixelTemporaryColor(8, pCC)
                section.revertPixel(8)
                anim.runSequential(anim.params.withModifications(animation = "Pixel Run",
                                                                 direction = Direction.BACKWARD),
                                   section.getSubSection(2, 8))
                section.setPixelTemporaryColor(2, pCC)
            }
        }

        "Cat Toy to Color" {
            val section = ledStrip.sectionManager.createSection("ctc", 0, 9)

            every { any<AnimationManager>().shuffledIndices() } returns listOf(1, 9, 4, 2, 7, 0, 3, 6, 8, 5)

            val anim = ledStrip.animationManager.startAnimation(AnimationToRunParams()
                                                                    .animation("Cat Toy to Color")
                                                                    .color(ColorContainer(0xFF, 0xFFFF))
                                                                    .delay(1)
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

        "Fade to Color" {
            val section = ledStrip.sectionManager.createSection("ftc", 0, 9)

            val anim = ledStrip.animationManager.startAnimation(AnimationToRunParams()
                                                                    .animation("Fade to Color")
                                                                    .addColor(ColorContainer(0xFF, 0xFFFF))
                                                                    .section("ftc"))

            anim.join()

            val pCC = ColorContainer(0xFF, 0xFFFF).prepare(10)

            verifyOrder {
                section.setStripProlongedColor(pCC)
                section.setStripFadeColor(CCBlack.prepare(10).toColorContainer().prepare(10))
            }
        }

        "Fireworks" {
            ledStrip.sectionManager.createSection("fwk", 0, 25)

            val pCC1 = ColorContainer(0xFF).prepare(26)
            val pCC2 = ColorContainer(0xFFFF).prepare(26)
            val pCC3 = ColorContainer(0xFF00).prepare(26)

            every { any<AnimationManager>().randomPixel() } returns 15 andThen 3 andThen 20
            every { any<RunningAnimationParams>().randomColor() } returns pCC3 andThen pCC1 andThen pCC2

            val anim = ledStrip.animationManager.startAnimation(AnimationToRunParams()
                                                                    .animation("Fireworks")
                                                                    .color(pCC1, index = 0)
                                                                    .color(pCC2, index = 1)
                                                                    .color(pCC3, index = 2)
                                                                    .runCount(3)
                                                                    .section("fwk"))

            anim.join()

            verifyOrder {
                anim.runParallel(anim.params.withModifications(animation = "Ripple",
                                                               center = 15,
                                                               colors = mutableListOf(pCC3)))
                anim.runParallel(anim.params.withModifications(animation = "Ripple",
                                                               center = 3,
                                                               colors = mutableListOf(pCC1)))
                anim.runParallel(anim.params.withModifications(animation = "Ripple",
                                                               center = 20,
                                                               colors = mutableListOf(pCC2)))
            }


//        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip
//
//        val anim1 = testLEDs.startAnimation(
//            AnimationData()
//                .animation("Fireworks")
//                .addColor(0xFF)
//                .addColor(0xFF00)
//                .addColor(0xFF0000)
//                .addColor(0xFFFF)
//                .addColor(ColorContainer())
//        )
//
//        val anim2 = testLEDs.startAnimation(
//            AnimationData()
//                .animation("Fireworks")
//                .addColor(ColorContainer())
//        )
//
//        assertNotNull(anim1)
//        assertNotNull(anim2)
////        delay(100)
//        anim1.endAnimation()
//        anim2.endAnimation()
//        anim1.join()
//        anim2.join()
//            Unit
        }

        "Merge Sort Sequential" {
            val section = ledStrip.sectionManager.createSection("mss", 0, 9)

            mockkStatic("animatedledstrip.colors.CCUtilsKt")
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

        "Merge Sort Parallel" {
            val section = ledStrip.sectionManager.createSection("msp", 0, 9)

            mockkStatic("animatedledstrip.colors.CCUtilsKt")
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
                                                                    .animation("Merge Sort Parallel")
                                                                    .color(ColorContainer(0xFF, 0xFFFF))
                                                                    .section("msp"))

            anim.join()

            verify {
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


//    @Test
//    fun testMultiPixelRun() = runBlocking {
//        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip
//
//        val anim1 = testLEDs.startAnimation(
//            AnimationData()
//                .animation("Multi Pixel Run")
//                .color(0xFF)
//                .direction(Direction.FORWARD)
//        )
//
//        val anim2 = testLEDs.startAnimation(
//            AnimationData()
//                .animation("Multi-pixel Run")
//                .color(0xFF00)
//                .direction(Direction.BACKWARD)
//        )
//
//        assertNotNull(anim1)
//        assertNotNull(anim2)
//
//        delay(100)
//        anim1.endAnimation()
//        anim2.endAnimation()
//
//        anim1.join()
//        anim2.join()
//        Unit
//    }
//
//    @Test
//    fun testMultiPixelRunToColor() = runBlocking {
//        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip
//
//        val anim1 = testLEDs.startAnimation(
//            AnimationData()
//                .animation("Multi Pixel Run to Color")
//                .color(0xFF)
//                .direction(Direction.FORWARD)
//        )
//
//        assertNotNull(anim1)
//        delay(100)
//        anim1.endAnimation()
//        anim1.join()
//        testLEDs.assertAllPixels(0xFF)
//
//        val anim2 = testLEDs.startAnimation(
//            AnimationData()
//                .animation("Multi-Pixel Run to Color")
//                .color(0xFF00)
//                .direction(Direction.BACKWARD)
//        )
//
//        assertNotNull(anim2)
//        delay(100)
//        anim2.endAnimation()
//        anim2.join()
//        testLEDs.assertAllPixels(0xFF00)
//    }
//
//    @Test
//    fun testPixelMarathon() = runBlocking {
//        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip
//
//        val anim = testLEDs.startAnimation(
//            AnimationData()
//                .animation("Pixel Marathon")
//                .color(0xFF, index = 0)
//                .color(0xFFFF, index = 1)
//                .color(0xFF00FF, index = 2)
//                .color(0xFF00, index = 3)
//                .color(0xFFFF00, index = 4)
//        )
//
//        assertNotNull(anim)
//        delay(100)
//        anim.endAnimation()
//        anim.join()
//        Unit
//    }
//
//    @Test
//    fun testPixelRun() = runBlocking {
//        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip
//
//        val anim1 = testLEDs.startAnimation(
//            AnimationData()
//                .animation("Pixel Run")
//                .color(0xFF)
//                .direction(Direction.FORWARD)
//        )
//
//        val anim2 = testLEDs.startAnimation(
//            AnimationData()
//                .animation("Pixel Run")
//                .color(0xFF00)
//                .direction(Direction.BACKWARD)
//                .delay(-1)
//        )
//
//        assertNotNull(anim1)
//        assertNotNull(anim2)
//
//        delay(100)
//        anim1.endAnimation()
//        anim2.endAnimation()
//
//        anim1.join()
//        anim2.join()
//        Unit
//    }
//
//    @Test
//    fun testRipple() = runBlocking {
//        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip
//
//        val anim1 = testLEDs.startAnimation(
//            AnimationData()
//                .animation("Ripple")
//                .color(0xFFFF)
//                .center(25)
//                .distance(10)
//        )
//
//        val anim2 = testLEDs.startAnimation(
//            AnimationData()
//                .animation("Ripple")
//                .color(0xFF)
//        )
//
//        assertNotNull(anim1)
//        assertNotNull(anim2)
//
//        delay(100)
//        anim1.endAnimation()
//        anim2.endAnimation()
//
//        anim1.join()
//        anim2.join()
//        Unit
//    }
//
//    @Test
//    fun testSmoothChase() = runBlocking {
//        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip
//
//        val anim1 = testLEDs.startAnimation(
//            AnimationData()
//                .animation("Smooth Chase")
//                .color(ColorContainer(0xFF, 0xFF00))
//                .direction(Direction.FORWARD)
//        )
//
//        val anim2 = testLEDs.startAnimation(
//            AnimationData()
//                .animation("Smooth Chase")
//                .color(ColorContainer(0xFF00, 0xFF))
//                .direction(Direction.BACKWARD)
//        )
//
//        assertNotNull(anim1)
//        assertNotNull(anim2)
//
//        delay(100)
//        anim1.endAnimation()
//        anim2.endAnimation()
//
//        anim1.join()
//        anim2.join()
//        Unit
//    }
//
//    @Test
//    fun testSmoothFade() = runBlocking {
//        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip
//
//        val anim1 = testLEDs.startAnimation(
//            AnimationData()
//                .animation("Smooth Fade")
//                .color(ColorContainer(0xFF, 0xFF00))
//                .direction(Direction.FORWARD)
//        )
//
//        val anim2 = testLEDs.startAnimation(
//            AnimationData()
//                .animation("Smooth Fade")
//                .color(ColorContainer(0xFF00, 0xFF))
//                .direction(Direction.BACKWARD)
//        )
//
//        assertNotNull(anim1)
//        assertNotNull(anim2)
//
//        delay(100)
//        anim1.endAnimation()
//        anim2.endAnimation()
//
//        anim1.join()
//        anim2.join()
//        Unit
//    }
//
//    @Test
//    fun testSparkle() = runBlocking {
//        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip
//
//        val anim1 = testLEDs.startAnimation(
//            AnimationData()
//                .animation("Sparkle")
//                .color(0xFF)
//        )
//
//        val anim2 = testLEDs.startAnimation(
//            AnimationData()
//                .animation("Sparkle")
//                .color(0xFF)
//                .delay(-1)
//        )
//
//        assertNotNull(anim1)
//        assertNotNull(anim2)
//
//        delay(100)
//        anim1.endAnimation()
//        anim2.endAnimation()
//
//        anim1.join()
//        anim2.join()
//        Unit
//    }
//
//    @Test
//    fun testSparkleFade() = runBlocking {
//        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip
//
//        val anim1 = testLEDs.startAnimation(
//            AnimationData()
//                .animation("Sparkle Fade")
//                .color(0xFF00)
//        )
//
//        val anim2 = testLEDs.startAnimation(
//            AnimationData()
//                .animation("Sparkle Fade")
//                .color(0xFF00)
//                .delay(-1)
//        )
//
//        assertNotNull(anim1)
//        assertNotNull(anim2)
//
//        delay(100)
//        anim1.endAnimation()
//        anim2.endAnimation()
//
//        anim1.join()
//        anim2.join()
//        Unit
//    }
//
//    @Test
//    fun testSparkleToColor() = runBlocking {
//        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip
//
//        val anim = testLEDs.startAnimation(
//            AnimationData()
//                .animation("Sparkle to Color")
//                .color(0xFF)
//        )
//
//        delay(100)
//        anim?.endAnimation()
//        anim?.join()
//        testLEDs.assertAllPixels(0xFF)
//    }
//
//    @Test
//    fun testSplat() = runBlocking {
//        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip
//
//        val anim1 = testLEDs.startAnimation(
//            AnimationData()
//                .animation("Splat")
//                .color(0xFFFF)
//                .center(15)
//                .distance(10)
//        )
//
//        assertNotNull(anim1)
//        delay(500)
//        anim1.endAnimation()
//        anim1.join()
//        testLEDs.assertPixels(6..25, 0xFFFF)
//
//        val anim2 = testLEDs.startAnimation(
//            AnimationData()
//                .animation("Splat")
//                .color(0xFF00)
//        )
//
//        assertNotNull(anim2)
//        delay(500)
//        anim2.endAnimation()
//        anim2.join()
//        testLEDs.assertAllPixels(0xFF00)
//    }
//
//    @Test
//    fun testStack() = runBlocking {
//        val testLEDs1 = EmulatedAnimatedLEDStrip(50).wholeStrip
//
//        val anim1 = testLEDs1.startAnimation(
//            AnimationData()
//                .animation("Stack")
//                .color(0xFF)
//                .direction(Direction.FORWARD)
//        )
//
//        val testLEDs2 = EmulatedAnimatedLEDStrip(50).wholeStrip
//
//        val anim2 = testLEDs2.startAnimation(
//            AnimationData()
//                .animation("Stack")
//                .color(0xFF00)
//                .direction(Direction.BACKWARD)
//                .delay(-1)
//        )
//
//        assertNotNull(anim1)
//        assertNotNull(anim2)
//
//        delay(500)
//        anim1.endAnimation()
//        anim2.endAnimation()
//
//        anim1.join()
//        anim2.join()
//
//        testLEDs1.assertAllPixels(0xFF)
//        testLEDs2.assertAllPixels(0xFF00)
//    }
//
//    @Test
//    fun testStackOverflow() = runBlocking {
//        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip
//
//        val anim = testLEDs.startAnimation(
//            AnimationData()
//                .animation("Stack Overflow")
//                .color(0xFF)
//                .color(0xFF00)
//        )
//
//        assertNotNull(anim)
//        delay(100)
//        anim.endAnimation()
//        anim.join()
//        Unit
//    }
//
//    @Test
//    fun testWipe() = runBlocking {
//        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip
//
//        val anim1 = testLEDs.startAnimation(
//            AnimationData()
//                .animation("Wipe")
//                .color(0xFF)
//                .direction(Direction.FORWARD)
//        )
//
//        assertNotNull(anim1)
//        delay(100)
//        anim1.endAnimation()
//        anim1.join()
//        testLEDs.assertAllPixels(0xFF)
//
//        val anim2 = testLEDs.startAnimation(
//            AnimationData()
//                .animation("Wipe")
//                .color(0xFF00)
//                .direction(Direction.BACKWARD)
//        )
//
//        assertNotNull(anim2)
//        delay(100)
//        anim2.endAnimation()
//        anim2.join()
//        testLEDs.assertAllPixels(0xFF00)
//    }
//
//    @Test
//    fun testNonAnimation() {
//        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip
//
//        testLEDs.run(AnimationData().animation("Im Not an Animation"))
//        Unit
//    }
//
//    @Test
//    fun testCreatePredefinedAnimation() {
//        val info = Animation.AnimationInfo(
//            "multipixelruntocolor",
//            "",
//            "",
//            "",
//            -1,
//            0,
//            false,
//            ParamUsage.NOTUSED,
//            ParamUsage.NOTUSED,
//            ParamUsage.NOTUSED,
//            ParamUsage.NOTUSED,
//            ParamUsage.NOTUSED,
//        )
//
//        val anim: (AnimatedLEDStrip.Section, AnimationData, CoroutineScope) -> Unit = { _, _, _ -> }
//
//        val pAnim = PredefinedAnimation(info, anim)
//
//        assertTrue { pAnim.info === info }
//        assertTrue { pAnim.animation === anim }
//    }
//
//    @Test
//    fun testAttemptToAddAlreadyDefinedAnimation() {
//        val anim1 = PredefinedAnimation(
//            Animation.AnimationInfo(
//                "multipixelruntocolor",
//                "",
//                "",
//                "",
//                -1,
//                0,
//                false,
//                ParamUsage.NOTUSED,
//                ParamUsage.NOTUSED,
//                ParamUsage.NOTUSED,
//                ParamUsage.NOTUSED,
//                ParamUsage.NOTUSED,
//            )
//        ) { _, _, _ -> }
//
//        startLogCapture()
//
//        addNewAnimation(anim1)
//
//        assertLogs(setOf(Pair(Level.ERROR, "Animation multipixelruntocolor already defined")))
//
//        stopLogCapture()
//
//        val anim2 = PredefinedAnimation(
//            Animation.AnimationInfo(
//                "",
//                "ALT",
//                "",
//                "",
//                -1,
//                0,
//                false,
//                ParamUsage.NOTUSED,
//                ParamUsage.NOTUSED,
//                ParamUsage.NOTUSED,
//                ParamUsage.NOTUSED,
//                ParamUsage.NOTUSED,
//            )
//        ) { _, _, _ -> }
//
//        startLogCapture()
//
//        addNewAnimation(anim2)
//
//        assertLogs(setOf(Pair(Level.ERROR, "Animation with abbreviation ALT already defined")))
//
//        stopLogCapture()
//    }
    }
)
