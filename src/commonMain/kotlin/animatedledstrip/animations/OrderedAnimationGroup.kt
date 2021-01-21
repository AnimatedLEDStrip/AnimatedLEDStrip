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

package animatedledstrip.animations

import animatedledstrip.communication.SendableData
import animatedledstrip.leds.animationmanagement.AnimationManager
import animatedledstrip.leds.animationmanagement.RunningAnimationParams
import animatedledstrip.leds.animationmanagement.findAnimation
import animatedledstrip.leds.animationmanagement.runSequential
import animatedledstrip.leds.sectionmanagement.Section
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("OrderedAnimationGroup")
class OrderedAnimationGroup(
    val groupInfo: AnimationInfo,
    val animationList: List<String>,
) : Animation(), SendableData {
    override val info: AnimationInfo = prepareGroupParameters(groupInfo, animationList)

    override suspend fun runAnimation(
        leds: AnimationManager,
        params: RunningAnimationParams,
        scope: CoroutineScope,
    ) {
        val nextAnimIndex = params.extraData.getOrPut("nextAnimIndex") { 0 } as Int
        val nextAnim = (leds.sectionManager as Section).findAnimation(animationList[nextAnimIndex])
        val animDuration =
            params.intParams.getValue("animationDuration-${nextAnim.info.name} ($nextAnimIndex)").toLong()
        val postAnimationDelay =
            params.intParams.getValue("postAnimationDelay-${nextAnim.info.name} ($nextAnimIndex)").toLong()

        leds.apply {
            if (animDuration < 0)
                runSequential(prepareAnimationToRunParams(params, nextAnim.info, nextAnimIndex), scope)
            else
                scope.launch {
                    withTimeoutOrNull(animDuration) {
                        runSequential(prepareAnimationToRunParams(params, nextAnim.info, nextAnimIndex),
                                      scope = this)
                    }
                }.join()
        }

        delay(postAnimationDelay)

        params.extraData["nextAnimIndex"] = if (nextAnimIndex == animationList.lastIndex) 0 else nextAnimIndex + 1
    }
}
