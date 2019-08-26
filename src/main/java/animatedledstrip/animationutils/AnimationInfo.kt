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


import animatedledstrip.leds.AnimatedLEDStrip

/**
 * Helper data class for specifying what parameters are required, optional and
 * not used for an animation along with default values for `delay` and `spacing`,
 * when applicable.
 *
 * @property abbr The abbreviation used to identify this animation
 * @property numReqColors The minimum number of colors required for the animation
 * @property numOptColors The number of additional colors beyond the required number
 * supported by the animation
 * @property delay Delay in animation
 * @property delayDefault Default if delay is missing
 * @property direction Direction of animation
 * @property spacing Spacing for animation
 * @property spacingDefault Default if spacing is missing
 */
data class AnimationInfo(
        val abbr: String = "",
        val numReqColors: Int = 0,
        val numOptColors: Int = 0,
        val delay: ReqLevel = ReqLevel.NOTUSED,
        val delayDefault: Long = 0,
        val direction: ReqLevel = ReqLevel.NOTUSED,
        val repetitive: Boolean = false,
        val spacing: ReqLevel = ReqLevel.NOTUSED,
        val spacingDefault: Int = 0
) {
    val numColors = numReqColors + numOptColors
}

/**
 * Helper enum for specifying the requirement level of an animation parameter.
 *
 */
enum class ReqLevel {
    /**
     * Animation parameter must be set by the user
     */
    REQUIRED,
    /**
     * Animation parameter may be set by the user, otherwise will be set to a
     * default as specified (`color#` will default to [animatedledstrip.colors.ccpresets.CCBlack])
     */
    OPTIONAL,
    /**
     * Animation does not use parameter
     */
    NOTUSED
}

/**
 * Info about the Alternate animation.
 *
 * @see AnimatedLEDStrip.alternate
 */
val Alternate = AnimationInfo(
        abbr = "ALT",
        numReqColors = 2,
        delay = ReqLevel.OPTIONAL,
        delayDefault = 1000,
        repetitive = true
)


/**
 * Info about the Bounce animation.
 *
 * @see AnimatedLEDStrip.bounce
 */
val Bounce = AnimationInfo(
        abbr = "BNC",
        numReqColors = 1,
        numOptColors = 1,
        delay = ReqLevel.OPTIONAL,
        delayDefault = 10,
        repetitive = true
)


/**
 * Info about the Bounce to Color animation.
 *
 * @see AnimatedLEDStrip.bounceToColor
 */
val BounceToColor = AnimationInfo(
        abbr = "BTC",
        numReqColors = 1,
        delay = ReqLevel.OPTIONAL,
        delayDefault = 5
)


/**
 * Info about the Meteor animation.
 *
 * @see AnimatedLEDStrip.meteor
 */
val Meteor = AnimationInfo(
        abbr = "PXRT",
        numReqColors = 1,
        numOptColors = 1,
        delay = ReqLevel.OPTIONAL,
        delayDefault = 10,
        direction = ReqLevel.REQUIRED,
        repetitive = true
)


/**
 * Info about the Multi-Pixel Run animation.
 *
 * @see AnimatedLEDStrip.multiPixelRun
 */
val MultiPixelRun = AnimationInfo(
        abbr = "MPR",
        numReqColors = 1,
        numOptColors = 1,
        delay = ReqLevel.OPTIONAL,
        delayDefault = 100,
        direction = ReqLevel.REQUIRED,
        repetitive = true,
        spacing = ReqLevel.OPTIONAL,
        spacingDefault = 3
)


/**
 * Info about the Multi-Pixel Run to Color animation.
 *
 * @see AnimatedLEDStrip.multiPixelRunToColor
 */
val MultiPixelRunToColor = AnimationInfo(
        abbr = "MTC",
        numReqColors = 1,
        delay = ReqLevel.OPTIONAL,
        delayDefault = 150,
        direction = ReqLevel.REQUIRED,
        spacing = ReqLevel.OPTIONAL,
        spacingDefault = 3
)


/**
 * Info about the Pixel Marathon animation.
 *
 *
 */
val PixelMarathon = AnimationInfo(
        abbr = "PXM",
        numReqColors = 5,
        delay = ReqLevel.OPTIONAL,
        delayDefault = 8,
        repetitive = true
)


