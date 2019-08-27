package animatedledstrip.animationutils.animationinfo

import animatedledstrip.animationutils.Animation
import animatedledstrip.animationutils.AnimationInfo
import animatedledstrip.animationutils.ReqLevel
import animatedledstrip.leds.AnimatedLEDStrip

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


val Ripple = AnimationInfo(
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