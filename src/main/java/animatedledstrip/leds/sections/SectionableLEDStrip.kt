/*
 *  Copyright (c) 2018-2020 AnimatedLEDStrip
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package animatedledstrip.leds.sections

import animatedledstrip.colors.ColorContainerInterface

/**
 * Adds functions for setting a section of the strip to a color.
 */
interface SectionableLEDStrip {

    /**
     * Set a section to a color.
     *
     * `start` and `end` are inclusive.
     */
    fun setSectionColor(start: Int, end: Int, color: ColorContainerInterface, prolonged: Boolean = false)

    /**
     * Set a section to a color.
     *
     * `start` and `end` are inclusive.
     */
    fun setSectionColor(start: Int, end: Int, color: Long, prolonged: Boolean = false)

    /**
     * Set a section to a color.
     *
     * `range` is inclusive.
     */
    fun setSectionColor(range: IntRange, color: ColorContainerInterface, prolonged: Boolean = false)

    /**
     * Set a section to a color.
     *
     * `range` is inclusive.
     */
    fun setSectionColor(range: IntRange, color: Long, prolonged: Boolean = false)
}