package animatedledstrip.animationutils.animationinfo

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


import animatedledstrip.animationutils.Animation
import animatedledstrip.animationutils.AnimationInfo
import animatedledstrip.animationutils.ReqLevel
import animatedledstrip.leds.AnimatedLEDStrip

/**
 * Info about the Alternate animation.
 *
 * @see AnimatedLEDStrip.alternate
 */
internal val Alternate = AnimationInfo(
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
internal val Bounce = AnimationInfo(
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
internal val BounceToColor = AnimationInfo(
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
internal val Meteor = AnimationInfo(
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
internal val MultiPixelRun = AnimationInfo(
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
internal val MultiPixelRunToColor = AnimationInfo(
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
internal val PixelMarathon = AnimationInfo(
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
internal val PixelRun = AnimationInfo(
    abbr = "PXR",
    numReqColors = 1,
    numOptColors = 1,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 10,
    direction = ReqLevel.REQUIRED,
    repetitive = true
)


internal val Ripple = AnimationInfo(
    abbr = "RPL",
    numReqColors = 1,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 30,
    repetitive = true
)

/**
 * Info about the Smooth Chase animation.
 *
 * @see AnimatedLEDStrip.smoothChase
 */
internal val SmoothChase = AnimationInfo(
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
internal val SmoothFade = AnimationInfo(
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
internal val Sparkle = AnimationInfo(
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
internal val SparkleFade = AnimationInfo(
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
internal val SparkleToColor = AnimationInfo(
    abbr = "STC",
    numReqColors = 1,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 50
)


internal val Splat = AnimationInfo(
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
internal val Stack = AnimationInfo(
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
internal val StackOverflow = AnimationInfo(
    abbr = "STO",
    numReqColors = 2,
    repetitive = true
)


/**
 * Info about the Wipe animation.
 *
 * @see AnimatedLEDStrip.wipe
 */
internal val Wipe = AnimationInfo(
    abbr = "WIP",
    numReqColors = 1,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 10,
    direction = ReqLevel.REQUIRED
)


/**
 * Map of animations to their `AnimationInfo` instances.
 */
@Suppress("EXPERIMENTAL_API_USAGE")
internal val animationInfoMap = mapOf(
    Animation.ALTERNATE to Alternate,
    Animation.BOUNCE to Bounce,
    Animation.BOUNCETOCOLOR to BounceToColor,
    Animation.METEOR to Meteor,
    Animation.MULTIPIXELRUN to MultiPixelRun,
    Animation.MULTIPIXELRUNTOCOLOR to MultiPixelRunToColor,
    Animation.PIXELMARATHON to PixelMarathon,
    Animation.PIXELRUN to PixelRun,
    Animation.RIPPLE to Ripple,
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