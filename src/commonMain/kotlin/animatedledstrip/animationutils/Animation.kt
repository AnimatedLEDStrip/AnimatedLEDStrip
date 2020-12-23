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
import kotlinx.serialization.Serializable

/**
 * Represents an animation
 *
 * @property info Information about the animation
 */
abstract class Animation(open val info: AnimationInfo) {

    companion object {
        const val DEFAULT_DELAY = 50L
        const val DEFAULT_SPACING = 3
    }

    /**
     * Run the animation, passing it the strip section, information about how to run the animation,
     * and the scope of the animation
     */
    abstract suspend fun runAnimation(leds: AnimatedLEDStrip.Section, params: RunningAnimationParams, scope: CoroutineScope)

    /**
     * Stores information about an animation.
     *
     * @property name The name used to identify this animation
     * @property abbr The abbreviation that can be used to identify the animation
     * @property description A description of the animation
     * @property signatureFile The name of the file that holds the signature of an example run of the animation
     * @property runCountDefault Default value for the `runCount` parameter
     * @property minimumColors The number of required animatedledstrip.colors for this animation
     * @property unlimitedColors Can this animation take an unlimited number of animatedledstrip.colors
     * @property center Does this animation use the `center` parameter
     * @property delay Does this animation use the `delay` parameter
     * @property direction Does this animation use the `direction` parameter
     * @property distance Does this animation use the `distance` parameter
     * @property spacing Does this animation use the `spacing` parameter
     * @property delayDefault Default value for the `delay` parameter
     * @property distanceDefault Default value for the `distance` parameter
     * @property spacingDefault Default value for the `spacing` parameter
     */
    @Serializable
    data class AnimationInfo(
        val name: String,
        val abbr: String,
        val description: String,
        val signatureFile: String,
        val runCountDefault: Int,
        val minimumColors: Int,
        val unlimitedColors: Boolean,
        val center: ParamUsage,
        val delay: ParamUsage,
        val direction: ParamUsage,
        val distance: ParamUsage,
        val spacing: ParamUsage,
        val delayDefault: Long = DEFAULT_DELAY,
        val distanceDefault: Int = -1,
        val spacingDefault: Int = DEFAULT_SPACING,
    ) : SendableData {

        override fun toHumanReadableString(): String =
            """
                Animation Info
                  name: $name
                  abbr: $abbr
                  runCountDefault: $runCountDefault
                  minimum colors: $minimumColors
                  unlimited colors: $unlimitedColors
                  center: $center
                  delay: $delay${if (delay == ParamUsage.USED) " ($delayDefault)" else ""}
                  direction: $direction
                  distance: $distance${if (distance == ParamUsage.USED) " (${if (distanceDefault == -1) "whole strip" else distanceDefault.toString()})" else ""}
                  spacing: $spacing${if (spacing == ParamUsage.USED) " ($spacingDefault)" else ""}
                End Info
            """.trimIndent()
    }
}
