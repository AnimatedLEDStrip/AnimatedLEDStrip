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

import animatedledstrip.leds.emulation.createNewEmulatedStrip
import animatedledstrip.leds.sectionmanagement.LEDStripSectionManager
import animatedledstrip.leds.sectionmanagement.Section
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.property.checkAll

class LEDStripSectionManagerTest : StringSpec(
    {
        val ledStrip = createNewEmulatedStrip(50)

        "construction" {
            val manager = LEDStripSectionManager(ledStrip)

            manager.sections.shouldHaveSize(2)
            manager.sections.shouldContainExactly(mapOf("" to manager.fullStripSection,
                                                        "fullStrip" to manager.fullStripSection))
            shouldThrow<IllegalStateException> {
                manager.subSections
            }
            manager.name shouldBe ""
            manager.numLEDs shouldBe ledStrip.numLEDs
            manager.numLEDs shouldBe 50
            manager.pixels.shouldHaveSize(50)
        }

        "physical index" {
            val manager = LEDStripSectionManager(ledStrip)

            checkAll<Int> { p ->
                manager.getPhysicalIndex(p) shouldBe p
            }
        }

        "get section" {
            val manager = LEDStripSectionManager(ledStrip)

            manager.getSection("test1") shouldBeSameInstanceAs manager.fullStripSection
            val sec = manager.createSection("test1", 0, 15)
            manager.getSection("test1") shouldBeSameInstanceAs sec

            manager.getSection("test2") shouldBeSameInstanceAs manager.fullStripSection
            val sec2 = Section("test2", (0..20).toList())
            manager.createSection(sec2)
            manager.getSection("test2").name shouldBe sec2.name
        }

        "get subsection" {
            val manager = LEDStripSectionManager(ledStrip)

            shouldThrow<IllegalStateException> {
                manager.getSubSection(0, 10)
            }
        }
    }
)
