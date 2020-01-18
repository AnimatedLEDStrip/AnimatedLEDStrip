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

import animatedledstrip.animationutils.Animation
import animatedledstrip.animationutils.AnimationData
import animatedledstrip.animationutils.RunningAnimationMap
import animatedledstrip.animationutils.isContinuous
import kotlinx.coroutines.*
import org.pmw.tinylog.Logger
import java.lang.Math.random

/**
 * A subclass of [LEDStrip] adding animations.
 *
 * @param stripInfo Information about this strip, such as number of
 * LEDs, etc.
 */
abstract class AnimatedLEDStrip(
    stripInfo: StripInfo
) : LEDStrip(stripInfo) {


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
     * number of threads a concurrent sparkle animation uses. This prevents
     * memory leaks caused by the overhead associated with creating new threads.
     */
    @Suppress("EXPERIMENTAL_API_USAGE")
    val sparkleThreadPool =
        newFixedThreadPoolContext(numLEDs + 1, "Sparkle Pool")


    /* Track running animations */

    /**
     * Class for tracking a currently running animation
     *
     * @property animation An `AnimationData` instance with the properties of the animation
     * @property id The string ID representing the animation
     * @property job The `Job` that is running the animation
     */
    data class RunningAnimation(
        val animation: AnimationData,
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


    /* Define custom animations */

    /**
     * Map containing defined custom animations.
     */
    private val customAnimationMap =
        mutableMapOf<String, (AnimationData, CoroutineScope) -> Unit>()

    // TODO: Add addCustomAnimation function


    /* Add and remove/end animations */

    /**
     * Add a new animation
     *
     * @param animId Optional `String` parameter for setting the ID of the animation.
     * If not set, ID will be set to a random number between 0 and 100000000.
     */
    fun addAnimation(animation: AnimationData, animId: String? = null): RunningAnimation? {
        return if (animation.animation == Animation.ENDANIMATION) {
            // Special "Animation" type that the client sends to end an animation
            endAnimation(animation)
            null
        } else {
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
    }

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
     * End animation by ID (by extracting ID from the `AnimationData` instance)
     */
    fun endAnimation(animation: AnimationData?) {
        if (animation == null) return
        endAnimation(animation.id)
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
        animation: AnimationData,
        threadPool: ExecutorCoroutineDispatcher = animationThreadPool,
        scope: CoroutineScope = GlobalScope,
        subAnimation: Boolean = false
    ): Job? {
        animation.prepare(this)
        Logger.trace("Starting $animation")

        val animationFunction: AnimatedLEDStrip.(AnimationData, CoroutineScope) -> Unit = when (animation.animation) {
            Animation.ALTERNATE -> alternate
            Animation.BOUNCE -> bounce
            Animation.BOUNCETOCOLOR -> bounceToColor
            Animation.CATTOY -> catToy
            Animation.CATTOYTOCOLOR -> catToyToColor
            Animation.COLOR -> run {
                setStripColor(animation.pCols[0], prolonged = true)
                null
            }
            Animation.FADETOCOLOR -> fadeToColor
            Animation.FIREWORKS -> fireworks
            Animation.METEOR -> meteor
            Animation.MULTIPIXELRUN -> multiPixelRun
            Animation.MULTIPIXELRUNTOCOLOR -> multiPixelRunToColor
            Animation.PIXELMARATHON -> pixelMarathon
            Animation.PIXELRUN -> pixelRun
            Animation.RIPPLE -> ripple
            Animation.SMOOTHCHASE -> smoothChase
            Animation.SMOOTHFADE -> smoothFade
            Animation.SPARKLE -> sparkle
            Animation.SPARKLEFADE -> sparkleFade
            Animation.SPARKLETOCOLOR -> sparkleToColor
            Animation.SPLAT -> splat
            Animation.STACK -> stack
            Animation.STACKOVERFLOW -> stackOverflow
            Animation.WIPE -> wipe
            Animation.CUSTOMANIMATION, Animation.CUSTOMREPETITIVEANIMATION ->
                customAnimationMap[animation.id] ?: run {
                    Logger.warn("Custom animation ${animation.id} not found")
                    null
                }
            else -> run {
                Logger.warn("Animation ${animation.animation} not supported by AnimatedLEDStrip")
                null
            }
        } ?: return null

        return scope.launch(threadPool) {
            if (!subAnimation) startAnimationCallback?.invoke(animation)

            val isContinuous = animation.isContinuous()
            do {
                Logger.trace("Run ${animation.id}: $isActive $isContinuous")
                animationFunction.invoke(this@AnimatedLEDStrip, animation, this)
            } while (isActive && isContinuous)

            if (!subAnimation) {
                endAnimationCallback?.invoke(animation)
                runningAnimations.remove(animation.id)
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
        pool: ExecutorCoroutineDispatcher = parallelAnimationThreadPool,
        continuous: Boolean = false
    ): Job? {
        return run(
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
    fun runSequential(animation: AnimationData, continuous: Boolean = false) = runBlocking {
        val job = run(
            animation.copy(continuous = continuous),
            threadPool = parallelAnimationThreadPool,
            scope = this,
            subAnimation = true
        )
        job?.join()
        Unit
    }
}
