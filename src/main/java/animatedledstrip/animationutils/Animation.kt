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

package animatedledstrip.animationutils

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
     * See [animatedledstrip.leds.alternate]
     */
    ALTERNATE,
    /**
     * See [animatedledstrip.leds.bounce]
     */
    BOUNCE,
    /**
     * See [animatedledstrip.leds.bounceToColor]
     */
    @NonRepetitive
    BOUNCETOCOLOR,
    /**
     * See [animatedledstrip.leds.catToy]
     */
    CATTOY,
    /**
     *  See [animatedledstrip.leds.catToyToColor]
     */
    @NonRepetitive
    CATTOYTOCOLOR,
    /**
     * See [animatedledstrip.leds.fadeToColor]
     */
    @NonRepetitive
    FADETOCOLOR,
    /**
     * See [animatedledstrip.leds.fireworks]
     */
    FIREWORKS,
    /**
     * See [animatedledstrip.leds.meteor]
     */
    METEOR,
    /**
     * See [animatedledstrip.leds.multiPixelRun]
     */
    MULTIPIXELRUN,
    /**
     * See [animatedledstrip.leds.multiPixelRunToColor]
     */
    @NonRepetitive
    MULTIPIXELRUNTOCOLOR,
    /**
     * See [animatedledstrip.leds.ripple]
     */
    @Radial
    RIPPLE,
    /**
     * See [animatedledstrip.leds.pixelMarathon]
     */
    PIXELMARATHON,
    /**
     * See [animatedledstrip.leds.pixelRun]
     */
    PIXELRUN,
    /**
     * See [animatedledstrip.leds.smoothChase]
     */
    SMOOTHCHASE,
    /**
     * See [animatedledstrip.leds.smoothFade]
     */
    SMOOTHFADE,
    /**
     * See [animatedledstrip.leds.sparkle]
     */
    SPARKLE,
    /**
     * See [animatedledstrip.leds.sparkleFade]
     */
    SPARKLEFADE,
    /**
     * See [animatedledstrip.leds.sparkleToColor]
     */
    @NonRepetitive
    SPARKLETOCOLOR,
    /**
     * See [animatedledstrip.leds.splat]
     */
    @NonRepetitive
    @Radial
    SPLAT,
    /**
     * See [animatedledstrip.leds.stack]
     */
    @NonRepetitive
    STACK,
    /**
     * See [animatedledstrip.leds.stackOverflow]
     */
    STACKOVERFLOW,
    /**
     * See [animatedledstrip.leds.wipe]
     */
    @NonRepetitive
    WIPE,
    /**
     * Special 'animation' sent by a client to stop a continuous animation
     */
    ENDANIMATION
}