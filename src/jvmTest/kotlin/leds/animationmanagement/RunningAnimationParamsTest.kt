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

import animatedledstrip.animations.AbsoluteDistance
import animatedledstrip.animations.Direction
import animatedledstrip.animations.Equation
import animatedledstrip.animations.RadiansRotation
import animatedledstrip.colors.PreparedColorContainer
import animatedledstrip.communication.decodeJson
import animatedledstrip.communication.serializer
import animatedledstrip.communication.toUTF8String
import animatedledstrip.leds.animationmanagement.AnimationToRunParams
import animatedledstrip.leds.animationmanagement.RunningAnimationParams
import animatedledstrip.leds.locationmanagement.Location
import animatedledstrip.test.*
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import kotlinx.serialization.encodeToString

class RunningAnimationParamsTest : StringSpec(
    {
        data class ArbRunningInfo(
            val animationName: String,
            val id: String,
            val section: String,
            val runCount: Int,
            val direction: Direction,
        )

        val animToRunInfoArb: Arb<ArbRunningInfo> =
            arbitrary { rs ->
                ArbRunningInfo(filteredStringArb.next(rs),
                               filteredStringArb.next(rs),
                               filteredStringArb.next(rs),
                               intArb.next(rs),
                               Arb.enum<Direction>().next(rs))
            }

        data class ArbRunningColorsParams(
            val colors: MutableList<PreparedColorContainer>,
            val intParams: MutableMap<String, Int>,
            val doubleParams: MutableMap<String, Double>,
            val stringParams: MutableMap<String, String>,
            val locationParams: MutableMap<String, Location>,
            val distanceParams: MutableMap<String, AbsoluteDistance>,
            val rotationParams: MutableMap<String, RadiansRotation>,
            val equationParams: MutableMap<String, Equation>,
        )

        val animToRunColorsParamsArb: Arb<ArbRunningColorsParams> =
            Arb.bind(Arb.list(preparedColorContainerArb, 0..5),
                     Arb.map(filteredStringArb, Arb.int(), 0, 5),
                     Arb.map(filteredStringArb, Arb.double(), 0, 5),
                     Arb.map(filteredStringArb, filteredStringArb, 0, 5),
                     Arb.map(filteredStringArb, locationArb, 0, 5),
                     Arb.map(filteredStringArb, absoluteDistanceArb, 0, 5),
                     Arb.map(filteredStringArb, radiansRotationArb, 0, 5),
                     Arb.map(filteredStringArb, equationArb, 0, 5)) { c, i, d, s, l, ad, rr, e ->
                ArbRunningColorsParams(c.toMutableList(),
                                       i.toMutableMap(),
                                       d.toMutableMap(),
                                       s.toMutableMap(),
                                       l.toMutableMap(),
                                       ad.toMutableMap(),
                                       rr.toMutableMap(),
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

        fun Map<String, AbsoluteDistance>.encodeToString(): String =
            this.toList().joinToString(",") { """"${it.first}":${serializer.encodeToString(it.second)}""" }

        fun Map<String, RadiansRotation>.encodeToString(): String =
            this.toList().joinToString(",") { """"${it.first}":${serializer.encodeToString(it.second)}""" }

        fun Map<String, Equation>.encodeToString(): String =
            this.toList().joinToString(",") { """"${it.first}":${serializer.encodeToString(it.second)}""" }


        "encode JSON" {
            checkAll(animToRunInfoArb, animToRunColorsParamsArb) { i, cp ->
                RunningAnimationParams(i.animationName,
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
                                       cp.equationParams,
                                       AnimationToRunParams())
                    .jsonString() shouldBe """{"type":"RunningAnimationParams",""" +
                        """"animationName":"${i.animationName}",""" +
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
                        """"equationParams":{${cp.equationParams.encodeToString()}},""" +
                        """"sourceParams":${serializer.encodeToString(AnimationToRunParams())}};;;"""
            }
        }

        "decode JSON" {
            checkAll(animToRunInfoArb, animToRunColorsParamsArb) { i, cp ->
                val json = """{"type":"RunningAnimationParams",""" +
                           """"animationName":"${i.animationName}",""" +
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
                           """"equationParams":{${cp.equationParams.encodeToString()}},""" +
                           """"sourceParams":${serializer.encodeToString(AnimationToRunParams())}};;;"""

                val correctData = RunningAnimationParams(i.animationName,
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
                                                         cp.equationParams,
                                                         AnimationToRunParams())

                json.decodeJson() as RunningAnimationParams shouldBe correctData
            }
        }

        "encode and decode JSON" {
            checkAll(animToRunInfoArb, animToRunColorsParamsArb) { i, cp ->
                val params1 = RunningAnimationParams(i.animationName,
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
                                                     cp.equationParams,
                                                     AnimationToRunParams())
                val paramsBytes = params1.json()

                val params2 = paramsBytes.toUTF8String().decodeJson() as RunningAnimationParams

                params2 shouldBe params1
            }
        }

        "with animation modification" {
            newRunningAnimationParams.withModifications().animation shouldBe ""

            checkAll<String> { a ->
                newRunningAnimationParams.withModifications(animation = a).animation shouldBe a
            }
        }

        "with colors modification" {
            // TODO
        }

        "with direction modification" {
            newRunningAnimationParams.withModifications().direction shouldBe Direction.FORWARD

            newRunningAnimationParams.withModifications(direction = Direction.FORWARD).direction shouldBe Direction.FORWARD

            newRunningAnimationParams.withModifications(direction = Direction.BACKWARD).direction shouldBe Direction.BACKWARD
        }

        "with id modification" {
            newRunningAnimationParams.withModifications().id shouldBe ""

            checkAll<String> { i ->
                newRunningAnimationParams.withModifications(id = i).id shouldBe i
            }
        }

        "with runCount modification" {
            newRunningAnimationParams.withModifications().runCount shouldBe -1

            checkAll<Int> { r ->
                newRunningAnimationParams.withModifications(runCount = r).runCount shouldBe r
            }
        }

        "with section modification" {
            newRunningAnimationParams.withModifications().section shouldBe ""

            checkAll<String> { s ->
                newRunningAnimationParams.withModifications(section = s).section shouldBe s
            }
        }
    }
)
