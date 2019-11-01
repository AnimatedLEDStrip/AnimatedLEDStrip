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


/**
 * Helper data class for specifying what parameters are required, optional and
 * not used for an animation along with default values for `delay` and `spacing`,
 * when applicable.
 *
 * @property animation The value from the Animation enum that this info corresponds to
 * @property name The name of this animation
 * @property abbr The abbreviation used to identify this animation
 * @property numReqColors The minimum number of colors required for the animation
 * @property numOptColors The number of additional colors beyond the required number
 * supported by the animation
 * @property delay Delay in animation
 * @property delayDefault Default if delay is missing
 * @property direction Direction of animation
 * @property repetitive Whether the animation is a repetitive animation
 * @property spacing Spacing for animation
 * @property spacingDefault Default if spacing is missing
 * @property numColors The total number of colors in the animation
 */
data class AnimationInfo(
    val animation: Animation,
    val name: String,
    val abbr: String,
    val numReqColors: Int = 0,
    val numOptColors: Int = 0,
    val center: ReqLevel = ReqLevel.NOTUSED,
    val delay: ReqLevel = ReqLevel.NOTUSED,
    val delayDefault: Long = 0,
    val direction: ReqLevel = ReqLevel.NOTUSED,
    val distance: ReqLevel = ReqLevel.NOTUSED,
    val spacing: ReqLevel = ReqLevel.NOTUSED,
    val spacingDefault: Int = 0
) {
    val numColors = numReqColors + numOptColors
    val repetitive: Boolean = animation.isNonRepetitive()
}
