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

package animatedledstrip.test.animationutils

import animatedledstrip.animationutils.*
import animatedledstrip.colors.ColorContainer
import animatedledstrip.colors.shuffledWithIndices
import animatedledstrip.leds.emulated.EmulatedAnimatedLEDStrip
import animatedledstrip.leds.endAnimation
import animatedledstrip.leds.join
import animatedledstrip.leds.joinBlocking
import animatedledstrip.test.assertAllPixels
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.delay
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

@Suppress("RedundantInnerClassModifier", "ClassName")
class AnimationTests : StringSpec(
    {

        val ledStrip = EmulatedAnimatedLEDStrip(50)

        beforeEach() {
            ledStrip.clear()
        }

        "Color" {
            val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip

            val anim = testLEDs.startAnimation(AnimationToRunParams().animation("Color").color(0xFF))
            assertNotNull(anim)
            delay(500)
            anim.endAnimation()
            anim.join()
            testLEDs.assertAllPixels(0xFF)
        }

        "Alternate" {
            val leds = spyk(ledStrip.wholeStrip)

            val anim = leds.startAnimation(AnimationToRunParams()
                                               .animation("Alternate")
                                               .color(0xFF, index = 0)
                                               .color(0xFFFF, index = 1)
                                               .color(0xFFFFFF, index = 2)
                                               .delay(100)
                                               .runCount(3))

            assertNotNull(anim)
            anim.joinBlocking()

            verify(exactly = 1) { leds.setProlongedStripColor(color = ColorContainer(0xFF)) }
            verify(exactly = 1) { leds.setProlongedStripColor(color = ColorContainer(0xFFFF)) }
            verify(exactly = 1) { leds.setProlongedStripColor(color = ColorContainer(0xFFFFFF)) }
        }

        "Bounce even number of pixels" {
            val leds =
                spyk(ledStrip.createSection("bnc-1", 0, 9))

            val anim = leds.startAnimation(
                AnimationToRunParams()
                    .animation("Bounce")
                    .color(0xFF)
                    .runCount(1)
            )

            assertNotNull(anim)
            anim.joinBlocking()

            verify {
                leds.setTemporaryPixelColor(0, 0xFF)
                leds.setTemporaryPixelColor(1, 0xFF)
                leds.setTemporaryPixelColor(2, 0xFF)
                leds.setTemporaryPixelColor(3, 0xFF)
                leds.setTemporaryPixelColor(4, 0xFF)
                leds.setTemporaryPixelColor(5, 0xFF)
                leds.setTemporaryPixelColor(6, 0xFF)
                leds.setTemporaryPixelColor(7, 0xFF)
                leds.setTemporaryPixelColor(8, 0xFF)
                leds.setTemporaryPixelColor(9, 0xFF)
            }

            verifyOrder {
                leds.runSequential(match { it.direction == Direction.FORWARD }, match { it.name == "bnc-1:0:9" })
                leds.setTemporaryPixelColor(9, 0xFF)
                leds.fadePixel(9, 25, 50)
                leds.runSequential(match { it.direction == Direction.BACKWARD }, match { it.name == "bnc-1:0:8" })
                leds.setTemporaryPixelColor(0, 0xFF)
                leds.fadePixel(0, 25, 50)
                leds.runSequential(match { it.direction == Direction.FORWARD }, match { it.name == "bnc-1:1:8" })
                leds.setTemporaryPixelColor(8, 0xFF)
                leds.fadePixel(8, 25, 50)
                leds.runSequential(match { it.direction == Direction.BACKWARD }, match { it.name == "bnc-1:1:7" })
                leds.setTemporaryPixelColor(1, 0xFF)
                leds.fadePixel(1, 25, 50)
                leds.runSequential(match { it.direction == Direction.FORWARD }, match { it.name == "bnc-1:2:7" })
                leds.setTemporaryPixelColor(7, 0xFF)
                leds.fadePixel(7, 25, 50)
                leds.runSequential(match { it.direction == Direction.BACKWARD }, match { it.name == "bnc-1:2:6" })
                leds.setTemporaryPixelColor(2, 0xFF)
                leds.fadePixel(2, 25, 50)
                leds.runSequential(match { it.direction == Direction.FORWARD }, match { it.name == "bnc-1:3:6" })
                leds.setTemporaryPixelColor(6, 0xFF)
                leds.fadePixel(6, 25, 50)
                leds.runSequential(match { it.direction == Direction.BACKWARD }, match { it.name == "bnc-1:3:5" })
                leds.setTemporaryPixelColor(3, 0xFF)
                leds.fadePixel(3, 25, 50)
                leds.runSequential(match { it.direction == Direction.FORWARD }, match { it.name == "bnc-1:4:5" })
                leds.setTemporaryPixelColor(5, 0xFF)
                leds.fadePixel(5, 25, 50)
                leds.runSequential(match { it.direction == Direction.BACKWARD }, match { it.name == "bnc-1:4:4" })
                leds.setTemporaryPixelColor(4, 0xFF)
                leds.fadePixel(4, 25, 50)
            }
        }

        "Bounce odd number of pixels" {
            val leds =
                spyk(ledStrip.createSection("bnc-2", 0, 10))

            val anim = leds.startAnimation(
                AnimationToRunParams()
                    .animation("Bounce")
                    .color(0xFF)
                    .runCount(1)
            )

            assertNotNull(anim)
            anim.joinBlocking()

            verify {
                leds.setTemporaryPixelColor(0, 0xFF)
                leds.setTemporaryPixelColor(1, 0xFF)
                leds.setTemporaryPixelColor(2, 0xFF)
                leds.setTemporaryPixelColor(3, 0xFF)
                leds.setTemporaryPixelColor(4, 0xFF)
                leds.setTemporaryPixelColor(5, 0xFF)
                leds.setTemporaryPixelColor(6, 0xFF)
                leds.setTemporaryPixelColor(7, 0xFF)
                leds.setTemporaryPixelColor(8, 0xFF)
                leds.setTemporaryPixelColor(9, 0xFF)
                leds.setTemporaryPixelColor(10, 0xFF)
            }

            verifyOrder {
                leds.runSequential(match { it.direction == Direction.FORWARD }, match { it.name == "bnc-2:0:10" })
                leds.setTemporaryPixelColor(10, 0xFF)
                leds.fadePixel(10, 25, 50)
                leds.runSequential(match { it.direction == Direction.BACKWARD }, match { it.name == "bnc-2:0:9" })
                leds.setTemporaryPixelColor(0, 0xFF)
                leds.fadePixel(0, 25, 50)
                leds.runSequential(match { it.direction == Direction.FORWARD }, match { it.name == "bnc-2:1:9" })
                leds.setTemporaryPixelColor(9, 0xFF)
                leds.fadePixel(9, 25, 50)
                leds.runSequential(match { it.direction == Direction.BACKWARD }, match { it.name == "bnc-2:1:8" })
                leds.setTemporaryPixelColor(1, 0xFF)
                leds.fadePixel(1, 25, 50)
                leds.runSequential(match { it.direction == Direction.FORWARD }, match { it.name == "bnc-2:2:8" })
                leds.setTemporaryPixelColor(8, 0xFF)
                leds.fadePixel(8, 25, 50)
                leds.runSequential(match { it.direction == Direction.BACKWARD }, match { it.name == "bnc-2:2:7" })
                leds.setTemporaryPixelColor(2, 0xFF)
                leds.fadePixel(2, 25, 50)
                leds.runSequential(match { it.direction == Direction.FORWARD }, match { it.name == "bnc-2:3:7" })
                leds.setTemporaryPixelColor(7, 0xFF)
                leds.fadePixel(7, 25, 50)
                leds.runSequential(match { it.direction == Direction.BACKWARD }, match { it.name == "bnc-2:3:6" })
                leds.setTemporaryPixelColor(3, 0xFF)
                leds.fadePixel(3, 25, 50)
                leds.runSequential(match { it.direction == Direction.FORWARD }, match { it.name == "bnc-2:4:6" })
                leds.setTemporaryPixelColor(6, 0xFF)
                leds.fadePixel(6, 25, 50)
                leds.runSequential(match { it.direction == Direction.BACKWARD }, match { it.name == "bnc-2:4:5" })
                leds.setTemporaryPixelColor(4, 0xFF)
                leds.fadePixel(4, 25, 50)
                leds.setTemporaryPixelColor(5, 0xFF)
                leds.fadePixel(5, 25, 50)
            }
        }

        @Test
        fun `btc even number of pixels`() {
            val leds =
                spyk(ledStrip.createSection("btc-1", 0, 9))

            val anim = leds.startAnimation(
                AnimationToRunParams()
                    .animation("Bounce to Color")
                    .color(0xFF)
                    .runCount(1)
            )

            assertNotNull(anim)
            anim.joinBlocking()

            leds.assertAllPixels(0xFF)

            verify {
                leds.setProlongedPixelColor(0, 0xFF)
                leds.setProlongedPixelColor(1, 0xFF)
                leds.setProlongedPixelColor(2, 0xFF)
                leds.setProlongedPixelColor(3, 0xFF)
                leds.setProlongedPixelColor(4, 0xFF)
                leds.setProlongedPixelColor(5, 0xFF)
                leds.setProlongedPixelColor(6, 0xFF)
                leds.setProlongedPixelColor(7, 0xFF)
                leds.setProlongedPixelColor(8, 0xFF)
                leds.setProlongedPixelColor(9, 0xFF)
            }

            verifyOrder {
                leds.runSequential(match { it.direction == Direction.FORWARD }, match { it.name == "btc-1:0:9" })
                leds.setProlongedPixelColor(9, 0xFF)
                leds.runSequential(match { it.direction == Direction.BACKWARD }, match { it.name == "btc-1:0:8" })
                leds.setProlongedPixelColor(0, 0xFF)
                leds.runSequential(match { it.direction == Direction.FORWARD }, match { it.name == "btc-1:1:8" })
                leds.setProlongedPixelColor(8, 0xFF)
                leds.runSequential(match { it.direction == Direction.BACKWARD }, match { it.name == "btc-1:1:7" })
                leds.setProlongedPixelColor(1, 0xFF)
                leds.runSequential(match { it.direction == Direction.FORWARD }, match { it.name == "btc-1:2:7" })
                leds.setProlongedPixelColor(7, 0xFF)
                leds.runSequential(match { it.direction == Direction.BACKWARD }, match { it.name == "btc-1:2:6" })
                leds.setProlongedPixelColor(2, 0xFF)
                leds.runSequential(match { it.direction == Direction.FORWARD }, match { it.name == "btc-1:3:6" })
                leds.setProlongedPixelColor(6, 0xFF)
                leds.runSequential(match { it.direction == Direction.BACKWARD }, match { it.name == "btc-1:3:5" })
                leds.setProlongedPixelColor(3, 0xFF)
                leds.runSequential(match { it.direction == Direction.FORWARD }, match { it.name == "btc-1:4:5" })
                leds.setProlongedPixelColor(5, 0xFF)
                leds.runSequential(match { it.direction == Direction.BACKWARD }, match { it.name == "btc-1:4:4" })
                leds.setProlongedPixelColor(4, 0xFF)
            }
        }

        @Test
        fun `btc odd number of pixels`() {
            val leds =
                spyk(ledStrip.createSection("btc-2", 0, 10))

            val anim = leds.startAnimation(
                AnimationToRunParams()
                    .animation("Bounce to Color")
                    .color(0xFF)
                    .runCount(1)
            )

            assertNotNull(anim)
            anim.joinBlocking()

            leds.assertAllPixels(0xFF)

            verify {
                leds.setProlongedPixelColor(0, 0xFF)
                leds.setProlongedPixelColor(1, 0xFF)
                leds.setProlongedPixelColor(2, 0xFF)
                leds.setProlongedPixelColor(3, 0xFF)
                leds.setProlongedPixelColor(4, 0xFF)
                leds.setProlongedPixelColor(5, 0xFF)
                leds.setProlongedPixelColor(6, 0xFF)
                leds.setProlongedPixelColor(7, 0xFF)
                leds.setProlongedPixelColor(8, 0xFF)
                leds.setProlongedPixelColor(9, 0xFF)
                leds.setProlongedPixelColor(10, 0xFF)
            }

            verifyOrder {
                leds.runSequential(match { it.direction == Direction.FORWARD }, match { it.name == "btc-2:0:10" })
                leds.setProlongedPixelColor(10, 0xFF)
                leds.runSequential(match { it.direction == Direction.BACKWARD }, match { it.name == "btc-2:0:9" })
                leds.setProlongedPixelColor(0, 0xFF)
                leds.runSequential(match { it.direction == Direction.FORWARD }, match { it.name == "btc-2:1:9" })
                leds.setProlongedPixelColor(9, 0xFF)
                leds.runSequential(match { it.direction == Direction.BACKWARD }, match { it.name == "btc-2:1:8" })
                leds.setProlongedPixelColor(1, 0xFF)
                leds.runSequential(match { it.direction == Direction.FORWARD }, match { it.name == "btc-2:2:8" })
                leds.setProlongedPixelColor(8, 0xFF)
                leds.runSequential(match { it.direction == Direction.BACKWARD }, match { it.name == "btc-2:2:7" })
                leds.setProlongedPixelColor(2, 0xFF)
                leds.runSequential(match { it.direction == Direction.FORWARD }, match { it.name == "btc-2:3:7" })
                leds.setProlongedPixelColor(7, 0xFF)
                leds.runSequential(match { it.direction == Direction.BACKWARD }, match { it.name == "btc-2:3:6" })
                leds.setProlongedPixelColor(3, 0xFF)
                leds.runSequential(match { it.direction == Direction.FORWARD }, match { it.name == "btc-2:4:6" })
                leds.setProlongedPixelColor(6, 0xFF)
                leds.runSequential(match { it.direction == Direction.BACKWARD }, match { it.name == "btc-2:4:5" })
                leds.setProlongedPixelColor(4, 0xFF)
                leds.setProlongedPixelColor(5, 0xFF)
            }
        }


        @Test
        fun testBubbleSort() {
            val leds =
                spyk(ledStrip.createSection("bst", 0, 9))

            val data = spyk(AnimationToRunParams()
                                .animation("Bubble Sort")
                                .color(ColorContainer(0xFF, 0xFFFF)))

            mockkStatic("animatedledstrip.colors.CCUtilsKt")
            every { data.colors[any()].shuffledWithIndices() } returns
                    listOf(
                        Pair(1, 0x33FF),
                        Pair(9, 0x33FF),
                        Pair(4, 0xCCFF),
                        Pair(2, 0x66FF),
                        Pair(7, 0x99FF),
                        Pair(0, 0x00FF),
                        Pair(3, 0x99FF),
                        Pair(6, 0xCCFF),
                        Pair(8, 0x66FF),
                        Pair(5, 0xFFFF),
                    )

            val anim = leds.startAnimation(data)

            assertNotNull(anim)
            anim.joinBlocking()

            verifyOrder {
                // |19|42703685
                // 1|94|2703685
                leds.setProlongedPixelColor(1, 0xCCFF)
                leds.setProlongedPixelColor(2, 0x33FF)
                // 14|92|703685
                leds.setProlongedPixelColor(2, 0x66FF)
                leds.setProlongedPixelColor(3, 0x33FF)
                // 142|97|03685
                leds.setProlongedPixelColor(3, 0x99FF)
                leds.setProlongedPixelColor(4, 0x33FF)
                // 1427|90|3685
                leds.setProlongedPixelColor(4, 0x00FF)
                leds.setProlongedPixelColor(5, 0x33FF)
                // 14270|93|685
                leds.setProlongedPixelColor(5, 0x99FF)
                leds.setProlongedPixelColor(6, 0x33FF)
                // 142703|96|85
                leds.setProlongedPixelColor(6, 0xCCFF)
                leds.setProlongedPixelColor(7, 0x33FF)
                // 1427036|98|5
                leds.setProlongedPixelColor(7, 0x66FF)
                leds.setProlongedPixelColor(8, 0x33FF)
                // 14270368|95|
                leds.setProlongedPixelColor(8, 0xFFFF)
                leds.setProlongedPixelColor(9, 0x33FF)
                // =1427036859=
                // |14|27036859
                // 1|42|7036859
                leds.setProlongedPixelColor(1, 0x66FF)
                leds.setProlongedPixelColor(2, 0xCCFF)
                // 12|47|036859
                // 124|70|36859
                leds.setProlongedPixelColor(3, 0x00FF)
                leds.setProlongedPixelColor(4, 0x99FF)
                // 1240|73|6859
                leds.setProlongedPixelColor(4, 0x99FF)
                leds.setProlongedPixelColor(5, 0x99FF)
                // 12403|76|859
                leds.setProlongedPixelColor(5, 0xCCFF)
                leds.setProlongedPixelColor(6, 0x99FF)
                // 124036|78|59
                // 1240367|85|9
                leds.setProlongedPixelColor(7, 0xFFFF)
                leds.setProlongedPixelColor(8, 0x66FF)
                // 12403675|89|
                // =1240367589=
                // |12|40367589
                // 1|24|0367589
                // 12|40|367589
                leds.setProlongedPixelColor(2, 0x00FF)
                leds.setProlongedPixelColor(3, 0xCCFF)
                // 120|43|67589
                leds.setProlongedPixelColor(3, 0x99FF)
                leds.setProlongedPixelColor(4, 0xCCFF)
                // 1203|46|7589
                // 12034|67|589
                // 120346|75|89
                leds.setProlongedPixelColor(6, 0xFFFF)
                leds.setProlongedPixelColor(7, 0x99FF)
                // 1203465|78|9
                // 12034657|89|
                // =1203465789=
                // |12|03465789
                // 1|20|3465789
                leds.setProlongedPixelColor(1, 0x00FF)
                leds.setProlongedPixelColor(2, 0x66FF)
                // 10|23|465789
                // 102|34|65789
                // 1023|46|5789
                // 10234|65|789
                leds.setProlongedPixelColor(5, 0xFFFF)
                leds.setProlongedPixelColor(6, 0xCCFF)
                // 102345|67|89
                // 1023456|78|9
                // 10234567|89|
                // =1203456789=
                // |10|23456789
                leds.setProlongedPixelColor(0, 0x00FF)
                leds.setProlongedPixelColor(1, 0x33FF)
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

            leds.pixelProlongedColorList shouldBe
                    listOf(
                        0x00FF,
                        0x33FF,
                        0x66FF,
                        0x99FF,
                        0xCCFF,
                        0xFFFF,
                        0xCCFF,
                        0x99FF,
                        0x66FF,
                        0x33FF,
                    )

            leds.pixelTemporaryColorList shouldBe
                    listOf(
                        0x00FF,
                        0x33FF,
                        0x66FF,
                        0x99FF,
                        0xCCFF,
                        0xFFFF,
                        0xCCFF,
                        0x99FF,
                        0x66FF,
                        0x33FF,
                    )
        }

        @Test
        fun testCatToy() {
            val leds =
                spyk(ledStrip.createSection("cat", 0, 9))

            every { leds.indices } returns
                    listOf(8) andThen
                    listOf(9) andThen
                    listOf(3) andThen
                    listOf(5) andThen
                    listOf(1) andThen
                    listOf(3) andThen
                    listOf(2) andThen
                    listOf(5) andThen
                    listOf(7) andThen
                    listOf(0) andThen
                    listOf(7) andThen
                    listOf(6) andThen
                    listOf(6) andThen
                    listOf(8) andThen
                    listOf(2)

            val anim = leds.startAnimation(
                AnimationToRunParams()
                    .animation("Cat Toy")
                    .color(0xFF)
                    .delay(1)
                    .runCount(15)
            )

            assertNotNull(anim)
            anim.joinBlocking()

            verifyOrder {
                leds.runSequential(match { it.direction == Direction.FORWARD && it.animation == "Pixel Run" },
                                   match { it.name == "cat:0:8" })
                leds.setTemporaryPixelColor(8, 0xFF)
                leds.runSequential(match { it.direction == Direction.FORWARD && it.animation == "Pixel Run" },
                                   match { it.name == "cat:8:9" })
                leds.setTemporaryPixelColor(9, 0xFF)
                leds.runSequential(match { it.direction == Direction.BACKWARD && it.animation == "Pixel Run" },
                                   match { it.name == "cat:3:9" })
                leds.setTemporaryPixelColor(3, 0xFF)
                leds.runSequential(match { it.direction == Direction.FORWARD && it.animation == "Pixel Run" },
                                   match { it.name == "cat:3:5" })
                leds.setTemporaryPixelColor(5, 0xFF)
                leds.runSequential(match { it.direction == Direction.BACKWARD && it.animation == "Pixel Run" },
                                   match { it.name == "cat:1:5" })
                leds.setTemporaryPixelColor(1, 0xFF)
                leds.runSequential(match { it.direction == Direction.FORWARD && it.animation == "Pixel Run" },
                                   match { it.name == "cat:1:3" })
                leds.setTemporaryPixelColor(3, 0xFF)
                leds.runSequential(match { it.direction == Direction.BACKWARD && it.animation == "Pixel Run" },
                                   match { it.name == "cat:2:3" })
                leds.setTemporaryPixelColor(2, 0xFF)
                leds.runSequential(match { it.direction == Direction.FORWARD && it.animation == "Pixel Run" },
                                   match { it.name == "cat:2:5" })
                leds.setTemporaryPixelColor(5, 0xFF)
                leds.runSequential(match { it.direction == Direction.FORWARD && it.animation == "Pixel Run" },
                                   match { it.name == "cat:5:7" })
                leds.setTemporaryPixelColor(7, 0xFF)
                leds.runSequential(match { it.direction == Direction.BACKWARD && it.animation == "Pixel Run" },
                                   match { it.name == "cat:0:7" })
                leds.setTemporaryPixelColor(0, 0xFF)
                leds.runSequential(match { it.direction == Direction.FORWARD && it.animation == "Pixel Run" },
                                   match { it.name == "cat:0:7" })
                leds.setTemporaryPixelColor(7, 0xFF)
                leds.runSequential(match { it.direction == Direction.BACKWARD && it.animation == "Pixel Run" },
                                   match { it.name == "cat:6:7" })
                leds.setTemporaryPixelColor(6, 0xFF)
                leds.setTemporaryPixelColor(6, 0xFF)
                leds.runSequential(match { it.direction == Direction.FORWARD && it.animation == "Pixel Run" },
                                   match { it.name == "cat:6:8" })
                leds.setTemporaryPixelColor(8, 0xFF)
                leds.runSequential(match { it.direction == Direction.BACKWARD && it.animation == "Pixel Run" },
                                   match { it.name == "cat:2:8" })
                leds.setTemporaryPixelColor(2, 0xFF)
            }
        }

        @Test
        fun testCatToyToColor() {
            val leds =
                spyk(ledStrip.createSection("ctc", 0, 9))

            every { leds.shuffledIndices } returns listOf(1, 9, 4, 2, 7, 0, 3, 6, 8, 5)

            val anim = leds.startAnimation(
                AnimationToRunParams()
                    .animation("Cat Toy to Color")
                    .color(ColorContainer(0xFF, 0xFFFF))
                    .delay(1)
            )

            assertNotNull(anim)
            anim.joinBlocking()

            verifyOrder {
                // set:
                leds.setTemporaryPixelColor(0, 0x0000FF)
                leds.revertPixel(0)
                leds.setProlongedPixelColor(1, 0x0034FF)
                // set: 1
                leds.setTemporaryPixelColor(1, 0xFFCC00)
                leds.revertPixel(1)
                leds.setTemporaryPixelColor(2, 0x0067FF)
                leds.revertPixel(2)
                leds.setTemporaryPixelColor(3, 0x009AFF)
                leds.revertPixel(3)
                leds.setTemporaryPixelColor(4, 0x00CDFF)
                leds.revertPixel(4)
                leds.setTemporaryPixelColor(5, 0x00FFFF)
                leds.revertPixel(5)
                leds.setTemporaryPixelColor(6, 0x00CCFF)
                leds.revertPixel(6)
                leds.setTemporaryPixelColor(7, 0x0099FF)
                leds.revertPixel(7)
                leds.setTemporaryPixelColor(8, 0x0066FF)
                leds.revertPixel(8)
                leds.setProlongedPixelColor(9, 0x0033FF)
                // set: 1 9
                leds.setTemporaryPixelColor(9, 0xFFCD00)
                leds.revertPixel(9)
                leds.setTemporaryPixelColor(8, 0x0066FF)
                leds.revertPixel(8)
                leds.setTemporaryPixelColor(7, 0x0099FF)
                leds.revertPixel(7)
                leds.setTemporaryPixelColor(6, 0x00CCFF)
                leds.revertPixel(6)
                leds.setTemporaryPixelColor(5, 0x00FFFF)
                leds.revertPixel(5)
                leds.setProlongedPixelColor(4, 0x00CDFF)
                // set: 1 4 9
                leds.setTemporaryPixelColor(4, 0xFF3300)
                leds.revertPixel(4)
                leds.setTemporaryPixelColor(3, 0x009AFF)
                leds.revertPixel(3)
                leds.setProlongedPixelColor(2, 0x0067FF)
                // set: 1 2 4 9
                leds.setTemporaryPixelColor(2, 0xFF9900)
                leds.revertPixel(2)
                leds.setTemporaryPixelColor(3, 0x009AFF)
                leds.revertPixel(3)
                leds.setTemporaryPixelColor(4, 0xFF3300)
                leds.revertPixel(4)
                leds.setTemporaryPixelColor(5, 0x00FFFF)
                leds.revertPixel(5)
                leds.setTemporaryPixelColor(6, 0x00CCFF)
                leds.revertPixel(6)
                leds.setProlongedPixelColor(7, 0x0099FF)
                // set: 1 2 4 7 9
                leds.setTemporaryPixelColor(7, 0xFF6700)
                leds.revertPixel(7)
                leds.setTemporaryPixelColor(6, 0x00CCFF)
                leds.revertPixel(6)
                leds.setTemporaryPixelColor(5, 0x00FFFF)
                leds.revertPixel(5)
                leds.setTemporaryPixelColor(4, 0xFF3300)
                leds.revertPixel(4)
                leds.setTemporaryPixelColor(3, 0x009AFF)
                leds.revertPixel(3)
                leds.setTemporaryPixelColor(2, 0xFF9900)
                leds.revertPixel(2)
                leds.setTemporaryPixelColor(1, 0xFFCC00)
                leds.revertPixel(1)
                leds.setProlongedPixelColor(0, 0x0000FF)
                // set: 0 1 2 4 7 9
                leds.setTemporaryPixelColor(0, 0xFFFF00)
                leds.revertPixel(0)
                leds.setTemporaryPixelColor(1, 0xFFCC00)
                leds.revertPixel(1)
                leds.setTemporaryPixelColor(2, 0xFF9900)
                leds.revertPixel(2)
                leds.setProlongedPixelColor(3, 0x009AFF)
                // set: 0 1 2 3 4 7 9
                leds.setTemporaryPixelColor(3, 0xFF6600)
                leds.revertPixel(3)
                leds.setTemporaryPixelColor(4, 0xFF3300)
                leds.revertPixel(4)
                leds.setTemporaryPixelColor(5, 0x00FFFF)
                leds.revertPixel(5)
                leds.setProlongedPixelColor(6, 0x00CCFF)
                // set: 0 1 2 3 4 6 7 9
                leds.setTemporaryPixelColor(6, 0xFF3400)
                leds.revertPixel(6)
                leds.setTemporaryPixelColor(7, 0xFF6700)
                leds.revertPixel(7)
                leds.setProlongedPixelColor(8, 0x0066FF)
                // set: 0 1 2 3 4 6 7 8 9
                leds.setTemporaryPixelColor(8, 0xFF9A00)
                leds.revertPixel(8)
                leds.setTemporaryPixelColor(7, 0xFF6700)
                leds.revertPixel(7)
                leds.setTemporaryPixelColor(6, 0xFF3400)
                leds.revertPixel(6)
                leds.setProlongedPixelColor(5, 0x00FFFF)
                // set: 0 1 2 3 4 5 6 7 8 9
            }

            leds.pixelProlongedColorList shouldBe
                    listOf(
                        //         Inverses:
                        0x00FF, // 0xFFFF00
                        0x34FF, // 0xFFCC00
                        0x67FF, // 0xFF9900
                        0x9AFF, // 0xFF6600
                        0xCDFF, // 0xFF3300
                        0xFFFF, // 0xFF0000
                        0xCCFF, // 0xFF3400
                        0x99FF, // 0xFF6700
                        0x66FF, // 0xFF9A00
                        0x33FF, // 0xFFCD00
                    )

            leds.pixelTemporaryColorList shouldBe
                    listOf(
                        0x00FF,
                        0x34FF,
                        0x67FF,
                        0x9AFF,
                        0xCDFF,
                        0xFFFF,
                        0xCCFF,
                        0x99FF,
                        0x66FF,
                        0x33FF,
                    )
        }

        @Test
        fun testFadeToColor() {
            val leds =
                spyk(ledStrip.createSection("ftc", 0, 9))

            val anim = leds.startAnimation(
                AnimationToRunParams()
                    .animation("Fade to Color")
                    .addColor(ColorContainer(0xFF, 0xFFFF))
            )

            assertNotNull(anim)
            anim.joinBlocking()

            verify(exactly = 1) {
                leds.fadePixel(0)
                leds.fadePixel(1)
                leds.fadePixel(2)
                leds.fadePixel(3)
                leds.fadePixel(4)
                leds.fadePixel(5)
                leds.fadePixel(6)
                leds.fadePixel(7)
                leds.fadePixel(8)
                leds.fadePixel(9)
            }

            leds.pixelProlongedColorList shouldBe
                    listOf(
                        0x00FF,
                        0x34FF,
                        0x67FF,
                        0x9AFF,
                        0xCDFF,
                        0xFFFF,
                        0xCCFF,
                        0x99FF,
                        0x66FF,
                        0x33FF,
                    )

            leds.pixelTemporaryColorList shouldBe
                    listOf(
                        0x00FF,
                        0x34FF,
                        0x67FF,
                        0x9AFF,
                        0xCDFF,
                        0xFFFF,
                        0xCCFF,
                        0x99FF,
                        0x66FF,
                        0x33FF,
                    )
        }

        @Test
        fun testFireworks() {
//        println(ColorContainer(0xFFFFFF, 0x123456).prepare(10))

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
            Unit
        }

//    @Test
//    fun testMergeSort() = runBlocking {
//        val testLEDs = EmulatedAnimatedLEDStrip(25).wholeStrip
//
//        val anim1 = testLEDs.startAnimation(
//            AnimationData()
//                .animation("Merge Sort Parallel")
//                .color(ColorContainer(0xFF, 0xFF00))
//        )
//
//        val anim2 = testLEDs.startAnimation(
//            AnimationData()
//                .animation("Merge Sort Sequential")
//                .color(ColorContainer(0xFF, 0xFF00))
//        )
//
//        assertNotNull(anim1)
//        assertNotNull(anim2)
//        delay(100)
//        anim1.endAnimation()
//        anim2.endAnimation()
//        anim1.join()
//        anim2.join()
//    }
//
//    @Test
//    fun testMeteor() = runBlocking {
//        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip
//
//        val anim1 = testLEDs.startAnimation(
//            AnimationData()
//                .animation("Meteor")
//                .color(0xFF)
//                .direction(Direction.FORWARD)
//        )
//
//        val anim2 = testLEDs.startAnimation(
//            AnimationData()
//                .animation("Meteor")
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
