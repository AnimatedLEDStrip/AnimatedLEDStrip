package animatedledstrip.test.leds.stripmanagement

import animatedledstrip.leds.emulation.createNewEmulatedStrip
import animatedledstrip.leds.sectionmanagement.LEDStripSectionManager
import animatedledstrip.leds.sectionmanagement.Section
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.property.checkAll

class SectionTest : StringSpec(
    {
        val ledStrip = createNewEmulatedStrip(50)

        "construction" {
            val section = Section()

            section.sections.shouldBeEmpty()
            section.subSections.shouldBeEmpty()
            section.name shouldBe ""
            section.startPixel shouldBe 0
            section.endPixel shouldBe 0
            section.numLEDs shouldBe 0
            section.validIndices.shouldHaveSize(1)
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
        }

        "get subsection" {
            val manager = LEDStripSectionManager(ledStrip)

            shouldThrow<IllegalStateException> {
                manager.getSubSection(0, 10)
            }
        }
    }
)
