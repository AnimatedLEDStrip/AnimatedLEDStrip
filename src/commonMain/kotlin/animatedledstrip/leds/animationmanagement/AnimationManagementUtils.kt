/*
 * Copyright (c) 2018-2021 AnimatedLEDStrip
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
import animatedledstrip.animations.prepareAnimIdentifier
import animatedledstrip.leds.sectionmanagement.Section
import animatedledstrip.leds.sectionmanagement.SectionManager
import animatedledstrip.utils.logger
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
                                       this)
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
        logger.w { "Animation $id is not running" }
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
 * @param animation Parameters for the animation
 * @param section The Section this animation should run on
 * @param runCount How many times the animation should run
 * @return The now-running animation
 */
fun AnimationManager.runParallel(
    animation: AnimationToRunParams,
    section: SectionManager = sectionManager,
    runCount: Int = 1,
): RunningAnimation {
    val params = animation.copy(runCount = runCount, section = section.name).prepare(section as Section,
                                                                                     sectionManager as Section)

    return RunningAnimation(
        params,
        animationScope,
        section,
        this,
    )
}

/**
 * Run many new subanimations in child coroutines and wait for all
 * to complete before returning
 *
 * @param animations Parameters for the animations
 */
suspend fun AnimationManager.runParallelAndJoin(vararg animations: Pair<AnimationToRunParams, SectionManager>) {
    val jobs = mutableListOf<Job>()
    animations.forEach {
        jobs += runParallel(it.first, it.second).job
    }
    jobs.joinAll()
}

/**
 * Run a new animation in a child coroutine and wait for it
 * to complete before returning
 *
 * @param animation Parameters for the animation
 * @param section The Section this animation should run on
 * @param runCount How many times the animation should run
 */
suspend fun AnimationManager.runSequential(
    animation: AnimationToRunParams,
    section: SectionManager = sectionManager,
    runCount: Int = 1,
) {
    val params = animation.copy(runCount = runCount, section = section.name).prepare(section as Section,
                                                                                     sectionManager as Section)

    RunningAnimation(
        params,
        animationScope,
        section,
        this,
    ).join()
}

fun Section.findAnimation(animId: String): Animation =
    this.findAnimationOrNull(animId)!!

fun Section.findAnimationOrNull(animId: String): Animation? =
    this.stripManager.animationManager.supportedAnimations[prepareAnimIdentifier(animId)] ?:
    this.stripManager.animationManager.supportedAnimationsByAbbr[prepareAnimIdentifier(animId)]

fun AnimationToRunParams.endAnimation(): EndAnimation = EndAnimation(this.id)
fun RunningAnimationParams.endAnimation(): EndAnimation = EndAnimation(this.id)

/**
 * Remove whitespace from a `String`
 */
fun String.removeWhitespace(): String = this.replace("\\s".toRegex(), "")
