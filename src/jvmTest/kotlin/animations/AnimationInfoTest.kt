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

import animatedledstrip.animations.Animation
import animatedledstrip.communication.decodeJson
import animatedledstrip.communication.serializer
import animatedledstrip.communication.toUTF8String
import animatedledstrip.test.animInfoArb
import animatedledstrip.test.animParamsArb
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import kotlinx.serialization.encodeToString

class AnimationInfoTest : StringSpec(
    {


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
