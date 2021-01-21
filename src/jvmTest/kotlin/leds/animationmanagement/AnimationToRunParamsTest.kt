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

package animatedledstrip.test.leds.animationmanagement

import animatedledstrip.animations.Direction
import animatedledstrip.animations.Distance
import animatedledstrip.animations.Equation
import animatedledstrip.animations.Rotation
import animatedledstrip.colors.ColorContainer
import animatedledstrip.colors.ColorContainerInterface
import animatedledstrip.communication.decodeJson
import animatedledstrip.communication.serializer
import animatedledstrip.communication.toUTF8String
import animatedledstrip.leds.animationmanagement.AnimationToRunParams
import animatedledstrip.leds.animationmanagement.runCount
import animatedledstrip.leds.emulation.createNewEmulatedStrip
import animatedledstrip.leds.locationmanagement.Location
import animatedledstrip.test.*
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import kotlinx.serialization.encodeToString

class AnimationToRunParamsTest : StringSpec(
    {
        data class ArbToRunInfo(
            val animation: String,
            val id: String,
            val section: String,
            val runCount: Int,
            val direction: Direction,
        )

        val animToRunInfoArb: Arb<ArbToRunInfo> =
            arbitrary { rs ->
                ArbToRunInfo(filteredStringArb.next(rs),
                             filteredStringArb.next(rs),
                             filteredStringArb.next(rs),
                             intArb.next(rs),
                             Arb.enum<Direction>().next(rs))
            }

        data class ArbToRunColorsParams(
            val colors: MutableList<ColorContainerInterface>,
            val intParams: MutableMap<String, Int>,
            val doubleParams: MutableMap<String, Double>,
            val stringParams: MutableMap<String, String>,
            val locationParams: MutableMap<String, Location>,
            val distanceParams: MutableMap<String, Distance>,
            val rotationParams: MutableMap<String, Rotation>,
            val equationParams: MutableMap<String, Equation>,
        )

        val animToRunColorsParamsArb: Arb<ArbToRunColorsParams> =
            Arb.bind(Arb.list(colorContainerArb, 0..5),
                     Arb.map(filteredStringArb, Arb.int(), 0, 5),
                     Arb.map(filteredStringArb, Arb.double(), 0, 5),
                     Arb.map(filteredStringArb, filteredStringArb, 0, 5),
                     Arb.map(filteredStringArb, locationArb, 0, 5),
                     Arb.map(filteredStringArb, distanceArb, 0, 5),
                     Arb.map(filteredStringArb, rotationArb, 0, 5),
                     Arb.map(filteredStringArb, equationArb, 0, 5)) { c, i, d, s, l, ds, r, e ->
                ArbToRunColorsParams(c.toMutableList(),
                                     i.toMutableMap(),
                                     d.toMutableMap(),
                                     s.toMutableMap(),
                                     l.toMutableMap(),
                                     ds.toMutableMap(),
                                     r.toMutableMap(),
                                     e.toMutableMap())
            }

        fun Map<String, Int>.encodeToString(): String =
            this.toList().joinToString(",") { """"${it.first}":${serializer.encodeToString(it.second)}""" }

        fun Map<String, Double>.encodeToString(): String =
            this.toList().joinToString(",") { """"${it.first}":${serializer.encodeToString(it.second)}""" }

        fun Map<String, String>.encodeToString(): String =
            this.toList().joinToString(",") { """"${it.first}":${serializer.encodeToString(it.second)}""" }

        fun Map<String, Location>.encodeToString(): String =
            this.toList().joinToString(",") { """"${it.first}":${serializer.encodeToString(it.second)}""" }

        fun Map<String, Distance>.encodeToString(): String =
            this.toList().joinToString(",") { """"${it.first}":${serializer.encodeToString(it.second)}""" }

        fun Map<String, Rotation>.encodeToString(): String =
            this.toList().joinToString(",") { """"${it.first}":${serializer.encodeToString(it.second)}""" }

        fun Map<String, Equation>.encodeToString(): String =
            this.toList().joinToString(",") { """"${it.first}":${serializer.encodeToString(it.second)}""" }


        "encode JSON" {
            checkAll(animToRunInfoArb, animToRunColorsParamsArb) { i, cp ->
                AnimationToRunParams(i.animation,
                                     cp.colors,
                                     i.id,
                                     i.section,
                                     i.runCount,
                                     i.direction,
                                     cp.intParams,
                                     cp.doubleParams,
                                     cp.stringParams,
                                     cp.locationParams,
                                     cp.distanceParams,
                                     cp.rotationParams,
                                     cp.equationParams)
                    .jsonString() shouldBe """{"type":"AnimationToRunParams",""" +
                        """"animation":"${i.animation}",""" +
                        """"colors":[${cp.colors.joinToString(",") { serializer.encodeToString(it) }}],""" +
                        """"id":"${i.id}",""" +
                        """"section":"${i.section}",""" +
                        """"runCount":${i.runCount},""" +
                        """"direction":"${i.direction}",""" +
                        """"intParams":{${cp.intParams.encodeToString()}},""" +
                        """"doubleParams":{${cp.doubleParams.encodeToString()}},""" +
                        """"stringParams":{${cp.stringParams.encodeToString()}},""" +
                        """"locationParams":{${cp.locationParams.encodeToString()}},""" +
                        """"distanceParams":{${cp.distanceParams.encodeToString()}},""" +
                        """"rotationParams":{${cp.rotationParams.encodeToString()}},""" +
                        """"equationParams":{${cp.equationParams.encodeToString()}}};;;"""
            }
        }

        "decode JSON" {
            checkAll(animToRunInfoArb, animToRunColorsParamsArb) { i, cp ->
                val json = """{"type":"AnimationToRunParams",""" +
                           """"animation":"${i.animation}",""" +
                           """"colors":[${cp.colors.joinToString(",") { serializer.encodeToString(it) }}],""" +
                           """"id":"${i.id}",""" +
                           """"section":"${i.section}",""" +
                           """"runCount":${i.runCount},""" +
                           """"direction":"${i.direction}",""" +
                           """"intParams":{${cp.intParams.encodeToString()}},""" +
                           """"doubleParams":{${cp.doubleParams.encodeToString()}},""" +
                           """"stringParams":{${cp.stringParams.encodeToString()}},""" +
                           """"locationParams":{${cp.locationParams.encodeToString()}},""" +
                           """"distanceParams":{${cp.distanceParams.encodeToString()}},""" +
                           """"rotationParams":{${cp.rotationParams.encodeToString()}},""" +
                           """"equationParams":{${cp.equationParams.encodeToString()}}};;;"""

                val correctData = AnimationToRunParams(i.animation,
                                                       cp.colors,
                                                       i.id,
                                                       i.section,
                                                       i.runCount,
                                                       i.direction,
                                                       cp.intParams,
                                                       cp.doubleParams,
                                                       cp.stringParams,
                                                       cp.locationParams,
                                                       cp.distanceParams,
                                                       cp.rotationParams,
                                                       cp.equationParams)

                json.decodeJson() as AnimationToRunParams shouldBe correctData
            }
        }

        "encode and decode JSON" {
            checkAll(animToRunInfoArb, animToRunColorsParamsArb) { i, cp ->
                val params1 = AnimationToRunParams(i.animation,
                                                   cp.colors,
                                                   i.id,
                                                   i.section,
                                                   i.runCount,
                                                   i.direction,
                                                   cp.intParams,
                                                   cp.doubleParams,
                                                   cp.stringParams,
                                                   cp.locationParams,
                                                   cp.distanceParams,
                                                   cp.rotationParams,
                                                   cp.equationParams)
                val paramsBytes = params1.json()

                val params2 = paramsBytes.toUTF8String().decodeJson() as AnimationToRunParams

                params2 shouldBe params1
            }
        }

        val ledStrip = createNewEmulatedStrip(10)
        val stripSection = ledStrip.sectionManager.createSection("test", 0, 9)

        afterSpec {
            ledStrip.renderer.close()
        }

        "prepare colors" {
            val anim = AnimationToRunParams(animation = "Color",
                                            colors = mutableListOf(ColorContainer(0x0, 0xFFFF),
                                                                   ColorContainer(0xFE2C12, 0x5A736B, 0xCD4881)),
                                            section = "test")

            val prep1 = anim.prepare(stripSection)

            prep1.colors.shouldHaveSize(2)
            prep1.colors[0].colors.shouldContainExactly(0x0, 0x3434, 0x6767, 0x9A9A, 0xCDCD,
                                                        0xFFFF, 0xCCCC, 0x9999, 0x6666, 0x3333)
            prep1.colors[1].colors.shouldContainExactly(0xFE2C12, 0xC74530, 0x915C4E, 0x5A736B, 0x786871,
                                                        0x945D77, 0xB1537C, 0xCD4881, 0xDF3E5C, 0xEF3537)

            val subSection = stripSection.getSubSection(3, 6)

            val prep2 = prep1.withModifications().prepare(subSection, stripSection)

            prep2.colors.shouldHaveSize(2)
            prep2.colors[0].colors.shouldContainExactly(0x9A9A, 0xCDCD, 0xFFFF, 0xCCCC)
            prep2.colors[1].colors.shouldContainExactly(0x5A736B, 0x786871, 0x945D77, 0xB1537C)
        }

        "prepare colors under minimum requirement" {
            val anim1 = AnimationToRunParams(animation = "Alternate",
                                             colors = mutableListOf(ColorContainer(0xFF)))

            val prep1 = anim1.prepare(stripSection)

            prep1.colors.shouldHaveSize(2)
            prep1.colors[0].colors.shouldContainExactly(0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
                                                        0xFF, 0xFF, 0xFF, 0xFF, 0xFF)
            prep1.colors[1].colors.shouldContainExactly(0x0, 0x0, 0x0, 0x0, 0x0,
                                                        0x0, 0x0, 0x0, 0x0, 0x0)

            val anim2 = AnimationToRunParams(animation = "Color")

            val prep2 = anim2.prepare(stripSection)

            prep2.colors.shouldHaveSize(1)
            prep2.colors[0].colors.shouldContainExactly(0x0, 0x0, 0x0, 0x0, 0x0,
                                                        0x0, 0x0, 0x0, 0x0, 0x0)
        }

        "prepare runCount" {
            val anim1 = AnimationToRunParams(animation = "Color")

            checkAll(Arb.int().filter { it > 0 }) { r ->
                anim1.runCount(r).prepare(stripSection).runCount shouldBe r
            }

            checkAll(Arb.int().filter { it < 0 }) { r ->
                anim1.runCount(r).prepare(stripSection).runCount shouldBe 1
            }

            val anim2 = AnimationToRunParams(animation = "Alternate")

            checkAll(Arb.int().filter { it < 0 }) { r ->
                anim2.runCount(r).prepare(stripSection).runCount shouldBe -1
            }
        }

        "prepare source params" {
            val anim = AnimationToRunParams(animation = "Color")

            anim.prepare(stripSection).sourceParams shouldBeSameInstanceAs anim
        }

        "prepare other params" {
            val anim = AnimationToRunParams(animation = "Color",
                                            direction = Direction.BACKWARD,
                                            id = "ABCD",
                                            section = "EFGH")

            val prep = anim.prepare(stripSection)

            prep.animationName shouldBe "Color"
            prep.direction shouldBe Direction.BACKWARD
            prep.id shouldBe "ABCD"
            prep.section shouldBe "EFGH"
        }

    }
)
