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

package animatedledstrip.test.utils

import animatedledstrip.colors.ColorContainer
import animatedledstrip.leds.stripmanagement.StripInfo
import animatedledstrip.utils.*
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ConversionUtilsTest : StringSpec(
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





//        "empty JSON to Message" {
//            val json = "MSG :{};;;"
//
//            val correctData = Message()
//
//            json.jsonToMessage() shouldBe correctData
//        }
//
//        "malformed JSON to Message" {
//            val json = "MSG :{;;;"
//
//            shouldThrow<JsonSyntaxException> { json.jsonToMessage() }
//
//            try {
//                json.jsonToMessage()
//            } catch (e: JsonSyntaxException) {
//                e.shouldHaveMessage("Malformed JSON: MSG :{;;;")
//            }
//        }

//        val leds = EmulatedAnimatedLEDStrip(StripInfo())
//
//        "good JSON to Section" {
//            val json = """SECT:{"name":"Section1","startPixel":30,"endPixel":40};;;"""
//
//            val correctData =
//                leds.Section(name = "Section1",
//                             startPixel = 30,
//                             endPixel = 40)
//
//            json.jsonToSection(leds) shouldBe correctData
//            leds.getSection("Section1") shouldBe correctData
//        }
//
//        "good JSON to subsection" {
//            val json = """SECT:{"parent":"Section2","startPixel":5,"endPixel":7};;;"""
//
//            leds.createSection(name = "Section2",
//                               startPixel = 30,
//                               endPixel = 40)
//
//            val correctData =
//                leds.Section(name = "Section2:5:7",
//                             startPixel = 5,
//                             endPixel = 7,
//                             parent = leds.getSection("Section2"))
//
//            json.jsonToSection(leds) shouldBe correctData
//            leds.getSection("Section2").getSubSection(5, 7) shouldBe correctData
//            leds.getSection("Section2").getSubSection(5, 7).physicalStart shouldBe 35
//        }

//        "bad JSON to Section missing name and parent" {
//            val json = """SECT:{"startPixel":30,"endPixel":40};;;"""
//
//            shouldThrow<IllegalArgumentException> { json.jsonToSection(leds) }
//            try {
//                json.jsonToSection(leds)
//            } catch (e: IllegalArgumentException) {
//                e.shouldHaveMessage("name or parent property of animatedledstrip.leds.AnimatedLEDStrip.Section must be specified")
//            }
//        }
//
//        "bad JSON to Section missing startPixel" {
//            val json = """SECT:{"name":"Section3","endPixel":40};;;"""
//
//            shouldThrow<IllegalArgumentException> { json.jsonToSection(leds) }
//            try {
//                json.jsonToSection(leds)
//            } catch (e: IllegalArgumentException) {
//                e.shouldHaveMessage("startPixel property of animatedledstrip.leds.AnimatedLEDStrip.Section must be specified")
//            }
//        }
//
//        "bad JSON to Section missing endPixel" {
//            val json = """SECT:{"name":"Section4","startPixel":30};;;"""
//
//            shouldThrow<IllegalArgumentException> { json.jsonToSection(leds) }
//            try {
//                json.jsonToSection(leds)
//            } catch (e: IllegalArgumentException) {
//                e.shouldHaveMessage("endPixel property of animatedledstrip.leds.AnimatedLEDStrip.Section must be specified")
//            }
//        }
//
//        "malformed JSON to Section" {
//            val json = "SECT:{;;;"
//
//            shouldThrow<JsonSyntaxException> { json.jsonToSection(leds) }
//
//            try {
//                json.jsonToSection(leds)
//            } catch (e: JsonSyntaxException) {
//                e.shouldHaveMessage("Malformed JSON: SECT:{;;;")
//            }
//        }
//
//        "JSON to Section null check" {
//            val json: String? = null
//
//            shouldThrow<IllegalArgumentException> { json.jsonToSection(leds) }
//        }

        "good JSON to StripInfo" {
            val json =
                """{"type":"animatedledstrip.leds.stripmanagement.StripInfo","numLEDs":240,"pin":12,"imageDebugging":true,"rendersBeforeSave":1000,"threadCount":100};;;"""

            val correctData = StripInfo(numLEDs = 240,
                                        pin = 12,
                                        imageDebugging = true,
                                        rendersBeforeSave = 1000,
                                        threadCount = 100)

            json.decodeJson() as StripInfo shouldBe correctData
        }

//        "empty JSON to StripInfo" {
//            val json = "{};;;"
//
//            val correctData = StripInfo()
//
//            json.jsonToStripInfo() shouldBe correctData
//        }
//
//        "malformed JSON to StripInfo" {
//            val json = "{;;;"
//
//            shouldThrow<JsonSyntaxException> { json.jsonToStripInfo() }
//
//            try {
//                json.jsonToStripInfo()
//            } catch (e: JsonSyntaxException) {
//                e.shouldHaveMessage("Malformed JSON: SINF:{;;;")
//            }
//        }

        "decode JSON null check" {
            shouldThrow<IllegalArgumentException> { (null as String?).decodeJson() }
        }

//        "data type prefix first 4 characters" {
//            "test".getDataTypePrefix() shouldBe "test"
//            "test123".getDataTypePrefix() shouldBe "test"
//            "ABC".getDataTypePrefix() shouldBe "ABC"
//            "".getDataTypePrefix() shouldBe ""
//        }
//
//        "data type prefix null check" {
//            shouldThrow<IllegalArgumentException> { (null as String?).getDataTypePrefix() }
//        }

        "bytearray to string length specified" {
            ByteArray(5).apply {
                this[0] = 't'.toByte()
                this[1] = 'e'.toByte()
                this[2] = 's'.toByte()
                this[3] = 't'.toByte()
                this[4] = 's'.toByte()
            }.toUTF8(5) shouldBe "tests"
        }

        "bytearray to string take only part of array" {
            ByteArray(5).apply {
                this[0] = 't'.toByte()
                this[1] = 'e'.toByte()
                this[2] = 's'.toByte()
                this[3] = 't'.toByte()
                this[4] = 's'.toByte()
            }.toUTF8(3) shouldBe "tes"
        }

        "bytearray to string length inferred" {
            ByteArray(5).apply {
                this[0] = 't'.toByte()
                this[1] = 'e'.toByte()
                this[2] = 's'.toByte()
                this[3] = 't'.toByte()
                this[4] = 's'.toByte()
            }.toUTF8() shouldBe "tests"
        }

        "bytearray to string null check" {
            shouldThrow<IllegalArgumentException> { (null as ByteArray?).toUTF8(5) }
        }

        "remove whitespace spaces" {
            "     ".removeWhitespace() shouldBe ""
            "a b c d e".removeWhitespace() shouldBe "abcde"
        }

        "remove whitespace tabs" {
            "\t".removeWhitespace() shouldBe ""
            "a\tb\tc\td\te".removeWhitespace() shouldBe "abcde"
        }

        "remove whitespace newlines" {
            "\n\r".removeWhitespace() shouldBe ""
            "a\nb\rc\nd\re".removeWhitespace() shouldBe "abcde"
        }
    })
