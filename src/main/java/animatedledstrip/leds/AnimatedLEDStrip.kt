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
import kotlinx.coroutines.*
import org.pmw.tinylog.Logger
import java.lang.Math.random
import javax.script.ScriptException

/**
 * A subclass of [LEDStrip] adding animations.
 *
 * @param stripInfo Information about this strip, such as number of
 * LEDs, etc.
 */
abstract class AnimatedLEDStrip(
    stripInfo: StripInfo
) : LEDStrip(stripInfo) {

    val wholeStrip = Section(0, stripInfo.numLEDs - 1, stripInfo.numLEDs - 1)

    /* Load predefined animations if they haven't been already */
    init {
        loadPredefinedAnimations(this::class.java.classLoader)
    }

    /* Thread pools */

    /**
     * Multiplier used when calculating thread pool sizes
     */
    private val threadCount: Int = stripInfo.threadCount ?: 100

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
     * Class for tracking a currently running animation
     *
     * @property data An `AnimationData` instance with the properties of the animation
     * @property id The string ID representing the animation
     * @property job The `Job` that is running the animation
     */
    data class RunningAnimation(
        val data: AnimationData,
        val id: String,
        val job: Job
    ) {
        /**
         * Cancel the coroutine running the animation
         * (in other words, end the animation after its current iteration is complete)
         */
        internal fun cancel(message: String, cause: Throwable? = null) = job.cancel(message, cause)
    }

    /**
     * Map containing all `RunningAnimation` instances
     */
    val runningAnimations = RunningAnimationMap()

    /* Add and end animations */

    fun startAnimation(animation: AnimationData, animId: String? = null) =
        wholeStrip.startAnimation(animation, animId)

    /**
     * End animation by ID
     */
    fun endAnimation(id: String) {
        runningAnimations[id]?.endAnimation()
            ?: run {
                Logger.warn { "Animation $id not running" }
                runningAnimations.remove(id)
                return
            }
    }

    /**
     * End animation using `EndAnimation` instance
     */
    fun endAnimation(animation: EndAnimation?) {
        endAnimation(animation?.id ?: return)
    }


    /* Callbacks */

    /**
     * Callback run before the first iteration of the animation
     */
    var startAnimationCallback: ((AnimationData) -> Any?)? = null

    /**
     * Callback run after the last iteration of the animation (but before the
     * animation is remove from `runningAnimations`)
     */
    var endAnimationCallback: ((AnimationData) -> Any?)? = null


    private val stripSections =
        mutableMapOf(Triple(0, stripInfo.numLEDs - 1, stripInfo.numLEDs - 1) to wholeStrip)

    fun getSection(startPixel: Int, endPixel: Int, parentSectionNumLEDs: Int): AnimatedLEDStrip.Section =
        stripSections.getOrPut(Triple(startPixel, endPixel, parentSectionNumLEDs)) {
            Section(startPixel, endPixel, parentSectionNumLEDs)
        }

    fun clear() = wholeStrip.clear()


    /* Strip Sections */

    inner class Section(val startPixel: Int, val endPixel: Int, val parentSectionNumLEDs: Int) {
        val ledStrip: AnimatedLEDStrip
            get() = this@AnimatedLEDStrip

        val prolongedColors: MutableList<Long>
            get() = ledStrip.prolongedColors

        val indices = IntRange(0, endPixel - startPixel).toList()

        val numLEDs = endPixel - startPixel + 1

        val parallelAnimationThreadPool: ExecutorCoroutineDispatcher
            get() = ledStrip.parallelAnimationThreadPool

        val sparkleThreadPool: ExecutorCoroutineDispatcher
            get() = ledStrip.sparkleThreadPool

        fun getSection(startPixel: Int, endPixel: Int, parentSectionNumLEDs: Int): AnimatedLEDStrip.Section =
            ledStrip.getSection(startPixel, endPixel, parentSectionNumLEDs)

        /**
         * Start a new animation
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
                runningAnimations[id] = RunningAnimation(animation, id, job)
            }
            // Will return null if job was null because runningAnimations[id] would not have been set
            return runningAnimations[id]
        }

        /* Run animations */

        /**
         * Run an animation
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
            subAnimation: Boolean = false
        ): Job? {
            val definedAnimation = findAnimation(data.animation) ?: run {
                Logger.warn("Animation ${data.animation} not found")
                Logger.warn("Possible animations: ${definedAnimations.map { it.value.info.name }}")
                return null
            }

            data.prepare(this)
            Logger.trace("Starting $data")

            return scope.launch(threadPool) {
                if (!subAnimation) startAnimationCallback?.invoke(data)

                val isContinuous = data.continuous ?: definedAnimation.info.repetitive
                try {
                    do {
                        Logger.trace("Run ${data.id}: $isActive $isContinuous")
                        definedAnimation.code.eval(
                            definedAnimation.animationScriptingEngine.createBindings().apply {
                                put("data", data)
                                put("leds", this@Section)
                                put("scope", this@launch)
                            })
                    } while (isActive && isContinuous)
                } catch (e: ScriptException) {
                    println("Error when running ${definedAnimation.info.name}:")
                    println(e)
                    e.printStackTrace()
                }
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
            continuous: Boolean = false
        ): Job? {
            return section.run(
                animation.copy(continuous = continuous),
                threadPool = pool,
                scope = scope,
                subAnimation = true
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
            continuous: Boolean = false
        ) = runBlocking {
            val job = section.run(
                animation.copy(continuous = continuous),
                threadPool = parallelAnimationThreadPool,
                scope = this,
                subAnimation = true
            )
            job?.join()
            Unit
        }


        /* Set pixel */

        fun setTemporaryPixelColor(pixel: Int, color: ColorContainerInterface) =
            setPixelColor(pixel + startPixel, color, prolonged = false)

        fun setProlongedPixelColor(pixel: Int, color: ColorContainerInterface) =
            setPixelColor(pixel + startPixel, color, prolonged = true)

        fun setTemporaryPixelColor(pixel: Int, color: Long) =
            setPixelColor(pixel + startPixel, color, prolonged = false)

        fun setProlongedPixelColor(pixel: Int, color: Long) =
            setPixelColor(pixel + startPixel, color, prolonged = true)


        /* Revert/fade pixel */

        /**
         * Revert a pixel to its prolonged color. If it is in the middle
         * of a fade, don't revert.
         */
        fun revertPixel(pixel: Int) = ledStrip.revertPixel(pixel + startPixel)

        fun fadePixel(pixel: Int, amountOfOverlay: Int = 25, delay: Int = 30) =
            ledStrip.fadePixel(pixel, amountOfOverlay, delay)


        /* Set strip color */

        /**
         * Set the color of all pixels in the strip. If `prolonged` is true,
         * set the prolonged color, otherwise set the actual color.
         */
        private fun setStripColor(color: ColorContainerInterface, prolonged: Boolean) {
            for (i in indices) ledStrip.setPixelColor(i + startPixel, color, prolonged)
        }

        /**
         * Set the color of all pixels in the strip. If `prolonged` is true,
         * set the prolonged color, otherwise set the actual color.
         */
        private fun setStripColor(color: Long, prolonged: Boolean) {
            for (i in indices) ledStrip.setPixelColor(i + startPixel, color, prolonged)
        }

        fun setTemporaryStripColor(color: ColorContainerInterface) =
            setStripColor(color, prolonged = false)

        fun setProlongedStripColor(color: ColorContainerInterface) =
            setStripColor(color, prolonged = true)

        fun setTemporaryStripColor(color: Long) =
            setStripColor(color, prolonged = false)

        fun setProlongedStripColor(color: Long) =
            setStripColor(color, prolonged = true)

        fun clear() = setProlongedStripColor(0)


        /* Get pixel */

        fun getTemporaryPixelColor(pixel: Int) = getPixelColor(pixel + startPixel, prolonged = false)

        fun getProlongedPixelColor(pixel: Int) = getPixelColor(pixel + startPixel, prolonged = true)

        /**
         * Get the actual colors of all pixels as a `List<Long>`
         */
        val pixelTemporaryColorList: List<Long>
            get() = ledStrip.pixelTemporaryColorList.subList(startPixel, endPixel)

        /**
         * Get the prolonged colors of all pixels as a `List<Long>`
         */
        val pixelProlongedColorList: List<Long>
            get() = ledStrip.pixelProlongedColorList.subList(startPixel, endPixel)
    }
}
