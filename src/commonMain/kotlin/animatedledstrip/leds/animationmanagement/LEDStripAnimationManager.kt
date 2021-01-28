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

import animatedledstrip.animations.*
import animatedledstrip.animations.groups.AnimationGroup
import animatedledstrip.animations.groups.prepareGroupParameters
import animatedledstrip.leds.sectionmanagement.SectionManager
import animatedledstrip.utils.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope

/**
 * Manages animations running on the entire strip and is the parent manager to all animations
 */
class LEDStripAnimationManager(override val sectionManager: SectionManager) : AnimationManager {
    /**
     * Tracks the currently running animations managed by this instance
     */
    override val runningAnimations: MutableMap<String, RunningAnimation> = mutableMapOf()

    /**
     * The `CoroutineScope` all animations managed by this instance will run in
     */
    override val animationScope: CoroutineScope
        get() = GlobalScope

    val supportedAnimations: MutableMap<String, Animation> = mutableMapOf()
    val supportedAnimationsByAbbr: MutableMap<String, Animation> = mutableMapOf()

    init {
        predefinedAnimations.filter {
            (it.info.dimensionality.contains(Dimensionality.ONE_DIMENSIONAL) && sectionManager.stripManager.stripInfo.is1DSupported) ||
            (it.info.dimensionality.contains(Dimensionality.TWO_DIMENSIONAL) && sectionManager.stripManager.stripInfo.is2DSupported) ||
            (it.info.dimensionality.contains(Dimensionality.THREE_DIMENSIONAL) && sectionManager.stripManager.stripInfo.is3DSupported)
        }.forEach { addNewAnimation(it) }

        predefinedGroups.filter {
            (it.groupInfo.dimensionality.contains(Dimensionality.ONE_DIMENSIONAL) && sectionManager.stripManager.stripInfo.is1DSupported) ||
            (it.groupInfo.dimensionality.contains(Dimensionality.TWO_DIMENSIONAL) && sectionManager.stripManager.stripInfo.is2DSupported) ||
            (it.groupInfo.dimensionality.contains(Dimensionality.THREE_DIMENSIONAL) && sectionManager.stripManager.stripInfo.is3DSupported)
        }.forEach { addNewAnimation(prepareGroupAnimation(it)) }
    }

    fun findAnimation(animId: String): Animation =
        this.findAnimationOrNull(animId)!!

    fun findAnimationOrNull(animId: String): Animation? =
        supportedAnimations[prepareAnimIdentifier(animId)]
        ?: supportedAnimationsByAbbr[prepareAnimIdentifier(animId)]

    fun prepareGroupAnimation(anim: AnimationGroup.NewAnimationGroupInfo): AnimationGroup =
        AnimationGroup(
            prepareGroupParameters(this, anim.groupInfo, anim.animationList),
            anim.groupType,
            anim.animationList)

    fun addNewAnimation(anim: Animation) {
        if (supportedAnimations.containsKey(prepareAnimIdentifier(anim.info.name))) {
            Logger.e { "Animation ${anim.info.name} already defined" }
            return
        }
        if (supportedAnimationsByAbbr.containsKey(prepareAnimIdentifier(anim.info.abbr))) {
            Logger.e { "Animation with abbreviation ${anim.info.abbr} already defined" }
            return
        }

        supportedAnimations[prepareAnimIdentifier(anim.info.name)] = anim
        supportedAnimationsByAbbr[prepareAnimIdentifier(anim.info.abbr)] = anim
        Logger.d { "Added animation ${anim.info.name}" }
    }
}
