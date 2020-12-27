package animatedledstrip.leds.animationmanagement

import animatedledstrip.leds.sectionmanagement.SectionManager
import kotlinx.coroutines.CoroutineScope

interface AnimationManager {

    val runningAnimations: RunningAnimationMap

    val animationScope: CoroutineScope

    val sectionManager: SectionManager

//    fun startAnimation(params: AnimationToRunParams, animId: String? = null): RunningAnimation {
//        val id = animId ?: (randomDouble() * 100000000).toInt().toString()
//        params.id = id
//
//        val section = sectionManager.getSection(params.section)
//
//        val runningAnim = RunningAnimation(params.prepare(section),
//                                           animationScope,
//                                           section,
//                                           this)
//        runningAnimations[id] = runningAnim
//        return runningAnim
//    }

//    fun endAnimation(id: String) {
//        runningAnimations[id]?.endAnimation()
//        ?: run {
//            logger.w { "Animation $id is not running" }
//            runningAnimations.remove(id)
//            return
//        }
//    }

//    fun endAnimation(endAnimation: EndAnimation?) {
//        if (endAnimation == null) return
//        else endAnimation(endAnimation.id)
//    }

//    fun runParallel(
//        animation: AnimationToRunParams,
//        section: SectionManager = sectionManager,
//        runCount: Int = 1,
//    ): RunningAnimation {
//        val params = animation.copy(runCount = runCount, section = section.name).prepare(section)
//
//        return RunningAnimation(
//            params,
//            animationScope,
//            section,
//            this,
//        )
//    }

//    suspend fun runSequential(
//        animation: AnimationToRunParams,
//        section: SectionManager = sectionManager,
//        runCount: Int = 1,
//    ) {
//        val params = animation.copy(runCount = runCount, section = section.name).prepare(section)
//
//        RunningAnimation(
//            params,
//            animationScope,
//            section,
//            this,
//        ).join()
//    }
}
