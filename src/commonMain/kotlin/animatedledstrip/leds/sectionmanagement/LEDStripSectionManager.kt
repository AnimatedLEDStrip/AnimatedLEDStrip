package animatedledstrip.leds.sectionmanagement

import animatedledstrip.leds.stripmanagement.LEDStrip

class LEDStripSectionManager(override val stripManager: LEDStrip) : SectionManager {
    override val sections: MutableMap<String, Section> = mutableMapOf()
    override val subSections: MutableMap<Pair<Int, Int>, Section> = mutableMapOf()

    override fun getPhysicalIndex(pixel: Int): Int = pixel

    override val name: String = ""
    override val startPixel: Int = 0
    override val endPixel: Int = stripManager.numLEDs - 1
    override val numLEDs: Int = stripManager.numLEDs
    override val validIndices: List<Int> = IntRange(0, endPixel).toList()

    init {
        createSection("", 0, endPixel)
    }

    override fun getSection(sectionName: String): Section =
        sections.getOrElse(sectionName) {
//            Logger.warn("Could not find section $sectionName, defaulting to whole strip")
        getSection("")
    }
}
