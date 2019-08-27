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


import animatedledstrip.animationutils.animationinfo.animationInfoMap
import animatedledstrip.colors.ColorContainer
import animatedledstrip.colors.ColorContainerInterface
import animatedledstrip.colors.PreparedColorContainer
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
 * @property _delay Delay time (in milliseconds) used in the animation
 * @property delayMod Multiplier for `delay`
 * @property direction The direction the animation will run
 * @property distance The distance a radial animation will travel from its center.
 * Defaults to running until the ends of the strip.
 * @property endPixel Last pixel on the strip that will show the
 * animation (inclusive)
 * @property id ID for the animation. Used by server and client for
 * stopping continuous animations.
 * @property _spacing Spacing used in the animation
 * @property startPixel First pixel on the strip will show the animation
 */
data class AnimationData(
    var animation: Animation = Animation.COLOR,
    val colors: MutableList<ColorContainerInterface> = mutableListOf(),
    var center: Int = -1,
    var continuous: Boolean = true,
    private var _delay: Long = -1L,
    var delayMod: Double = 1.0,
    var direction: Direction = Direction.FORWARD,
    var distance: Int = -1,
    var endPixel: Int = -1,
    var id: String = "",
    private var _spacing: Int = -1,
    var startPixel: Int = 0
) : Serializable {

    lateinit var pCols: MutableList<PreparedColorContainer>

    /**
     * Delay time (in milliseconds) used in the animation.
     */
    var delay: Long
        get() {
            return (when (_delay) {
                -1L, 0L -> {
                    when (animationInfoMap[animation]?.delay) {
                        ReqLevel.REQUIRED -> throw Exception("Animation delay required for $animation")
                        ReqLevel.OPTIONAL -> animationInfoMap[animation]?.delayDefault ?: 50L
                        ReqLevel.NOTUSED -> 50L
                        null -> 50L
                    }
                }
                else -> _delay
            } * delayMod).toLong()
        }
        set(value) {
            _delay = value
        }


    /**
     * Spacing used in the animation.
     */
    var spacing: Int
        get() {
            return (when (_spacing) {
                -1, 0 -> {
                    when (animationInfoMap[animation]?.spacing) {
                        ReqLevel.REQUIRED -> throw Exception("Animation spacing required for $animation")
                        ReqLevel.OPTIONAL -> animationInfoMap[animation]?.spacingDefault ?: 3
                        ReqLevel.NOTUSED -> 3
                        null -> 3
                    }
                }
                else -> _spacing
            })
        }
        set(value) {
            _spacing = value
        }
}
