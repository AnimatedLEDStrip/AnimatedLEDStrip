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

import animatedledstrip.colors.ColorContainer
import animatedledstrip.colors.ColorContainerInterface
import animatedledstrip.colors.ccpresets.CCBlack
import animatedledstrip.utils.parseHex

/* Helper functions for setting values */

/**
 * Sets the `animation` parameter.
 */
fun AnimationData.animation(animation: String): AnimationData {
    this.animation = animation
    return this
}


/**
 * Set the color using a ColorContainer, hex String, or Int or Long
 * in range(0..16777215)
 */
fun AnimationData.color(color: Any, index: Int = 0): AnimationData {
    if (colors.size <= index)
        for (i in colors.size..index)
            colors += CCBlack

    when (color) {
        is ColorContainerInterface -> colors[index] = color.toColorContainer()
        is Long -> colors[index] = ColorContainer(color)
        is Int -> colors[index] = ColorContainer(color.toLong())
        is String -> colors[index] = ColorContainer(parseHex(color))
        else -> throw IllegalArgumentException("Invalid data type: ${color::class}")
    }

    return this
}

/**
 * Append a color to the end of `colors`
 */
fun AnimationData.addColor(color: ColorContainerInterface): AnimationData {
    colors += color.toColorContainer()
    return this
}

/**
 * Append multiple colors to the end of `colors`
 */
fun AnimationData.addColors(vararg colors: ColorContainerInterface): AnimationData {
    colors.forEach { addColor(it) }
    return this
}

/**
 * Append a color to the end of `colors`
 */
fun AnimationData.addColor(color: Long): AnimationData {
    colors += ColorContainer(color)
    return this
}

/**
 * Append multiple colors to the end of `colors`
 */
fun AnimationData.addColors(vararg colors: Long): AnimationData {
    colors.forEach { addColor(it) }
    return this
}

/**
 * Append a color to the end of `colors`
 */
fun AnimationData.addColor(color: Int): AnimationData {
    colors += ColorContainer(color.toLong())
    return this
}

/**
 * Append multiple colors to the end of `colors`
 */
fun AnimationData.addColors(vararg colors: Int): AnimationData {
    colors.forEach { addColor(it) }
    return this
}

/**
 * Append a color to the end of `colors`
 *
 * @param color A hexadecimal `String` representing the color
 */
fun AnimationData.addColor(color: String): AnimationData {
    colors += ColorContainer(parseHex(color))
    return this
}

/**
 * Append multiple colors to the end of `colors`
 *
 * @param colors Hexadecimal `String`s representing the colors
 */
fun AnimationData.addColors(vararg colors: String): AnimationData {
    colors.forEach { addColor(it) }
    return this
}


/**
 * Append multiple colors to the end of `colors`
 */
fun AnimationData.addColors(colors: List<*>): AnimationData {
    require(colors.isNotEmpty())
    colors.forEach {
        when (it) {
            is ColorContainerInterface -> addColor(it)
            is Long -> addColor(it)
            is Int -> addColor(it)
            is String -> addColor(it)
            null -> throw IllegalArgumentException("Null color")
            else -> throw IllegalArgumentException("Invalid data type: ${it::class}")
        }
    }
    return this
}


/* Helpers for setting the first 5 ColorContainers */


/**
 * Set `colors[0]`
 */
fun AnimationData.color0(color: Any): AnimationData = color(color, 0)

/**
 * Set `colors[1]`
 */
fun AnimationData.color1(color: Any): AnimationData = color(color, 1)

/**
 * Set `colors[2]`
 */
fun AnimationData.color2(color: Any): AnimationData = color(color, 2)

/**
 * Set `colors[3]`
 */
fun AnimationData.color3(color: Any): AnimationData = color(color, 3)

/**
 * Set `colors[4]`
 */
fun AnimationData.color4(color: Any): AnimationData = color(color, 4)

/**
 * Set the `continuous` parameter.
 */
fun AnimationData.continuous(continuous: Boolean): AnimationData {
    this.continuous = continuous
    return this
}

/**
 * Set the `center` parameter.
 *
 * @param pixel The index of the pixel at the center of a radial animation
 */
fun AnimationData.center(pixel: Int): AnimationData {
    this.center = pixel
    return this
}

/**
 * Set the `delay` parameter.
 *
 * @param delay An `Int` representing the delay time in milliseconds
 */
fun AnimationData.delay(delay: Int): AnimationData {
    this.delay = delay.toLong()
    return this
}

/**
 * Set the `delay` parameter.
 *
 * @param delay A `Long` representing the delay time in milliseconds
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

/**
 * Set the `distance` parameter.
 *
 * @param pixels The number of pixels away from the center pixel
 * that the radial animation should travel
 */
fun AnimationData.distance(pixels: Int): AnimationData {
    this.distance = pixels
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
 * Set the `section` parameter.
 *
 * @param sectionId A `String` used to identify a section of the strip
 */
fun AnimationData.section(sectionId: String): AnimationData {
    this.section = sectionId
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
        AnimationSpeed.DEFAULT -> 1.0
        AnimationSpeed.FAST -> 2.0
    }
    return this
}
