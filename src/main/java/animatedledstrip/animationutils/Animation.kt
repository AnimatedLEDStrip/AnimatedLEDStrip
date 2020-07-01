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

import animatedledstrip.leds.AnimatedLEDStrip
import animatedledstrip.utils.SendableData
import kotlinx.coroutines.CoroutineScope

abstract class Animation(open val info: AnimationInfo) : SendableData {

    companion object {
        const val DEFAULT_DELAY = 50L
        const val DEFAULT_SPACING = 3
    }

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
     * @property minimumColors The number of required colors for this animation
     * @property unlimitedColors Can this animation take an unlimited number of colors
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
        val repetitive: Boolean,
        val minimumColors: Int = 0,
        val unlimitedColors: Boolean = false,
        val center: ParamUsage = ParamUsage.NOTUSED,
        val delay: ParamUsage = ParamUsage.NOTUSED,
        val direction: ParamUsage = ParamUsage.NOTUSED,
        val distance: ParamUsage = ParamUsage.NOTUSED,
        val spacing: ParamUsage = ParamUsage.NOTUSED,
        val delayDefault: Long = DEFAULT_DELAY,
        val distanceDefault: Int = -1,
        val spacingDefault: Int = DEFAULT_SPACING
    ) : SendableData {

        companion object {
            const val prefix = "AINF"
        }

        override val prefix = AnimationInfo.prefix

        override fun toHumanReadableString(): String =
            """
                Animation Info
                  name: $name
                  abbr: $abbr
                  required colors: $minimumColors
                  optional colors: $unlimitedColors
                  repetitive: $repetitive
                  center: $center
                  delay: $delay ($delayDefault)
                  direction: $direction
                  distance: $distance (${if (distanceDefault == -1) "whole strip" else distanceDefault.toString()})
                  spacing: $spacing ($spacingDefault)
                End Info 
            """.trimIndent()
    }
}
