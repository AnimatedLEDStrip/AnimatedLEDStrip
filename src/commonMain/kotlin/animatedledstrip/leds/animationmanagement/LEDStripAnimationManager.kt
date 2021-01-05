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
import animatedledstrip.animations.Dimensionality
import animatedledstrip.animations.definedAnimations
import animatedledstrip.animations.definedAnimationsByAbbr
import animatedledstrip.leds.sectionmanagement.SectionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope

/**
 * Manages animations running on the entire strip and is the parent manager to all animations
 */
class LEDStripAnimationManager(override val sectionManager: SectionManager) : AnimationManager {
    /**
     * Tracks the currently running animations managed by this instance
     */
    override val runningAnimations: RunningAnimationMap = RunningAnimationMap()

    /**
     * The `CoroutineScope` all animations managed by this instance will run in
     */
    override val animationScope: CoroutineScope = GlobalScope

    val supportedAnimations: MutableMap<String, Animation> =
        definedAnimations.filter {
            (it.value.info.dimensionality.contains(Dimensionality.ONE_DIMENSIONAL) && sectionManager.stripManager.stripInfo.include1D) ||
            (it.value.info.dimensionality.contains(Dimensionality.TWO_DIMENSIONAL) && sectionManager.stripManager.stripInfo.include2D) ||
            (it.value.info.dimensionality.contains(Dimensionality.THREE_DIMENSIONAL) && sectionManager.stripManager.stripInfo.include3D)
        }.toMutableMap()

    val supportedAnimationsByAbbr: MutableMap<String, Animation> =
        definedAnimationsByAbbr.filter {
            (it.value.info.dimensionality.contains(Dimensionality.ONE_DIMENSIONAL) && sectionManager.stripManager.stripInfo.include1D) ||
            (it.value.info.dimensionality.contains(Dimensionality.TWO_DIMENSIONAL) && sectionManager.stripManager.stripInfo.include2D) ||
            (it.value.info.dimensionality.contains(Dimensionality.THREE_DIMENSIONAL) && sectionManager.stripManager.stripInfo.include3D)
        }.toMutableMap()
}