/**
 * Info about the Pixel Run animation.
 *
 * @see AnimatedLEDStrip.pixelRun
 */
val PixelRun = AnimationInfo(
        abbr = "PXR",
        numReqColors = 1,
        numOptColors = 1,
        delay = ReqLevel.OPTIONAL,
        delayDefault = 10,
        direction = ReqLevel.REQUIRED,
        repetitive = true
)


/**
 * Info about the Smooth Chase animation.
 *
 * @see AnimatedLEDStrip.smoothChase
 */
val SmoothChase = AnimationInfo(
        abbr = "SCH",
        numReqColors = 1,
        delay = ReqLevel.OPTIONAL,
        delayDefault = 50,
        direction = ReqLevel.REQUIRED,
        repetitive = true
)


/**
 * Info about the Smooth Fade animation.
 *
 * @see AnimatedLEDStrip.smoothFade
 */
val SmoothFade = AnimationInfo(
        abbr = "SMF",
        numReqColors = 1,
        delay = ReqLevel.OPTIONAL,
        delayDefault = 50,
        repetitive = true
)

/**
 * Info about the Sparkle animation.
 *
 * @see AnimatedLEDStrip.sparkle
 */
val Sparkle = AnimationInfo(
        abbr = "SPK",
        numReqColors = 1,
        delay = ReqLevel.OPTIONAL,
        delayDefault = 50,
        repetitive = true
)


/**
 * Info about the Sparkle Fade animation.
 *
 * @see AnimatedLEDStrip.sparkleFade
 */
val SparkleFade = AnimationInfo(
        abbr = "SPF",
        numReqColors = 1,
        numOptColors = 1,
        delay = ReqLevel.OPTIONAL,
        delayDefault = 50,
        repetitive = true
)


/**
 * Info about the Sparkle to Color animation.
 *
 * @see AnimatedLEDStrip.sparkleToColor
 */
val SparkleToColor = AnimationInfo(
        abbr = "STC",
        numReqColors = 1,
        delay = ReqLevel.OPTIONAL,
        delayDefault = 50
)


val Splat = AnimationInfo(
        abbr = "SPT",
        numReqColors = 1,
        delay = ReqLevel.OPTIONAL,
        delayDefault = 5
)


/**
 * Info about the Stack animation.
 *
 * @see AnimatedLEDStrip.stack
 */
val Stack = AnimationInfo(
        abbr = "STK",
        numReqColors = 1,
        delay = ReqLevel.OPTIONAL,
        delayDefault = 10,
        direction = ReqLevel.REQUIRED
)


/**
 * Info about the Stack Overflow animation.
 *
 *
 */
val StackOverflow = AnimationInfo(
        abbr = "STO",
        numReqColors = 2,
        repetitive = true
)


/**
 * Info about the Wipe animation.
 *
 * @see AnimatedLEDStrip.wipe
 */
val Wipe = AnimationInfo(
        abbr = "WIP",
        numReqColors = 1,
        delay = ReqLevel.OPTIONAL,
        delayDefault = 10,
        direction = ReqLevel.REQUIRED
)


/**
 * Map of animations to their `AnimationInfo` instances.
 */
val animationInfoMap = mapOf(
        Animation.ALTERNATE to Alternate,
        Animation.BOUNCE to Bounce,
        Animation.BOUNCETOCOLOR to BounceToColor,
        Animation.METEOR to Meteor,
        Animation.MULTIPIXELRUN to MultiPixelRun,
        Animation.MULTIPIXELRUNTOCOLOR to MultiPixelRunToColor,
        Animation.PIXELMARATHON to PixelMarathon,
        Animation.PIXELRUN to PixelRun,
        Animation.SMOOTHCHASE to SmoothChase,
        Animation.SMOOTHFADE to SmoothFade,
        Animation.SPARKLE to Sparkle,
        Animation.SPARKLEFADE to SparkleFade,
        Animation.SPARKLETOCOLOR to SparkleToColor,
        Animation.SPLAT to Splat,
        Animation.STACK to Stack,
        Animation.STACKOVERFLOW to StackOverflow,
        Animation.WIPE to Wipe
)