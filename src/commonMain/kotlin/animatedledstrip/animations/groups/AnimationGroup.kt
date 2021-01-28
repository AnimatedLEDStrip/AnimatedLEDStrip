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

package animatedledstrip.animations.groups

import animatedledstrip.animations.Animation
import animatedledstrip.communication.SendableData
import animatedledstrip.leds.animationmanagement.*
import animatedledstrip.leds.sectionmanagement.Section
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("AnimationGroup")
class AnimationGroup(
    override val info: AnimationInfo,
    val groupType: GroupType,
    val animationList: List<String>,
) : Animation(), SendableData {

    override suspend fun runAnimation(
        leds: AnimationManager,
        params: RunningAnimationParams,
        scope: CoroutineScope,
    ) {
        val nextAnimIndex = when (groupType) {
            GroupType.ORDERED -> params.extraData.getOrPut("nextAnimIndex") { 0 } as Int
            GroupType.RANDOMIZED -> animationList.indices.random()
        }
        val nextAnim = (leds.sectionManager as Section).findAnimation(animationList[nextAnimIndex])
        val animDuration =
            params.intParams.getValue("animationDuration-${nextAnim.info.name} ($nextAnimIndex)").toLong()
        val postAnimationDelay =
            params.intParams.getValue("postAnimationDelay-${nextAnim.info.name} ($nextAnimIndex)").toLong()

        leds.apply {
            if (animDuration < 0)
                runSequential(SubAnimationToRunParams(prepareAnimationToRunParams(params,
                                                                                  nextAnim.info,
                                                                                  nextAnimIndex),
                                                      scope,
                                                      sectionManager))
            else
                scope.launch {
                    withTimeoutOrNull(animDuration) {
                        runSequential(SubAnimationToRunParams(prepareAnimationToRunParams(params,
                                                                                          nextAnim.info,
                                                                                          nextAnimIndex),
                                                              this,
                                                              sectionManager))
                    }
                }.join()
        }

        delay(postAnimationDelay)

        if (groupType == GroupType.ORDERED) params.extraData["nextAnimIndex"] =
            if (nextAnimIndex == animationList.lastIndex) 0 else nextAnimIndex + 1

    }

    @Serializable
    @SerialName("NewAnimationGroupInfo")
    data class NewAnimationGroupInfo(
        val groupType: GroupType,
        val groupInfo: AnimationInfo,
        val animationList: List<String>,
    ) : SendableData
}
