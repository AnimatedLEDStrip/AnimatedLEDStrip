package animatedledstrip.animationutils

import animatedledstrip.utils.removeSpaces
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
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

    fun String.toReqLevelOrNull(): ParamUsage? =
        when (this.toUpperCase()) {
            "USED" -> ParamUsage.USED
            "NOTUSED" -> ParamUsage.NOTUSED
            else -> null
        }

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

    val info = (Regex("// ## animation info ##[\\s\\S]*// ## end info ##").find(code)
        ?: throw Exception("Could not find info in $code")).groupValues[0]

    parse@ for (line in info.split(Regex("[\\r\\n]"))) {
        val identifiers = line.removePrefix("// ").split(" ")
        when (identifiers[0]) {
            "name" -> animName =
                if (identifiers.size > 1)
                    line.removePrefix("// name ")
                else
                    throw BadParamException("name", BadParamReason.NOT_ENOUGH_ARGS)
            "abbr" -> animAbbr = identifiers.getOrNull(1)
                ?: throw BadParamException("abbr", BadParamReason.NOT_ENOUGH_ARGS)
            "colors" -> animReqColors =
                (identifiers.getOrNull(1)
                    ?: throw BadParamException("colors", BadParamReason.NOT_ENOUGH_ARGS))
                    .toIntOrNull()
                    ?: throw BadParamException("colors", BadParamReason.INVALID_TYPE)
            "optColors" -> animOptColors =
                (identifiers.getOrNull(1)
                    ?: throw BadParamException("optColors", BadParamReason.NOT_ENOUGH_ARGS))
                    .toIntOrNull()
                    ?: throw BadParamException("optColors", BadParamReason.INVALID_TYPE)
            "repetitive" -> animRepetitive =
                (identifiers.getOrNull(1)
                    ?: throw BadParamException("repetitive", BadParamReason.NOT_ENOUGH_ARGS))
                    .toBoolean()
            "center" -> animCenter =
                (identifiers.getOrNull(1)
                    ?: throw BadParamException("center", BadParamReason.NOT_ENOUGH_ARGS))
                    .toReqLevelOrNull()
                    ?: throw BadParamException("center", BadParamReason.INVALID_TYPE)
            "delay" -> {
                animDelay = (identifiers.getOrNull(1)
                    ?: throw BadParamException("delay", BadParamReason.NOT_ENOUGH_ARGS))
                    .toReqLevelOrNull()
                    ?: throw BadParamException("delay", BadParamReason.INVALID_TYPE)
                if (animDelay == ParamUsage.USED)
                    if (identifiers.size > 2)
                        animDelayDefault = identifiers[2].toLongOrNull()
                            ?: throw BadParamException("delay (bad default)", BadParamReason.INVALID_TYPE)
                    else
                        Logger.warn("Param delay does not have a default, using $DEFAULT_DELAY")
            }
            "direction" -> animDirection =
                (identifiers.getOrNull(1)
                    ?: throw BadParamException("direction", BadParamReason.NOT_ENOUGH_ARGS))
                    .toReqLevelOrNull()
                    ?: throw BadParamException("direction", BadParamReason.INVALID_TYPE)
            "distance" -> {
                animDistance = (identifiers.getOrNull(1)
                    ?: throw BadParamException("distance", BadParamReason.NOT_ENOUGH_ARGS))
                    .toReqLevelOrNull() ?: throw BadParamException("distance", BadParamReason.INVALID_TYPE)
                if (animDistance == ParamUsage.USED)
                    if (identifiers.size > 2)
                        animDistanceDefault = identifiers[2].toIntOrNull()
                            ?: throw BadParamException("distance (bad default)", BadParamReason.INVALID_TYPE)
                    else
                        Logger.warn("$name: Param distance does not have a default, using full length of strip")
            }
            "spacing" -> {
                animSpacing = (identifiers.getOrNull(1)
                    ?: throw BadParamException("spacing", BadParamReason.NOT_ENOUGH_ARGS))
                    .toReqLevelOrNull() ?: throw BadParamException("spacing", BadParamReason.INVALID_TYPE)
                if (animSpacing == ParamUsage.USED)
                    if (identifiers.size > 2)
                        animSpacingDefault = identifiers[2].toIntOrNull()
                            ?: throw BadParamException("spacing (bad default)", BadParamReason.INVALID_TYPE)
                    else
                        Logger.warn("$name: Param spacing does not have a default, using $DEFAULT_SPACING")
            }
        }
    }

    /* Create Animation */

    val animInfo = Animation.AnimationInfo(
        animName ?: throw BadParamException("name", BadParamReason.MISSING),
        animAbbr ?: throw BadParamException("abbr", BadParamReason.MISSING),
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

    try {
        val anim = Animation(
            animInfo,
            code
        )

        definedAnimations[prepareAnimName(anim.info.name)] = anim
        Logger.info("Successfully loaded new animation ${anim.info.name}")
    } catch (e: ScriptException) {
        println("Error when compiling ${animInfo.name}:")
        println(e)
        e.printStackTrace()
    }
}

fun loadPredefinedAnimations(classLoader: ClassLoader) {
    if (definedAnimations.isNotEmpty()) return
    Logger.info("Loading predefined animations")

    GlobalScope.launch {
        val jobs = predefinedAnimations.map { anim ->
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
        }
        jobs.joinAll()
        Logger.info("Finished loading predefined animations")
        predefinedAnimLoadComplete = true
    }
}