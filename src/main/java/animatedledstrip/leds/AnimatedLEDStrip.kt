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
import animatedledstrip.utils.SendableData
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import kotlinx.coroutines.*
import org.pmw.tinylog.Logger
import java.lang.Math.random

/**
 * A subclass of [LEDStrip] that manages the running of animations.
 *
 * @param stripInfo Information about this strip, such as number of
 * LEDs, etc. See [StripInfo].
 */
abstract class AnimatedLEDStrip(
    stripInfo: StripInfo,
) : LEDStrip(stripInfo) {

    /* Thread pools */

    /**
     * Multiplier used when calculating thread pool sizes
     */
    private val threadCount: Int = stripInfo.threadCount

    /**
     * A pool of threads used to run animations.
     * (Animations spawned by other animations use the [parallelAnimationThreadPool])
     */
    @Suppress("EXPERIMENTAL_API_USAGE")
    val animationThreadPool =
        newFixedThreadPoolContext(threadCount, "Animation Pool")

    /**
     * A pool of threads to be used for animations that spawn new sub-threads
     * (with the exception of sparkle-type animations, those use the
     * [sparkleThreadPool]).
     */
    @Suppress("EXPERIMENTAL_API_USAGE")
    val parallelAnimationThreadPool =
        newFixedThreadPoolContext(3 * threadCount, "Parallel Animation Pool")

    /**
     * A pool of threads to be used for sparkle-type animations due to the
     * number of threads a concurrent sparkle animation uses.
     */
    @Suppress("EXPERIMENTAL_API_USAGE")
    val sparkleThreadPool =
        newFixedThreadPoolContext(stripInfo.numLEDs + 1, "Sparkle Pool")


    /* Track running animations */

    /**
     * Class for tracking a currently running animation.
     *
     * @property data An `AnimationData` instance with the properties of the animation
     * @property job The `Job` that is running the animation
     */
    data class RunningAnimation(
        val data: AnimationData,
        val job: Job,
    ) {
        /**
         * Cancel the coroutine running the animation
         * (in other words, end the animation after its current iteration is complete).
         */
        internal fun cancel(message: String, cause: Throwable? = null) = job.cancel(message, cause)
    }

    /**
     * Map containing all `RunningAnimation` instances.
     */
    val runningAnimations = RunningAnimationMap()


    /* Start and end animations */

    /**
     * Start an animation. Determines the section based on the `section` parameter in `animation`.
     * See [Section.startAnimation].
     */
    fun startAnimation(animation: AnimationData, animId: String? = null) =
        getSection(animation.section).startAnimation(animation, animId)

    /**
     * End animation by ID.
     */
    fun endAnimation(id: String) {
        runningAnimations[id]
            ?.endAnimation()
        ?: run {
            Logger.warn("Animation $id is not running")
            runningAnimations.remove(id)
            return
        }
    }

    /**
     * End animation using `EndAnimation` instance.
     */
    fun endAnimation(animation: EndAnimation?) {
        if (animation == null) return
        endAnimation(animation.id)
    }


    /* Callbacks */

    /**
     * Callback run before the first iteration of the animation.
     */
    var startAnimationCallback: ((AnimationData) -> Any?)? = null

    /**
     * Callback run after the last iteration of the animation (but before the
     * animation is removed from `runningAnimations`).
     */
    var endAnimationCallback: ((AnimationData) -> Any?)? = null

    /**
     * Callback to run when a new section is created.
     */
    var newSectionCallback: ((Section) -> Any?)? = null

    /* Strip Sections */

    /**
     * A map containing all the sections associated with this LED strip.
     */
    val sections = mutableMapOf<String, Section>()

    /**
     * The section that represents the full strip.
     */
    val wholeStrip = createSection("", 0, stripInfo.numLEDs - 1)

    /**
     * Create a new named section.
     */
    fun createSection(name: String, startPixel: Int, endPixel: Int): Section {
        val newSection = Section(name, startPixel, endPixel)
        sections[name] = newSection
        newSectionCallback?.invoke(newSection)
        return newSection
    }

    fun createSection(section: Section): Section = createSection(section.name, section.startPixel, section.endPixel)

    /**
     * Get a section by its name.
     *
     * Defaults to the whole strip if the section cannot be found.
     */
    fun getSection(sectionName: String): Section =
        sections.getOrElse(sectionName) {
            Logger.warn("Could not find section $sectionName, defaulting to whole strip")
            wholeStrip
        }


    /**
     * Clears all pixels in the strip.
     * See [Section.clear].
     */
    fun clear() = wholeStrip.clear()


    /**
     * Represents a section of the strip.
     *
     * @property startPixel The first pixel in the section (relative to the parent section)
     * @property endPixel The last pixel in the section, inclusive (relative to the parent section).
     * @param parentSection The parent section of this section. A null parentSection implies that the parent
     *   is the whole strip.
     */
    inner class Section(
        val name: String,
        val startPixel: Int,
        val endPixel: Int,
        parentSection: Section? = null,
    ) : SendableData {

        override val prefix = sectionPrefix

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

        /* Utility values */

        /**
         * Allow external functions to access the strip associated with this instance directly.
         */
        val ledStrip: AnimatedLEDStrip
            get() = this@AnimatedLEDStrip

        /**
         * The start of this section on the physical LED strip.
         */
        val physicalStart: Int = startPixel + (parentSection?.startPixel ?: 0)

        /**
         * Get the actual index for a pixel on the physical strip.
         */
        private fun getPhysicalIndex(pixel: Int): Int = pixel + physicalStart

        /**
         * See [LEDStrip.prolongedColors].
         */
        val prolongedColors: MutableList<Long>
            get() = ledStrip.prolongedColors

        /**
         * See [AnimatedLEDStrip.parallelAnimationThreadPool].
         */
        val parallelAnimationThreadPool: ExecutorCoroutineDispatcher
            get() = ledStrip.parallelAnimationThreadPool

        /**
         * See [AnimatedLEDStrip.sparkleThreadPool].
         */
        val sparkleThreadPool: ExecutorCoroutineDispatcher
            get() = ledStrip.sparkleThreadPool

        /**
         * Valid indices for this section.
         */
        val indices = IntRange(0, endPixel - startPixel).toList()

        /**
         * The number of LEDs in this section.
         */
        val numLEDs = endPixel - startPixel + 1


        /**
         * A map of all subsections of this section.
         */
        private val subSections = mutableMapOf<Pair<Int, Int>, Section>()

        /**
         * Get a subsection of this section from the `subSections` map.
         * Creates the subsection if it doesn't exist.
         */
        fun getSubSection(startPixel: Int, endPixel: Int): Section =
            subSections.getOrPut(Pair(startPixel, endPixel)) {
                Section(
                    "$name:$startPixel:$endPixel",
                    startPixel,
                    endPixel,
                    this,
                )
            }

        /**
         * See [AnimatedLEDStrip.getSection].
         */
        fun getSection(sectionName: String): Section = this@AnimatedLEDStrip.getSection(sectionName)

        /**
         * Start a new animation.
         *
         * @param animId Optional `String` parameter for setting the ID of the animation.
         * If not set, ID will be set to a random number between 0 and 100000000.
         */
        fun startAnimation(animation: AnimationData, animId: String? = null): RunningAnimation? {
            val id = animId ?: (random() * 100000000).toInt().toString()
            animation.id = id
            val job = run(animation)
            Logger.trace(job)
            if (job != null) {
                runningAnimations[id] = RunningAnimation(animation, job)
            }
            // Will return null if job was null because runningAnimations[id] would not have been set
            return runningAnimations[id]
        }

        /* Run animations */

        /**
         * Run an animation.
         *
         * @param threadPool Optionally specify the thread pool to run the new
         * animation in
         * @param scope Optionally specify the parent `CoroutineScope` for the
         * coroutine that will run the animation
         */
        internal fun run(
            data: AnimationData,
            threadPool: ExecutorCoroutineDispatcher = animationThreadPool,
            scope: CoroutineScope = GlobalScope,
            subAnimation: Boolean = false,
        ): Job? {
            val definedAnimation = findAnimationOrNull(data.animation) ?: run {
                Logger.warn("Animation ${data.animation} not found")
                Logger.warn("Possible animations: ${definedAnimations.map { it.value.info.name }}")
                return null
            }

            data.prepare(this)
            Logger.trace("Starting $data")

            return scope.launch(threadPool) {
                if (!subAnimation) startAnimationCallback?.invoke(data)

                val isContinuous = data.continuous ?: definedAnimation.info.repetitive
                do {
                    definedAnimation.runAnimation(leds = this@Section, data = data, scope = this)
                } while (isActive && isContinuous)
                if (!subAnimation) {
                    endAnimationCallback?.invoke(data)
                    runningAnimations.remove(data.id)
                }
            }
        }


        /**
         * Start an animation in a child coroutine.
         *
         * Note: Animation is not run continuously unless if [continuous] is `true`
         * (even if the AnimationData instance has `continuous` as true).
         *
         * @param scope The parent `CoroutineScope` for the coroutine that will
         * run the animation
         * @param pool The pool of threads to start the animation in
         * @return The `Job` associated with the new coroutine
         */
        fun runParallel(
            animation: AnimationData,
            scope: CoroutineScope,
            section: Section = this,
            pool: ExecutorCoroutineDispatcher = parallelAnimationThreadPool,
            continuous: Boolean = false,
        ): Job? {
            return section.run(
                animation.copy(continuous = continuous),
                threadPool = pool,
                scope = scope,
                subAnimation = true,
            )
        }

        /**
         * Start an animation and wait for it to complete.
         *
         * Note: Animation is not run continuously unless if [continuous] is `true`
         * (even if the AnimationData instance has `continuous` as true).
         */
        fun runSequential(
            animation: AnimationData,
            section: Section = this,
            continuous: Boolean = false,
        ) = runBlocking {
            val job = section.run(
                animation.copy(continuous = continuous),
                threadPool = parallelAnimationThreadPool,
                scope = this,
                subAnimation = true,
            )
            job?.join()
            Unit
        }


        /* Set pixel */

        /**
         * Set the temporary color of a pixel.
         */
        fun setTemporaryPixelColor(pixel: Int, color: ColorContainerInterface) =
            setPixelColor(getPhysicalIndex(pixel), color, prolonged = false)

        /**
         * Set the prolonged color of a pixel.
         */
        fun setProlongedPixelColor(pixel: Int, color: ColorContainerInterface) =
            setPixelColor(getPhysicalIndex(pixel), color, prolonged = true)

        /**
         * Set the temporary color of a pixel.
         */
        fun setTemporaryPixelColor(pixel: Int, color: Long) =
            setPixelColor(getPhysicalIndex(pixel), color, prolonged = false)

        /**
         * Set the prolonged color of a pixel.
         */
        fun setProlongedPixelColor(pixel: Int, color: Long) =
            setPixelColor(getPhysicalIndex(pixel), color, prolonged = true)


        /* Revert/fade pixel */

        /**
         * Revert a pixel to its prolonged color.
         *
         * See [LEDStrip.revertPixel]
         */
        fun revertPixel(pixel: Int) = ledStrip.revertPixel(getPhysicalIndex(pixel))

        /**
         * Fade a pixel to it's prolonged color.
         *
         * See [LEDStrip.fadePixel]
         */
        fun fadePixel(pixel: Int, amountOfOverlay: Int = 25, delay: Int = 30) =
            ledStrip.fadePixel(getPhysicalIndex(pixel), amountOfOverlay, delay)


        /* Set strip color */

        /**
         * Set the color of all pixels in the strip. If `prolonged` is true,
         * set the prolonged color, otherwise set the temporary color.
         */
        private fun setStripColor(color: ColorContainerInterface, prolonged: Boolean) {
            for (i in indices) ledStrip.setPixelColor(getPhysicalIndex(i), color, prolonged)
        }

        /**
         * Set the color of all pixels in the strip. If `prolonged` is true,
         * set the prolonged color, otherwise set the temporary color.
         */
        private fun setStripColor(color: Long, prolonged: Boolean) {
            for (i in indices) ledStrip.setPixelColor(getPhysicalIndex(i), color, prolonged)
        }

        /**
         * Set the temporary color of all pixels in the strip (or section).
         */
        fun setTemporaryStripColor(color: ColorContainerInterface) =
            setStripColor(color, prolonged = false)

        /**
         * Set the prolonged color of all pixels in the strip (or section).
         */
        fun setProlongedStripColor(color: ColorContainerInterface) =
            setStripColor(color, prolonged = true)

        /**
         * Set the temporary color of all pixels in the strip (or section).
         */
        fun setTemporaryStripColor(color: Long) =
            setStripColor(color, prolonged = false)

        /**
         * Set the prolonged color of all pixels in the strip (or section).
         */
        fun setProlongedStripColor(color: Long) =
            setStripColor(color, prolonged = true)

        /**
         * Clear the section (set all pixels to black).
         */
        fun clear() {
            setProlongedStripColor(0)
            setTemporaryStripColor(0)
        }


        /* Get pixel */

        /**
         * Get the temporary color of a pixel.
         */
        fun getTemporaryPixelColor(pixel: Int) = getPixelColor(getPhysicalIndex(pixel), prolonged = false)

        /**
         * Get the prolonged color of a pixel.
         */
        fun getProlongedPixelColor(pixel: Int) = getPixelColor(getPhysicalIndex(pixel), prolonged = true)

        /**
         * Get the temporary colors of all pixels as a `List<Long>`.
         */
        val pixelTemporaryColorList: List<Long>
            get() = ledStrip.pixelTemporaryColorList.slice(getPhysicalIndex(startPixel)..getPhysicalIndex(endPixel))

        /**
         * Get the prolonged colors of all pixels as a `List<Long>`.
         */
        val pixelProlongedColorList: List<Long>
            get() = ledStrip.pixelProlongedColorList.slice(getPhysicalIndex(startPixel)..getPhysicalIndex(endPixel))

    }

    companion object {
        const val sectionPrefix = "SECT"

        object SectionExStrategy : ExclusionStrategy {
            override fun shouldSkipClass(p0: Class<*>?) = false

            override fun shouldSkipField(field: FieldAttributes): Boolean {
                if (field.declaringClass != Section::class.java)
                    return false
                return when (field.name) {
                    "startPixel", "endPixel", "physicalStart", "numLEDs", "name" -> false
                    else -> true
                }
            }
        }
    }

}
