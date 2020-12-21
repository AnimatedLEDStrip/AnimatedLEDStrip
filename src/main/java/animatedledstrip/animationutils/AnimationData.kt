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
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes

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
class AnimationData(
    var animation: String = "Color",
    colors: List<ColorContainerInterface> = listOf(),
    var center: Int = -1,
    delay: Long = -1L,
    var delayMod: Double = 1.0,
    var direction: Direction = Direction.FORWARD,
    var distance: Int = -1,
    var id: String = "",
    runCount: Int = 0,
    var section: String = "",
    spacing: Int = -1,
) : SendableData {

    companion object {
        const val prefix = "DATA"

        object ExStrategy : ExclusionStrategy {
            override fun shouldSkipClass(p0: Class<*>?) = false

            override fun shouldSkipField(field: FieldAttributes): Boolean {
                if (field.declaringClass != AnimationData::class.java)
                    return false
                return when (field.name) {
                    "pCols" -> true
                    else -> false
                }
            }
        }
    }

    override val prefix = AnimationData.prefix

    val colors = mutableListOf<ColorContainerInterface>().apply {
        addAll(colors)
    }

    lateinit var pCols: MutableList<PreparedColorContainer>

    private var baseDelay: Long = delay
        get() {
            return when (field) {
                -1L, 0L -> findAnimationOrNull(animId = animation)?.info?.delayDefault ?: DEFAULT_DELAY
                else -> field
            }
        }

    var delay: Long
        get() = (baseDelay * delayMod).toLong()
        set(value) {
            baseDelay = value
        }

    var runCount: Int = runCount
        get() {
            return (when (field) {
                0 -> findAnimationOrNull(animId = animation)?.info?.runCountDefault ?: -1
                else -> field
            })
        }

    var spacing: Int = spacing
        get() {
            return (when (field) {
                -1, 0 -> findAnimationOrNull(animId = animation)?.info?.spacingDefault ?: DEFAULT_SPACING
                else -> field
            })
        }


    /**
     * Prepare the `AnimationData` instance for use by the specified `AnimatedLedStrip.Section`.
     *
     * Sets defaults for properties with length-dependent defaults (`center` and `distance`)
     * and populates `pCols`.
     */
    fun prepare(ledStrip: AnimatedLEDStrip.Section): AnimationData {
        section = when (section) {
            "" -> ledStrip.name
            else -> section
        }

        val definedAnimation = findAnimation(animation)

        val sectionRunningFullAnimation = ledStrip.getSection(sectionName = section)

        center = when (center) {
            -1 -> ledStrip.numLEDs / 2
            else -> center
        }

        distance = when (distance) {
            -1 -> if (definedAnimation.info.distanceDefault != -1) definedAnimation.info.distanceDefault else ledStrip.numLEDs
            else -> distance
        }

        if (colors.isEmpty()) color(CCBlack)

        if (ledStrip == sectionRunningFullAnimation) {
            pCols = mutableListOf()
            colors.forEach {
                pCols.add(it.prepare(numLEDs = sectionRunningFullAnimation.numLEDs))
            }

            for (i in colors.size until definedAnimation.info.minimumColors) {
                pCols.add(CCBlack.prepare(numLEDs = sectionRunningFullAnimation.numLEDs))
            }
        } else {
            val temp = mutableListOf<PreparedColorContainer>()
            for (c in pCols) {
                temp += PreparedColorContainer(
                    colors = c.colors.subList(ledStrip.startPixel, ledStrip.endPixel) + c.colors[ledStrip.endPixel],
                    originalColors = c.originalColors,
                )
            }
        }

        return this
    }

    /**
     * A map used internally by animations to communicate between
     * runs of the animation (see Alternate or Cat Toy)
     */
    val extraData = mutableMapOf<String, Any?>()

    /* Note: If any other properties are added, they must be added to the five methods below */

    /**
     * Create a copy.
     */
    fun copy(
        animation: String = this.animation,
        colors: List<ColorContainerInterface> = this.colors.toList(),
        center: Int = this.center,
        delay: Long = this.baseDelay,
        delayMod: Double = this.delayMod,
        direction: Direction = this.direction,
        distance: Int = this.distance,
        id: String = this.id,
        runCount: Int = this.runCount,
        section: String = this.section,
        spacing: Int = this.spacing,
    ): AnimationData {
        val newData = AnimationData(
            animation = animation,
            colors = colors,
            center = center,
            delay = delay,
            delayMod = delayMod,
            direction = direction,
            distance = distance,
            id = id,
            runCount = runCount,
            section = section,
            spacing = spacing
        )
        if (this::pCols.isInitialized) {
            newData.pCols = mutableListOf()
            newData.pCols.addAll(pCols)
        }
        return newData
    }

    /**
     * Create a string representation.
     */
    override fun toString() =
        "AnimationData(animation=$animation, colors=$colors, center=$center, " +
        "delay=$baseDelay, delayMod=$delayMod, direction=$direction, distance=$distance, " +
        "id=$id, runCount=$runCount, section=$section, spacing=$spacing)"

    /**
     * Create a nicely formatted string representation.
     */
    override fun toHumanReadableString() =
        """
            AnimationData for $id
              animation: $animation
              colors: $colors
              center: $center
              delay: $baseDelay
              delayMod: $delayMod
              direction: $direction
              distance: $distance
              runCount: $runCount
              section: $section
              spacing: $spacing
            End AnimationData
        """.trimIndent()


    /**
     * Override `equals()`.
     */
    override fun equals(other: Any?): Boolean {
        return super.equals(other) ||
               (other is AnimationData &&
                prepareAnimIdentifier(animation) == prepareAnimIdentifier(other.animation) &&
                colors == other.colors &&
                center == other.center &&
                delay == other.delay &&
                delayMod == other.delayMod &&
                direction == other.direction &&
                distance == other.distance &&
                id == other.id &&
                runCount == other.runCount &&
                section == other.section &&
                spacing == other.spacing)
    }

    /**
     * Override `hashCode()`.
     */
    override fun hashCode(): Int {
        var result = animation.hashCode()
        result = 31 * result + center.hashCode()
        result = 31 * result + delayMod.hashCode()
        result = 31 * result + direction.hashCode()
        result = 31 * result + distance.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + runCount.hashCode()
        result = 31 * result + section.hashCode()
        result = 31 * result + spacing.hashCode()
        result = 31 * result + colors.hashCode()
        return result
    }

}
