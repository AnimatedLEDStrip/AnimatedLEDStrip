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

package animatedledstrip.leds.sectionmanagement

import animatedledstrip.leds.stripmanagement.LEDStrip

/**
 * Manages sections and subsections of this section.
 *
 * A section can run full animations while subsections are used to run
 * subanimations.
 */
interface SectionManager {
    /**
     * The strip manager this section manager is managing sections for
     */
    val stripManager: LEDStrip

    /**
     * The sections managed by this section manager
     */
    val sections: MutableMap<String, Section>

    /**
     * The subsections managed by this section manager
     */
    val subSections: MutableMap<Int, Section>

    /**
     * Get the appropriate index on the full strip for the specified pixel
     */
    fun getPhysicalIndex(pixel: Int): Int

    /**
     * The string used to identify this section
     */
    val name: String

    /**
     * The number of pixels in this section
     */
    val numLEDs: Int

    /**
     * A list of all pixels included in this section
     */
    val pixels: List<Int>

    /**
     * @return A new section, identified by [name], including [pixels],
     * with this section manager as its parent
     */
    fun createSection(name: String, pixels: List<Int>, parentSectionName: String = ""): Section {
        for (pixel in pixels)
            require(pixel in this.pixels.indices) { "Pixel $pixel not in this section (${this.pixels.indices})" }

        val parentSection = getSection(parentSectionName)

        val newSection = Section(name,
                                 pixels.map { this.pixels[it] }, // Allows us to directly find the physical index
                                 stripManager,
                                 parentSection)
        sections[name] = newSection
        stripManager.newSectionCallback?.invoke(newSection)
        return newSection
    }

    /**
     * @return A new section, identified by [name], starting at [startPixel]
     * and ending at [endPixel], with this section manager as its parent
     */
    fun createSection(name: String, startPixel: Int, endPixel: Int, parentSectionName: String = ""): Section =
        createSection(name, (startPixel..endPixel).toList(), parentSectionName)


    fun createSection(section: Section): Section = createSection(section.name,
                                                                 section.pixels,
                                                                 section.parentSectionName)


    /**
     * Get the section specified by [sectionName]. If it doesn't exist,
     * return this section.
     */
    fun getSection(sectionName: String): Section

    /**
     * @return A subsection for a subanimation running on a part of this section.
     * If the subsection already exists, reuse it, otherwise create a new one.
     */
    fun getSubSection(pixels: List<Int>): Section {
        for (pixel in pixels)
            require(pixel in this.pixels.indices) { "Pixel $pixel must be in parent section (${this.pixels.indices})" }

        return subSections.getOrPut(pixels.hashCode()) {
            Section("$name:${pixels.hashCode()}",
                    pixels,
                    stripManager,
                    this)
        }
    }


    /**
     * @return A subsection for a subanimation running on a part of this section.
     * If the subsection already exists, reuse it, otherwise create a new one.
     */
    fun getSubSection(startPixel: Int, endPixel: Int): Section = getSubSection((startPixel..endPixel).toList())
}
