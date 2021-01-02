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

package animatedledstrip.leds.animationmanagement

import animatedledstrip.animations.Animation
import animatedledstrip.animations.Direction
import animatedledstrip.colors.ColorContainerInterface
import animatedledstrip.colors.PreparedColorContainer
import animatedledstrip.communication.SendableData
import animatedledstrip.leds.stripmanagement.LEDLocation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Describes the properties of a currently running animation
 *
 * @property animation The animation being run
 * @property animationName The name of the animation being run
 * @property colors The list of [PreparedColorContainer]s used
 * @property center The pixel at the center of the animation, if applicable
 * @property delay Delay time (in milliseconds) used in the animation
 * @property direction The direction the animation will appear to move
 * @property distance The distance an animation will travel from its center
 * @property id ID for the animation.
 *   Used by server and clients to identify a specific animation.
 * @property runCount The number of times the animation should be run. `-1` means until stopped.
 * @property section The id of the section of the strip that will be running the whole animation
 *   (not necessarily the section running this animation, such as if this is a subanimation).
 *   An empty string means the whole strip.
 * @property spacing Spacing used in the animation
 * @property sourceParams The [AnimationToRunParams] instance that created this [RunningAnimationParams]
 */
@Suppress("DataClassPrivateConstructor")
@Serializable
@SerialName("RunningAnimationParams")
data class RunningAnimationParams private constructor(
    val animationName: String,
    val colors: List<PreparedColorContainer>,
    val center: LEDLocation,
    val delay: Long,
    val direction: Direction,
    val distance: Int,
    val id: String,
    val runCount: Int,
    val section: String,
    val spacing: Int,
    val sourceParams: AnimationToRunParams,
) : SendableData {

    @Transient
    lateinit var animation: Animation

    /**
     * Constructor so we can have the [animation] parameter but not include it in serialization
     */
    constructor(
        animation: Animation,
        animationName: String,
        colors: List<PreparedColorContainer>,
        center: LEDLocation,
        delay: Long,
        direction: Direction,
        distance: Int,
        id: String,
        runCount: Int,
        section: String,
        spacing: Int,
        sourceParams: AnimationToRunParams,
    ) : this(animationName, colors, center, delay, direction, distance, id, runCount, section, spacing, sourceParams) {
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
        colors: MutableList<ColorContainerInterface> = this.colors.toMutableList(),
        center: LEDLocation = this.center,
        delay: Long = this.sourceParams.delay,
        delayMod: Double = this.sourceParams.delayMod,
        direction: Direction = this.direction,
        distance: Int = this.distance,
        id: String = this.id,
        runCount: Int = this.runCount,
        section: String = this.section,
        spacing: Int = this.spacing,
    ): AnimationToRunParams = AnimationToRunParams(animation, colors, center, delay, delayMod,
                                                   direction, distance, id, runCount, section, spacing)

    override fun toHumanReadableString(): String = toString()
}
