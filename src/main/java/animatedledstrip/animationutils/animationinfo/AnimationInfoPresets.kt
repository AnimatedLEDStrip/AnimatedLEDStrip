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
val Alternate = AnimationInfo(
    animation = Animation.ALTERNATE,
    name = "Alternate",
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
    animation = Animation.BOUNCE,
    name = "Bounce",
    abbr = "BNC",
    numReqColors = 1,
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
    animation = Animation.BOUNCETOCOLOR,
    name = "Bounce to Color",
    abbr = "BTC",
    numReqColors = 1,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 5
)


val CatToy = AnimationInfo(
    animation = Animation.CATTOY,
    name = "Cat Toy",
    abbr = "CAT",
    numReqColors = 1,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 5,
    repetitive = true
    )

val Color = AnimationInfo(
    animation = Animation.COLOR,
    name = "Color",
    abbr = "COL",
    numReqColors = 1
)

/**
 * Info about the Meteor animation.
 *
 * @see AnimatedLEDStrip.meteor
 */
val Meteor = AnimationInfo(
    animation = Animation.METEOR,
    name = "Meteor",
    abbr = "PXRT",
    numReqColors = 1,
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
    animation = Animation.MULTIPIXELRUN,
    name = "Multi-Pixel Run",
    abbr = "MPR",
    numReqColors = 1,
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
    animation = Animation.MULTIPIXELRUNTOCOLOR,
    name = "Multi-Pixel Run to Color",
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
    animation = Animation.PIXELMARATHON,
    name = "Pixel Marathon",
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
    animation = Animation.PIXELRUN,
    name = "Pixel Run",
    abbr = "PXR",
    numReqColors = 1,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 10,
    direction = ReqLevel.REQUIRED,
    repetitive = true
)


/**
 * Info about the Ripple animation.
 *
 * @see AnimatedLEDStrip.ripple
 */
val Ripple = AnimationInfo(
    animation = Animation.RIPPLE,
    name = "Ripple",
    abbr = "RPL",
    numReqColors = 1,
    center = ReqLevel.OPTIONAL,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 30,
    distance = ReqLevel.OPTIONAL,
    repetitive = true
)

/**
 * Info about the Smooth Chase animation.
 *
 * @see AnimatedLEDStrip.smoothChase
 */
val SmoothChase = AnimationInfo(
    animation = Animation.SMOOTHCHASE,
    name = "Smooth Chase",
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
    animation = Animation.SMOOTHFADE,
    name = "Smooth Fade",
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
    animation = Animation.SPARKLE,
    name = "Sparkle",
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
    animation = Animation.SPARKLEFADE,
    name = "Sparkle Fade",
    abbr = "SPF",
    numReqColors = 1,
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
    animation = Animation.SPARKLETOCOLOR,
    name = "Sparkle to Color",
    abbr = "STC",
    numReqColors = 1,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 50
)


/**
 * Info about the Splat animation.
 *
 * @see AnimatedLEDStrip.splat
 */
val Splat = AnimationInfo(
    animation = Animation.SPLAT,
    name = "Splat",
    abbr = "SPT",
    numReqColors = 1,
    center = ReqLevel.OPTIONAL,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 5,
    distance = ReqLevel.OPTIONAL
)


/**
 * Info about the Stack animation.
 *
 * @see AnimatedLEDStrip.stack
 */
val Stack = AnimationInfo(
    animation = Animation.STACK,
    name = "Stack",
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
    animation = Animation.STACKOVERFLOW,
    name = "Stack Overflow",
    abbr = "STO",
    numReqColors = 2,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 2,
    repetitive = true
)


/**
 * Info about the Wipe animation.
 *
 * @see AnimatedLEDStrip.wipe
 */
val Wipe = AnimationInfo(
    animation = Animation.WIPE,
    name = "Wipe",
    abbr = "WIP",
    numReqColors = 1,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 10,
    direction = ReqLevel.REQUIRED
)


val animationInfoList = listOf(
    Alternate,
    Bounce,
    BounceToColor,
    CatToy,
    Color,
    Meteor,
    MultiPixelRun,
    MultiPixelRunToColor,
    PixelMarathon,
    PixelRun,
    Ripple,
    SmoothChase,
    SmoothFade,
    Sparkle,
    SparkleFade,
    SparkleToColor,
    Splat,
    Stack,
    StackOverflow,
    Wipe
)

///**
// * Map of animations to their `AnimationInfo` instances.
// */
//@Suppress("EXPERIMENTAL_API_USAGE")
//val animationInfoMap = mapOf(
//    Animation.ALTERNATE to Alternate,
//    Animation.BOUNCE to Bounce,
//    Animation.BOUNCETOCOLOR to BounceToColor,
//    Animation.CATTOY to CatToy,
//    Animation.METEOR to Meteor,
//    Animation.MULTIPIXELRUN to MultiPixelRun,
//    Animation.MULTIPIXELRUNTOCOLOR to MultiPixelRunToColor,
//    Animation.PIXELMARATHON to PixelMarathon,
//    Animation.PIXELRUN to PixelRun,
//    Animation.RIPPLE to Ripple,
//    Animation.SMOOTHCHASE to SmoothChase,
//    Animation.SMOOTHFADE to SmoothFade,
//    Animation.SPARKLE to Sparkle,
//    Animation.SPARKLEFADE to SparkleFade,
//    Animation.SPARKLETOCOLOR to SparkleToColor,
//    Animation.SPLAT to Splat,
//    Animation.STACK to Stack,
//    Animation.STACKOVERFLOW to StackOverflow,
//    Animation.WIPE to Wipe
//)