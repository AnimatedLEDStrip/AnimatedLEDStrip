package animatedledstrip.leds.sectionmanagement

import animatedledstrip.leds.stripmanagement.LEDStrip
import animatedledstrip.utils.SendableData
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.properties.Delegates

@Serializable
class Section private constructor(
    override val name: String = "",
    override val startPixel: Int = 0,
    override val endPixel: Int = 0,
) : SectionManager, SendableData {

    override val numLEDs: Int = endPixel - startPixel + 1

    @Transient
    override lateinit var stripManager: LEDStrip

    @Transient
    private lateinit var parentSection: SectionManager

    var physicalStart by Delegates.notNull<Int>()

    override fun getPhysicalIndex(pixel: Int): Int = pixel + physicalStart

    @Transient
    override val validIndices: List<Int> = IntRange(0, endPixel - startPixel).toList()

    constructor(
        name: String,
        startPixel: Int,
        endPixel: Int,
        stripManager: LEDStrip,
        parentSection: SectionManager,
    ) : this(name, startPixel, endPixel) {
        this.stripManager = stripManager
        this.parentSection = parentSection
        this.physicalStart = startPixel + parentSection.startPixel
    }

    @Transient
    override val sections: MutableMap<String, Section> = mutableMapOf()

    @Transient
    override val subSections: MutableMap<Pair<Int, Int>, Section> = mutableMapOf()

    override fun getSection(sectionName: String): Section =
        sections.getOrElse(sectionName) {
//            Logger.warn("Could not find section $sectionName, defaulting to whole strip")
            this
        }

    override fun toHumanReadableString() =
        """
                Section Info
                  name: $name
                  numLEDs: $numLEDs
                  startPixel: $startPixel
                  endPixel: $endPixel
                  physicalStart: $physicalStart
                End Info
            """.trimIndent()
}
