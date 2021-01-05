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

import animatedledstrip.animations.Direction
import animatedledstrip.animations.Distance
import animatedledstrip.animations.Equation
import animatedledstrip.colors.ColorContainer
import animatedledstrip.colors.ColorContainerInterface
import animatedledstrip.colors.PreparedColorContainer
import animatedledstrip.colors.ccpresets.Black
import animatedledstrip.communication.SendableData
import animatedledstrip.leds.sectionmanagement.Section
import animatedledstrip.leds.stripmanagement.Location
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Describes the properties of an animation to run
 *
 * @property animation Name of the animation
 * @property colors The list of [ColorContainer]s to use
 * @property center The pixel at the center of an animation.
 *   Defaults to the center of the strip.
 * @property delay Delay time (in milliseconds) used in the animation
 * @property delayMod Multiplier for `delay`
 * @property direction The direction the animation will appear to move
 * @property distance The distance an animation will travel from its center.
 *   Defaults to the length of the strip, meaning it will run until the ends of the strip.
 * @property id ID for the animation.
 *   Used by server and clients to identify a specific animation.
 * @property runCount The number of times the animation should be run. `-1` means until stopped.
 * @property section The id of the section of the strip that will be running the whole animation
 *   (not necessarily the section running this animation, such as if this is a subanimation).
 *   This is the section that ColorContainer blend preparation will be based upon.
 *   An empty string means the whole strip.
 * @property spacing Spacing used in the animation
 */
@Serializable
@SerialName("AnimationToRunParams")
data class AnimationToRunParams(
    var animation: String = "",
    var colors: MutableList<ColorContainerInterface> = mutableListOf(),
    var id: String = "",
    var section: String = "",
    var runCount: Int = 0,
    var direction: Direction = Direction.FORWARD,
    var intParams: MutableMap<String, Int> = mutableMapOf(),
    var doubleParams: MutableMap<String, Double> = mutableMapOf(),
    var locationParams: MutableMap<String, Location> = mutableMapOf(),
    var distanceParams: MutableMap<String, Distance> = mutableMapOf(),
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
        // Animation's existence should be verified before now
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
                colors = it.colors.slice(
                    sectionRunningAnimation.startPixel - sectionRunningFullAnimation.startPixel..
                            sectionRunningAnimation.endPixel - sectionRunningFullAnimation.startPixel),
                originalColors = it.originalColors,
            )
        }

        val preparedIntParams: MutableMap<String, Int> = mutableMapOf()
        val preparedDoubleParams: MutableMap<String, Double> = mutableMapOf()
        val preparedLocationParams: MutableMap<String, Location> = mutableMapOf()
        val preparedDistanceParams: MutableMap<String, Distance> = mutableMapOf()
        val preparedEquationParams: MutableMap<String, Equation> = mutableMapOf()

        for (intParam in definedAnimation.info.intParams)
            preparedIntParams[intParam.name] =
                when (val paramValue = intParams[intParam.name]) {
                    null -> intParam.default ?: 0
                    else -> paramValue
                }

        for (doubleParam in definedAnimation.info.doubleParams)
            preparedDoubleParams[doubleParam.name] =
                when (val paramValue = doubleParams[doubleParam.name]) {
                    null -> doubleParam.default ?: 0.0
                    else -> paramValue
                }

        for (distanceParam in definedAnimation.info.distanceParams)
            preparedDistanceParams[distanceParam.name] =
                when (val paramValue = distanceParams[distanceParam.name]) {
                    null -> sectionRunningAnimation.stripManager.pixelLocationManager.defaultDistance * distanceParam.default
                            ?: sectionRunningAnimation.stripManager.pixelLocationManager.defaultDistance
                    else -> paramValue
                }

        for (locationParam in definedAnimation.info.locationParams)
            preparedLocationParams[locationParam.name] =
                when (val paramValue = locationParams[locationParam.name]) {
                    null -> locationParam.default
                            ?: sectionRunningAnimation.stripManager.pixelLocationManager.defaultLocation
                    else -> paramValue
                }

        for (equationParam in definedAnimation.info.equationParams)
            preparedEquationParams[equationParam.name] =
                when (val paramValue = equationParams[equationParam.name]) {
                    null -> equationParam.default ?: Equation.default
                    else -> paramValue
                }

//        val calculatedCenter = when (center) {
//            null -> Location((sectionRunningAnimation.numLEDs / 2).toDouble(), 0.0, 0.0)
//            else -> center!!
//        }
//
//        val tempDelay = if (delay < 0L) definedAnimation.info.delayDefault
//        else delay
//
//        val calculatedDelay = (tempDelay * delayMod).toLong()
//
//        val calculatedDistance = if (distance < 0) {
//            if (definedAnimation.info.distanceDefault != -1)
//                definedAnimation.info.distanceDefault
//            else sectionRunningAnimation.numLEDs
//        } else distance
//
        val calculatedRunCount = if (runCount <= 0) definedAnimation.info.runCountDefault
        else runCount
//
//        val calculatedSpacing = if (spacing <= 0) definedAnimation.info.spacingDefault
//        else spacing


        return RunningAnimationParams(definedAnimation,
                                      animation,
                                      calculatedAndTrimmedColors,
                                      id,
                                      section,
                                      calculatedRunCount,
                                      direction,
                                      preparedIntParams,
                                      preparedDoubleParams,
                                      preparedLocationParams,
                                      preparedDistanceParams,
                                      preparedEquationParams,
                                      this)
    }

    /**
     * Create a nicely formatted string representation.
     */
    override fun toHumanReadableString() =
        """
            AnimationData for $id
              animation: $animation
              colors: $colors
              section: $section
              runCount: $runCount
              direction: $direction
            End AnimationData
        """.trimIndent()

}
