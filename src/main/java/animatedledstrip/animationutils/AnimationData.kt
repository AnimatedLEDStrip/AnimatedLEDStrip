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


import animatedledstrip.colors.ColorContainerInterface
import animatedledstrip.colors.PreparedColorContainer
import java.io.Serializable

/**
 * Class used when calling animations to specify colors, parameters, etc.
 * for the animation.
 */
open class AnimationData : Serializable {

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

    var center = -1

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
                    check(this::animation.isInitialized)
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
        set(value) {
            delayMod = 1.0
            speed = AnimationSpeed.CUSTOM
            field = value
        }

    /**
     * Multiplier for the `delay` value.
     */
    var delayMod = 1.0
        set(value) {
            field = value
            when (value) {
                0.5 -> if (speed != AnimationSpeed.SLOW) speed = AnimationSpeed.SLOW
                1.0 -> if (speed != AnimationSpeed.DEFAULT) speed = AnimationSpeed.DEFAULT
                2.0 -> if (speed != AnimationSpeed.FAST) speed = AnimationSpeed.FAST
                else -> speed = AnimationSpeed.CUSTOM
            }
        }

    /**
     * 'Direction' the animation should run.
     */
    var direction = Direction.FORWARD


    var distance = -1

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
                    check(this::animation.isInitialized)
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

    var speed = AnimationSpeed.DEFAULT
        set(value) {
            field = value
            when (value) {
                AnimationSpeed.SLOW -> if (delayMod != 0.5) delayMod = 0.5
                AnimationSpeed.DEFAULT -> if (delayMod != 1.0) delayMod = 1.0
                AnimationSpeed.FAST -> if (delayMod != 2.0) delayMod = 2.0
                AnimationSpeed.CUSTOM -> {
                }
            }
        }

    /**
     * First pixel on the strip will show the animation.
     */
    var startPixel = 0


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
