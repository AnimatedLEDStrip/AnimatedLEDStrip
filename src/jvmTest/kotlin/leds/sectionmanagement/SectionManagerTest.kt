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

package animatedledstrip.test.leds.sectionmanagement

import animatedledstrip.leds.emulation.createNewEmulatedStrip
import animatedledstrip.leds.sectionmanagement.LEDStripSectionManager
import animatedledstrip.leds.sectionmanagement.Section
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs

class SectionManagerTest : StringSpec(
    {
        val ledStrip = createNewEmulatedStrip(50)

        afterSpec {
            ledStrip.renderer.close()
        }

        "create section" {
            val manager = LEDStripSectionManager(ledStrip)

            val sec = manager.createSection("test1", 0, 15)
            manager.getSection("test1") shouldBeSameInstanceAs sec

            val sec2 = Section("test2", 0, 20)
            manager.createSection(sec2)
            manager.getSection("test2").name shouldBe sec2.name
            manager.getSection("test2").startPixel shouldBe sec2.startPixel
            manager.getSection("test2").endPixel shouldBe sec2.endPixel

            shouldThrow<IllegalArgumentException> {
                manager.createSection("test3", -10, 5)
            }
            manager.getSection("test3") shouldBeSameInstanceAs manager.fullStripSection

            shouldThrow<IllegalArgumentException> {
                manager.createSection("test4", 5, 53)
            }
            manager.getSection("test4") shouldBeSameInstanceAs manager.fullStripSection
        }

        "getSubSection" {
            val manager = LEDStripSectionManager(ledStrip)

            shouldThrow<IllegalStateException> {
                manager.getSubSection(5, 20)
            }

            val section = manager.createSection("A", 15, 40)
            val sSec = section.getSubSection(5, 10)
            sSec.name shouldBe "A:5:10"
            sSec.physicalStart shouldBe 20
            sSec.startPixel shouldBe 5
            sSec.endPixel shouldBe 10

            val sSec2 = section.getSubSection(5, 10)
            sSec2 shouldBeSameInstanceAs sSec

            val sSec3 = section.getSubSection(5, 11)
            sSec3 shouldNotBeSameInstanceAs sSec

            shouldThrow<IllegalArgumentException> {
                section.getSubSection(-5, 10)
            }

            shouldThrow<IllegalArgumentException> {
                section.getSubSection(5, 60)
            }
        }
    }
)
