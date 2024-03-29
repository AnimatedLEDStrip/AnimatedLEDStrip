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

package animatedledstrip.animations

import animatedledstrip.animations.groups.AnimationGroup
import animatedledstrip.animations.predefined.*
import animatedledstrip.animations.predefinedgroups.sortingAnimations
import animatedledstrip.leds.animationmanagement.removeWhitespace
import java.util.*

val predefinedAnimations: List<Animation> = listOf(
    alterFade,
    alternate,
    bounce,
    bounceToColor,
    bubbleSort,
    catToy,
    catToyToColor,
    christmas,
    christmasTwinkle,
    color,
    fadeToColor,
    fireworks,
    heapSort,
    insertionSort,
    mergeSortParallel,
    mergeSortSequential,
    meteor,
    pixelMarathon,
    pixelRun,
    planeRun,
    quickSortParallel,
    quickSortSequential,
    ripple,
    runwayLights,
    runwayLightsToColor,
    shellSort,
    smoothChase,
    smoothFade,
    sparkle,
    sparkleFade,
    sparkleToColor,
    splat,
    stack,
    stackOverflow,
    wave,
    wipe,
)

val predefinedGroups: List<AnimationGroup.NewAnimationGroupInfo> = listOf(
    sortingAnimations,
)

fun prepareAnimIdentifier(name: String): String =
    name.removeWhitespace()
        .replace("[-_()]".toRegex(), "")
        .lowercase(Locale.getDefault())
