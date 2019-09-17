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


import animatedledstrip.colors.ColorContainer
import animatedledstrip.colors.ColorContainerInterface
import animatedledstrip.colors.ccpresets.CCBlack
import animatedledstrip.utils.parseHex

/* Helper functions for setting values */

/**
 * Sets the `animation` parameter.
 *
 * @param animation The animation to run.
 */
fun AnimationData.animation(animation: Animation): AnimationData {
    this.animation = animation
    return this
}


/**
 * Set the color using a ColorContainer, hex String, or Int or Long
 * in range(0..16777215)
 *
 * @param color
 * @param index The index of the color in the list of colors
 */
fun AnimationData.color(color: Any, index: Int = 0): AnimationData {
    if (colors.size <= index) for (i in colors.size..index) colors += CCBlack

    when (color) {
        is ColorContainerInterface -> colors[index] = color.toColorContainer()
        is Long -> colors[index] = ColorContainer(color)
        is Int -> colors[index] = ColorContainer(color.toLong())
        is String -> colors[index] = ColorContainer(parseHex(color))
    }

    return this
}

fun AnimationData.addColor(color: Any): AnimationData {
    when (color) {
        is ColorContainerInterface -> colors += color.toColorContainer()
        is Long -> colors += ColorContainer(color)
        is Int -> colors += ColorContainer(color.toLong())
        is String -> colors += ColorContainer(parseHex(color))
    }

    return this
}
// TODO: tests
fun AnimationData.addColors(vararg colors: ColorContainerInterface): AnimationData {
    colors.forEach { addColor(it) }
    return this
}

fun AnimationData.addColors(vararg colors: Long): AnimationData {
    colors.forEach { addColor(it) }
    return this
}

fun AnimationData.addColors(vararg colors: Int): AnimationData {
    colors.forEach { addColor(it) }
    return this
}

fun AnimationData.addColors(vararg colors: String): AnimationData {
    colors.forEach { addColor(it) }
    return this
}

fun AnimationData.addColors(colors: List<Any>): AnimationData {
    colors.forEach { addColor(it) }
    return this
}

/* Helpers for setting the first 5 ColorContainers */

fun AnimationData.color0(color: Any) = color(color, 0)
fun AnimationData.color1(color: Any) = color(color, 1)
fun AnimationData.color2(color: Any) = color(color, 2)
fun AnimationData.color3(color: Any) = color(color, 3)
fun AnimationData.color4(color: Any) = color(color, 4)

/**
 * Set the `continuous` parameter.
 *
 * @param continuous A `Boolean`
 */
fun AnimationData.continuous(continuous: Boolean): AnimationData {
    this.continuous = continuous
    return this
}
// TODO: test
fun AnimationData.center(pixel: Int): AnimationData {
    center = pixel
    return this
}

/**
 * Set the `delay` parameter.
 *
 * @param delay An `Int` representing the delay time in milliseconds the
 * animation will use
 */
fun AnimationData.delay(delay: Int): AnimationData {
    this.delay = delay.toLong()
    return this
}

/**
 * Set the `delay` parameter.
 *
 * @param delay A `Long` representing the delay time in milliseconds the
 * animation will use
 */
fun AnimationData.delay(delay: Long): AnimationData {
    this.delay = delay
    return this
}

/**
 * Set the `delayMod` parameter.
 *
 * @param delayMod A `Double` that is a multiplier for `delay`
 */
fun AnimationData.delayMod(delayMod: Double): AnimationData {
    this.delayMod = delayMod
    return this
}

/**
 * Set the `direction` parameter.
 *
 * @param direction A `Direction` value ([Direction].`FORWARD` or [Direction].`BACKWARD`)
 */
fun AnimationData.direction(direction: Direction): AnimationData {
    this.direction = direction
    return this
}

/**
 * Set the `direction` parameter with a `Char`.
 *
 * @param direction A `Char` representing `Direction.FORWARD` ('`F`') or
 * `Direction.BACKWARD` ('`B`')
 */
fun AnimationData.direction(direction: Char): AnimationData {
    this.direction = when (direction) {
        'F', 'f' -> Direction.FORWARD
        'B', 'b' -> Direction.BACKWARD
        else -> throw Exception("Direction chars can be 'F' or 'B'")
    }
    return this
}
// TODO: Test
fun AnimationData.distance(pixels: Int): AnimationData {
    distance = pixels
    return this
}

/**
 * Set the `endPixel` parameter.
 *
 * @param endPixel An `Int` that is the index of the last pixel showing the
 * animation (inclusive)
 */
fun AnimationData.endPixel(endPixel: Int): AnimationData {
    this.endPixel = endPixel
    return this
}

/**
 * Set the `id` parameter.
 *
 * @param id A `String` used to identify a continuous animation instance
 */
fun AnimationData.id(id: String): AnimationData {
    this.id = id
    return this
}

/**
 * Set the `spacing` parameter.
 *
 * @param spacing An `Int` that is the spacing used by the animation
 */
fun AnimationData.spacing(spacing: Int): AnimationData {
    this.spacing = spacing
    return this
}


/**
 * Simple way to set the speed of an animation. Setting this will modify delayMod accordingly.
 *
 * @param speed The speed to set
 */
fun AnimationData.speed(speed: AnimationSpeed): AnimationData {
    delayMod = when (speed) {
        AnimationSpeed.SLOW -> 0.5
        AnimationSpeed.DEFAULT -> 1.0       // TODO: Test
        AnimationSpeed.FAST -> 2.0
    }
    return this
}

/**
 * Set the `startPixel` parameter.
 *
 * @param startPixel An `Int` that is the index of the first pixel showing the
 * animation (inclusive)
 */
fun AnimationData.startPixel(startPixel: Int): AnimationData {
    this.startPixel = startPixel
    return this
}