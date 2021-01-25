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

import animatedledstrip.leds.animationmanagement.AnimationToRunParams
import animatedledstrip.leds.animationmanagement.LEDStripAnimationManager
import animatedledstrip.leds.animationmanagement.RunningAnimationParams
import animatedledstrip.leds.locationmanagement.Location
import animatedledstrip.utils.Logger

fun prepareGroupParameters(
    manager: LEDStripAnimationManager,
    groupInfo: Animation.AnimationInfo,
    animationList: List<String>,
): Animation.AnimationInfo {
    val intParams: MutableList<AnimationParameter<Int>> = mutableListOf()
    val doubleParams: MutableList<AnimationParameter<Double>> = mutableListOf()
    val stringParams: MutableList<AnimationParameter<String>> = mutableListOf()
    val locationParams: MutableList<AnimationParameter<Location>> = mutableListOf()
    val distanceParams: MutableList<AnimationParameter<Distance>> = mutableListOf()
    val rotationParams: MutableList<AnimationParameter<Rotation>> = mutableListOf()

    var animCount = 0
    animationList.forEach { n ->
        manager.findAnimationOrNull(n)?.apply {
            intParams.add(AnimationParameter("postAnimationDelay-${this.info.name} ($animCount)",
                                             "Delay after this animation before the next animation starts",
                                             1000))
            intParams.add(AnimationParameter("animationDuration-${this.info.name} ($animCount)",
                                             "Maximum duration of this animation before it gets cancelled (negative means no timeout)",
                                             -1))

            this.info.intParams.forEach {
                intParams.add(AnimationParameter("${it.name}-${this.info.name} ($animCount)",
                                                 it.description,
                                                 it.default))
            }
            this.info.doubleParams.forEach {
                doubleParams.add(AnimationParameter("${it.name}-${this.info.name} ($animCount)",
                                                    it.description,
                                                    it.default))
            }
            this.info.stringParams.forEach {
                stringParams.add(AnimationParameter("${it.name}-${this.info.name} ($animCount)",
                                                    it.description,
                                                    it.default))
            }
            this.info.locationParams.forEach {
                locationParams.add(AnimationParameter("${it.name}-${this.info.name} ($animCount)",
                                                      it.description,
                                                      it.default))
            }
            this.info.distanceParams.forEach {
                distanceParams.add(AnimationParameter("${it.name}-${this.info.name} ($animCount)",
                                                      it.description,
                                                      it.default))
            }
            this.info.rotationParams.forEach {
                rotationParams.add(AnimationParameter("${it.name}-${this.info.name} ($animCount)",
                                                      it.description,
                                                      it.default))
            }
            animCount++
        } ?: Logger.w { "Animation $n not found, skipping" }
    }

    return Animation.AnimationInfo(groupInfo.name,
                                   groupInfo.abbr,
                                   groupInfo.description,
                                   groupInfo.runCountDefault,
                                   groupInfo.minimumColors,
                                   groupInfo.unlimitedColors,
                                   groupInfo.dimensionality,
                                   intParams,
                                   doubleParams,
                                   stringParams,
                                   locationParams,
                                   distanceParams,
                                   rotationParams)
}

fun <V> Map<String, V>.filterGroupParams(animName: String, animIndex: Int): MutableMap<String, V> =
    filter { it.key.endsWith("$animName ($animIndex)") }
        .map { it.key.removeSuffix("-$animName ($animIndex)") to it.value }.toMap()
        .toMutableMap()

fun prepareAnimationToRunParams(
    params: RunningAnimationParams,
    animInfo: Animation.AnimationInfo,
    animIndex: Int,
): AnimationToRunParams =
    AnimationToRunParams(
        animation = animInfo.name,
        colors = params.colors.toMutableList(),
        id = params.id,
        section = params.section,
        runCount = 1,
        intParams = params.intParams.filterGroupParams(animInfo.name, animIndex),
        doubleParams = params.doubleParams.filterGroupParams(animInfo.name, animIndex),
        stringParams = params.stringParams.filterGroupParams(animInfo.name, animIndex),
        locationParams = params.locationParams.filterGroupParams(animInfo.name, animIndex),
        distanceParams = params.distanceParams.filterGroupParams(animInfo.name, animIndex),
        rotationParams = params.rotationParams.filterGroupParams(animInfo.name, animIndex),
        equationParams = params.equationParams.filterGroupParams(animInfo.name, animIndex),
    )
