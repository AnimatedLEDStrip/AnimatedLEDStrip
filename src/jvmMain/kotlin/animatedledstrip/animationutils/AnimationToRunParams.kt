/*
 *  Copyright (c) 2018-2020 AnimatedLEDStrip
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

import animatedledstrip.colors.ColorContainer
import animatedledstrip.colors.ColorContainerInterface
import animatedledstrip.colors.PreparedColorContainer
import animatedledstrip.colors.ccpresets.CCBlack
import animatedledstrip.leds.AnimatedLEDStrip
import animatedledstrip.utils.SendableData
import kotlinx.serialization.Serializable

/**
 * Used to describe the properties of animations to run or that are running.
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
 * @property pCols The list of [PreparedColorContainer]s after preparation of [colors].
 *   Will be populated when [prepare] is called.
 * @property runCount The number of times the animation should be run. `-1` means until stopped.
 * @property section The id of the section of the strip that will be running the whole animation
 *   (not necessarily the section running this animation, such as if this is a subanimation).
 *   This is the section that ColorContainer blend preparation will be based upon.
 *   An empty string means the whole strip.
 * @property spacing Spacing used in the animation
 */
@Serializable
data class AnimationToRunParams(
    var animation: String = "",
    var colors: MutableList<ColorContainerInterface> = mutableListOf(),
    var center: Int = -1,
    var delay: Long = -1L,
    var delayMod: Double = 1.0,
    var direction: Direction = Direction.FORWARD,
    var distance: Int = -1,
    var id: String = "",
    var runCount: Int = 0,
    var section: String = "",
    var spacing: Int = -1,
) : SendableData {

    /**
     * Prepare the `AnimationData` instance for use by the specified `AnimatedLedStrip.Section`.
     *
     * Sets defaults for properties with length-dependent defaults (`center` and `distance`)
     * and populates `pCols`.
     */
    fun prepare(sectionRunningAnimation: AnimatedLEDStrip.Section): RunningAnimationParams {

        val sectionName = when (section) {
            "" -> sectionRunningAnimation.name
            else -> section
        }

        // Animation's existence should be verified before now
        val definedAnimation = findAnimation(animation)

        // Isn't actually dependent on the section running animation,
        // it just needs it to get to the AnimatedLEDStrip containing it
        val sectionRunningFullAnimation = sectionRunningAnimation.getSection(sectionName = sectionName)

        val calculatedColors = mutableListOf<PreparedColorContainer>()

        colors.forEach {
            calculatedColors.add(it.prepare(numLEDs = sectionRunningFullAnimation.numLEDs))
        }

        for (i in colors.size until definedAnimation.info.minimumColors) {
            calculatedColors.add(CCBlack.prepare(numLEDs = sectionRunningFullAnimation.numLEDs))
        }

        val calculatedAndTrimmedColors = mutableListOf<PreparedColorContainer>()

        calculatedColors.forEach {
            calculatedAndTrimmedColors += PreparedColorContainer(
                colors = it.colors.subList(sectionRunningAnimation.startPixel,
                                           sectionRunningAnimation.endPixel) + it.colors[sectionRunningAnimation.endPixel],
                originalColors = it.originalColors,
            )
        }

        val calculatedCenter = when {
            center < 0 -> sectionRunningAnimation.numLEDs / 2
            center > sectionRunningAnimation.endPixel -> throw IllegalArgumentException("Center must be within strip")
            else -> center
        }

        val tempDelay = if (delay < 0L) definedAnimation.info.delayDefault
        else delay

        val calculatedDelay = (tempDelay * delayMod).toLong()

        val calculatedDistance = if (distance < 0) {
            if (definedAnimation.info.distanceDefault != -1)
                definedAnimation.info.distanceDefault
            else sectionRunningAnimation.numLEDs
        } else distance

        val calculatedRunCount = if (runCount <= 0) definedAnimation.info.runCountDefault
        else runCount

        val calculatedSpacing = if (spacing <= 0) definedAnimation.info.spacingDefault
        else spacing


        return RunningAnimationParams(animation,
                                      calculatedAndTrimmedColors,
                                      calculatedCenter,
                                      calculatedDelay,
                                      direction,
                                      calculatedDistance,
                                      id,
                                      calculatedRunCount,
                                      section,
                                      calculatedSpacing,
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
              center: $center
              delay: $delay
              delayMod: $delayMod
              direction: $direction
              distance: $distance
              runCount: $runCount
              section: $section
              spacing: $spacing
            End AnimationData
        """.trimIndent()

}
