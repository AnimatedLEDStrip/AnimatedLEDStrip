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

import animatedledstrip.colors.ColorContainer
import animatedledstrip.leds.animationmanagement.*
import animatedledstrip.test.distanceArb
import animatedledstrip.test.locationArb
import animatedledstrip.test.rotationArb
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlin.test.assertFailsWith

class AnimationToRunParamsUtilsTests : StringSpec(
    {
        "set animation" {
            val testAnimation = AnimationToRunParams()

            testAnimation.animation shouldBe ""

            checkAll<String> { a ->
                testAnimation.animation(a)
                testAnimation.animation shouldBe a
            }
        }

        "set colors" {
            // TODO: Refactor to use property testing
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

            assertFailsWith<IllegalArgumentException> {
                testAnimation.addColors(listOf<Int?>(null))
            }
            assertFailsWith<IllegalArgumentException> { testAnimation.addColors(listOf(0.0)) }
            assertFailsWith<NumberFormatException> { testAnimation.addColors(listOf("0XG")) }
        }

        "set id" {
            val testAnimation = AnimationToRunParams()

            testAnimation.id shouldBe ""

            checkAll<String> { i ->
                testAnimation.id(i)
                testAnimation.id shouldBe i
            }
        }

        "set section" {
            val testAnimation = AnimationToRunParams()

            testAnimation.section shouldBe ""

            checkAll<String> { s ->
                testAnimation.section(s)
                testAnimation.section shouldBe s
            }
        }

        "set runCount" {
            val testAnimation = AnimationToRunParams()

            testAnimation.runCount shouldBe 0

            checkAll<Int> { r ->
                testAnimation.runCount(r)
                testAnimation.runCount shouldBe r
            }
        }

        "set int param" {
            val testAnimation = AnimationToRunParams()

            checkAll(Arb.string(), Arb.int(), Arb.int()) { k, v1, v2 ->
                testAnimation.intParam(k, v1)
                testAnimation.intParams.shouldContainKey(k)
                testAnimation.intParams[k] shouldBe v1
                testAnimation.intParam(k, v2)
                testAnimation.intParams.shouldContainKey(k)
                testAnimation.intParams[k] shouldBe v2
            }
        }

        "set double param" {
            val testAnimation = AnimationToRunParams()

            checkAll(Arb.string(), Arb.double(), Arb.double()) { k, v1, v2 ->
                testAnimation.doubleParam(k, v1)
                testAnimation.doubleParams.shouldContainKey(k)
                testAnimation.doubleParams[k] shouldBe v1
                testAnimation.doubleParam(k, v2)
                testAnimation.doubleParams.shouldContainKey(k)
                testAnimation.doubleParams[k] shouldBe v2
            }
        }

        "set string param" {
            val testAnimation = AnimationToRunParams()

            checkAll(Arb.string(), Arb.string(), Arb.string()) { k, v1, v2 ->
                testAnimation.stringParam(k, v1)
                testAnimation.stringParams.shouldContainKey(k)
                testAnimation.stringParams[k] shouldBe v1
                testAnimation.stringParam(k, v2)
                testAnimation.stringParams.shouldContainKey(k)
                testAnimation.stringParams[k] shouldBe v2
            }
        }

        "set location param" {
            val testAnimation = AnimationToRunParams()

            checkAll(Arb.string(), locationArb, locationArb) { k, v1, v2 ->
                testAnimation.locationParam(k, v1)
                testAnimation.locationParams.shouldContainKey(k)
                testAnimation.locationParams[k] shouldBe v1
                testAnimation.locationParam(k, v2)
                testAnimation.locationParams.shouldContainKey(k)
                testAnimation.locationParams[k] shouldBe v2
            }
        }

        "set distance param" {
            val testAnimation = AnimationToRunParams()

            checkAll(Arb.string(), distanceArb, distanceArb) { k, v1, v2 ->
                testAnimation.distanceParam(k, v1)
                testAnimation.distanceParams.shouldContainKey(k)
                testAnimation.distanceParams[k] shouldBe v1
                testAnimation.distanceParam(k, v2)
                testAnimation.distanceParams.shouldContainKey(k)
                testAnimation.distanceParams[k] shouldBe v2
            }
        }

        "set rotation param" {
            val testAnimation = AnimationToRunParams()

            checkAll(Arb.string(), rotationArb, rotationArb) { k, v1, v2 ->
                testAnimation.rotationParam(k, v1)
                testAnimation.rotationParams.shouldContainKey(k)
                testAnimation.rotationParams[k] shouldBe v1
                testAnimation.rotationParam(k, v2)
                testAnimation.rotationParams.shouldContainKey(k)
                testAnimation.rotationParams[k] shouldBe v2
            }
        }
    }
)
