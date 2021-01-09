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
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import kotlinx.serialization.encodeToString

class AnimationInfoTest : StringSpec(
    {
        val largeDoubleArb: Arb<Double> = arbitrary { rs ->
            val i = rs.random.nextInt(Int.MIN_VALUE + 1, Int.MAX_VALUE - 1)
            return@arbitrary if (i < 0) i - rs.random.nextDouble()
            else i + rs.random.nextDouble()
        }

        val locationArb: Arb<Location> =
            Arb.bind(largeDoubleArb,
                     largeDoubleArb,
                     largeDoubleArb) { x, y, z -> Location(x, y, z) }

        val distanceArb: Arb<Distance> =
            Arb.bind(largeDoubleArb,
                     largeDoubleArb,
                     largeDoubleArb,
                     Arb.bool()) { x, y, z, aOrP ->
                if (aOrP) AbsoluteDistance(x, y, z) else PercentDistance(x, y, z)
            }

        val filteredStringArb = Arb.string().filter { !it.contains("\"") && !it.contains("\\") }
        val intArb = Arb.int()
        val dimensionalityArb = Arb.enum<Dimensionality>()
        val nullableIntArb = Arb.int().orNull()
        val nullableDoubleArb = Arb.double().orNull()
        val nullableLocationArb = locationArb.orNull()
        val nullableDistanceArb = distanceArb.orNull()

        val animIntParamArb: Arb<AnimationParameter<Int>> =
            arbitrary { rs ->
                AnimationParameter(filteredStringArb.next(rs), filteredStringArb.next(rs), nullableIntArb.next(rs))
            }

        val animDoubleParamArb: Arb<AnimationParameter<Double>> =
            arbitrary { rs ->
                AnimationParameter(filteredStringArb.next(rs), filteredStringArb.next(rs), nullableDoubleArb.next(rs))
            }

        val animLocationParamArb: Arb<AnimationParameter<Location>> =
            arbitrary { rs ->
                AnimationParameter(filteredStringArb.next(rs), filteredStringArb.next(rs), nullableLocationArb.next(rs))
            }

        val animDistanceParamArb: Arb<AnimationParameter<Distance>> =
            arbitrary { rs ->
                AnimationParameter(filteredStringArb.next(rs), filteredStringArb.next(rs), nullableDistanceArb.next(rs))
            }

        data class ArbParams(
            val intParams: List<AnimationParameter<Int>>,
            val doubleParams: List<AnimationParameter<Double>>,
            val locationParams: List<AnimationParameter<Location>>,
            val distanceParams: List<AnimationParameter<Distance>>,
        )

        val animParamsArb: Arb<ArbParams> =
            Arb.bind(Arb.list(animIntParamArb, 0..3),
                     Arb.list(animDoubleParamArb, 0..3),
                     Arb.list(animLocationParamArb, 0..3),
                     Arb.list(animDistanceParamArb, 0..3)) { i, d, l, ds ->
                ArbParams(i, d, l, ds)
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
                Animation.AnimationInfo(ai.name, ai.abbr, ai.description, ai.signatureFile, ai.runCountDefault,
                                        ai.minimumColors, ai.unlimitedColors, ai.dimensionality, ai.directional,
                                        ap.intParams, ap.doubleParams, ap.locationParams, ap.distanceParams, listOf())
                    .jsonString() shouldBe """{"type":"AnimationInfo","name":"${ai.name}","abbr":"${ai.abbr}","description":"${ai.description}","signatureFile":"${ai.signatureFile}","runCountDefault":${ai.runCountDefault},"minimumColors":${ai.minimumColors},"unlimitedColors":${ai.unlimitedColors},""" +
                        """"dimensionality":[${ai.dimensionality.joinToString(",") { "\"$it\"" }}],"directional":${ai.directional},""" +
                        """"intParams":[${ap.intParams.joinToString(",") { serializer.encodeToString(it) }}],""" +
                        """"doubleParams":[${ap.doubleParams.joinToString(",") { serializer.encodeToString(it) }}],""" +
                        """"locationParams":[${ap.locationParams.joinToString(",") { serializer.encodeToString(it) }}],""" +
                        """"distanceParams":[${ap.distanceParams.joinToString(",") { serializer.encodeToString(it) }}],"equationParams":[]};;;"""
            }
        }

        "decode JSON" {
            checkAll(animInfoArb, animParamsArb) { ai, ap ->
                val json =
                    """{"type":"AnimationInfo","name":"${ai.name}","abbr":"${ai.abbr}","description":"${ai.description}","signatureFile":"${ai.signatureFile}","runCountDefault":${ai.runCountDefault},"minimumColors":${ai.minimumColors},"unlimitedColors":${ai.unlimitedColors},""" +
                    """"dimensionality":[${ai.dimensionality.joinToString(",") { "\"$it\"" }}],"directional":${ai.directional},""" +
                    """"intParams":[${ap.intParams.joinToString(",") { serializer.encodeToString(it) }}],""" +
                    """"doubleParams":[${ap.doubleParams.joinToString(",") { serializer.encodeToString(it) }}],""" +
                    """"locationParams":[${ap.locationParams.joinToString(",") { serializer.encodeToString(it) }}],""" +
                    """"distanceParams":[${ap.distanceParams.joinToString(",") { serializer.encodeToString(it) }}],"equationParams":[]};;;"""

                val correctData = Animation.AnimationInfo(name = ai.name,
                                                          abbr = ai.abbr,
                                                          description = ai.description,
                                                          signatureFile = ai.signatureFile,
                                                          runCountDefault = ai.runCountDefault,
                                                          minimumColors = ai.minimumColors,
                                                          unlimitedColors = ai.unlimitedColors,
                                                          dimensionality = ai.dimensionality,
                                                          directional = ai.directional,
                                                          intParams = ap.intParams,
                                                          doubleParams = ap.doubleParams,
                                                          locationParams = ap.locationParams,
                                                          distanceParams = ap.distanceParams,
                                                          equationParams = listOf())

                json.decodeJson() as Animation.AnimationInfo shouldBe correctData
            }
        }

        "encode and decode JSON" {
            checkAll(animInfoArb, animParamsArb) { ai, ap ->
                val info1 = Animation.AnimationInfo(name = ai.name,
                                                    abbr = ai.abbr,
                                                    description = ai.description,
                                                    signatureFile = ai.signatureFile,
                                                    runCountDefault = ai.runCountDefault,
                                                    minimumColors = ai.minimumColors,
                                                    unlimitedColors = ai.unlimitedColors,
                                                    dimensionality = ai.dimensionality,
                                                    directional = ai.directional,
                                                    intParams = ap.intParams,
                                                    doubleParams = ap.doubleParams,
                                                    locationParams = ap.locationParams,
                                                    distanceParams = ap.distanceParams,
                                                    equationParams = listOf())
                val infoBytes = info1.json()

                val info2 = infoBytes.toUTF8String().decodeJson() as Animation.AnimationInfo

                info2 shouldBe info1
            }
        }
    })
