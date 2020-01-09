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

package animatedledstrip.animationutils

import animatedledstrip.leds.AnimatedLEDStrip
import animatedledstrip.leds.LEDStrip

/**
 * A list of animations used when communicating between clients and servers.
 */
enum class Animation {
    /**
     * See [LEDStrip.setStripColor]
     */
    @NonRepetitive
    COLOR,
    /**
     * Used to represent a custom animation. Put the animation's abbreviation
     * in the AnimationData instance's ID parameter.
     */
    @NonRepetitive
    CUSTOMANIMATION,
    /**
     * Used to represent a repetitive custom animation. Put the animation's
     * abbreviation in the AnimationData instance's ID parameter.
     */
    CUSTOMREPETITIVEANIMATION,
    /**
     * See [AnimatedLEDStrip.alternate]
     */
    ALTERNATE,
    /**
     * See [AnimatedLEDStrip.bounce]
     */
    BOUNCE,
    /**
     * See [AnimatedLEDStrip.bounceToColor]
     */
    @NonRepetitive
    BOUNCETOCOLOR,
    /**
     * See [AnimatedLEDStrip.catToy]
     */
    CATTOY,
    /**
     *  See [AnimatedLEDStrip.catToyToColor]
     */
    @NonRepetitive
    CATTOYTOCOLOR,
    /**
     * See [AnimatedLEDStrip.meteor]
     */
    METEOR,
    /**
     * See [AnimatedLEDStrip.multiPixelRun]
     */
    MULTIPIXELRUN,
    /**
     * See [AnimatedLEDStrip.multiPixelRunToColor]
     */
    @NonRepetitive
    MULTIPIXELRUNTOCOLOR,
    /**
     * See [AnimatedLEDStrip.ripple]
     */
    @Radial
    RIPPLE,
    /**
     * See [AnimatedLEDStrip.pixelMarathon]
     */
    PIXELMARATHON,
    /**
     * See [AnimatedLEDStrip.pixelRun]
     */
    PIXELRUN,
    /**
     * See [AnimatedLEDStrip.smoothChase]
     */
    SMOOTHCHASE,
    /**
     * See [AnimatedLEDStrip.smoothFade]
     */
    SMOOTHFADE,
    /**
     * See [AnimatedLEDStrip.sparkle]
     */
    SPARKLE,
    /**
     * See [AnimatedLEDStrip.sparkleFade]
     */
    SPARKLEFADE,
    /**
     * See [AnimatedLEDStrip.sparkleToColor]
     */
    @NonRepetitive
    SPARKLETOCOLOR,
    /**
     * See [AnimatedLEDStrip.splat]
     */
    @NonRepetitive
    @Radial
    SPLAT,
    /**
     * See [AnimatedLEDStrip.stack]
     */
    @NonRepetitive
    STACK,
    /**
     * See [AnimatedLEDStrip.stackOverflow]
     */
    STACKOVERFLOW,
    /**
     * See [AnimatedLEDStrip.wipe]
     */
    @NonRepetitive
    WIPE,
    /**
     * Special 'animation' sent by a client to stop a continuous animation
     */
    ENDANIMATION
}