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

package animatedledstrip.leds

import animatedledstrip.animationutils.*
import animatedledstrip.colors.ColorContainerInterface
import animatedledstrip.colors.PreparedColorContainer
import animatedledstrip.utils.SendableData
import kotlinx.coroutines.*

/**
 * A subclass of [LEDStrip] that manages the running of animations.
 *
 * @param stripInfo Information about this strip, such as number of
 * LEDs, etc. See [StripInfo].
 */
expect abstract class AnimatedLEDStrip(
    stripInfo: StripInfo,
) : LEDStrip {

    /**
     * Map containing all `RunningAnimation` instances.
     */
    val runningAnimations: RunningAnimationMap


    /* Start and end animations */

    /**
     * Start an animation. Determines the section based on the `section` parameter in `animation`.
     * See [Section.startAnimation].
     */
    fun startAnimation(animation: AnimationToRunParams, animId: String? = null): RunningAnimation?

    /**
     * End animation by ID.
     */
    fun endAnimation(id: String)

    /**
     * End animation using `EndAnimation` instance.
     */
    fun endAnimation(animation: EndAnimation?)


    /* Callbacks */

    /**
     * Callback run before the first iteration of the animation.
     */
    var startAnimationCallback: ((RunningAnimationParams) -> Any?)?

    /**
     * Callback run after the last iteration of the animation (but before the
     * animation is removed from `runningAnimations`).
     */
    var endAnimationCallback: ((RunningAnimationParams) -> Any?)?

    /**
     * Callback to run when a new section is created.
     */
    var newSectionCallback: ((Section) -> Any?)?

    /* Strip Sections */

    /**
     * A map containing all the sections associated with this LED strip.
     */
    val sections: MutableMap<String, Section>

    /**
     * The section that represents the full strip.
     */
    val wholeStrip: Section

    /**
     * Create a new named section.
     */
    fun createSection(name: String, startPixel: Int, endPixel: Int): Section

    fun createSection(section: Section): Section

    /**
     * Get a section by its name.
     *
     * Defaults to the whole strip if the section cannot be found.
     */
    fun getSection(sectionName: String): Section


    /**
     * Clears all pixels in the strip.
     * See [Section.clear].
     */
    fun clear()


    /**
     * Represents a section of the strip.
     *
     * @property startPixel The first pixel in the section (relative to the parent section)
     * @property endPixel The last pixel in the section, inclusive (relative to the parent section).
     * @param parent The parent section of this section. A null parentSection implies that the parent
     *   is the whole strip.
     */
    inner class Section(
        name: String,
        startPixel: Int,
        endPixel: Int,
    ) : SendableData {

        fun name(): String

        fun startPixel(): Int

        fun endPixel(): Int

        constructor(name: String, startPixel: Int, endPixel: Int, parent: Section?)

        override fun toHumanReadableString(): String

        /* Utility values */

        /**
         * Allow external functions to access the strip associated with this instance directly.
         */
        val ledStrip: AnimatedLEDStrip

        /**
         * The start of this section on the physical LED strip.
         */
        val physicalStart: Int

        /**
         * Get the actual index for a pixel on the physical strip.
         */
//        private fun getPhysicalIndex(pixel: Int): Int = pixel + physicalStart

//        /**
//         * See [LEDStrip.prolongedColors].
//         */
//        val prolongedColors: MutableList<Long>

        /**
         * Valid indices for this section.
         */
        val indices: List<Int>

        val shuffledIndices: List<Int>
//            get() = indices.shuffled()

        /**
         * The number of LEDs in this section.
         */
        val numLEDs: Int

        /**
         * Get a subsection of this section from the `subSections` map.
         * Creates the subsection if it doesn't exist.
         */
        fun getSubSection(startPixel: Int, endPixel: Int): Section

        /**
         * See [AnimatedLEDStrip.getSection].
         */
        fun getSection(sectionName: String): Section

        /**
         * Start a new animation.
         *
         * @param animId Optional `String` parameter for setting the ID of the animation.
         * If not set, ID will be set to a random number between 0 and 100000000.
         */
        fun startAnimation(animation: AnimationToRunParams, animId: String? = null): RunningAnimation?

        /* Run animations */

        /**
         * Run an animation.
         *
         * @param threadPool Optionally specify the thread pool to run the new
         * animation in
         * @param scope Optionally specify the parent `CoroutineScope` for the
         * coroutine that will run the animation
         * @param subAnimation If this animation was started by another animation
         * (rather than a user)
         * @return The `Job` associated with the new coroutine
         */
        internal suspend fun run(
            data: AnimationToRunParams,
            scope: CoroutineScope? = null,
            subAnimation: Boolean = false,
        ): RunningAnimation?


        /**
         * Start an animation in a child coroutine.
         *
         * @param scope The parent `CoroutineScope` for the coroutine that will
         * run the animation
         * @param section The section to run the animation on
         * @param pool The pool of threads to start the animation in
         * @param runCount The number of times to run the animation
         * @return The `Job` associated with the new coroutine
         */
        suspend fun runParallel(
            animation: AnimationToRunParams,
            scope: CoroutineScope,
            section: Section = this,
            runCount: Int = 1,
        ): RunningAnimation?

        /**
         * Start an animation and wait for it to complete.
         *
         * @param section The section to run the animation on
         * @param runCount The number of times to run the animation
         */
        fun runSequential(
            animation: AnimationToRunParams,
            section: Section = this,
            runCount: Int = 1,
        )


        /* Set pixel */

        /**
         * Set the temporary color of a pixel.
         */
        fun setTemporaryPixelColor(pixel: Int, color: PreparedColorContainer)

        /**
         * Set the prolonged color of a pixel.
         */
        fun setProlongedPixelColor(pixel: Int, color: PreparedColorContainer)

        fun setPixelFadeColor(pixel: Int, color: PreparedColorContainer)

        /**
         * Set the temporary color of a pixel.
         */
        fun setTemporaryPixelColor(pixel: Int, color: Int)

        /**
         * Set the prolonged color of a pixel.
         */
        fun setProlongedPixelColor(pixel: Int, color: Int)

        fun setPixelFadeColor(pixel: Int, color: Int)


        /* Revert/fade pixel */

        /**
         * Revert a pixel to its prolonged color.
         *
         * See [LEDStrip.revertPixel]
         */
        fun revertPixel(pixel: Int)

//        /**
//         * Fade a pixel to its prolonged color.
//         *
//         * See [LEDStrip.fadePixel]
//         */
//        fun fadePixel(pixel: Int, amountOfOverlay: Int = 25, delay: Int = 30, timeout: Int = 2000)

        /**
         * Set the temporary color of all pixels in the strip (or section).
         */
        fun setTemporaryStripColor(color: ColorContainerInterface)

        /**
         * Set the prolonged color of all pixels in the strip (or section).
         */
        fun setProlongedStripColor(color: ColorContainerInterface)

        fun setStripFadeColor(color: ColorContainerInterface)

        /**
         * Set the temporary color of all pixels in the strip (or section).
         */
        fun setTemporaryStripColor(color: Int)

        /**
         * Set the prolonged color of all pixels in the strip (or section).
         */
        fun setProlongedStripColor(color: Int)

        fun setStripFadeColor(color: Int)

        /**
         * Clear the section (set all pixels to black).
         */
        fun clear()


        /* Get pixel */

        /**
         * Get the temporary color of a pixel.
         */
        fun getTemporaryPixelColor(pixel: Int): Int

        /**
         * Get the prolonged color of a pixel.
         */
        fun getProlongedPixelColor(pixel: Int): Int

        fun getPixelActualColor(pixel: Int): Int

        /**
         * Get the temporary animatedledstrip.colors of all pixels as a `List<Long>`.
         */
        val pixelTemporaryColorList: List<Int>

        /**
         * Get the prolonged animatedledstrip.colors of all pixels as a `List<Long>`.
         */
        val pixelProlongedColorList: List<Int>

        override fun equals(other: Any?): Boolean

        override fun hashCode(): Int

        override fun toString(): String

    }

}
