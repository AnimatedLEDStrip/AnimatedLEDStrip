package animatedledstrip.animationutils

import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

/**
 * Class for tracking a currently running animation.
 *
 * @property params An `AnimationData` instance with the properties of the animation
 * @property job The `Job` that is running the animation
 */
data class RunningAnimation(
    val params: RunningAnimationParams,
    val job: Job,
) {
    /**
     * Cancel the coroutine running the animation
     * (in other words, end the animation after its current iteration is complete).
     */
    internal fun cancel(message: String, cause: Throwable? = null) = job.cancel(message, cause)
}
