package animatedledstrip.leds.sectionmanagement

import animatedledstrip.leds.stripmanagement.LEDStrip

interface SectionManager {
    val stripManager: LEDStrip
    val sections: MutableMap<String, Section>
    val subSections: MutableMap<Pair<Int, Int>, Section>

    fun getPhysicalIndex(pixel: Int): Int

    val name: String
    val startPixel: Int
    val endPixel: Int

    val numLEDs: Int
    val validIndices: List<Int>

    fun createSection(name: String, startPixel: Int, endPixel: Int): Section {
        val newSection = Section(name, startPixel, endPixel, stripManager, this)
        sections[name] = newSection
        stripManager.newSectionCallback?.invoke(newSection)
        return newSection
    }

    fun createSection(section: Section): Section = createSection(section.name,
                                                                 section.startPixel,
                                                                 section.endPixel)

    fun getSection(sectionName: String): Section

    fun getSubSection(startPixel: Int, endPixel: Int): Section =
        subSections.getOrPut(Pair(startPixel, endPixel)) {
            Section(
                "$name:$startPixel:$endPixel",
                startPixel,
                endPixel,
                stripManager,
                this,
            )
        }
}