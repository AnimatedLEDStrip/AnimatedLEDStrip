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

package animatedledstrip.test

import animatedledstrip.animationutils.*
import animatedledstrip.colors.ColorContainer
import animatedledstrip.leds.StripInfo
import animatedledstrip.leds.emulated.EmulatedAnimatedLEDStrip
import animatedledstrip.utils.*
import com.google.gson.JsonSyntaxException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage

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
            0x0.toColorContainer() shouldBe ColorContainer(0x0L)
            0x158FE3.toColorContainer() shouldBe ColorContainer(0x158FE3L)
            0xFFFFFF.toColorContainer() shouldBe ColorContainer(0xFFFFFFL)
        }

        "Long to ColorContainer" {
            0x0L.toColorContainer() shouldBe ColorContainer(0x0L)
            0x158FE3L.toColorContainer() shouldBe ColorContainer(0x158FE3L)
            0xFFFFFFL.toColorContainer() shouldBe ColorContainer(0xFFFFFFL)
        }

        "good JSON to AnimationData" {
            val json =
                """DATA:{"animation":"Meteor","center":50,"colors":[{"colors":[255,65280]},{"colors":[16711680]}],"delay":10,"delayMod":1.5,"direction":"BACKWARD","distance":45,"id":"TEST","runCount":2,"section":"SECT","spacing":5};;;"""

            val correctData = AnimationData(animation = "Meteor",
                                            colors = listOf(ColorContainer(0xFF, 0xFF00),
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

            json.jsonToAnimationData() shouldBe correctData
        }

        "empty JSON to AnimationData" {
            val json = "DATA:{};;;"

            val correctData = AnimationData()

            json.jsonToAnimationData() shouldBe correctData
        }

        "malformed JSON to AnimationData" {
            val json = "DATA:{;;;"

            shouldThrow<JsonSyntaxException> { json.jsonToAnimationData() }

            try {
                json.jsonToAnimationData()
            } catch (e: JsonSyntaxException) {
                e.shouldHaveMessage("Malformed JSON: DATA:{;;;")
            }
        }

        "good JSON to AnimationInfo" {
            val json =
                """AINF:{"name":"Alternate","abbr":"ALT","description":"A description","signatureFile":"alternate.png","runCountDefault":1,"minimumColors":2,"unlimitedColors":true,"center":"NOTUSED","delay":"USED","direction":"NOTUSED","distance":"NOTUSED","spacing":"NOTUSED","delayDefault":1000,"distanceDefault":20,"spacingDefault":3}"""

            val correctData = Animation.AnimationInfo(name = "Alternate",
                                                      abbr = "ALT",
                                                      description = "A description",
                                                      signatureFile = "alternate.png",
                                                      runCountDefault = 1,
                                                      minimumColors = 2,
                                                      unlimitedColors = true,
                                                      center = ParamUsage.NOTUSED,
                                                      delay = ParamUsage.USED,
                                                      direction = ParamUsage.NOTUSED,
                                                      distance = ParamUsage.NOTUSED,
                                                      spacing = ParamUsage.NOTUSED,
                                                      delayDefault = 1000,
                                                      distanceDefault = 20,
                                                      spacingDefault = 3)

            json.jsonToAnimationInfo() shouldBe correctData
        }

        "malformed JSON to AnimationInfo" {
            val json = "INFO:{;;;"

            shouldThrow<JsonSyntaxException> { json.jsonToAnimationInfo() }

            try {
                json.jsonToAnimationInfo()
            } catch (e: JsonSyntaxException) {
                e.shouldHaveMessage("Malformed JSON: INFO:{;;;")
            }
        }

        "good JSON to Command" {
            val json =
                """CMD :{"command":"run a command"};;;"""

            val correctData = Command("run a command")

            json.jsonToCommand() shouldBe correctData
        }

        "empty JSON to Command" {
            val json = "CMD :{};;;"

            val correctData = Command()

            json.jsonToCommand() shouldBe correctData
        }

        "malformed JSON to Command" {
            val json = "CMD :{;;;"

            shouldThrow<JsonSyntaxException> { json.jsonToCommand() }

            try {
                json.jsonToCommand()
            } catch (e: JsonSyntaxException) {
                e.shouldHaveMessage("Malformed JSON: CMD :{;;;")
            }
        }

        "good JSON to EndAnimation" {
            val json =
                """END :{"id":"12345"};;;"""

            val correctData = EndAnimation("12345")

            json.jsonToEndAnimation() shouldBe correctData
        }

        "empty JSON to EndAnimation" {
            val json = "END :{};;;"

            val correctData = EndAnimation()

            json.jsonToEndAnimation() shouldBe correctData
        }

        "malformed JSON to EndAnimation" {
            val json = "END :{;;;"

            shouldThrow<JsonSyntaxException> { json.jsonToEndAnimation() }

            try {
                json.jsonToEndAnimation()
            } catch (e: JsonSyntaxException) {
                e.shouldHaveMessage("Malformed JSON: END :{;;;")
            }
        }

        "good JSON to Message" {
            val json =
                """MSG :{"message":"a message"};;;"""

            val correctData = Message("a message")

            json.jsonToMessage() shouldBe correctData
        }

        "empty JSON to Message" {
            val json = "MSG :{};;;"

            val correctData = Message()

            json.jsonToMessage() shouldBe correctData
        }

        "malformed JSON to Message" {
            val json = "MSG :{;;;"

            shouldThrow<JsonSyntaxException> { json.jsonToMessage() }

            try {
                json.jsonToMessage()
            } catch (e: JsonSyntaxException) {
                e.shouldHaveMessage("Malformed JSON: MSG :{;;;")
            }
        }

        val leds = EmulatedAnimatedLEDStrip(StripInfo())

        "good JSON to Section" {
            val json = """SECT:{"name":"Section1","startPixel":30,"endPixel":40};;;"""

            val correctData =
                leds.Section(name = "Section1",
                             startPixel = 30,
                             endPixel = 40)

            json.jsonToSection(leds) shouldBe correctData
            leds.getSection("Section1") shouldBe correctData
        }

        "good JSON to subsection" {
            val json = """SECT:{"parent":"Section2","startPixel":5,"endPixel":7};;;"""

            leds.createSection(name = "Section2",
                               startPixel = 30,
                               endPixel = 40)

            val correctData =
                leds.Section(name = "Section2:5:7",
                             startPixel = 5,
                             endPixel = 7,
                             parent = leds.getSection("Section2"))

            json.jsonToSection(leds) shouldBe correctData
            leds.getSection("Section2").getSubSection(5, 7) shouldBe correctData
            leds.getSection("Section2").getSubSection(5, 7).physicalStart shouldBe 35
        }

        "bad JSON to Section missing name and parent" {
            val json = """SECT:{"startPixel":30,"endPixel":40};;;"""

            shouldThrow<IllegalArgumentException> { json.jsonToSection(leds) }
            try {
                json.jsonToSection(leds)
            } catch (e: IllegalArgumentException) {
                e.shouldHaveMessage("name or parent property of animatedledstrip.leds.AnimatedLEDStrip.Section must be specified")
            }
        }

        "bad JSON to Section missing startPixel" {
            val json = """SECT:{"name":"Section3","endPixel":40};;;"""

            shouldThrow<IllegalArgumentException> { json.jsonToSection(leds) }
            try {
                json.jsonToSection(leds)
            } catch (e: IllegalArgumentException) {
                e.shouldHaveMessage("startPixel property of animatedledstrip.leds.AnimatedLEDStrip.Section must be specified")
            }
        }

        "bad JSON to Section missing endPixel" {
            val json = """SECT:{"name":"Section4","startPixel":30};;;"""

            shouldThrow<IllegalArgumentException> { json.jsonToSection(leds) }
            try {
                json.jsonToSection(leds)
            } catch (e: IllegalArgumentException) {
                e.shouldHaveMessage("endPixel property of animatedledstrip.leds.AnimatedLEDStrip.Section must be specified")
            }
        }

        "malformed JSON to Section" {
            val json = "SECT:{;;;"

            shouldThrow<JsonSyntaxException> { json.jsonToSection(leds) }

            try {
                json.jsonToSection(leds)
            } catch (e: JsonSyntaxException) {
                e.shouldHaveMessage("Malformed JSON: SECT:{;;;")
            }
        }

        "JSON to Section null check" {
            val json: String? = null

            shouldThrow<IllegalArgumentException> { json.jsonToSection(leds) }
        }

        "good JSON to StripInfo" {
            val json =
                """SINF:{"numLEDs":240,"pin":12,"imageDebugging":true,"rendersBeforeSave":1000,"threadCount":100};;;"""

            val correctData = StripInfo(numLEDs = 240,
                                        pin = 12,
                                        imageDebugging = true,
                                        rendersBeforeSave = 1000,
                                        threadCount = 100)

            json.jsonToStripInfo() shouldBe correctData
        }

        "empty JSON to StripInfo" {
            val json = "SINF:{};;;"

            val correctData = StripInfo()

            json.jsonToStripInfo() shouldBe correctData
        }

        "malformed JSON to StripInfo" {
            val json = "SINF:{;;;"

            shouldThrow<JsonSyntaxException> { json.jsonToStripInfo() }

            try {
                json.jsonToStripInfo()
            } catch (e: JsonSyntaxException) {
                e.shouldHaveMessage("Malformed JSON: SINF:{;;;")
            }
        }

        "JSON null check" {
            shouldThrow<IllegalArgumentException> { (null as String?).jsonToSendableData<AnimationData>() }
        }

        "data type prefix first 4 characters" {
            "test".getDataTypePrefix() shouldBe "test"
            "test123".getDataTypePrefix() shouldBe "test"
            "ABC".getDataTypePrefix() shouldBe "ABC"
            "".getDataTypePrefix() shouldBe ""
        }

        "data type prefix null check" {
            shouldThrow<IllegalArgumentException> { (null as String?).getDataTypePrefix() }
        }

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
