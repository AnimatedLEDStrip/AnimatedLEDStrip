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
import animatedledstrip.utils.removeSpaces

const val DEFAULT_DELAY = 50L
const val DEFAULT_SPACING = 3

val predefinedAnimations = listOf(
    alternate,
    bounce,
    bounceToColor,
    catToy,
    catToyToColor,
    color,
    fadeToColor,
    fireworks,
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
    wipe
)

val definedAnimations = mutableMapOf<String, Animation>().apply {
    predefinedAnimations.forEach { this[prepareAnimName(it.info.name)] = it }
}

fun prepareAnimName(name: String): String = name.removeSpaces().replace("-", "").toLowerCase()

fun findAnimation(animName: String): Animation? = definedAnimations[prepareAnimName(animName)]

val definedAnimationNames: List<String>
    get() = definedAnimations.keys.toList()

//fun defineNewAnimation(code: String) {
//
//    fun String.toReqLevelOrNull(): ParamUsage? =
//        when (this.toUpperCase()) {
//            "USED" -> ParamUsage.USED
//            "NOTUSED" -> ParamUsage.NOTUSED
//            else -> null
//        }
//
//        val anim = Animation(
//            animInfo,
//            code
//        )
//
//        definedAnimations[prepareAnimName(anim.info.name)] = anim
//        Logger.info("Successfully loaded new animation ${anim.info.name}")
//    } catch (e: BadParamException) {
//        Logger.error(e.toString())
//    } catch (e: ScriptException) {
//        Logger.error("Error when compiling ${name}:")
//        Logger.error(e.toString())
//    }
//}

//fun parseAnimationInfo(info: String, name: String): Animation.AnimationInfo {
//    var animName: String? = null
//    var animAbbr: String? = null
//    var animReqColors = 0
//    var animOptColors = 0
//    var animRepetitive = false
//    var animCenter = ParamUsage.NOTUSED
//    var animDelay = ParamUsage.NOTUSED
//    var animDelayDefault = DEFAULT_DELAY
//    var animDirection = ParamUsage.NOTUSED
//    var animDistance = ParamUsage.NOTUSED
//    var animDistanceDefault = -1
//    var animSpacing = ParamUsage.NOTUSED
//    var animSpacingDefault = DEFAULT_SPACING
//
//    parse@ for (line in info.split(Regex("[\\r\\n]"))) {
//        val identifiers = line.removePrefix("// ").split(" ")
//        when (identifiers[0]) {
//            "name" -> animName =
//                if (identifiers.size > 1)
//                    line.removePrefix("// name ")
//                else
//                    throw BadParamException("name", BadParamReason.NOT_ENOUGH_ARGS)
//            "abbr" -> animAbbr = identifiers.getOrNull(1)
//                ?: throw BadParamException("abbr", BadParamReason.NOT_ENOUGH_ARGS)
//            "colors" -> animReqColors =
//                (identifiers.getOrNull(1)
//                    ?: throw BadParamException("colors", BadParamReason.NOT_ENOUGH_ARGS))
//                    .toIntOrNull()
//                    ?: throw BadParamException("colors", BadParamReason.INVALID_TYPE)
//            "optColors" -> animOptColors =
//                (identifiers.getOrNull(1)
//                    ?: throw BadParamException("optColors", BadParamReason.NOT_ENOUGH_ARGS))
//                    .toIntOrNull()
//                    ?: throw BadParamException("optColors", BadParamReason.INVALID_TYPE)
//            "repetitive" -> animRepetitive =
//                (identifiers.getOrNull(1)
//                    ?: throw BadParamException("repetitive", BadParamReason.NOT_ENOUGH_ARGS))
//                    .toBoolean()
//            "center" -> animCenter =
//                (identifiers.getOrNull(1)
//                    ?: throw BadParamException("center", BadParamReason.NOT_ENOUGH_ARGS))
//                    .toReqLevelOrNull()
//                    ?: throw BadParamException("center", BadParamReason.INVALID_TYPE)
//            "delay" -> {
//                animDelay = (identifiers.getOrNull(1)
//                    ?: throw BadParamException("delay", BadParamReason.NOT_ENOUGH_ARGS))
//                    .toReqLevelOrNull()
//                    ?: throw BadParamException("delay", BadParamReason.INVALID_TYPE)
//                if (animDelay == ParamUsage.USED)
//                    if (identifiers.size > 2)
//                        animDelayDefault = identifiers[2].toLongOrNull()
//                            ?: throw BadParamException("delay (bad default)", BadParamReason.INVALID_TYPE)
//                    else
//                        Logger.warn("Param delay does not have a default, using $DEFAULT_DELAY")
//            }
//            "direction" -> animDirection =
//                (identifiers.getOrNull(1)
//                    ?: throw BadParamException("direction", BadParamReason.NOT_ENOUGH_ARGS))
//                    .toReqLevelOrNull()
//                    ?: throw BadParamException("direction", BadParamReason.INVALID_TYPE)
//            "distance" -> {
//                animDistance = (identifiers.getOrNull(1)
//                    ?: throw BadParamException("distance", BadParamReason.NOT_ENOUGH_ARGS))
//                    .toReqLevelOrNull() ?: throw BadParamException("distance", BadParamReason.INVALID_TYPE)
//                if (animDistance == ParamUsage.USED)
//                    if (identifiers.size > 2)
//                        animDistanceDefault = identifiers[2].toIntOrNull()
//                            ?: throw BadParamException("distance (bad default)", BadParamReason.INVALID_TYPE)
//                    else
//                        Logger.warn("Param distance does not have a default, using full length of strip")
//            }
//            "spacing" -> {
//                animSpacing = (identifiers.getOrNull(1)
//                    ?: throw BadParamException("spacing", BadParamReason.NOT_ENOUGH_ARGS))
//                    .toReqLevelOrNull() ?: throw BadParamException("spacing", BadParamReason.INVALID_TYPE)
//                if (animSpacing == ParamUsage.USED)
//                    if (identifiers.size > 2)
//                        animSpacingDefault = identifiers[2].toIntOrNull()
//                            ?: throw BadParamException("spacing (bad default)", BadParamReason.INVALID_TYPE)
//                    else
//                        Logger.warn("Param spacing does not have a default, using $DEFAULT_SPACING")
//            }
//        }
//    }
//
//    /* Create Animation */
//
//    val animInfo = Animation.AnimationInfo(
//        animName ?: throw BadParamException("name", BadParamReason.MISSING),
//        animAbbr ?: throw BadParamException("abbr", BadParamReason.MISSING),
//        animReqColors,
//        animOptColors,
//        animRepetitive,
//        animCenter,
//        animDelay,
//        animDelayDefault,
//        animDirection,
//        animDistance,
//        animDistanceDefault,
//        animSpacing,
//        animSpacingDefault
//    )
//
//    try {
//        val anim = Animation(
//            animInfo,
//            code
//        )
//
//        definedAnimations[prepareAnimName(anim.info.name)] = anim
//    } catch (e: ScriptException) {
//        println("Error when compiling ${animInfo.name}:")
//        println(e)
//        e.printStackTrace()
//    }
//}

//fun loadPredefinedAnimations(classLoader: ClassLoader) {
//    if (definedAnimations.isNotEmpty()) return
//    for (animation in predefinedAnimations) {
//        val animCode = classLoader.getResource("animations/$animation.kts")?.readText() ?: run {
//            Logger.warn("Animation $animation resource file could not be found")
//            null
//        } ?: continue
//
//        defineNewAnimation(animCode)
//    }
//}