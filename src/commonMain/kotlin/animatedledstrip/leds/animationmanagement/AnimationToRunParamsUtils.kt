/*
 * Copyright (c) 2018-2021 AnimatedLEDStrip
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package animatedledstrip.leds.animationmanagement

import animatedledstrip.animations.Rotation
import animatedledstrip.animations.parameters.Distance
import animatedledstrip.animations.parameters.Equation
import animatedledstrip.colors.ColorContainer
import animatedledstrip.colors.ColorContainerInterface
import animatedledstrip.colors.ccpresets.Black
import animatedledstrip.colors.parseHex
import animatedledstrip.leds.locationmanagement.Location

/* Helper functions for setting values */

/**
 * Sets the `animation` parameter.
 */
fun AnimationToRunParams.animation(animation: String): AnimationToRunParams {
    this.animation = animation
    return this
}


/**
 * Set a color using a ColorContainer, hex String, or Int or Long
 * in range(0..0xFFFFFF), along with the index of the color
 */
fun AnimationToRunParams.color(color: Any, index: Int = 0): AnimationToRunParams {
    if (colors.size <= index)
        for (i in colors.size..index)
            colors.add(ColorContainer.Black)

    when (color) {
        is ColorContainerInterface -> colors[index] = color.toColorContainer()
        is Long -> colors[index] = ColorContainer(color.toInt())
        is Int -> colors[index] = ColorContainer(color)
        is String -> colors[index] = ColorContainer(parseHex(color))
        else -> throw IllegalArgumentException("Invalid data type: ${color::class}")
    }

    return this
}

/**
 * Append a color to the end of `colors`
 */
fun AnimationToRunParams.addColor(color: ColorContainerInterface): AnimationToRunParams {
    colors.add(color.toColorContainer())
    return this
}

/**
 * Append multiple colors to the end of `colors`
 */
fun AnimationToRunParams.addColors(vararg colors: ColorContainerInterface): AnimationToRunParams {
    colors.forEach { addColor(it) }
    return this
}

/**
 * Append a color to the end of `colors`
 */
fun AnimationToRunParams.addColor(color: Long): AnimationToRunParams {
    colors.add(ColorContainer(color.toInt()))
    return this
}

/**
 * Append multiple colors to the end of `colors`
 */
fun AnimationToRunParams.addColors(vararg colors: Long): AnimationToRunParams {
    colors.forEach { addColor(it) }
    return this
}

/**
 * Append a color to the end of `colors`
 */
fun AnimationToRunParams.addColor(color: Int): AnimationToRunParams {
    colors.add(ColorContainer(color))
    return this
}

/**
 * Append multiple colors to the end of `colors`
 */
fun AnimationToRunParams.addColors(vararg colors: Int): AnimationToRunParams {
    colors.forEach { addColor(it) }
    return this
}

/**
 * Append a color to the end of `colors`
 *
 * @param color A hexadecimal `String` representing the color
 */
fun AnimationToRunParams.addColor(color: String): AnimationToRunParams {
    colors.add(ColorContainer(parseHex(color)))
    return this
}

/**
 * Append multiple colors to the end of `colors`
 *
 * @param colors Hexadecimal `String`s representing the animatedledstrip.colors
 */
fun AnimationToRunParams.addColors(vararg colors: String): AnimationToRunParams {
    colors.forEach { addColor(it) }
    return this
}


/**
 * Append multiple colors to the end of `colors`
 */
fun AnimationToRunParams.addColors(colors: List<*>): AnimationToRunParams {
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
fun AnimationToRunParams.color0(color: Any): AnimationToRunParams = color(color, 0)

/**
 * Set `colors[1]`
 */
fun AnimationToRunParams.color1(color: Any): AnimationToRunParams = color(color, 1)

/**
 * Set `colors[2]`
 */
fun AnimationToRunParams.color2(color: Any): AnimationToRunParams = color(color, 2)

/**
 * Set `colors[3]`
 */
fun AnimationToRunParams.color3(color: Any): AnimationToRunParams = color(color, 3)

/**
 * Set `colors[4]`
 */
fun AnimationToRunParams.color4(color: Any): AnimationToRunParams = color(color, 4)

/**
 * Set the `id` parameter.
 *
 * @param id A `String` used to identify a continuous animation instance
 */
fun AnimationToRunParams.id(id: String): AnimationToRunParams {
    this.id = id
    return this
}


fun AnimationToRunParams.runCount(runs: Int): AnimationToRunParams {
    this.runCount = runs
    return this
}


/**
 * Set the `section` parameter.
 *
 * @param sectionId A `String` used to identify a section of the strip
 */
fun AnimationToRunParams.section(sectionId: String): AnimationToRunParams {
    this.section = sectionId
    return this
}

fun AnimationToRunParams.intParam(key: String, value: Int): AnimationToRunParams {
    this.intParams[key] = value
    return this
}

fun AnimationToRunParams.doubleParam(key: String, value: Double): AnimationToRunParams {
    this.doubleParams[key] = value
    return this
}

fun AnimationToRunParams.stringParam(key: String, value: String): AnimationToRunParams {
    this.stringParams[key] = value
    return this
}

fun AnimationToRunParams.locationParam(key: String, value: Location): AnimationToRunParams {
    this.locationParams[key] = value
    return this
}

fun AnimationToRunParams.distanceParam(key: String, value: Distance): AnimationToRunParams {
    this.distanceParams[key] = value
    return this
}

fun AnimationToRunParams.rotationParam(key: String, value: Rotation): AnimationToRunParams {
    this.rotationParams[key] = value
    return this
}

fun AnimationToRunParams.equationParam(key: String, value: Equation): AnimationToRunParams {
    this.equationParams[key] = value
    return this
}
