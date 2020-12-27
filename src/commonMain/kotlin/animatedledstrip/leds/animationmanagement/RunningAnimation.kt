package animatedledstrip.leds.animationmanagement

import animatedledstrip.leds.sectionmanagement.SectionManager
import animatedledstrip.utils.logger
import kotlinx.coroutines.*

/**
 * Class for tracking a currently running animation.
 *
 * @property params An `AnimationData` instance with the properties of the animation
 * @property job The `Job` that is running the animation
 */
data class RunningAnimation(
    val params: RunningAnimationParams,
    override val animationScope: CoroutineScope,
    override val sectionManager: SectionManager,
    val parentManager: AnimationManager,
) : AnimationManager {

    override val runningAnimations: RunningAnimationMap = RunningAnimationMap()

    /**
     * Join the `RunningAnimation`'s job
     */
    suspend fun join() = job.join()

    /**
     * End the animation
     */
    fun endAnimation() = job.cancel("End of Animation")

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
