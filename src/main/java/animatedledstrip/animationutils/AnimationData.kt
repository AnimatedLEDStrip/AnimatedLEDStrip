package animatedledstrip.animationutils

/*
 *  Copyright (c) 2019 AnimatedLEDStrip
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


import animatedledstrip.colors.ColorContainer
import animatedledstrip.colors.ColorContainerInterface
import animatedledstrip.colors.PreparedColorContainer
import animatedledstrip.colors.ccpresets.CCBlack
import animatedledstrip.utils.parseHex
import java.io.Serializable

/**
 * Class used when calling animations to specify colors, parameters, etc.
 * for the animation.
 */
open class AnimationData() : Serializable {

    /* parameters */

    /**
     * The animation to be run (REQUIRED).
     */
    lateinit var animation: Animation

    /**
     * The list of ColorContainers
     */
    val colors = mutableListOf<ColorContainerInterface>()

    lateinit var pCols: MutableList<PreparedColorContainer>

    /**
     * Specifies if the animation will run endlessly until stopped.
     */
    var continuous = true

    /**
     * Delay time (in milliseconds) used in the animation.
     */
    var delay = 0L
        get() {
            return (when (field) {
                0L -> {
                    when (animationInfoMap[animation]?.delay) {
                        ReqLevel.REQUIRED -> throw Exception("Animation delay required for $animation")
                        ReqLevel.OPTIONAL -> animationInfoMap[animation]?.delayDefault ?: 50L
                        ReqLevel.NOTUSED -> 50L
                        null -> 50L
                    }
                }
                else -> field
            } * delayMod).toLong()
        }

    /**
     * Multiplier for the `delay` value.
     */
    var delayMod = 1.0

    /**
     * 'Direction' the animation should run.
     */
    var direction = Direction.FORWARD

    /**
     * Last pixel on the strip that will show the animation (inclusive).
     */
    var endPixel = 0

    /**
     * ID for the animation (used by server and client for stopping continuous
     * animations).
     */
    var id = ""

    /**
     * Spacing used in the animation.
     */
    var spacing = 0
        get() {
            return (when (field) {
                0 -> {
                    when (animationInfoMap[animation]?.spacing) {
                        ReqLevel.REQUIRED -> throw Exception("Animation spacing required for $animation")
                        ReqLevel.OPTIONAL -> animationInfoMap[animation]?.spacingDefault ?: 3
                        ReqLevel.NOTUSED -> 3
                        null -> 3
                    }
                }
                else -> field
            })
        }

    /**
     * First pixel on the strip will show the animation.
     */
    var startPixel = 0


    /* Helper functions for setting values */

    /**
     * Sets the `animation` parameter.
     *
     * @param animation The animation to run.
     */
    fun animation(animation: Animation): AnimationData {
        this.animation = animation
        return this
    }


    /**
     * Set the color using a ColorContainer, hex String, or Int or Long
     * in range(0..16777215)
     *
     * @param color
     * @param index The index of the color in the list of colors
     */
    fun color(color: Any, index: Int = 0): AnimationData {

        if (colors.size <= index) for (i in colors.size..index) colors += CCBlack

        when (color) {
            is ColorContainerInterface -> colors[index] = color.toColorContainer()
            is Long -> colors[index] = ColorContainer(color)
            is Int -> colors[index] = ColorContainer(color.toLong())
            is String -> colors[index] = ColorContainer(parseHex(color))
        }

        return this
    }

    fun addColor(color: Any): AnimationData {
        when (color) {
            is ColorContainerInterface -> colors += color.toColorContainer()
            is Long -> colors += ColorContainer(color)
            is Int -> colors += ColorContainer(color.toLong())
            is String -> colors += ColorContainer(parseHex(color))
        }

        return this
    }

    /* Helpers for setting the first 5 ColorContainers */

