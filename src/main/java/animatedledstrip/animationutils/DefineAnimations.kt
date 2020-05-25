package animatedledstrip.animationutils

import animatedledstrip.utils.removeSpaces
import animatedledstrip.utils.toReqLevelOrNull
import kotlinx.coroutines.*
import org.pmw.tinylog.Logger
import javax.script.ScriptException

const val DEFAULT_DELAY = 50L
const val DEFAULT_SPACING = 3

@Suppress("EXPERIMENTAL_API_USAGE")
val animationLoadingTreadPool =
    newFixedThreadPoolContext(10, "Animation Loading Pool")

val predefinedAnimations = listOf(
    "alternate",
    "bounce",
    "bounce_to_color",
    "cat_toy",
    "cat_toy_to_color",
    "color",
    "fade_to_color",
    "fireworks",
    "meteor",
    "multi_pixel_run",
    "multi_pixel_run_to_color",
    "pixel_marathon",
    "pixel_run",
    "ripple",
    "smooth_chase",
    "smooth_fade",
    "sparkle",
    "sparkle_fade",
    "sparkle_to_color",
    "splat",
    "stack",
    "stack_overflow",
    "wipe"
)

var predefinedAnimLoadComplete: Boolean = false
    private set

val definedAnimations = mutableMapOf<String, Animation>()

fun definedAnimationNames(): List<String> = definedAnimations.map { it.value.info.name }

fun prepareAnimName(name: String): String = name.removeSpaces().replace("-", "").toLowerCase()

fun findAnimation(animName: String): Animation? = definedAnimations[prepareAnimName(animName)]

fun defineNewAnimation(code: String, name: String) {
    val info = (Regex("// ## animation info ##[\\s\\S]*// ## end info ##")
        .find(code)
        ?: run { Logger.error("Could not find info for animation $name in $code"); return })
        .groupValues[0]

    try {
        val animInfo = parseAnimationInfo(info, name)

        val anim = Animation(
            animInfo,
            code
        )

        definedAnimations[prepareAnimName(anim.info.name)] = anim
        Logger.info("Successfully loaded new animation ${anim.info.name}")
    } catch (e: BadParamException) {
        Logger.error(e.toString())
    } catch (e: ScriptException) {
        Logger.error("Error when compiling ${name}:")
        Logger.error(e.toString())
        Logger.error(e.stackTrace)
    }
}

fun parseAnimationInfo(info: String, name: String): Animation.AnimationInfo {
    var animName: String? = null
    var animAbbr: String? = null
    var animReqColors = 0
    var animOptColors = 0
    var animRepetitive = false
    var animCenter = ParamUsage.NOTUSED
    var animDelay = ParamUsage.NOTUSED
    var animDelayDefault = DEFAULT_DELAY
    var animDirection = ParamUsage.NOTUSED
    var animDistance = ParamUsage.NOTUSED
    var animDistanceDefault = -1
    var animSpacing = ParamUsage.NOTUSED
    var animSpacingDefault = DEFAULT_SPACING

    parse@ for (line in info.split(Regex("[\\r\\n]"))) {
        val identifiers = line.removePrefix("// ").split(" ")
        when (identifiers.getOrNull(0)) {
            null -> continue@parse
            "name" -> animName =
                if (identifiers.size > 1) line.removePrefix("// name ")
                else throw BadParamNotEnoughArgsException("name")
            "abbr" -> animAbbr = identifiers.getOrNull(1)
                ?: throw BadParamNotEnoughArgsException("abbr")
            "colors" -> animReqColors =
                (identifiers.getOrNull(1)
                    ?: throw BadParamNotEnoughArgsException("colors"))
                    .toIntOrNull()
                    ?: throw BadParamInvalidTypeException("colors")
            "optColors" -> animOptColors =
                (identifiers.getOrNull(1)
                    ?: throw BadParamNotEnoughArgsException("optColors"))
                    .toIntOrNull()
                    ?: throw BadParamInvalidTypeException("optColors")
            "repetitive" -> animRepetitive =
                (identifiers.getOrNull(1)
                    ?: throw BadParamNotEnoughArgsException("repetitive"))
                    .toBoolean()
            "center" -> animCenter =
                (identifiers.getOrNull(1)
                    ?: throw BadParamNotEnoughArgsException("center"))
                    .toReqLevelOrNull()
                    ?: throw BadParamInvalidTypeException("center")
            "delay" -> {
                animDelay = (identifiers.getOrNull(1)
                    ?: throw BadParamNotEnoughArgsException("delay"))
                    .toReqLevelOrNull()
                    ?: throw BadParamInvalidTypeException("delay")
                if (animDelay == ParamUsage.USED)
                    if (identifiers.size > 2)
                        animDelayDefault = identifiers[2].toLongOrNull()
                            ?: throw BadParamInvalidTypeException("delay (bad default)")
                    else
                        Logger.warn("$name: Param delay does not have a default, using $DEFAULT_DELAY")
            }
            "direction" -> animDirection =
                (identifiers.getOrNull(1)
                    ?: throw BadParamNotEnoughArgsException("direction"))
                    .toReqLevelOrNull()
                    ?: throw BadParamInvalidTypeException("direction")
            "distance" -> {
                animDistance = (identifiers.getOrNull(1)
                    ?: throw BadParamNotEnoughArgsException("distance"))
                    .toReqLevelOrNull() ?: throw BadParamInvalidTypeException("distance")
                if (animDistance == ParamUsage.USED)
                    if (identifiers.size > 2)
                        animDistanceDefault = identifiers[2].toIntOrNull()
                            ?: throw BadParamInvalidTypeException("distance (bad default)")
                    else
                        Logger.warn("$name: Param distance does not have a default, using full length of strip")
            }
            "spacing" -> {
                animSpacing = (identifiers.getOrNull(1)
                    ?: throw BadParamNotEnoughArgsException("spacing"))
                    .toReqLevelOrNull() ?: throw BadParamInvalidTypeException("spacing")
                if (animSpacing == ParamUsage.USED)
                    if (identifiers.size > 2)
                        animSpacingDefault = identifiers[2].toIntOrNull()
                            ?: throw BadParamInvalidTypeException("spacing (bad default)")
                    else
                        Logger.warn("$name: Param spacing does not have a default, using $DEFAULT_SPACING")
            }
        }
    }

    /* Create Animation */

    return Animation.AnimationInfo(
        animName ?: throw MissingParamException("name"),
        animAbbr ?: throw MissingParamException("abbr"),
        animReqColors,
        animOptColors,
        animRepetitive,
        animCenter,
        animDelay,
        animDelayDefault,
        animDirection,
        animDistance,
        animDistanceDefault,
        animSpacing,
        animSpacingDefault
    )
}

fun loadPredefinedAnimations(classLoader: ClassLoader) {
    if (definedAnimations.isNotEmpty()) return
    Logger.info("Loading predefined animations")

    GlobalScope.launch {
        predefinedAnimations.map { anim ->
            loadPredefinedAnimation(classLoader, anim)
        }.joinAll()
        Logger.info("Finished loading predefined animations")
        predefinedAnimLoadComplete = true
    }
}

fun CoroutineScope.loadPredefinedAnimation(classLoader: ClassLoader, anim: String): Job =
    launch(animationLoadingTreadPool) load@{
        Logger.debug("Loading animation $anim")
        val animCode = classLoader
            .getResource("animations/$anim.kts")
            ?.readText()
            ?: run {
                Logger.warn("Animation $anim resource file could not be found")
                null
            } ?: return@load

        defineNewAnimation(animCode, anim)
    }

