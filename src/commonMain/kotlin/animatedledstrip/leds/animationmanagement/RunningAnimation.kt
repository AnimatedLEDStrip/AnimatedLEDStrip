/*
 * Copyright (c) 2018-2022 AnimatedLEDStrip
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

package animatedledstrip.leds.animationmanagement

import animatedledstrip.leds.sectionmanagement.SectionManager
import animatedledstrip.utils.Logger
import kotlinx.coroutines.*

/**
 * Tracks a currently running animation
 *
 * @property params The parameters specifying how to run the animation
 * @property animationScope The `CoroutineScope` this animation will run in
 * @property sectionManager The section associated with this animation
 * @property parentManager The animation manager that started this animation
 */
data class RunningAnimation(
    val params: RunningAnimationParams,
    override val animationScope: CoroutineScope,
    override val sectionManager: SectionManager,
    val parentManager: AnimationManager,
    val topLevelAnimation: Boolean = false,
    var paused: Boolean = false,
) : AnimationManager {

    /**
     * Tracks the currently running subanimations managed by this instance
     */
    override val runningAnimations: MutableMap<String, RunningAnimation> = mutableMapOf()

    /**
     * Join the `RunningAnimation`'s job
     */
    suspend fun join(): Unit = job.join()

    /**
     * End the animation
     */
    fun endAnimation(): Unit = job.cancel("End of Animation")

    fun pause() {
        paused = true
        sectionManager.stripManager.pauseAnimationCallback?.invoke(params)
    }

    fun resume() {
        paused = false
        sectionManager.stripManager.resumeAnimationCallback?.invoke(params)
    }

    /**
     * The `Job` running the animation
     */
    val job: Job = animationScope.launch {
        if (topLevelAnimation) {
            sectionManager.stripManager.startAnimationCallback?.invoke(params)
        }

        Logger.v("Running Animation: Starting $params")

        try {
            var runs = 0
            while (isActive && (params.runCount == -1 || runs < params.runCount)) {
                if (paused) {
                    delay(10)
                    continue
                }
                params.extraData["completedRuns"] = runs
                params.animation.runAnimation(
                    leds = this@RunningAnimation,
                    params = params,
                    this,
                )
                runs++
            }
        } catch (e: Exception) {
            if (e !is CancellationException)
                Logger.e("Running Animation: Animation ${params.id} errored with\n${e.stackTraceToString()}")
        } finally {
            if (topLevelAnimation) {
                sectionManager.stripManager.endAnimationCallback?.invoke(params)
            }
            parentManager.runningAnimations.remove(params.id)
            Logger.v("Running Animation: Animation ${params.id} complete")
        }
    }
}
