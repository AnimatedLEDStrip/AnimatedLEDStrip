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
import animatedledstrip.leds.animationmanagement.AnimationManager
import animatedledstrip.leds.animationmanagement.RunningAnimationParams
import animatedledstrip.leds.locationmanagement.Location
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents an animation that can be run
 *
 * @property info Information about the animation
 */
abstract class Animation {

    abstract val info: AnimationInfo

    /**
     * Run the animation, passing it the AnimationManager running it, parameters specifying how to run the animation,
     * and the CoroutineScope the animation is running in
     */
    abstract suspend fun runAnimation(leds: AnimationManager, params: RunningAnimationParams, scope: CoroutineScope)

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
     * @property intParams A list of integer parameters the animation uses
     * @property doubleParams A list of double parameters the animation uses
     * @property locationParams A list of [Location] parameters the animation uses
     * @property distanceParams A list of [Distance] parameters the animation uses
     * (note that default values are percentages of the strip span in that dimension, not the full distance)
     * @property equationParams A list of [Equation] parameters the animation uses
     */
    @Serializable
    @SerialName("AnimationInfo")
    data class AnimationInfo(
        val name: String,
        val abbr: String,
        val description: String,
        val runCountDefault: Int,
        val minimumColors: Int,
        val unlimitedColors: Boolean,
        val dimensionality: Set<Dimensionality>,
        @Deprecated("Directional animations will be replaced by animations which can be rotated")
        val directional: Boolean,
        val intParams: List<AnimationParameter<Int>> = listOf(),
        val doubleParams: List<AnimationParameter<Double>> = listOf(),
        val stringParams: List<AnimationParameter<String>> = listOf(),
        val locationParams: List<AnimationParameter<Location>> = listOf(),
        val distanceParams: List<AnimationParameter<Distance>> = listOf(),
        val rotationParams: List<AnimationParameter<Rotation>> = listOf(),
        val equationParams: List<AnimationParameter<Equation>> = listOf(),
    ) : SendableData
}
