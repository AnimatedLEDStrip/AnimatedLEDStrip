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

package animatedledstrip.test.animations

import animatedledstrip.animations.*
import animatedledstrip.communication.decodeJson
import animatedledstrip.communication.serializer
import animatedledstrip.communication.toUTF8String
import animatedledstrip.leds.locationmanagement.Location
import animatedledstrip.test.*
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import kotlinx.serialization.encodeToString

class AnimationInfoTest : StringSpec(
    {
        data class ArbParams(
            val intParams: List<AnimationParameter<Int>>,
            val doubleParams: List<AnimationParameter<Double>>,
            val stringParams: List<AnimationParameter<String>>,
            val locationParams: List<AnimationParameter<Location>>,
            val distanceParams: List<AnimationParameter<Distance>>,
            val rotationParams: List<AnimationParameter<Rotation>>,
            val equationParams: List<AnimationParameter<Equation>>,
        )

        val animParamsArb: Arb<ArbParams> =
            Arb.bind(Arb.list(animIntParamArb, 0..3),
                     Arb.list(animDoubleParamArb, 0..3),
                     Arb.list(animStringParamArb, 0..3),
                     Arb.list(animLocationParamArb, 0..3),
                     Arb.list(animDistanceParamArb, 0..3),
                     Arb.list(animRotationParamArb, 0..3),
                     Arb.list(animEquationParamArb, 0..3)) { i, d, s, l, ds, r, e ->
                ArbParams(i, d, s, l, ds, r, e)
            }

        data class ArbInfo(
            val name: String,
            val abbr: String,
            val description: String,
            val signatureFile: String,
            val runCountDefault: Int,
            val minimumColors: Int,
            val unlimitedColors: Boolean,
            val dimensionality: Set<Dimensionality>,
            val directional: Boolean,
        )

        val animInfoArb: Arb<ArbInfo> =
            arbitrary { rs ->
                ArbInfo(
                    filteredStringArb.next(rs),
                    filteredStringArb.next(rs),
                    filteredStringArb.next(rs),
                    filteredStringArb.next(rs),
                    intArb.next(rs),
                    intArb.next(rs),
                    Arb.bool().next(rs),
                    Arb.set(dimensionalityArb, 1..3).next(rs),
                    Arb.bool().next(rs))
            }

        "encode JSON" {
            checkAll(animInfoArb, animParamsArb) { ai, ap ->
                Animation.AnimationInfo(ai.name,
                                        ai.abbr,
                                        ai.description,
                                        ai.runCountDefault,
                                        ai.minimumColors,
                                        ai.unlimitedColors,
                                        ai.dimensionality,
                                        ai.directional,
                                        ap.intParams,
                                        ap.doubleParams,
                                        ap.stringParams,
                                        ap.locationParams,
                                        ap.distanceParams,
                                        ap.rotationParams,
                                        ap.equationParams)
                    .jsonString() shouldBe """{"type":"AnimationInfo",""" +
                        """"name":"${ai.name}",""" +
                        """"abbr":"${ai.abbr}",""" +
                        """"description":"${ai.description}",""" +
                        """"runCountDefault":${ai.runCountDefault},""" +
                        """"minimumColors":${ai.minimumColors},""" +
                        """"unlimitedColors":${ai.unlimitedColors},""" +
                        """"dimensionality":[${ai.dimensionality.joinToString(",") { "\"$it\"" }}],""" +
                        """"directional":${ai.directional},""" +
                        """"intParams":[${ap.intParams.joinToString(",") { serializer.encodeToString(it) }}],""" +
                        """"doubleParams":[${ap.doubleParams.joinToString(",") { serializer.encodeToString(it) }}],""" +
                        """"stringParams":[${ap.stringParams.joinToString(",") { serializer.encodeToString(it) }}],""" +
                        """"locationParams":[${ap.locationParams.joinToString(",") { serializer.encodeToString(it) }}],""" +
                        """"distanceParams":[${ap.distanceParams.joinToString(",") { serializer.encodeToString(it) }}],""" +
                        """"rotationParams":[${ap.rotationParams.joinToString(",") { serializer.encodeToString(it) }}],""" +
                        """"equationParams":[${ap.equationParams.joinToString(",") { serializer.encodeToString(it) }}];;;"""
            }
        }

        "decode JSON" {
            checkAll(animInfoArb, animParamsArb) { ai, ap ->
                val json =
                    """{"type":"AnimationInfo",""" +
                    """"name":"${ai.name}",""" +
                    """"abbr":"${ai.abbr}",""" +
                    """"description":"${ai.description}",""" +
                    """"runCountDefault":${ai.runCountDefault},""" +
                    """"minimumColors":${ai.minimumColors},""" +
                    """"unlimitedColors":${ai.unlimitedColors},""" +
                    """"dimensionality":[${ai.dimensionality.joinToString(",") { "\"$it\"" }}],""" +
                    """"directional":${ai.directional},""" +
                    """"intParams":[${ap.intParams.joinToString(",") { serializer.encodeToString(it) }}],""" +
                    """"doubleParams":[${ap.doubleParams.joinToString(",") { serializer.encodeToString(it) }}],""" +
                    """"stringParams":[${ap.stringParams.joinToString(",") { serializer.encodeToString(it) }}],""" +
                    """"locationParams":[${ap.locationParams.joinToString(",") { serializer.encodeToString(it) }}],""" +
                    """"distanceParams":[${ap.distanceParams.joinToString(",") { serializer.encodeToString(it) }}],""" +
                    """"rotationParams":[${ap.rotationParams.joinToString(",") { serializer.encodeToString(it) }}],""" +
                    """"equationParams":[${ap.equationParams.joinToString(",") { serializer.encodeToString(it) }}];;;"""

                val correctData = Animation.AnimationInfo(name = ai.name,
                                                          abbr = ai.abbr,
                                                          description = ai.description,
                                                          runCountDefault = ai.runCountDefault,
                                                          minimumColors = ai.minimumColors,
                                                          unlimitedColors = ai.unlimitedColors,
                                                          dimensionality = ai.dimensionality,
                                                          directional = ai.directional,
                                                          intParams = ap.intParams,
                                                          doubleParams = ap.doubleParams,
                                                          stringParams = ap.stringParams,
                                                          locationParams = ap.locationParams,
                                                          distanceParams = ap.distanceParams,
                                                          rotationParams = ap.rotationParams,
                                                          equationParams = ap.equationParams)

                json.decodeJson() as Animation.AnimationInfo shouldBe correctData
            }
        }

        "encode and decode JSON" {
            checkAll(animInfoArb, animParamsArb) { ai, ap ->
                val info1 = Animation.AnimationInfo(name = ai.name,
                                                    abbr = ai.abbr,
                                                    description = ai.description,
                                                    runCountDefault = ai.runCountDefault,
                                                    minimumColors = ai.minimumColors,
                                                    unlimitedColors = ai.unlimitedColors,
                                                    dimensionality = ai.dimensionality,
                                                    directional = ai.directional,
                                                    intParams = ap.intParams,
                                                    doubleParams = ap.doubleParams,
                                                    stringParams = ap.stringParams,
                                                    locationParams = ap.locationParams,
                                                    distanceParams = ap.distanceParams,
                                                    rotationParams = ap.rotationParams,
                                                    equationParams = ap.equationParams)
                val infoBytes = info1.json()

                val info2 = infoBytes.toUTF8String().decodeJson() as Animation.AnimationInfo

                info2 shouldBe info1
            }
        }
    }
)
