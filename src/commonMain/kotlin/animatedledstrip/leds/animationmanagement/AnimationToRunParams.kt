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

import animatedledstrip.animations.*
import animatedledstrip.colors.ColorContainer
import animatedledstrip.colors.ColorContainerInterface
import animatedledstrip.colors.PreparedColorContainer
import animatedledstrip.colors.ccpresets.Black
import animatedledstrip.communication.SendableData
import animatedledstrip.leds.locationmanagement.Location
import animatedledstrip.leds.sectionmanagement.Section
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Describes the properties of an animation to run
 *
 * @property animation Name of the animation
 * @property colors The list of [ColorContainer]s to use
 * @property id ID for the animation.
 *   Used by server and clients to identify a specific animation.
 * @property section The id of the section of the strip that will be running the whole animation
 *   (not necessarily the section running this animation, such as if this is a subanimation).
 *   This is the section that ColorContainer blend preparation will be based upon.
 *   An empty string means the whole strip.
 * @property runCount The number of times the animation should be run. `-1` means until stopped.
 * @property intParams A map of integer parameters for the animation
 * @property doubleParams A map of double parameters for the animation
 * @property stringParams A map of string parameters for the animation
 * @property locationParams A map of [Location] parameters for the animation
 * @property distanceParams A map of [Distance] parameters for the animation
 * @property rotationParams A map of [Rotation] parameters for the animation
 * @property equationParams A map of [Equation] parameters for the animation
 */
@Serializable
@SerialName("AnimationToRunParams")
data class AnimationToRunParams(
    var animation: String = "",
    var colors: MutableList<ColorContainerInterface> = mutableListOf(),
    var id: String = "",
    var section: String = "",
    var runCount: Int = 0,
    var intParams: MutableMap<String, Int> = mutableMapOf(),
    var doubleParams: MutableMap<String, Double> = mutableMapOf(),
    var stringParams: MutableMap<String, String> = mutableMapOf(),
    var locationParams: MutableMap<String, Location> = mutableMapOf(),
    var distanceParams: MutableMap<String, Distance> = mutableMapOf(),
    var rotationParams: MutableMap<String, Rotation> = mutableMapOf(),
    var equationParams: MutableMap<String, Equation> = mutableMapOf(),
) : SendableData {

    /**
     * Prepare the animation parameters for running the animation.
     * Prepares colors, replaces values with defaults when necessary.
     *
     * @return A [RunningAnimationParams] ready for running an animation
     */
    fun prepare(
        sectionRunningAnimation: Section,
        sectionRunningFullAnimation: Section = sectionRunningAnimation,
    ): RunningAnimationParams {
        // Animation's existence (and support by strip) should be verified before now
        val definedAnimation = sectionRunningAnimation.findAnimation(animation)

        val calculatedColors = mutableListOf<PreparedColorContainer>()

        colors.forEach {
            calculatedColors.add(it.prepare(numLEDs = sectionRunningFullAnimation.numLEDs))
        }

        for (i in colors.size until definedAnimation.info.minimumColors) {
            calculatedColors.add(ColorContainer.Black.prepare(numLEDs = sectionRunningFullAnimation.numLEDs))
        }

        val calculatedAndTrimmedColors = mutableListOf<PreparedColorContainer>()

        calculatedColors.forEach {
            calculatedAndTrimmedColors += PreparedColorContainer(
                colors = it.colors.filterIndexed { index, _ -> index in sectionRunningAnimation.pixels },
                originalColors = it.originalColors,
            )
        }

        val preparedIntParams: MutableMap<String, Int> = mutableMapOf()
        val preparedDoubleParams: MutableMap<String, Double> = mutableMapOf()
        val preparedStringParams: MutableMap<String, String> = mutableMapOf()
        val preparedLocationParams: MutableMap<String, Location> = mutableMapOf()
        val preparedDistanceParams: MutableMap<String, AbsoluteDistance> = mutableMapOf()
        val preparedRotationParams: MutableMap<String, RadiansRotation> = mutableMapOf()
        val preparedEquationParams: MutableMap<String, Equation> = mutableMapOf()

        for (intParam in definedAnimation.info.intParams)
            preparedIntParams[intParam.name] = intParams[intParam.name] ?: intParam.default ?: 0

        for (doubleParam in definedAnimation.info.doubleParams)
            preparedDoubleParams[doubleParam.name] = doubleParams[doubleParam.name] ?: doubleParam.default ?: 0.0

        for (stringParam in definedAnimation.info.stringParams)
            preparedStringParams[stringParam.name] = stringParams[stringParam.name] ?: stringParam.default ?: ""

        for (distanceParam in definedAnimation.info.distanceParams)
            preparedDistanceParams[distanceParam.name] =
                when (val paramValue = distanceParams[distanceParam.name]) {
                    null -> sectionRunningAnimation.stripManager.pixelLocationManager.defaultDistance *
                            (distanceParam.default ?: AbsoluteDistance(1.0, 1.0, 1.0))
                    is PercentDistance -> sectionRunningAnimation.stripManager.pixelLocationManager.defaultDistance *
                                          paramValue
                    else -> paramValue
                }.asAbsoluteDistance()

        for (locationParam in definedAnimation.info.locationParams)
            preparedLocationParams[locationParam.name] =
                when (val paramValue = locationParams[locationParam.name] ?: locationParam.default) {
                    null -> sectionRunningAnimation.stripManager.pixelLocationManager.defaultLocation
                    else -> paramValue
                }

        for (rotationParam in definedAnimation.info.rotationParams)
            preparedRotationParams[rotationParam.name] =
                when (val paramValue = rotationParams[rotationParam.name]
                                       ?: rotationParam.default
                                       ?: RadiansRotation(0.0, 0.0, 0.0)) {
                    is DegreesRotation -> paramValue.toRadiansRotation()
                    else -> paramValue as RadiansRotation
                }

        for (equationParam in definedAnimation.info.equationParams)
            preparedEquationParams[equationParam.name] =
                when (val paramValue = equationParams[equationParam.name]) {
                    null -> equationParam.default ?: Equation()
                    else -> paramValue
                }

        val calculatedRunCount = if (runCount <= 0) definedAnimation.info.runCountDefault
        else runCount

        return RunningAnimationParams(definedAnimation,
                                      animation,
                                      calculatedAndTrimmedColors,
                                      id,
                                      section,
                                      calculatedRunCount,
                                      preparedIntParams,
                                      preparedDoubleParams,
                                      preparedStringParams,
                                      preparedLocationParams,
                                      preparedDistanceParams,
                                      preparedRotationParams,
                                      preparedEquationParams,
                                      this)
    }
}
