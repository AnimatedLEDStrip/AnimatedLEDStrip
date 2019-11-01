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
import animatedledstrip.utils.infoOrNull
import java.io.Serializable

/**
 * Class used when calling animations to specify colors, parameters, etc.
 * for the animation.
 *
 * @property animation The animation to be run
 * @property colors The list of [ColorContainer]s to use
 * @property center The pixel at the center of a radial animation.
 * Defaults to the center of the strip.
 * @property continuous If the animation will run endlessly until stopped
 * @property delay Delay time (in milliseconds) used in the animation
 * @property delayMod Multiplier for `delay`
 * @property direction The direction the animation will run
 * @property distance The distance a radial animation will travel from its center.
 * Defaults to running until the ends of the strip.
 * @property endPixel Last pixel on the strip that will show the
 * animation (inclusive)
 * @property id ID for the animation. Used by server and client for
 * stopping continuous animations.
 * @property pCols The list of [PreparedColorContainer]s after preparation of [colors]
 * @property spacing Spacing used in the animation
 * @property startPixel First pixel on the strip will show the animation
 */
class AnimationData(
    var animation: Animation = Animation.COLOR,
    colors: List<ColorContainerInterface> = listOf(),
    var center: Int = -1,
    var continuous: Boolean? = null,
    delay: Long = -1L,
    var delayMod: Double = 1.0,
    var direction: Direction = Direction.FORWARD,
    var distance: Int = -1,
    var endPixel: Int = -1,
    var id: String = "",
    spacing: Int = -1,
    var startPixel: Int = 0
) : Serializable {

    val colors = mutableListOf<ColorContainerInterface>().apply {
        addAll(colors)
    }

    lateinit var pCols: MutableList<PreparedColorContainer>

    var delay: Long = delay
        get() {
            return (when (field) {
                -1L, 0L -> {
                    when (animation.infoOrNull()?.delay) {
                        ReqLevel.OPTIONAL -> animation.infoOrNull()?.delayDefault ?: 50L
                        ReqLevel.NOTUSED -> 50L
                        else -> 50L
                    }
                }
                else -> field
            } * delayMod).toLong()
        }


    var spacing: Int = spacing
        get() {
            return (when (field) {
                -1, 0 -> {
                    when (animation.infoOrNull()?.spacing) {
                        ReqLevel.OPTIONAL -> animation.infoOrNull()?.spacingDefault ?: 3
                        ReqLevel.NOTUSED -> 3
                        else -> 3
                    }
                }
                else -> field
            })
        }

    /* Note: If any other properties are added, they must be added to the four methods below */

    /**
     * Create a copy of this [AnimationData] instance
     */
    fun copy(
        animation: Animation = this.animation,
        colors: List<ColorContainerInterface> = this.colors,
        center: Int = this.center,
        continuous: Boolean? = this.continuous,
        delay: Long = this.delay,
        delayMod: Double = this.delayMod,
        direction: Direction = this.direction,
        distance: Int = this.distance,
        endPixel: Int = this.endPixel,
        id: String = this.id,
        spacing: Int = this.spacing,
        startPixel: Int = this.startPixel
    ) = AnimationData(
        animation,
        colors,
        center,
        continuous,
        delay,
        delayMod,
        direction,
        distance,
        endPixel,
        id,
        spacing,
        startPixel
    )

    /**
     * Create a string representation of this [AnimationData] instance
     */
    override fun toString() =
        "AnimationData(animation=$animation, colors=$colors, center=$center, continuous=$continuous, " +
                "delay=$delay, delayMod=$delayMod, direction=$direction, distance=$distance, " +
                "endPixel=$endPixel, id=$id, spacing=$spacing, startPixel=$startPixel)"


    /**
     * Override `equals()`
     */
    override fun equals(other: Any?): Boolean {
        return super.equals(other) ||
                (other is AnimationData &&
                        animation == other.animation &&
                        colors == other.colors &&
                        center == other.center &&
                        continuous == other.continuous &&
                        delay == other.delay &&
                        delayMod == other.delayMod &&
                        direction == other.direction &&
                        distance == other.distance &&
                        endPixel == other.endPixel &&
                        id == other.id &&
                        spacing == other.spacing &&
                        startPixel == other.startPixel)
    }

    /**
     * Override `hashCode()`
     */
    override fun hashCode(): Int {
        var result = animation.hashCode()
        result = 31 * result + center
        result = 31 * result + continuous.hashCode()
        result = 31 * result + delayMod.hashCode()
        result = 31 * result + direction.hashCode()
        result = 31 * result + distance
        result = 31 * result + endPixel
        result = 31 * result + id.hashCode()
        result = 31 * result + startPixel
        result = 31 * result + colors.hashCode()
        return result
    }

}
