/*
 *  Copyright (c) 2020 AnimatedLEDStrip
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package animatedledstrip.animationutils

import animatedledstrip.animationutils.predefined.*
import animatedledstrip.utils.removeWhitespace
import org.pmw.tinylog.Logger

const val DEFAULT_DELAY = 50L
const val DEFAULT_SPACING = 3

val definedAnimations = mutableMapOf<String, Animation>()
val definedAnimationsByAbbr = mutableMapOf<String, Animation>()

fun addNewAnimation(anim: Animation) {
    if (definedAnimations.containsKey(prepareAnimIdentifier(anim.info.name))) {
        Logger.error("Animation ${anim.info.name} already defined")
        return
    }
    if (definedAnimationsByAbbr.containsKey(prepareAnimIdentifier(anim.info.abbr))) {
        Logger.error("Animation with abbreviation ${anim.info.abbr} already defined")
        return
    }

    definedAnimations[prepareAnimIdentifier(anim.info.name)] = anim
    definedAnimationsByAbbr[prepareAnimIdentifier(anim.info.abbr)] = anim
}

val predefinedAnimations = listOf(
    alternate,
    bounce,
    bounceToColor,
    bubbleSort,
    catToy,
    catToyToColor,
    color,
    fadeToColor,
    fireworks,
    mergeSortParallel,
    mergeSortSequential,
    meteor,
    multiPixelRun,
    multiPixelRunToColor,
    pixelMarathon,
    pixelRun,
    ripple,
    smoothChase,
    smoothFade,
    sparkle,
    sparkleFade,
    sparkleToColor,
    splat,
    stack,
    stackOverflow,
    wipe,
).apply {
    forEach { addNewAnimation(it) }
}

fun prepareAnimIdentifier(name: String): String =
    name.removeWhitespace()
        .replace("-", "")
        .replace("_", "")
        .replace("(", "")
        .replace(")", "")
        .toLowerCase()

fun findAnimation(animId: String): Animation = findAnimationOrNull(animId)!!

fun findAnimationOrNull(animId: String): Animation? =
    definedAnimations[prepareAnimIdentifier(animId)] ?: definedAnimationsByAbbr[prepareAnimIdentifier(animId)]

val definedAnimationNames: List<String>
    get() = definedAnimations.keys.toList()
