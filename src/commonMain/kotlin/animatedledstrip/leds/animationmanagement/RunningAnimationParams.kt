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

package animatedledstrip.leds.animationmanagement

import animatedledstrip.animations.Animation
import animatedledstrip.animations.parameters.*
import animatedledstrip.colors.ColorContainerInterface
import animatedledstrip.colors.PreparedColorContainer
import animatedledstrip.communication.SendableData
import animatedledstrip.leds.locationmanagement.Location
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Describes the properties of a currently running animation
 *
 * @property animation The animation being run
 * @property animationName The name of the animation being run
 * @property colors The list of [PreparedColorContainer]s used
 * @property id ID for the animation. Used by server and clients to identify a specific animation.
 * @property section The id of the section of the strip that will be running the whole animation
 *   (not necessarily the section running this animation, such as if this is a subanimation).
 *   An empty string means the whole strip.
 * @property runCount The number of times the animation should be run. `-1` means until stopped.
 * @property intParams A map of integer parameters for the animation
 * @property doubleParams A map of double parameters for the animation
 * @property stringParams A map of string parameters for the animation
 * @property locationParams A map of [Location] parameters for the animation
 * @property distanceParams A map of [Distance] parameters for the animation
 * @property rotationParams A map of [Rotation] parameters for the animation
 * @property equationParams A map of [Equation] parameters for the animation
 * @property sourceParams The [AnimationToRunParams] instance that created this [RunningAnimationParams]
 */
@Serializable
@SerialName("RunningAnimationParams")
data class RunningAnimationParams internal constructor(
    val animationName: String,
    val colors: List<PreparedColorContainer>,
    val id: String,
    val section: String,
    val runCount: Int,
    val intParams: Map<String, Int>,
    val doubleParams: Map<String, Double>,
    val stringParams: Map<String, String>,
    val locationParams: Map<String, Location>,
    val distanceParams: Map<String, AbsoluteDistance>,
    val rotationParams: Map<String, RadiansRotation>,
    val equationParams: Map<String, Equation>,
    val sourceParams: AnimationToRunParams,
) : SendableData {

    @Suppress("JoinDeclarationAndAssignment")
    @Transient
    lateinit var animation: Animation

    /**
     * Constructor so we can have the [animation] parameter but not include it in serialization
     */
    constructor(
        animation: Animation,
        animationName: String,
        colors: List<PreparedColorContainer>,
        id: String,
        section: String,
        runCount: Int,
        intParams: Map<String, Int>,
        doubleParams: Map<String, Double>,
        stringParams: Map<String, String>,
        locationParams: Map<String, Location>,
        distanceParams: Map<String, AbsoluteDistance>,
        rotationParams: Map<String, RadiansRotation>,
        equationParams: Map<String, Equation>,
        sourceParams: AnimationToRunParams,
    ) : this(animationName,
             colors,
             id,
             section,
             runCount,
             intParams,
             doubleParams,
             stringParams,
             locationParams,
             distanceParams,
             rotationParams,
             equationParams,
             sourceParams) {
        this.animation = animation
    }

    /**
     * A map used internally by animations to communicate between
     * runs of the animation (see Alternate or Cat Toy)
     */
    @Transient
    val extraData = mutableMapOf<String, Any?>()

    /**
     * Create a new [AnimationToRunParams] instance with the values in this [RunningAnimationParams],
     * unless if specified otherwise
     */
    fun withModifications(
        animation: String = this.animationName,
        colors: List<ColorContainerInterface> = this.colors,
        id: String = this.id,
        section: String = this.section,
        runCount: Int = this.runCount,
        intParamMods: Map<String, Int> = mapOf(),
        doubleParamMods: Map<String, Double> = mapOf(),
        stringParamMods: Map<String, String> = mapOf(),
        locationParamMods: Map<String, Location> = mapOf(),
        distanceParamMods: Map<String, Distance> = mapOf(),
        rotationParamMods: Map<String, Rotation> = mapOf(),
        equationParamMods: Map<String, Equation> = mapOf(),
    ): AnimationToRunParams = AnimationToRunParams(animation,
                                                   colors.toMutableList(),
                                                   id,
                                                   section,
                                                   runCount,
                                                   (intParams + intParamMods).toMutableMap(),
                                                   (doubleParams + doubleParamMods).toMutableMap(),
                                                   (stringParams + stringParamMods).toMutableMap(),
                                                   (locationParams + locationParamMods).toMutableMap(),
                                                   (distanceParams + distanceParamMods).toMutableMap(),
                                                   (rotationParams + rotationParamMods).toMutableMap(),
                                                   (equationParams + equationParamMods).toMutableMap())
}
