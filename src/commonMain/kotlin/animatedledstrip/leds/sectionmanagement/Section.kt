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
import kotlin.properties.Delegates

/**
 * Represents a section of the full strip and is used by running animations
 * when they need information about the strip or section or when they need
 * to start a subanimation in a subsection
 */
@Serializable
@SerialName("Section")
class Section(
    override val name: String = "",
    override val startPixel: Int = 0,
    override val endPixel: Int = 0,
) : SectionManager, SendableData {

    init {
        require(startPixel <= endPixel) { "startPixel should be less than or equal to endPixel" }
    }

    @Transient
    override val numLEDs: Int = endPixel - startPixel + 1

    @Suppress("JoinDeclarationAndAssignment")
    @Transient
    override lateinit var stripManager: LEDStrip

    @Suppress("JoinDeclarationAndAssignment")
    @Transient
    private lateinit var parentSection: SectionManager

    /**
     * The index on the full strip associated with index 0 in this section
     */
    var physicalStart: Int by Delegates.notNull()

    /**
     * Get the appropriate index on the full strip for the specified pixel
     * by adding the index to the index on the full strip associated with
     * index 0 on this section
     */
    override fun getPhysicalIndex(pixel: Int): Int {
        require(pixel in validIndices) { "$pixel not in indices (${validIndices.first()}..${validIndices.last()})" }

        return pixel + physicalStart
    }

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

    /**
     * @return the section specified for the animation. If it doesn't exist,
     * return this section.
     */
    override fun getSection(sectionName: String): Section =
        sections.getOrElse(sectionName) { this }
}
