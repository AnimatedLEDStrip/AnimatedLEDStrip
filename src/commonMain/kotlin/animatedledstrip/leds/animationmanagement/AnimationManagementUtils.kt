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

import animatedledstrip.animations.Animation
import animatedledstrip.animations.groups.AnimationGroup
import animatedledstrip.animations.prepareAnimIdentifier
import animatedledstrip.leds.sectionmanagement.Section
import animatedledstrip.leds.stripmanagement.LEDStrip
import animatedledstrip.utils.Logger
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll


/**
 * Start running a new animation
 *
 * @param params Parameters for the animation
 * @param animId An optional ID for the animation
 * (otherwise it will be a random number in 0..99999999)
 * @return The now-running animation
 */
fun AnimationManager.startAnimation(params: AnimationToRunParams, animId: String? = null): RunningAnimation {
    val id = animId ?: (randomDouble() * 100000000).toInt().toString()
    params.id = id

    val section: Section = sectionManager.getSection(params.section)

    val runningAnim = RunningAnimation(params.prepare(section),
                                       animationScope,
                                       section,
                                       this,
                                       this is LEDStripAnimationManager)
    runningAnimations[id] = runningAnim
    return runningAnim
}

/**
 * End a currently running animation
 *
 * @param id The ID identifying the animation
 */
fun AnimationManager.endAnimation(id: String) {
    runningAnimations[id]?.endAnimation()
    ?: run {
        Logger.w("Animation $id is not running")
        runningAnimations.remove(id)
        return
    }
}

/**
 * End a currently running animation
 */
fun AnimationManager.endAnimation(endAnimation: EndAnimation): Unit =
    endAnimation(endAnimation.id)

/**
 * Run a new subanimation in a child coroutine
 *
 * @param params Parameters for the animation
 * @return The now-running animation
 */
fun AnimationManager.runParallel(params: SubAnimationToRunParams): RunningAnimation {
    val animParams: RunningAnimationParams =
        params.animationParams.copy(runCount = params.runCount,
                                    section = params.section.name)
            .prepare(params.section as Section,
                     sectionManager as Section)

    return RunningAnimation(
        animParams,
        params.scope,
        params.section,
        this,
    )
}

/**
 * Run many new subanimations in child coroutines and wait for all
 * to complete before returning
 *
 * @param animations Parameters for the animations
 */
suspend fun AnimationManager.runParallelAndJoin(vararg animations: SubAnimationToRunParams) {
    val jobs = mutableListOf<Job>()
    animations.forEach {
        jobs += runParallel(it).job
    }
    jobs.joinAll()
}

/**
 * Run a new animation in a child coroutine and wait for it
 * to complete before returning
 *
 * @param params Parameters for the animation
 */
suspend fun AnimationManager.runSequential(params: SubAnimationToRunParams): Unit = runParallel(params).join()

/**
 * Add a new animation to the list of supported animations
 */
fun LEDStrip.addNewAnimation(anim: Animation) = animationManager.addNewAnimation(anim)

/**
 * Add a new animation group to the list of supported animations
 */
fun LEDStrip.addNewGroup(anim: AnimationGroup.NewAnimationGroupInfo) =
    animationManager.addNewGroup(anim)

fun Section.findAnimation(animId: String): Animation =
    this.findAnimationOrNull(animId)!!

fun Section.findAnimationOrNull(animId: String): Animation? =
    this.stripManager.animationManager.supportedAnimations[prepareAnimIdentifier(animId)]
    ?: this.stripManager.animationManager.supportedAnimationsByAbbr[prepareAnimIdentifier(animId)]

fun AnimationToRunParams.endAnimation(): EndAnimation = EndAnimation(this.id)
fun RunningAnimationParams.endAnimation(): EndAnimation = EndAnimation(this.id)

/**
 * Remove whitespace from a `String`
 */
fun String.removeWhitespace(): String = this.replace("\\s".toRegex(), "")
