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

package animatedledstrip.test.animationutils

import animatedledstrip.animationutils.*
import animatedledstrip.colors.ColorContainer
import animatedledstrip.utils.decodeJson
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlin.test.assertFails
import kotlin.test.assertFailsWith

class AnimationDataTest : StringSpec(
    {
        "set animation" {
            val testAnimation = AnimationToRunParams()

            testAnimation.animation shouldBe "Color"

            testAnimation.animation = "Bounce"
            testAnimation.animation shouldBe "Bounce"

            testAnimation.animation("Splat")
            testAnimation.animation shouldBe "Splat"
        }

        "set colors" {
            val testAnimation = AnimationToRunParams()

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

        "set center" {
            val testAnimation = AnimationToRunParams()

            testAnimation.center = 10
            testAnimation.center shouldBe 10

            testAnimation.center(15)
            testAnimation.center shouldBe 15
        }

        "set delay" {
            val testAnimation = AnimationToRunParams()

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

        "set delayMod" {
            val testAnimation = AnimationToRunParams()

            testAnimation.delayMod shouldBe 1.0

            testAnimation.delayMod = 2.5
            testAnimation.delayMod shouldBe 2.5

            testAnimation.delayMod(0.5)
            testAnimation.delayMod shouldBe 0.5

            testAnimation.animation shouldBe "Color"
            testAnimation.delay shouldBe 25L

        }

        "set direction" {
            val testAnimation = AnimationToRunParams()

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

        "set distance" {
            val testAnimation = AnimationToRunParams()

            testAnimation.distance = 50
            testAnimation.distance shouldBe 50

            testAnimation.distance(35)
            testAnimation.distance shouldBe 35

        }

        "set id" {
            val testAnimation = AnimationToRunParams()

            testAnimation.id shouldBe ""

            testAnimation.id = "TEST"
            testAnimation.id shouldBe "TEST"

            testAnimation.id("TEST2")
            testAnimation.id shouldBe "TEST2"

        }

        "set section" {
            val testAnimation = AnimationToRunParams()

            testAnimation.section shouldBe ""

            testAnimation.section = "test"
            testAnimation.section shouldBe "test"

            testAnimation.section("section")
            testAnimation.section shouldBe "section"

        }

        "set spacing" {
            val testAnimation = AnimationToRunParams()

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

        "set speed" {
            val testAnimation = AnimationToRunParams()

            testAnimation.speed(AnimationSpeed.FAST)
            testAnimation.delayMod shouldBe 2.0

            testAnimation.speed(AnimationSpeed.SLOW)
            testAnimation.delayMod shouldBe 0.5

            testAnimation.speed(AnimationSpeed.DEFAULT)
            testAnimation.delayMod shouldBe 1.0

        }

        "decode JSON" {
            val json =
                """{"type":"animatedledstrip.animationutils.AnimationData","animation":"Meteor","center":50,"colors":[{"type":"animatedledstrip.colors.ColorContainer","colors":[255,65280]},{"type":"animatedledstrip.colors.ColorContainer","colors":[16711680]}],"delay":10,"delayMod":1.5,"direction":"BACKWARD","distance":45,"id":"TEST","runCount":2,"section":"SECT","spacing":5};;;"""

            val correctData = AnimationToRunParams(animation = "Meteor",
                                                   colors = mutableListOf(ColorContainer(0xFF, 0xFF00),
                                                                   ColorContainer(0xFF0000)),
                                                   center = 50,
                                                   delay = 10,
                                                   delayMod = 1.5,
                                                   direction = Direction.BACKWARD,
                                                   distance = 45,
                                                   id = "TEST",
                                                   runCount = 2,
                                                   section = "SECT",
                                                   spacing = 5)

            json.decodeJson() as AnimationToRunParams shouldBe correctData
        }


    })
