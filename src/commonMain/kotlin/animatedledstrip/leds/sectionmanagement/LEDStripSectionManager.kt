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
 * Manages sections of the full strip.
 *
 * A section can run full animations while subsections are used to run
 * subanimations.
 */
class LEDStripSectionManager(override val stripManager: LEDStrip) : SectionManager {
    override val sections: MutableMap<String, Section> = mutableMapOf()
    override val subSections: MutableMap<Int, Section>
        get() = error("LEDStripSectionManager should not be running any animations, and thus should not have any subsections. " +
                      "Animations on the full strip should be run on the Section identified with an empty string.")

    /**
     * Returns the pixel specified. This function is usually used by sections
     * whose first pixel is not pixel 0 on the parent section.
     */
    override fun getPhysicalIndex(pixel: Int): Int = pixel

    override val name: String = ""
    override val numLEDs: Int = stripManager.numLEDs
    override val pixels: List<Int> = IntRange(0, numLEDs - 1).toList()

    /**
     * The section that will actually run animations on the full strip
     */
    internal val fullStripSection: Section = Section("", (0 until numLEDs).toList(), stripManager, this)

    init {
        sections[""] = fullStripSection
        sections["fullStrip"] = fullStripSection
    }

    /**
     * @return The section specified for the animation. If it doesn't exist,
     * return the section representing the full strip
     */
    override fun getSection(sectionName: String): Section =
        sections.getOrElse(sectionName) { fullStripSection }
}
