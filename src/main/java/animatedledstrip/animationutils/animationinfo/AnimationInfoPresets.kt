/*
 *  Copyright (c) 2018-2020 AnimatedLEDStrip
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

package animatedledstrip.animationutils.animationinfo

import animatedledstrip.animationutils.Animation
import animatedledstrip.animationutils.AnimationInfo
import animatedledstrip.animationutils.ReqLevel

/**
 * Info about the Alternate animation.
 *
 * @see animatedledstrip.animationutils.alternate
 */
val Alternate = AnimationInfo(
    animation = Animation.ALTERNATE,
    name = "Alternate",
    abbr = "ALT",
    numReqColors = 2,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 1000
)


/**
 * Info about the Bounce animation.
 *
 * @see animatedledstrip.animationutils.bounce
 */
val Bounce = AnimationInfo(
    animation = Animation.BOUNCE,
    name = "Bounce",
    abbr = "BNC",
    numReqColors = 1,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 10
)


/**
 * Info about the Bounce to Color animation.
 *
 * @see animatedledstrip.animationutils.bounceToColor
 */
val BounceToColor = AnimationInfo(
    animation = Animation.BOUNCETOCOLOR,
    name = "Bounce to Color",
    abbr = "BTC",
    numReqColors = 1,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 5
)


/**
 * Info about the Cat Toy animation.
 *
 * @see animatedledstrip.animationutils.catToy
 */
val CatToy = AnimationInfo(
    animation = Animation.CATTOY,
    name = "Cat Toy",
    abbr = "CAT",
    numReqColors = 1,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 5
)

/**
 * Info about the Cat Toy to Color animation.
 *
 * @see animatedledstrip.animationutils.catToyToColor
 */
val CatToyToColor = AnimationInfo(
    animation = Animation.CATTOYTOCOLOR,
    name = "Cat Toy to Color",
    abbr = "CTC",
    numReqColors = 1,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 5
)


/**
 * Info about the Color animation.
 */
val Color = AnimationInfo(
    animation = Animation.COLOR,
    name = "Color",
    abbr = "COL",
    numReqColors = 1
)

/**
 * Info about the Fade to Color animation.
 *
 * @see animatedledstrip.animationutils.fadeToColor
 */
val FadeToColor = AnimationInfo(
    animation = Animation.FADETOCOLOR,
    name = "Fade to Color",
    abbr = "FTC",
    numReqColors = 1,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 30
)

/**
 * Info about the Fireworks animation
 *
 * @see animatedledstrip.animationutils.fireworks
 */
val Fireworks = AnimationInfo(
    animation = Animation.FIREWORKS,
    name = "Fireworks",
    abbr = "FWS",
    numReqColors = 1,
    numOptColors = 4,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 30,
    distance = ReqLevel.OPTIONAL,
    distanceDefault = 20
)

/**
 * Info about the Meteor animation.
 *
 * @see animatedledstrip.animationutils.meteor
 */
val Meteor = AnimationInfo(
    animation = Animation.METEOR,
    name = "Meteor",
    abbr = "PXRT",
    numReqColors = 1,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 10,
    direction = ReqLevel.REQUIRED
)


/**
 * Info about the Multi-Pixel Run animation.
 *
 * @see animatedledstrip.animationutils.multiPixelRun
 */
val MultiPixelRun = AnimationInfo(
    animation = Animation.MULTIPIXELRUN,
    name = "Multi-Pixel Run",
    abbr = "MPR",
    numReqColors = 1,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 100,
    direction = ReqLevel.REQUIRED,
    spacing = ReqLevel.OPTIONAL,
    spacingDefault = 3
)


/**
 * Info about the Multi-Pixel Run to Color animation.
 *
 * @see animatedledstrip.animationutils.multiPixelRunToColor
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
 * @see animatedledstrip.animationutils.pixelMarathon
 */
val PixelMarathon = AnimationInfo(
    animation = Animation.PIXELMARATHON,
    name = "Pixel Marathon",
    abbr = "PXM",
    numReqColors = 5,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 8
)


/**
 * Info about the Pixel Run animation.
 *
 * @see animatedledstrip.animationutils.pixelRun
 */
val PixelRun = AnimationInfo(
    animation = Animation.PIXELRUN,
    name = "Pixel Run",
    abbr = "PXR",
    numReqColors = 1,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 10,
    direction = ReqLevel.REQUIRED
)


/**
 * Info about the Ripple animation.
 *
 * @see animatedledstrip.animationutils.ripple
 */
val Ripple = AnimationInfo(
    animation = Animation.RIPPLE,
    name = "Ripple",
    abbr = "RPL",
    numReqColors = 1,
    center = ReqLevel.OPTIONAL,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 30,
    distance = ReqLevel.OPTIONAL
)

/**
 * Info about the Smooth Chase animation.
 *
 * @see animatedledstrip.animationutils.smoothChase
 */
val SmoothChase = AnimationInfo(
    animation = Animation.SMOOTHCHASE,
    name = "Smooth Chase",
    abbr = "SCH",
    numReqColors = 1,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 50,
    direction = ReqLevel.REQUIRED
)


/**
 * Info about the Smooth Fade animation.
 *
 * @see animatedledstrip.animationutils.smoothFade
 */
val SmoothFade = AnimationInfo(
    animation = Animation.SMOOTHFADE,
    name = "Smooth Fade",
    abbr = "SMF",
    numReqColors = 1,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 50
)

/**
 * Info about the Sparkle animation.
 *
 * @see animatedledstrip.animationutils.sparkle
 */
val Sparkle = AnimationInfo(
    animation = Animation.SPARKLE,
    name = "Sparkle",
    abbr = "SPK",
    numReqColors = 1,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 50
)


/**
 * Info about the Sparkle Fade animation.
 *
 * @see animatedledstrip.animationutils.sparkleFade
 */
val SparkleFade = AnimationInfo(
    animation = Animation.SPARKLEFADE,
    name = "Sparkle Fade",
    abbr = "SPF",
    numReqColors = 1,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 50
)


/**
 * Info about the Sparkle to Color animation.
 *
 * @see animatedledstrip.animationutils.sparkleToColor
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
 * @see animatedledstrip.animationutils.splat
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
 * @see animatedledstrip.animationutils.stack
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
 * @see animatedledstrip.animationutils.stackOverflow
 */
val StackOverflow = AnimationInfo(
    animation = Animation.STACKOVERFLOW,
    name = "Stack Overflow",
    abbr = "STO",
    numReqColors = 2,
    delay = ReqLevel.OPTIONAL,
    delayDefault = 2
)


/**
 * Info about the Wipe animation.
 *
 * @see animatedledstrip.animationutils.wipe
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


/**
 * A list of all `AnimationInfo` presets
 */
val animationInfoList = listOf(
    Alternate,
    Bounce,
    BounceToColor,
    CatToy,
    CatToyToColor,
    Color,
    FadeToColor,
    Fireworks,
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
