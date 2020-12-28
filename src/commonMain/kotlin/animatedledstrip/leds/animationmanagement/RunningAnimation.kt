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

package animatedledstrip.leds.animationmanagement

import animatedledstrip.leds.sectionmanagement.SectionManager
import animatedledstrip.utils.logger
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
) : AnimationManager {

    /**
     * Tracks the currently running subanimations managed by this instance
     */
    override val runningAnimations: RunningAnimationMap = RunningAnimationMap()

    /**
     * Join the `RunningAnimation`'s job
     */
    suspend fun join(): Unit = job.join()

    /**
     * End the animation
     */
    fun endAnimation(): Unit = job.cancel("End of Animation")

    /**
     * The `Job` running the animation
     */
    val job: Job = animationScope.launch {
        if (animationScope == GlobalScope) {
            sectionManager.stripManager.startAnimationCallback?.invoke(params)
        }

        logger.v { "Starting $params" }

        var runs = 0
        while (isActive && (params.runCount == -1 || runs < params.runCount)) {
            params.animation.runAnimation(leds = this@RunningAnimation,
                                          params = params,
                                          this)
            runs++
        }
        if (animationScope == GlobalScope) {
            sectionManager.stripManager.endAnimationCallback?.invoke(params)
            parentManager.runningAnimations.remove(params.id)
        }
    }
}
