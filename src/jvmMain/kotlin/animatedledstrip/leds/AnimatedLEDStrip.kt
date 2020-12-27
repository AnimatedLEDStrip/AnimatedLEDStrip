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

import animatedledstrip.animations.*
import animatedledstrip.colors.ColorContainerInterface
import animatedledstrip.colors.PreparedColorContainer
import animatedledstrip.leds.animationmanagement.*
import animatedledstrip.leds.colormanagement.PixelColorType
import animatedledstrip.leds.stripmanagement.StripInfo
import animatedledstrip.utils.SendableData
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import org.pmw.tinylog.Logger
import java.lang.Math.random

/**
 * A subclass of [OldLEDStrip] that manages the running of animations.
 *
 * @param stripInfo Information about this strip, such as number of
 * LEDs, etc. See [StripInfo].
 */
actual abstract class AnimatedLEDStrip actual constructor(
    stripInfo: StripInfo,
) : OldLEDStrip(stripInfo) {

    /* Track running animations */

    /**
     * Map containing all `RunningAnimation` instances.
     */
    actual val runningAnimations: RunningAnimationMap = RunningAnimationMap()


    /* Start and end animations */

    /**
     * Start an animation. Determines the section based on the `section` parameter in `animation`.
     * See [Section.startAnimation].
     */
    actual fun startAnimation(animation: AnimationToRunParams, animId: String?) =
        getSection(animation.section).startAnimation(animation, animId)

    /**
     * End animation by ID.
     */
    actual fun endAnimation(id: String) {
        runningAnimations[id]?.endAnimation()
        ?: run {
            Logger.warn("Animation $id is not running")
            runningAnimations.remove(id)
            return
        }
    }

    /**
     * End animation using `EndAnimation` instance.
     */
    actual fun endAnimation(animation: EndAnimation?) {
        if (animation == null) return
        endAnimation(animation.id)
    }


    /* Callbacks */

    /**
     * Callback run before the first iteration of the animation.
     */
    actual var startAnimationCallback: ((RunningAnimationParams) -> Any?)? = null

    /**
     * Callback run after the last iteration of the animation (but before the
     * animation is removed from `runningAnimations`).
     */
    actual var endAnimationCallback: ((RunningAnimationParams) -> Any?)? = null

    /**
     * Callback to run when a new section is created.
     */
    actual var newSectionCallback: ((Section) -> Any?)? = null


    /* Strip Sections */

    /**
     * A map containing all the sections associated with this LED strip.
     */
    actual val sections = mutableMapOf<String, Section>()

    /**
     * The section that represents the full strip.
     */
    actual val wholeStrip = createSection("", 0, stripInfo.numLEDs - 1)

    /**
     * Create a new named section.
     */
    actual fun createSection(name: String, startPixel: Int, endPixel: Int): Section {
        val newSection = Section(name, startPixel, endPixel)
        sections[name] = newSection
        newSectionCallback?.invoke(newSection)
        return newSection
    }

    actual fun createSection(section: Section): Section =
        createSection(section.name, section.startPixel, section.endPixel)

    /**
     * Get a section by its name.
     *
     * Defaults to the whole strip if the section cannot be found.
     */
    actual fun getSection(sectionName: String): Section =
        sections.getOrElse(sectionName) {
            Logger.warn("Could not find section $sectionName, defaulting to whole strip")
            wholeStrip
        }


    /**
     * Clears all pixels in the strip.
     * See [Section.clear].
     */
    actual fun clear() = wholeStrip.clear()


    /**
     * Represents a section of the strip.
     *
     * @property startPixel The first pixel in the section (relative to the parent section)
     * @property endPixel The last pixel in the section, inclusive (relative to the parent section).
     * @param parent The parent section of this section. A null parentSection implies that the parent
     *   is the whole strip.
     */
    @Serializable
    actual inner class Section actual constructor(
        val name: String,
        val startPixel: Int,
        val endPixel: Int,
    ) : SendableData {

        actual fun name() = name
        actual fun startPixel() = startPixel
        actual fun endPixel() = endPixel

        private var parentStartPixel: Int = 0

        actual constructor(name: String, startPixel: Int, endPixel: Int, parent: Section?) : this(name,
                                                                                                  startPixel,
                                                                                                  endPixel) {
            parentStartPixel = parent?.startPixel ?: 0
        }

        actual override fun toHumanReadableString() =
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
        actual val ledStrip: AnimatedLEDStrip
            get() = this@AnimatedLEDStrip

        /**
         * The start of this section on the physical LED strip.
         */
        actual val physicalStart: Int = startPixel + parentStartPixel

        /**
         * Get the actual index for a pixel on the physical strip.
         */
        private fun getPhysicalIndex(pixel: Int): Int = pixel + physicalStart

        /**
         * Valid indices for this section.
         */
        actual val validIndices = IntRange(0, endPixel - startPixel).toList()

        actual val shuffledIndices: List<Int>
            get() = validIndices.shuffled()

        /**
         * The number of LEDs in this section.
         */
        actual val numLEDs = endPixel - startPixel + 1


        /**
         * A map of all subsections of this section.
         */
        private val subSections = mutableMapOf<Pair<Int, Int>, Section>()

        /**
         * Get a subsection of this section from the `subSections` map.
         * Creates the subsection if it doesn't exist.
         */
        actual fun getSubSection(startPixel: Int, endPixel: Int): Section =
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
        actual fun getSection(sectionName: String): Section = this@AnimatedLEDStrip.getSection(sectionName)

        /**
         * Start a new animation.
         *
         * @param animId Optional `String` parameter for setting the ID of the animation.
         * If not set, ID will be set to a random number between 0 and 100000000.
         */
        actual fun startAnimation(animation: AnimationToRunParams, animId: String?): RunningAnimation? {
            val id = animId ?: (random() * 100000000).toInt().toString()
            animation.id = id
            val runningAnim = runBlocking { run(animation) }
            Logger.trace(runningAnim)
            if (runningAnim != null) {
                runningAnimations[id] = runningAnim
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
         * @param subAnimation If this animation was started by another animation
         * (rather than a user)
         * @return The `Job` associated with the new coroutine
         */
        internal actual suspend fun run(
            data: AnimationToRunParams,
            scope: CoroutineScope?,
            subAnimation: Boolean,
        ): RunningAnimation? {
//            val definedAnimation = findAnimationOrNull(data.animation) ?: run {
//                Logger.warn("Animation ${data.animation} not found")
//                Logger.warn("Possible animations: ${definedAnimations.map { it.value.info.name }}")
                return null
//            }
//
//            val params = data.prepare(this)
//
//            Logger.trace("Starting $data")
//
//            val animationScope = scope ?: GlobalScope
//
//            return RunningAnimation(
//                params,
//                animationScope.launch {
//                    if (!subAnimation) startAnimationCallback?.invoke(params)
//
//                    var runs = 0
//                    while (isActive && (params.runCount == -1 || runs < params.runCount)) {
//                        definedAnimation.runAnimation(leds = this@Section,
//                                                      params = params,
//                                                      scope = this)
//                        runs++
//                    }
//                    if (!subAnimation) {
//                        endAnimationCallback?.invoke(params)
//                        runningAnimations.remove(params.id)
//                    }
//                })
        }


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
        actual suspend fun runParallel(
            animation: AnimationToRunParams,
            scope: CoroutineScope,
            section: Section,
            runCount: Int,
        ): RunningAnimation? {
            return section.run(
                animation.copy(runCount = runCount, section = section.name),
                scope = scope,
                subAnimation = true,
            )
        }

        /**
         * Start an animation and wait for it to complete.
         *
         * @param section The section to run the animation on
         * @param runCount The number of times to run the animation
         */
        actual fun runSequential(
            animation: AnimationToRunParams,
            section: Section,
            runCount: Int,
        ) = runBlocking {
            val anim = section.run(
                animation.copy(runCount = runCount, section = section.name),
                scope = this,
                subAnimation = true,
            )
            anim?.join()
            Unit
        }


        /* Set pixel */

        /**
         * Set the temporary color of a pixel.
         */
        actual fun setTemporaryPixelColor(pixel: Int, color: PreparedColorContainer) =
            setTemporaryPixelColor(pixel, color[pixel])

        /**
         * Set the prolonged color of a pixel.
         */
        actual fun setProlongedPixelColor(pixel: Int, color: PreparedColorContainer) =
            setProlongedPixelColor(pixel, color[pixel])

        actual fun setPixelFadeColor(pixel: Int, color: PreparedColorContainer) =
            setPixelFadeColor(pixel, color[pixel])

        /**
         * Set the temporary color of a pixel.
         */
        actual fun setTemporaryPixelColor(pixel: Int, color: Int): Unit =
            setPixelColor(getPhysicalIndex(pixel), color, PixelColorType.TEMPORARY)

        /**
         * Set the prolonged color of a pixel.
         */
        actual fun setProlongedPixelColor(pixel: Int, color: Int): Unit =
            setPixelColor(getPhysicalIndex(pixel), color, PixelColorType.PROLONGED)

        actual fun setPixelFadeColor(pixel: Int, color: Int): Unit =
            setPixelColor(getPhysicalIndex(pixel), color, PixelColorType.FADE)

        /* Revert/fade pixel */

        /**
         * Revert a pixel to its prolonged color.
         *
         * See [OldLEDStrip.revertPixel]
         */
        actual fun revertPixel(pixel: Int) = ledStrip.revertPixel(getPhysicalIndex(pixel))


        /* Set strip color */

        /**
         * Set the color of all pixels in the strip. If `prolonged` is true,
         * set the prolonged color, otherwise set the temporary color.
         */
        private fun setStripColor(color: ColorContainerInterface, colorType: PixelColorType) {
            for (i in validIndices) setPixelColor(getPhysicalIndex(i), color, colorType)
        }

        /**
         * Set the color of all pixels in the strip. If `prolonged` is true,
         * set the prolonged color, otherwise set the temporary color.
         */
        private fun setStripColor(color: Int, colorType: PixelColorType) {
            for (i in validIndices) setPixelColor(getPhysicalIndex(i), color, colorType)
        }

        /**
         * Set the temporary color of all pixels in the strip (or section).
         */
        actual fun setTemporaryStripColor(color: ColorContainerInterface) =
            setStripColor(color, PixelColorType.TEMPORARY)

        /**
         * Set the prolonged color of all pixels in the strip (or section).
         */
        actual fun setProlongedStripColor(color: ColorContainerInterface) =
            setStripColor(color, PixelColorType.PROLONGED)

        actual fun setStripFadeColor(color: ColorContainerInterface) =
            setStripColor(color, PixelColorType.FADE)

        /**
         * Set the temporary color of all pixels in the strip (or section).
         */
        actual fun setTemporaryStripColor(color: Int) =
            setStripColor(color, PixelColorType.TEMPORARY)

        /**
         * Set the prolonged color of all pixels in the strip (or section).
         */
        actual fun setProlongedStripColor(color: Int) =
            setStripColor(color, PixelColorType.PROLONGED)

        actual fun setStripFadeColor(color: Int) =
            setStripColor(color, PixelColorType.FADE)

        /**
         * Clear the section (set all pixels to black).
         */
        actual fun clear() {
            setProlongedStripColor(0)
            setTemporaryStripColor(0)
        }


        /* Get pixel */

        /**
         * Get the temporary color of a pixel.
         */
        actual fun getTemporaryPixelColor(pixel: Int) = getPixelColor(getPhysicalIndex(pixel), PixelColorType.TEMPORARY)

        /**
         * Get the prolonged color of a pixel.
         */
        actual fun getProlongedPixelColor(pixel: Int) = getPixelColor(getPhysicalIndex(pixel), PixelColorType.PROLONGED)

        actual fun getPixelActualColor(pixel: Int) = getPixelColor(getPhysicalIndex(pixel), PixelColorType.ACTUAL)

        /**
         * Get the temporary animatedledstrip.colors of all pixels as a `List<Long>`.
         */
        actual val pixelTemporaryColorList: List<Int>
            get() = ledStrip.pixelTemporaryColorList.slice(getPhysicalIndex(startPixel)..getPhysicalIndex(endPixel))

        /**
         * Get the prolonged animatedledstrip.colors of all pixels as a `List<Long>`.
         */
        actual val pixelProlongedColorList: List<Int>
            get() = ledStrip.pixelProlongedColorList.slice(getPhysicalIndex(startPixel)..getPhysicalIndex(endPixel))

        actual override fun equals(other: Any?): Boolean {
            return super.equals(other) ||
                   (other is AnimatedLEDStrip.Section &&
                    name == other.name &&
                    startPixel == other.startPixel &&
                    endPixel == other.endPixel &&
                    physicalStart == other.physicalStart)
        }

        actual override fun hashCode(): Int {
            var result = name.hashCode()
            result = 31 * result + startPixel
            result = 31 * result + endPixel
            result = 31 * result + physicalStart
            return result
        }

        actual override fun toString() =
            "AnimatedLEDStrip.Section(name=$name, numLEDs=$numLEDs, startPixel=$startPixel, " +
            "endPixel=$endPixel, physicalStart=$physicalStart)"

    }

}
