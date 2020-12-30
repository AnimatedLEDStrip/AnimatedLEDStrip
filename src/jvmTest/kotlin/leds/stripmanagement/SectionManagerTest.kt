package animatedledstrip.test.leds.stripmanagement

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