    fun color0(color: Any) = color(color, 0)
    fun color1(color: Any) = color(color, 1)
    fun color2(color: Any) = color(color, 2)
    fun color3(color: Any) = color(color, 3)
    fun color4(color: Any) = color(color, 4)

    /**
     * Set the `continuous` parameter.
     *
     * @param continuous A `Boolean`
     */
    fun continuous(continuous: Boolean): AnimationData {
        this.continuous = continuous
        return this
    }

    /**
     * Set the `delay` parameter.
     *
     * @param delay An `Int` representing the delay time in milliseconds the
     * animation will use
     */
    fun delay(delay: Int): AnimationData {
        this.delay = delay.toLong()
        return this
    }

    /**
     * Set the `delay` parameter.
     *
     * @param delay A `Long` representing the delay time in milliseconds the
     * animation will use
     */
    fun delay(delay: Long): AnimationData {
        this.delay = delay
        return this
    }

    /**
     * Set the `delayMod` parameter.
     *
     * @param delayMod A `Double` that is a multiplier for `delay`
     */
    fun delayMod(delayMod: Double): AnimationData {
        this.delayMod = delayMod
        return this
    }

    /**
     * Set the `direction` parameter.
     *
     * @param direction A `Direction` value ([Direction].`FORWARD` or [Direction].`BACKWARD`)
     */
    fun direction(direction: Direction): AnimationData {
        this.direction = direction
        return this
    }

    /**
     * Set the `direction` parameter with a `Char`.
     *
     * @param direction A `Char` representing `Direction.FORWARD` ('`F`') or
     * `Direction.BACKWARD` ('`B`')
     */
    fun direction(direction: Char): AnimationData {
        this.direction = when (direction) {
            'F', 'f' -> Direction.FORWARD
            'B', 'b' -> Direction.BACKWARD
            else -> throw Exception("Direction chars can be 'F' or 'B'")
        }
        return this
    }

    /**
     * Set the `endPixel` parameter.
     *
     * @param endPixel An `Int` that is the index of the last pixel showing the
     * animation (inclusive)
     */
    fun endPixel(endPixel: Int): AnimationData {
        this.endPixel = endPixel
        return this
    }

    /**
     * Set the `id` parameter.
     *
     * @param id A `String` used to identify a continuous animation instance
     */
    fun id(id: String): AnimationData {
        this.id = id
        return this
    }

    /**
     * Set the `spacing` parameter.
     *
     * @param spacing An `Int` that is the spacing used by the animation
     */
    fun spacing(spacing: Int): AnimationData {
        this.spacing = spacing
        return this
    }

    /**
     * Set the `startPixel` parameter.
     *
     * @param startPixel An `Int` that is the index of the first pixel showing the
     * animation (inclusive)
     */
    fun startPixel(startPixel: Int): AnimationData {
        this.startPixel = startPixel
        return this
    }

    /* Utility Functions */

    /**
     * Create a `String` out of the values of this instance.
     */
    override fun toString() =
            "$animation: $colors, $continuous, $delay, $direction, $id, $spacing"

    override fun equals(other: Any?): Boolean {
        if (other !is AnimationData) return false
        if (animation != other.animation) return false
        if (colors != other.colors) return false
        if (continuous != other.continuous) return false
        if (delay != other.delay) return false
        if (delayMod != other.delayMod) return false
        if (direction != other.direction) return false
        if (endPixel != other.endPixel) return false
        if (id != other.id) return false
        if (spacing != other.spacing) return false
        if (startPixel != other.startPixel) return false

        return true
    }

    override fun hashCode(): Int {
        var result = animation.hashCode()
        result = 31 * result + colors.hashCode()
        result = 31 * result + continuous.hashCode()
        result = 31 * result + delayMod.hashCode()
        result = 31 * result + direction.hashCode()
        result = 31 * result + endPixel
        result = 31 * result + id.hashCode()
        result = 31 * result + startPixel
        return result
    }

}
