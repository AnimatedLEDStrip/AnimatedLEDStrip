package animatedledstrip.test.leds.stripmanagement

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

            manager.sections.shouldHaveSize(1)
            manager.sections.shouldContainExactly(mapOf("" to manager.fullStripSection))
            shouldThrow<IllegalStateException> {
                manager.subSections
            }
            manager.name shouldBe ""
            manager.startPixel shouldBe 0
            manager.endPixel shouldBe ledStrip.numLEDs - 1
            manager.endPixel shouldBe 49
            manager.numLEDs shouldBe ledStrip.numLEDs
            manager.numLEDs shouldBe 50
            manager.validIndices.shouldHaveSize(50)
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
            val sec2 = Section("test2", 0, 20)
            manager.createSection(sec2)
            manager.getSection("test2").name shouldBe sec2.name
            manager.getSection("test2").startPixel shouldBe sec2.startPixel
            manager.getSection("test2").endPixel shouldBe sec2.endPixel
        }

        "get subsection" {
            val manager = LEDStripSectionManager(ledStrip)

            shouldThrow<IllegalStateException> {
                manager.getSubSection(0, 10)
            }
        }
    }
)
