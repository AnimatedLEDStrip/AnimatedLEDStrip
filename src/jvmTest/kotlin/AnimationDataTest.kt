/*
 *  Copyright (c) 2018-2020 AnimatedLEDStrip
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

package animatedledstrip.test

import animatedledstrip.animationutils.*
import animatedledstrip.colors.ColorContainer
import animatedledstrip.utils.toColorContainer
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

class AnimationDataTest : StringSpec(
    {
        "animation" {
            val testAnimation = AnimationData()

            testAnimation.animation shouldBe "Color"

            testAnimation.animation = "Bounce"
            testAnimation.animation shouldBe "Bounce"

            testAnimation.animation("Splat")
            testAnimation.animation shouldBe "Splat"
        }

        "colors" {
            val testAnimation = AnimationData()

            testAnimation.color(0xFF)
            testAnimation.colors[0] shouldBe ColorContainer(0xFF)

            testAnimation.color(ColorContainer(0xFFFF))
            testAnimation.colors[0] shouldBe ColorContainer(0xFFFF)

            testAnimation.color(0xFFFFFFL)
            testAnimation.colors[0] shouldBe ColorContainer(0xFFFFFF)

            testAnimation.color("0xFF00")
            testAnimation.colors[0] shouldBe ColorContainer(0xFF00)

            testAnimation.color(0xFF, index = 3)
            testAnimation.colors[3] shouldBe ColorContainer(0xFF)
            testAnimation.colors[2] shouldBe ColorContainer(0x0)

            assertFailsWith<IllegalArgumentException> { testAnimation.color(0.0) }

            testAnimation.color0(0xFFFF)
            testAnimation.colors[0] shouldBe ColorContainer(0xFFFF)

            testAnimation.color1(0xFFFF)
            testAnimation.colors[1] shouldBe ColorContainer(0xFFFF)

            testAnimation.color2(0xFFFF)
            testAnimation.colors[2] shouldBe ColorContainer(0xFFFF)

            testAnimation.color3(0xFFFF)
            testAnimation.colors[3] shouldBe ColorContainer(0xFFFF)

            testAnimation.color4(0xFFFF)
            testAnimation.colors[4] shouldBe ColorContainer(0xFFFF)

            testAnimation.addColor(0xFFFFFF)
            testAnimation.colors[5] shouldBe ColorContainer(0xFFFFFF)

            testAnimation.addColors(ColorContainer(0xFFFF), ColorContainer(0xFF00FF))
            testAnimation.colors[6] shouldBe ColorContainer(0xFFFF)
            testAnimation.colors[7] shouldBe ColorContainer(0xFF00FF)

            testAnimation.addColors(0xFF88L, 0xFFL)
            testAnimation.colors[8] shouldBe ColorContainer(0xFF88)
            testAnimation.colors[9] shouldBe ColorContainer(0xFF)

            testAnimation.addColors(0xFF, 0xFF00)
            testAnimation.colors[10] shouldBe ColorContainer(0xFF)
            testAnimation.colors[11] shouldBe ColorContainer(0xFF00)

            testAnimation.addColors("0xFF", "0x88")
            testAnimation.colors[12] shouldBe ColorContainer(0xFF)
            testAnimation.colors[13] shouldBe ColorContainer(0x88)

            testAnimation.addColors(listOf(0xAA,
                                           "0xFFFF",
                                           0xFF00FFL,
                                           ColorContainer(0x8888)))
            testAnimation.colors[14] shouldBe ColorContainer(0xAA)
            testAnimation.colors[15] shouldBe ColorContainer(0xFFFF)
            testAnimation.colors[16] shouldBe ColorContainer(0xFF00FF)
            testAnimation.colors[17] shouldBe ColorContainer(0x8888)

            assertFailsWith<IllegalArgumentException> { testAnimation.addColors(listOf<Int>()) }
            assertFailsWith<IllegalArgumentException> {
                testAnimation.addColors(listOf<Int?>(null))
            }
            assertFailsWith<IllegalArgumentException> { testAnimation.addColors(listOf(0.0)) }
            assertFailsWith<NumberFormatException> { testAnimation.addColors(listOf("0XG")) }

        }

        "center" {
            val testAnimation = AnimationData()

            testAnimation.center = 10
            testAnimation.center shouldBe 10

            testAnimation.center(15)
            testAnimation.center shouldBe 15
        }

        "delay" {
            val testAnimation = AnimationData()

            DEFAULT_DELAY shouldBe 50L

            testAnimation.delay = 15
            testAnimation.delay shouldBe 15L

            testAnimation.delay(30)
            testAnimation.delay shouldBe 30L

            testAnimation.delay(40L)
            testAnimation.delay shouldBe 40L

            findAnimation("Bounce").info.delayDefault shouldBe 10L
            testAnimation.animation = "Bounce"

            testAnimation.delay = -1
            testAnimation.delay shouldBe 10L

            testAnimation.delay = -2
            testAnimation.delay shouldBe -2L

            testAnimation.delay = 0
            testAnimation.delay shouldBe 10L

            testAnimation.delay = 1
            testAnimation.delay shouldBe 1L

        }

        "delayMod" {
            val testAnimation = AnimationData()

            testAnimation.delayMod shouldBe 1.0

            testAnimation.delayMod = 2.5
            testAnimation.delayMod shouldBe 2.5

            testAnimation.delayMod(0.5)
            testAnimation.delayMod shouldBe 0.5

            testAnimation.animation shouldBe "Color"
            testAnimation.delay shouldBe 25L

        }

        "direction" {
            val testAnimation = AnimationData()

            testAnimation.direction shouldBe Direction.FORWARD

            testAnimation.direction = Direction.FORWARD
            testAnimation.direction shouldBe Direction.FORWARD

            testAnimation.direction = Direction.BACKWARD
            testAnimation.direction shouldBe Direction.BACKWARD

            testAnimation.direction(Direction.FORWARD)
            testAnimation.direction shouldBe Direction.FORWARD

            testAnimation.direction(Direction.BACKWARD)
            testAnimation.direction shouldBe Direction.BACKWARD

            testAnimation.direction('F')
            testAnimation.direction shouldBe Direction.FORWARD

            testAnimation.direction('B')
            testAnimation.direction shouldBe Direction.BACKWARD

            testAnimation.direction('f')
            testAnimation.direction shouldBe Direction.FORWARD

            testAnimation.direction('b')
            testAnimation.direction shouldBe Direction.BACKWARD

            assertFails {
                testAnimation.direction('G')
            }

        }

        "distance" {
            val testAnimation = AnimationData()

            testAnimation.distance = 50
            testAnimation.distance shouldBe 50

            testAnimation.distance(35)
            testAnimation.distance shouldBe 35

        }

        "id" {
            val testAnimation = AnimationData()

            testAnimation.id shouldBe ""

            testAnimation.id = "TEST"
            testAnimation.id shouldBe "TEST"

            testAnimation.id("TEST2")
            testAnimation.id shouldBe "TEST2"

        }

        "section" {
            val testAnimation = AnimationData()

            testAnimation.section shouldBe ""

            testAnimation.section = "test"
            testAnimation.section shouldBe "test"

            testAnimation.section("section")
            testAnimation.section shouldBe "section"

        }

        "spacing" {
            val testAnimation = AnimationData()

            DEFAULT_SPACING shouldBe 3
            testAnimation.spacing shouldBe DEFAULT_SPACING

            testAnimation.spacing = 5
            testAnimation.spacing shouldBe 5

            testAnimation.spacing(10)
            testAnimation.spacing shouldBe 10

            findAnimation("MultiPixelRun").info.spacingDefault shouldBe 3
            testAnimation.animation = "MultiPixelRun"

            testAnimation.spacing = -1
            testAnimation.spacing shouldBe 3

            testAnimation.spacing = -2
            testAnimation.spacing shouldBe -2

            testAnimation.spacing = 0
            testAnimation.spacing shouldBe 3

            testAnimation.spacing = 1
            testAnimation.spacing shouldBe 1

        }

        "speed" {
            val testAnimation = AnimationData()

            testAnimation.speed(AnimationSpeed.FAST)
            testAnimation.delayMod shouldBe 2.0

            testAnimation.speed(AnimationSpeed.SLOW)
            testAnimation.delayMod shouldBe 0.5

            testAnimation.speed(AnimationSpeed.DEFAULT)
            testAnimation.delayMod shouldBe 1.0

        }


        "equality" {
            val testAnimation1 = AnimationData()
                .animation("Stack")
                .color0(0xFFFF)
                .color1(0xFFFFFF)
                .delay(10)
                .direction(Direction.BACKWARD)
                .spacing(4)

            val testAnimation2 = AnimationData().apply {
                animation = "sta ck"
                addColor(0xFFFF)
                addColor(0xFFFFFF)
                delay = 10
                direction = Direction.BACKWARD
                spacing = 4
            }

            testAnimation1 shouldBe testAnimation1
            testAnimation1 shouldBe testAnimation2
            testAnimation1 shouldNotBe 0xFF

        }

        "copy" {
            val testAnimation1 = AnimationData()
                .animation("Stack")
                .color0(0xFFFF)
                .color1(0xFFFFFF)
                .delay(10)
                .direction(Direction.BACKWARD)
                .spacing(4)

            val testAnimation2 = testAnimation1.copy()
            assertFalse { testAnimation1 === testAnimation2 }
            testAnimation1 shouldBe testAnimation2

            val testAnimation3 = testAnimation1.copy(
                animation = "Color",
                colors = listOf(0xFF.toColorContainer()),
                center = 30,
                delay = 10,
                delayMod = 2.0,
                direction = Direction.FORWARD,
                distance = 50,
                id = "test",
                section = "section",
                spacing = 4,
            )

            assertFalse { testAnimation1 === testAnimation3 }
            assertFalse { testAnimation1 == testAnimation3 }

        }

//        "string" {
//            assertTrue {
//                AnimationData().toString() ==
//                        "AnimationData(animation=Color, colors=[], center=-1, delay=50, " +
//                        "delayMod=1.0, direction=FORWARD, distance=-1, id=, runCount=1, section=, spacing=3)"
//            }
//
//            assertTrue {
//                AnimationData(
//                    animation = "Bounce",
//                    colors = listOf(0xFF.toColorContainer()),
//                    center = 30,
//                    delay = 10,
//                    delayMod = 2.0,
//                    direction = Direction.BACKWARD,
//                    distance = 50,
//                    id = "test",
//                    section = "section",
//                    spacing = 4,
//                ).toString() ==
//                        "AnimationData(animation=Bounce, colors=[ff], center=30, delay=10, delayMod=2.0, " +
//                        "direction=Direction.BACKWARD, distance=50, id=test, runCount=-1, section=section, spacing=4)"
//            }
//
//        }

//        "serializable" {
//            val testAnimation = AnimationData().animation("Stack")
//                .color(ColorContainer(0xFF, 0xFFFF).prepare(5), index = 0)
//                .color(0xFF, index = 1)
//                .color(0xFF, index = 2)
//                .color(0xFF, index = 3)
//                .color(0xFF, index = 4)
//                .delay(50)
//                .direction(Direction.FORWARD)
//                .id("TEST")
//                .spacing(5)
//            val fileOut = FileOutputStream("animation.ser")
//            val out = ObjectOutputStream(fileOut)
//            out.writeObject(testAnimation)
//            out.close()
//            fileOut.close()
//            val fileIn = FileInputStream("animation.ser")
//            val input = ObjectInputStream(fileIn)
//            val testAnimation2: AnimationData = input.readObject() as AnimationData
//            input.close()
//            fileIn.close()
//            testAnimation == testAnimation2
//            Files.delete(Paths.get("animation.ser"))
//
//        }

    })
