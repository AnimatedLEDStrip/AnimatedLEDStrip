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
import animatedledstrip.leds.emulated.EmulatedAnimatedLEDStrip
import animatedledstrip.utils.decodeJson
import animatedledstrip.utils.toUTF8
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.enum
import io.kotest.property.exhaustive.ints
import kotlin.test.assertFailsWith

class AnimationToRunParamsTest : StringSpec(
    {
        "set animation" {
            val testAnimation = AnimationToRunParams()

            testAnimation.animation shouldBe ""

            checkAll<String> { a ->
                testAnimation.animation = a
                testAnimation.animation shouldBe a
            }

            checkAll<String> { a ->
                testAnimation.animation(a)
                testAnimation.animation shouldBe a
            }
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

            checkAll<Int> { c ->
                testAnimation.center = c
                testAnimation.center shouldBe c
            }

            checkAll<Int> { c ->
                testAnimation.center(c)
                testAnimation.center shouldBe c
            }
        }

        "set delay" {
            val testAnimation = AnimationToRunParams()

            testAnimation.delay shouldBe -1L

            checkAll<Long> { d ->
                testAnimation.delay = d
                testAnimation.delay shouldBe d
            }

            checkAll<Long> { d ->
                testAnimation.delay(d)
                testAnimation.delay shouldBe d
            }

            checkAll<Int> { d ->
                testAnimation.delay(d)
                testAnimation.delay shouldBe d.toLong()
            }
        }

        "set delayMod" {
            val testAnimation = AnimationToRunParams()

            testAnimation.delayMod shouldBe 1.0

            checkAll<Double> { d ->
                testAnimation.delayMod = d
                testAnimation.delayMod shouldBe d
            }

            checkAll<Double> { d ->
                testAnimation.delayMod(d)
                testAnimation.delayMod shouldBe d
            }
        }

        "set direction" {
            val testAnimation = AnimationToRunParams()

            testAnimation.direction shouldBe Direction.FORWARD

            checkAll(Exhaustive.enum<Direction>()) { d ->
                testAnimation.direction = d
                testAnimation.direction shouldBe d
            }

            checkAll(Exhaustive.enum<Direction>()) { d ->
                testAnimation.direction(d)
                testAnimation.direction shouldBe d
            }

            testAnimation.direction('F')
            testAnimation.direction shouldBe Direction.FORWARD

            testAnimation.direction('B')
            testAnimation.direction shouldBe Direction.BACKWARD

            testAnimation.direction('f')
            testAnimation.direction shouldBe Direction.FORWARD

            testAnimation.direction('b')
            testAnimation.direction shouldBe Direction.BACKWARD

            shouldThrow<IllegalArgumentException> {
                testAnimation.direction('G')
            }
        }

        "set distance" {
            val testAnimation = AnimationToRunParams()

            testAnimation.distance shouldBe -1

            checkAll<Int> { d ->
                testAnimation.distance = d
                testAnimation.distance shouldBe d
            }

            checkAll<Int> { d ->
                testAnimation.distance(d)
                testAnimation.distance shouldBe d
            }
        }

        "set id" {
            val testAnimation = AnimationToRunParams()

            testAnimation.id shouldBe ""

            checkAll<String> { i ->
                testAnimation.id = i
                testAnimation.id shouldBe i
            }

            checkAll<String> { i ->
                testAnimation.id(i)
                testAnimation.id shouldBe i
            }
        }

        "set section" {
            val testAnimation = AnimationToRunParams()

            testAnimation.section shouldBe ""

            checkAll<String> { s ->
                testAnimation.section = s
                testAnimation.section shouldBe s
            }

            checkAll<String> { s ->
                testAnimation.section(s)
                testAnimation.section shouldBe s
            }
        }

        "set spacing" {
            val testAnimation = AnimationToRunParams()

            testAnimation.spacing shouldBe -1

            checkAll<Int> { s ->
                testAnimation.spacing = s
                testAnimation.spacing shouldBe s
            }

            checkAll<Int> { s ->
                testAnimation.spacing(s)
                testAnimation.spacing shouldBe s
            }
        }

        "decode JSON" {
            val json =
                """{"type":"animatedledstrip.animationutils.AnimationToRunParams","animation":"Meteor","center":50,"colors":[{"type":"animatedledstrip.colors.ColorContainer","colors":[255,65280]},{"type":"animatedledstrip.colors.ColorContainer","colors":[16711680]}],"delay":10,"delayMod":1.5,"direction":"BACKWARD","distance":45,"id":"TEST","runCount":2,"section":"SECT","spacing":5};;;"""

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

        "encode JSON" {
            AnimationToRunParams(animation = "Color",
                                 colors = mutableListOf(ColorContainer(0xFFFF, 0xFF, 0xF0F0F0),
                                                        ColorContainer(0x3333, 0x1234, 0xFF00FF),
                                                        ColorContainer(0xFFFFFF, 0xF0000F, 0xF8F88F)),
                                 center = 30,
                                 delay = 300,
                                 delayMod = 1.8,
                                 direction = Direction.FORWARD,
                                 distance = 12,
                                 id = "A TEST",
                                 runCount = 50,
                                 section = "EEEE",
                                 spacing = 15).jsonString() shouldBe
                    """{"type":"animatedledstrip.animationutils.AnimationToRunParams","animation":"Color","colors":[{"type":"animatedledstrip.colors.ColorContainer","colors":[65535,255,15790320]},{"type":"animatedledstrip.colors.ColorContainer","colors":[13107,4660,16711935]},{"type":"animatedledstrip.colors.ColorContainer","colors":[16777215,15728655,16316559]}],"center":30,"delay":300,"delayMod":1.8,"direction":"FORWARD","distance":12,"id":"A TEST","runCount":50,"section":"EEEE","spacing":15};;;"""
        }

        "encode and decode JSON" {
            val anim1 = AnimationToRunParams(animation = "Bubble Sort",
                                             colors = mutableListOf(ColorContainer(0x31FF4, 0xFFF, 0xF00FFF),
                                                                    ColorContainer(0x3FCB3, 0x16F4C, 0xDFDDF),
                                                                    ColorContainer(0xFBAC9F, 0xFBEE0F, 0xF263F7)),
                                             center = 50,
                                             delay = 20,
                                             delayMod = 5.0,
                                             direction = Direction.BACKWARD,
                                             distance = 16,
                                             id = "TEST123",
                                             runCount = 3,
                                             section = "ABCDE",
                                             spacing = 4)
            val animBytes = anim1.json()

            val anim2 = animBytes.toUTF8().decodeJson() as AnimationToRunParams

            anim2 shouldBe anim1
        }

        val ledStrip = EmulatedAnimatedLEDStrip(10).wholeStrip

        "prepare colors" {
            val anim = AnimationToRunParams(animation = "Color",
                                            colors = mutableListOf(ColorContainer(0x0, 0xFFFF),
                                                                   ColorContainer(0xFE2C12, 0x5A736B, 0xCD4881)))

            val prep1 = anim.prepare(ledStrip)

            prep1.colors.shouldHaveSize(2)
            prep1.colors[0].colors.shouldContainExactly(0x0, 0x3434, 0x6767, 0x9A9A, 0xCDCD,
                                                        0xFFFF, 0xCCCC, 0x9999, 0x6666, 0x3333)
            prep1.colors[1].colors.shouldContainExactly(0xFE2C12, 0xC74530, 0x915C4E, 0x5A736B, 0x786871,
                                                        0x945D77, 0xB1537C, 0xCD4881, 0xDF3E5C, 0xEF3537)

            val subSection = ledStrip.getSubSection(3, 6)

            val prep2 = prep1.withModifications().prepare(subSection)

            prep2.colors.shouldHaveSize(2)
            prep2.colors[0].colors.shouldContainExactly(0x9A9A, 0xCDCD, 0xFFFF, 0xCCCC)
            prep2.colors[1].colors.shouldContainExactly(0x5A736B, 0x786871, 0x945D77, 0xB1537C)
        }

        "prepare colors under minimum requirement" {
            val anim1 = AnimationToRunParams(animation = "Alternate",
                                             colors = mutableListOf(ColorContainer(0xFF)))

            val prep1 = anim1.prepare(ledStrip)

            prep1.colors.shouldHaveSize(2)
            prep1.colors[0].colors.shouldContainExactly(0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
                                                        0xFF, 0xFF, 0xFF, 0xFF, 0xFF)
            prep1.colors[1].colors.shouldContainExactly(0x0, 0x0, 0x0, 0x0, 0x0,
                                                        0x0, 0x0, 0x0, 0x0, 0x0)

            val anim2 = AnimationToRunParams(animation = "Color")

            val prep2 = anim2.prepare(ledStrip)

            prep2.colors.shouldHaveSize(1)
            prep2.colors[0].colors.shouldContainExactly(0x0, 0x0, 0x0, 0x0, 0x0,
                                                        0x0, 0x0, 0x0, 0x0, 0x0)
        }

        "prepare center" {
            val anim = AnimationToRunParams(animation = "Color")

            checkAll(Exhaustive.ints(0..9)) { c ->
                anim.center(c).prepare(ledStrip).center shouldBe c
            }

            checkAll(Arb.int().filter { it < 0 }) { c ->
                anim.center(c).prepare(ledStrip).center shouldBe 5
            }

            checkAll(Arb.int().filter { it > 9 }) { c ->
                shouldThrow<IllegalArgumentException> {
                    anim.center(c).prepare(ledStrip)
                }
            }
        }

        "prepare delay" {
            val anim = AnimationToRunParams(animation = "Alternate")

            checkAll(Arb.int().filter { it >= 0 }) { d ->
                anim.delay(d).prepare(ledStrip).delay shouldBe d
            }

            checkAll(Arb.int().filter { it < 0 }) { d ->
                anim.delay(d).prepare(ledStrip).delay shouldBe 1000
            }
        }

        "prepare distance" {
            val anim1 = AnimationToRunParams(animation = "Color")

            checkAll(Arb.int().filter { it >= 0 }) { d ->
                anim1.distance(d).prepare(ledStrip).distance shouldBe d
            }

            checkAll(Arb.int().filter { it < 0 }) { d ->
                anim1.distance(d).prepare(ledStrip).distance shouldBe 10
            }

            val anim2 = AnimationToRunParams(animation = "Fireworks")

            checkAll(Arb.int().filter { it < 0 }) { d ->
                anim2.distance(d).prepare(ledStrip).distance shouldBe 20
            }
        }

        "prepare runCount" {
            val anim1 = AnimationToRunParams(animation = "Color")

            checkAll(Arb.int().filter { it > 0 }) { r ->
                anim1.runCount(r).prepare(ledStrip).runCount shouldBe r
            }

            checkAll(Arb.int().filter { it < 0 }) { r ->
                anim1.runCount(r).prepare(ledStrip).runCount shouldBe 1
            }

            val anim2 = AnimationToRunParams(animation = "Alternate")

            checkAll(Arb.int().filter { it < 0 }) { r ->
                anim2.runCount(r).prepare(ledStrip).runCount shouldBe -1
            }
        }

        "prepare spacing" {
            val anim = AnimationToRunParams(animation = "Multi Pixel Run")

            checkAll(Arb.int().filter { it > 0 }) { s ->
                anim.spacing(s).prepare(ledStrip).spacing shouldBe s
            }

            checkAll(Arb.int().filter { it <= 0 }) { s ->
                anim.spacing(s).prepare(ledStrip).spacing shouldBe 3
            }
        }

        "prepare source params" {
            val anim = AnimationToRunParams(animation = "Color")

            anim.prepare(ledStrip).sourceParams shouldBeSameInstanceAs anim
        }

        "prepare other params" {
            val anim = AnimationToRunParams(animation = "Color",
                                            direction = Direction.BACKWARD,
                                            id = "ABCD",
                                            section = "EFGH")

            val prep = anim.prepare(ledStrip)

            prep.animation shouldBe "Color"
            prep.direction shouldBe Direction.BACKWARD
            prep.id shouldBe "ABCD"
            prep.section shouldBe "EFGH"
        }

    })
