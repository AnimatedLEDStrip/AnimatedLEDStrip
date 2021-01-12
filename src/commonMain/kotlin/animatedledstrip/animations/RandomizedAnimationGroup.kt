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
import animatedledstrip.leds.animationmanagement.*
import animatedledstrip.leds.locationmanagement.Location
import animatedledstrip.leds.sectionmanagement.Section
import animatedledstrip.utils.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("RandomizedAnimationGroup")
class RandomizedAnimationGroup(
    val groupInfo: AnimationInfo,
    val animationList: List<String>,
) : Animation(), SendableData {
    override val info: AnimationInfo

    init {
        val intParams: MutableList<AnimationParameter<Int>> = mutableListOf()
        val doubleParams: MutableList<AnimationParameter<Double>> = mutableListOf()
        val stringParams: MutableList<AnimationParameter<String>> = mutableListOf()
        val locationParams: MutableList<AnimationParameter<Location>> = mutableListOf()
        val distanceParams: MutableList<AnimationParameter<Distance>> = mutableListOf()
        val rotationParams: MutableList<AnimationParameter<Rotation>> = mutableListOf()

        intParams.add(AnimationParameter("interAnimationDelay-Group",
                                         "Delay between animations in the group",
                                         1000))
        animationList.forEach { n ->
            findAnimationOrNull(n)?.apply {
                this.info.intParams.forEach {
                    intParams.add(AnimationParameter("${it.name}-${this.info.name}", it.description, it.default))
                }
                this.info.doubleParams.forEach {
                    doubleParams.add(AnimationParameter("${it.name}-${this.info.name}", it.description, it.default))
                }
                this.info.stringParams.forEach {
                    stringParams.add(AnimationParameter("${it.name}-${this.info.name}", it.description, it.default))
                }
                this.info.locationParams.forEach {
                    locationParams.add(AnimationParameter("${it.name}-${this.info.name}", it.description, it.default))
                }
                this.info.distanceParams.forEach {
                    distanceParams.add(AnimationParameter("${it.name}-${this.info.name}", it.description, it.default))
                }
                this.info.rotationParams.forEach {
                    rotationParams.add(AnimationParameter("${it.name}-${this.info.name}", it.description, it.default))
                }
            } ?: Logger.w { "Animation $n not found, skipping" }
        }

        info = AnimationInfo(groupInfo.name,
                             groupInfo.abbr,
                             groupInfo.description,
                             groupInfo.runCountDefault,
                             groupInfo.minimumColors,
                             groupInfo.unlimitedColors,
                             groupInfo.dimensionality,
                             groupInfo.directional,
                             intParams,
                             doubleParams,
                             stringParams,
                             locationParams,
                             distanceParams,
                             rotationParams)
    }

    override suspend fun runAnimation(
        leds: AnimationManager,
        params: RunningAnimationParams,
        scope: CoroutineScope,
    ): Unit =
        animation(leds, params)

    suspend fun animation(leds: AnimationManager, params: RunningAnimationParams) {
        val interAnimationDelayGroup = params.intParams.getValue("interAnimationDelay-Group").toLong()
        val nextAnim = (leds.sectionManager as Section).findAnimation(animationList.random())

        leds.apply {
            runSequential(AnimationToRunParams(
                animation = nextAnim.info.name,
                colors = params.colors.toMutableList(),
                id = params.id,
                section = params.section,
                runCount = 1,
                direction = params.direction,
                intParams = params.intParams.filter { it.key.endsWith(nextAnim.info.name) }
                    .map { it.key.removeSuffix("-${nextAnim.info.name}") to it.value }.toMap()
                    .toMutableMap(),
                doubleParams = params.doubleParams.filter { it.key.endsWith(nextAnim.info.name) }
                    .map { it.key.removeSuffix("-${nextAnim.info.name}") to it.value }.toMap()
                    .toMutableMap(),
                stringParams = params.stringParams.filter { it.key.endsWith(nextAnim.info.name) }
                    .map { it.key.removeSuffix("-${nextAnim.info.name}") to it.value }.toMap()
                    .toMutableMap(),
                locationParams = params.locationParams.filter { it.key.endsWith(nextAnim.info.name) }
                    .map { it.key.removeSuffix("-${nextAnim.info.name}") to it.value }.toMap()
                    .toMutableMap(),
                distanceParams = params.distanceParams.filter { it.key.endsWith(nextAnim.info.name) }
                    .map { it.key.removeSuffix("-${nextAnim.info.name}") to it.value }.toMap()
                    .toMutableMap(),
                equationParams = params.equationParams.filter { it.key.endsWith(nextAnim.info.name) }
                    .map { it.key.removeSuffix("-${nextAnim.info.name}") to it.value }.toMap()
                    .toMutableMap(),
            ))
        }

        delay(interAnimationDelayGroup)
    }
}
