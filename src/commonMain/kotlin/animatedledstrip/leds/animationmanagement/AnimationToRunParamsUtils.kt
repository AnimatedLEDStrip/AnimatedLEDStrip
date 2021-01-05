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

import animatedledstrip.animations.Direction
import animatedledstrip.animations.Distance
import animatedledstrip.animations.Equation
import animatedledstrip.colors.ColorContainer
import animatedledstrip.colors.ColorContainerInterface
import animatedledstrip.colors.ccpresets.Black
import animatedledstrip.colors.parseHex
import animatedledstrip.leds.stripmanagement.Location

/* Helper functions for setting values */

/**
 * Sets the `animation` parameter.
 */
fun AnimationToRunParams.animation(animation: String): AnimationToRunParams {
    this.animation = animation
    return this
}


/**
 * Set the color using a ColorContainer, hex String, or Int or Long
 * in range(0..16777215)
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

///**
// * Set the `center` parameter.
// *
// * @param pixel The index of the pixel at the center of a radial animation
// */
//fun AnimationToRunParams.center(pixelLocation: Location): AnimationToRunParams {
//    this.center = pixelLocation
//    return this
//}

///**
// * Set the `delay` parameter.
// *
// * @param delay An `Int` representing the delay time in milliseconds
// */
//fun AnimationToRunParams.delay(delay: Int): AnimationToRunParams {
//    this.delay = delay.toLong()
//    return this
//}

///**
// * Set the `delay` parameter.
// *
// * @param delay A `Long` representing the delay time in milliseconds
// */
//fun AnimationToRunParams.delay(delay: Long): AnimationToRunParams {
//    this.delay = delay
//    return this
//}

///**
// * Set the `delayMod` parameter.
// *
// * @param delayMod A `Double` that is a multiplier for `delay`
// */
//fun AnimationToRunParams.delayMod(delayMod: Double): AnimationToRunParams {
//    this.delayMod = delayMod
//    return this
//}

/**
 * Set the `direction` parameter.
 *
 * @param direction A `Direction` value ([Direction].`FORWARD` or [Direction].`BACKWARD`)
 */
fun AnimationToRunParams.direction(direction: Direction): AnimationToRunParams {
    this.direction = direction
    return this
}

/**
 * Set the `direction` parameter with a `Char`.
 *
 * @param direction A `Char` representing `Direction.FORWARD` ('`F`') or
 * `Direction.BACKWARD` ('`B`')
 */
fun AnimationToRunParams.direction(direction: Char): AnimationToRunParams {
    this.direction = when (direction) {
        'F', 'f' -> Direction.FORWARD
        'B', 'b' -> Direction.BACKWARD
        else -> throw IllegalArgumentException("Direction chars can be 'F' or 'B'")
    }
    return this
}

///**
// * Set the `distance` parameter.
// *
// * @param pixels The number of pixels away from the center pixel
// * that the radial animation should travel
// */
//fun AnimationToRunParams.distance(pixels: Int): AnimationToRunParams {
//    this.distance = pixels
//    return this
//}

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

fun AnimationToRunParams.locationParam(key: String, value: Location): AnimationToRunParams {
    this.locationParams[key] = value
    return this
}

fun AnimationToRunParams.distanceParam(key: String, value: Distance): AnimationToRunParams {
    this.distanceParams[key] = value
    return this
}

fun AnimationToRunParams.equationParam(key: String, value: Equation): AnimationToRunParams {
    this.equationParams[key] = value
    return this
}

///**
// * Set the `spacing` parameter.
// *
// * @param spacing An `Int` that is the spacing used by the animation
// */
//fun AnimationToRunParams.spacing(spacing: Int): AnimationToRunParams {
//    this.spacing = spacing
//    return this
//}
