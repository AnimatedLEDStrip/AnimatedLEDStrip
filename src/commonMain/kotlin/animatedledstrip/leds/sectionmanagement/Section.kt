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

import animatedledstrip.communication.SendableData
import animatedledstrip.leds.stripmanagement.LEDStrip
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Represents a section of the full strip and is used by running animations
 * when they need information about the strip or section or when they need
 * to start a subanimation in a subsection
 */
@Serializable
@SerialName("Section")
class Section(
    override val name: String = "",
    override val pixels: List<Int> = listOf(),
    val parentSectionName: String = "",
) : SectionManager, SendableData {

    @Transient
    override val numLEDs: Int = pixels.size

    @Transient
    override val pixelIndices: List<Int> = IntRange(0, numLEDs - 1).toList()

    @Suppress("JoinDeclarationAndAssignment")
    @Transient
    override lateinit var stripManager: LEDStrip

    @Suppress("JoinDeclarationAndAssignment")
    @Transient
    lateinit var parentSection: SectionManager

    constructor(
        name: String,
        pixels: List<Int>,
        stripManager: LEDStrip,
        parentSection: SectionManager,
    ) : this(name, pixels, parentSection.name) {
        this.stripManager = stripManager
        this.parentSection = parentSection
    }

    /**
     * Get the appropriate index on the full strip for the specified pixel.
     *
     * During creation, [pixels] is populated with the physical index of the pixel
     * on the strip, meaning we don't have to perform recursive calculations
     * through all parent sections.
     */
    override fun getPhysicalIndex(pixel: Int): Int {
        require(pixel in pixels.indices) { "$pixel not in section (${pixels.indices})" }

        return pixels[pixel]
    }

    @Transient
    override val sections: MutableMap<String, Section> = mutableMapOf()

    @Transient
    override val subSections: MutableMap<Int, Section> = mutableMapOf()

    /**
     * @return the section specified for the animation. If it doesn't exist,
     * return this section.
     */
    override fun getSection(sectionName: String): Section =
        sections.getOrElse(sectionName) { this }
}
