package animatedledstrip.leds

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
 * @property color1 First color
 * @property color2 Second color
 * @property color3 Third color
 * @property color4 Fourth color
 * @property color5 Fifth color
 * @property colorList List of colors
 * @property delay Delay in animation
 * @property delayDefault Default if delay is missing
 * @property direction Direction of animation
 * @property spacing Spacing for animation
 * @property spacingDefault Default if spacing is missing
 */
data class AnimationInfo(
    val abbr: String = "",
    val color1: ReqLevel = ReqLevel.NOTUSED,
    val color2: ReqLevel = ReqLevel.NOTUSED,
    val color3: ReqLevel = ReqLevel.NOTUSED,
    val color4: ReqLevel = ReqLevel.NOTUSED,
    val color5: ReqLevel = ReqLevel.NOTUSED,
    val colorList: ReqLevel = ReqLevel.NOTUSED,
    val delay: ReqLevel = ReqLevel.NOTUSED,
    val delayDefault: Int = 0,
    val direction: ReqLevel = ReqLevel.NOTUSED,
    val spacing: ReqLevel = ReqLevel.NOTUSED,
    val spacingDefault: Int = 0
)

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
     * default as specified (`color#` will default to [animatedledstrip.ccpresets.CCBlack])
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
    color1 = ReqLevel.REQUIRED,
    color2 = ReqLevel.REQUIRED,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 1000
)


/**
 * Info about the Bounce animation.
 *
 * @see AnimatedLEDStrip.bounce
 */
val Bounce = AnimationInfo(
    abbr = "BNC",
    color1 = ReqLevel.REQUIRED,
    color2 = ReqLevel.OPTIONAL,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 10
)


/**
 * Info about the Bounce to Color animation.
 *
 * @see AnimatedLEDStrip.bounceToColor
 */
val BounceToColor = AnimationInfo(
    abbr = "BTC",
    color1 = ReqLevel.REQUIRED,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 5
)


/**
 * Info about the Multi-Pixel Run animation.
 *
 * @see AnimatedLEDStrip.multiPixelRun
 */
val MultiPixelRun = AnimationInfo(
    abbr = "MPR",
    color1 = ReqLevel.REQUIRED,
    color2 = ReqLevel.OPTIONAL,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 100,
    direction = ReqLevel.REQUIRED,
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
    color1 = ReqLevel.REQUIRED,
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
    color1 = ReqLevel.REQUIRED,
    color2 = ReqLevel.REQUIRED,
    color3 = ReqLevel.REQUIRED,
    color4 = ReqLevel.REQUIRED,
    color5 = ReqLevel.REQUIRED,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 8
)


/**
 * Info about the Pixel Run animation.
 *
 * @see AnimatedLEDStrip.pixelRun
 */
val PixelRun = AnimationInfo(
    abbr = "PXR",
    color1 = ReqLevel.REQUIRED,
    color2 = ReqLevel.OPTIONAL,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 10,
    direction = ReqLevel.REQUIRED
)


/**
 * Info about the Pixel Run with Trail animation.
 *
 * @see AnimatedLEDStrip.pixelRunWithTrail
 */
val PixelRunWithTrail = AnimationInfo(
    abbr = "PXRT",
    color1 = ReqLevel.REQUIRED,
    color2 = ReqLevel.OPTIONAL,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 10,
    direction = ReqLevel.REQUIRED
)


/**
 * Info about the Smooth Chase animation.
 *
 * @see AnimatedLEDStrip.smoothChase
 */
val SmoothChase = AnimationInfo(
    abbr = "SCH",
    colorList = ReqLevel.REQUIRED,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 50,
    direction = ReqLevel.REQUIRED
)


/**
 * Info about the Smooth Fade animation.
 *
 * @see AnimatedLEDStrip.smoothFade
 */
val SmoothFade = AnimationInfo(
    abbr = "SMF",
    colorList = ReqLevel.REQUIRED,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 50
)

/**
 * Info about the Sparkle animation.
 *
 * @see AnimatedLEDStrip.sparkle
 */
val Sparkle = AnimationInfo(
    abbr = "SPK",
    color1 = ReqLevel.REQUIRED,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 50
)


/**
 * Info about the Sparkle Fade animation.
 *
 * @see AnimatedLEDStrip.sparkleFade
 */
val SparkleFade = AnimationInfo(
    abbr = "SPF",
    color1 = ReqLevel.REQUIRED,
    color2 = ReqLevel.OPTIONAL,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 50
)


/**
 * Info about the Sparkle to Color animation.
 *
 * @see AnimatedLEDStrip.sparkleToColor
 */
val SparkleToColor = AnimationInfo(
    abbr = "STC",
    color1 = ReqLevel.REQUIRED,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 50
)


/**
 * Info about the Stack animation.
 *
 * @see AnimatedLEDStrip.stack
 */
val Stack = AnimationInfo(
    abbr = "STK",
    color1 = ReqLevel.REQUIRED,
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
    color1 = ReqLevel.REQUIRED,
    color2 = ReqLevel.REQUIRED
)


/**
 * Info about the Wipe animation.
 *
 * @see AnimatedLEDStrip.wipe
 */
val Wipe = AnimationInfo(
    abbr = "WIP",
    color1 = ReqLevel.REQUIRED,
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
    Animation.MULTIPIXELRUN to MultiPixelRun,
    Animation.MULTIPIXELRUNTOCOLOR to MultiPixelRunToColor,
    Animation.PIXELMARATHON to PixelMarathon,
    Animation.PIXELRUN to PixelRun,
    Animation.PIXELRUNWITHTRAIL to PixelRunWithTrail,
    Animation.SMOOTHCHASE to SmoothChase,
    Animation.SMOOTHFADE to SmoothFade,
    Animation.SPARKLE to Sparkle,
    Animation.SPARKLEFADE to SparkleFade,
    Animation.SPARKLETOCOLOR to SparkleToColor,
    Animation.STACK to Stack,
    Animation.STACKOVERFLOW to StackOverflow,
    Animation.WIPE to Wipe
)