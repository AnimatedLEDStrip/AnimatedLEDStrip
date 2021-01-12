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
 *
 */

package animatedledstrip.test.colors

import animatedledstrip.colors.*
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ColorUtilsTests : StringSpec(
    {
        "Int to ARGB" {
            0x0.toARGB() shouldBe 0xFF000000.toInt()
            0x158FE3.toARGB() shouldBe 0xFF158FE3.toInt()
            0xFFFFFF.toARGB() shouldBe 0xFFFFFFFF.toInt()
        }

        "Long to ARGB" {
            0x0L.toARGB() shouldBe 0xFF000000.toInt()
            0x158FE3L.toARGB() shouldBe 0xFF158FE3.toInt()
            0xFFFFFFL.toARGB() shouldBe 0xFFFFFFFF.toInt()
        }

        "Int to ColorContainer" {
            0x0.toColorContainer() shouldBe ColorContainer(0x0)
            0x158FE3.toColorContainer() shouldBe ColorContainer(0x158FE3)
            0xFFFFFF.toColorContainer() shouldBe ColorContainer(0xFFFFFF)
        }

        "Long to ColorContainer" {
            0x0L.toColorContainer() shouldBe ColorContainer(0x0)
            0x158FE3L.toColorContainer() shouldBe ColorContainer(0x158FE3)
            0xFFFFFFL.toColorContainer() shouldBe ColorContainer(0xFFFFFF)
        }

        "parse hex" {
            parseHex("FFFF") shouldBe 0xFFFF
            parseHex("fFFf") shouldBe 0xFFFF
            parseHex("FED432") shouldBe 0xFED432
            parseHex("CBA987") shouldBe 0xCBA987
        }

        "parse hex with 0x" {
            parseHex("0xFF") shouldBe 0xFF
        }

        "parse hex with capitalization" {
            parseHex("FE4d") shouldBe 0xFE4D
        }

        "parse hex bad input" {
            shouldThrow<NumberFormatException> { parseHex("0xG") }
        }

        "parse hex or default" {
            parseHexOrDefault("FFFF") shouldBe 0xFFFF
            parseHexOrDefault("fFFf") shouldBe 0xFFFF
            parseHexOrDefault("FED432") shouldBe 0xFED432
            parseHexOrDefault("CBA987") shouldBe 0xCBA987
        }

        "parse hex or default with 0x" {
            parseHexOrDefault("0xAA") shouldBe 0xAA
        }

        "parse hex or default with capitalization" {
            parseHexOrDefault("aeC5") shouldBe 0xAEC5
        }

        "parse hex or default bad input" {
            parseHexOrDefault("0xABT") shouldBe 0
            parseHexOrDefault("0xGF3", 0xF1E) shouldBe 0xF1E
        }

        "remove 0x prefix" {
            "0x55".remove0xPrefix() shouldBe "55"
            "0x".remove0xPrefix() shouldBe ""
        }

        "remove 0x with capitalization" {
            "0X55".remove0xPrefix() shouldBe "55"
        }

        "don't remove 0x if not at beginning" {
            "340x55".remove0xPrefix() shouldBe "340X55"
        }
    }
)
