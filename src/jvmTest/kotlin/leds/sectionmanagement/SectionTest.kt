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

package animatedledstrip.test.leds.sectionmanagement

import animatedledstrip.communication.decodeJson
import animatedledstrip.communication.toUTF8String
import animatedledstrip.leds.emulation.createNewEmulatedStrip
import animatedledstrip.leds.sectionmanagement.LEDStripSectionManager
import animatedledstrip.leds.sectionmanagement.Section
import animatedledstrip.test.filteredStringArb
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.ints

class SectionTest : StringSpec(
    {
        val ledStrip = createNewEmulatedStrip(50)

        afterSpec {
            ledStrip.renderer.close()
        }

        "construction" {
            val section = Section()

            section.sections.shouldBeEmpty()
            section.subSections.shouldBeEmpty()
            section.name shouldBe ""
            section.numLEDs shouldBe 0
            section.pixels.shouldHaveSize(0)

            shouldThrow<UninitializedPropertyAccessException> {
                section.stripManager
            }
        }

        "get physical index" {
            checkAll(Exhaustive.ints(0 until 49)) { s ->
                val section = LEDStripSectionManager(ledStrip).createSection("test:$s", s, 49)

                checkAll(Exhaustive.ints(0 until 49 - s)) { p ->
                    section.getPhysicalIndex(p) shouldBe p + s
                }

                checkAll(Exhaustive.ints(0 until 49 - s)) { s2 ->
                    val section2 = section.createSection("test:$s:$s2", s2, section.pixels.lastIndex)

                    checkAll(Exhaustive.ints(0 until 49 - s - s2)) { p ->
                        section2.getPhysicalIndex(p) shouldBe p + s + s2
                    }
                }

                checkAll(Arb.int().filter { it !in section.pixels.indices }) { p ->
                    shouldThrow<IllegalArgumentException> {
                        section.getPhysicalIndex(p)
                    }
                }
            }
        }

        "get section" {
            val section = LEDStripSectionManager(ledStrip).fullStripSection

            section.getSection("test1") shouldBeSameInstanceAs section

            val sec = section.createSection("test1", 0, 15)
            section.getSection("test1") shouldBeSameInstanceAs sec

            section.getSection("test2") shouldBeSameInstanceAs section
        }

        "encode JSON" {
            checkAll(100, filteredStringArb, Arb.list(Arb.int(0..100000), 1..5000)) { n, p ->
                Section(n, p).jsonString() shouldBe
                        """{"type":"Section","name":"$n","pixels":${
                            p.toString().replace(" ", "")
                        },"parentSectionName":""}"""
            }
        }

        "decode JSON" {
            checkAll(100, filteredStringArb, Arb.list(Arb.int(0..100000), 1..5000)) { n, p ->
                val json = """{"type":"Section","name":"$n","pixels":${
                    p.toString().replace(" ", "")
                },"parentSectionName":""}"""

                val correctData = Section(n, p)

                val decodedData = json.decodeJson() as Section
                decodedData.name shouldBe correctData.name
                decodedData.numLEDs shouldBe correctData.numLEDs
                decodedData.pixels shouldBe correctData.pixels
            }
        }

        "encode and decode JSON" {
            checkAll(100, Arb.string(), Arb.list(Arb.int(0..100000), 1..5000)) { n, p ->
                val sec1 = Section(n, p)
                val secBytes = sec1.json()

                val sec2 = secBytes.toUTF8String().decodeJson() as Section

                sec2.name shouldBe sec1.name
                sec2.numLEDs shouldBe sec1.numLEDs
                sec2.pixels shouldBe sec1.pixels
            }
        }
    }
)
