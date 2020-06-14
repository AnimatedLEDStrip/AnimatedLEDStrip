package animatedledstrip.animationutils

import animatedledstrip.leds.AnimatedLEDStrip
import animatedledstrip.utils.SendableData
import kotlinx.coroutines.CoroutineScope

abstract class Animation(open val info: AnimationInfo): SendableData {

    abstract fun runAnimation(leds: AnimatedLEDStrip.Section, data: AnimationData, scope: CoroutineScope)

    override fun toHumanReadableString(): String =
        """
            Animation Definition
              info: 
              ${info.toHumanReadableString()}
            End Definition
        """.trimMargin()

    /**
     * Stores information about an animation.
     *
     * @property name The name used to identify this animation
     * @property abbr
     * @property numReqColors The number of required colors for this animation
     * @property numOptColors The number of optional colors for this animation
     * @property repetitive Can this animation be repeated
     *   (see https://github.com/AnimatedLEDStrip/AnimatedLEDStrip/wiki/Repetitive-vs-NonRepetitive-vs-Radial)
     * @property center Does this animation use the `center` parameter
     * @property delay Does this animation use the `delay` parameter
     * @property delayDefault Default value for the `delay` parameter
     * @property direction Does this animation use the `direction` parameter
     * @property distance Does this animation use the `distance` parameter
     * @property distanceDefault Default value for the `distance` parameter
     * @property spacing Does this animation use the `spacing` parameter
     * @property spacingDefault Default value for the `spacing` parameter
     */
    data class AnimationInfo(
        val name: String,
        val abbr: String,
        val numReqColors: Int,
        val numOptColors: Int,
        val repetitive: Boolean,
        val center: ParamUsage,
        val delay: ParamUsage,
        val delayDefault: Long,
        val direction: ParamUsage,
        val distance: ParamUsage,
        val distanceDefault: Int,
        val spacing: ParamUsage,
        val spacingDefault: Int
    ) : SendableData {

        companion object {
            const val prefix = "AINF"
        }

        override val prefix = AnimationInfo.prefix

        val numColors: Int = numReqColors + numOptColors

        override fun toHumanReadableString(): String =
            """
                Animation Info
                  name: $name
                  abbr: $abbr
                  required colors: $numReqColors
                  optional colors: $numOptColors
                  repetitive: $repetitive
                  center: $center
                  delay: $delay ($delayDefault)
                  direction: $direction
                  distance: $distance (${if (distanceDefault == -1) "whole strip" else distanceDefault})
                  spacing: $spacing ($spacingDefault)
                End Info 
            """.trimIndent()
    }

}