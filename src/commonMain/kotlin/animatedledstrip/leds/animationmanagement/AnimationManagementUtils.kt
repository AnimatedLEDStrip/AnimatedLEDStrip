package animatedledstrip.leds.animationmanagement

import animatedledstrip.leds.sectionmanagement.SectionManager
import animatedledstrip.utils.logger
import animatedledstrip.utils.randomDouble
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll

fun AnimationManager.startAnimation(params: AnimationToRunParams, animId: String? = null): RunningAnimation {
    val id = animId ?: (randomDouble() * 100000000).toInt().toString()
    params.id = id

    val section = sectionManager.getSection(params.section)

    val runningAnim = RunningAnimation(params.prepare(section),
                                       animationScope,
                                       section,
                                       this)
    runningAnimations[id] = runningAnim
    return runningAnim
}

fun AnimationManager.endAnimation(id: String) {
    runningAnimations[id]?.endAnimation()
    ?: run {
        logger.w { "Animation $id is not running" }
        runningAnimations.remove(id)
        return
    }
}

fun AnimationManager.endAnimation(endAnimation: EndAnimation?) {
    if (endAnimation == null) return
    else endAnimation(endAnimation.id)
}

fun AnimationManager.runParallel(
    animation: AnimationToRunParams,
    section: SectionManager = sectionManager,
    runCount: Int = 1,
): RunningAnimation {
    val params = animation.copy(runCount = runCount, section = section.name).prepare(section)

    return RunningAnimation(
        params,
        animationScope,
        section,
        this,
    )
}

suspend fun AnimationManager.runParallelAndJoin(vararg animations: Pair<AnimationToRunParams, SectionManager>) {
    val jobs = mutableListOf<Job>()
    animations.forEach {
        jobs += runParallel(it.first, it.second).job
    }
    jobs.joinAll()
}

suspend fun AnimationManager.runSequential(
    animation: AnimationToRunParams,
    section: SectionManager = sectionManager,
    runCount: Int = 1,
) {
    val params = animation.copy(runCount = runCount, section = section.name).prepare(section)

    RunningAnimation(
        params,
        animationScope,
        section,
        this,
    ).join()
}
