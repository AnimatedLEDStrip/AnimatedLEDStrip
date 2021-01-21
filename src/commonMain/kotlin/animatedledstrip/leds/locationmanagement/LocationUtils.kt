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

package animatedledstrip.leds.locationmanagement

import animatedledstrip.animations.AbsoluteDistance
import animatedledstrip.animations.Equation
import animatedledstrip.animations.RadiansRotation
import animatedledstrip.animations.RotationAxis
import animatedledstrip.leds.animationmanagement.AnimationManager
import animatedledstrip.leds.animationmanagement.PixelModificationLists
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.truncate

/**
 * Rotate a point around the X axis by [rotation] radians using the matrix
 * | 1 0              0               |
 * | 0 cos(xRotation) -sin(xRotation) |
 * | 0 sin(xRotation) cos(xRotation)  |
 *
 * Adapted from Matt Parker's video
 * (https://youtu.be/TvlpIojusBE?t=1078)
 */
fun Location.xRotate(rotation: Double): Location =
    Location(x * 1 + y * 0 + z * 0,
             x * 0 + y * cos(rotation) + z * sin(rotation),
             x * 0 + y * -sin(rotation) + z * cos(rotation))

/**
 * Rotate a point around the Y axis by [rotation] radians using the matrix
 * | cos(yRotation)  0 sin(xRotation) |
 * | 0               1 0              |
 * | -sin(xRotation) 0 cos(xRotation) |
 *
 * Adapted from Matt Parker's video
 * (https://youtu.be/TvlpIojusBE?t=1078)
 */
fun Location.yRotate(rotation: Double): Location =
    Location(x * cos(rotation) + y * 0 + sin(rotation),
             x * 0 + y * 1 + z * 0,
             x * -sin(rotation) + y * 0 + z * cos(rotation))

/**
 * Rotate a point around the Z axis by [rotation] radians using the matrix
 * | cos(zRotation) -sin(zRotation) 0 |
 * | sin(zRotation) cos(zRotation)  0 |
 * | 0              0               1 |
 *
 * Adapted from Matt Parker's video
 * (https://youtu.be/TvlpIojusBE?t=1078)
 */
fun Location.zRotate(rotation: Double): Location =
    Location(x * cos(rotation) + y * sin(rotation) + z * 0,
             x * -sin(rotation) + y * cos(rotation) + z * 0,
             x * 0 + y * 0 + z * 1)

/**
 * Transform a location of a pixel with an offset and rotation around axes.
 *
 * @param offset How much and in what direction to offset the pixels
 * @param rotation How far to rotate around each axis and in what order
 * @return A new [Location]
 */
fun Location.transform(offset: AbsoluteDistance, rotation: RadiansRotation): Location {
    var tmpLocation = this.offsetBy(offset)
    for (axis in rotation.rotationOrder) {
        tmpLocation = when (axis) {
            RotationAxis.ROTATE_X -> tmpLocation.xRotate(rotation.xRotation)
            RotationAxis.ROTATE_Y -> tmpLocation.yRotate(rotation.yRotation)
            RotationAxis.ROTATE_Z -> tmpLocation.zRotate(rotation.zRotation)
        }
    }
    return tmpLocation
}

fun List<Location>.transformLocations(
    offset: AbsoluteDistance,
    rotation: RadiansRotation,
): List<Location> =
    map { it.transform(offset, rotation) }

fun List<Location>.invertXIf(predicate: Boolean): List<Location> =
    if (predicate) map { Location(-it.x, it.y, it.z) } else this

val AnimationManager.pixelLocationManager: PixelLocationManager
    get() = sectionManager.stripManager.pixelLocationManager

/**
 * Group pixels based on their location along the X axis after a rotation is performed.
 *
 * Used by animations such as Plane Run, Wave, and Wipe.
 *
 * @param rotation How to rotate the animation
 * @param movementPerIteration How far to move along the X axis in each iteration
 * @return Lists of pixels to modify in each iteration as a [PixelModificationLists]
 */
fun AnimationManager.groupPixelsByXLocation(
    rotation: RadiansRotation,
    movementPerIteration: Double,
): PixelModificationLists {
    require(movementPerIteration != 0.0) { "Movement per iteration cannot be 0" }

    // Negative used because we want it to appear like the animation moved, not the pixels
    val locationManager = PixelLocationManager(pixelLocationManager,
                                               rotation = -rotation,
                                               invertX = movementPerIteration < 0.0)

    val pixelsToSetPerIteration: MutableList<List<Int>> = mutableListOf()
    val pixelsToRevertPerIteration: MutableList<List<Int>> = mutableListOf(listOf())
    val changedPixels: MutableList<PixelLocation> = mutableListOf()

    val movementDistance = abs(movementPerIteration)

    var iteration = 0
    var currentX = locationManager.xMin
    while (currentX < locationManager.xMax + movementPerIteration) {
        val pixelsToSet: MutableList<Int> = mutableListOf()
        for (pixel in locationManager.pixelLocations) {
            if (pixel !in changedPixels && pixel.location.x <= currentX) {
                pixelsToSet.add(pixel.index)
                changedPixels.add(pixel)
            }
        }
        pixelsToSetPerIteration.add(pixelsToSet.toList())
        if (iteration > 0)
            pixelsToRevertPerIteration.add(pixelsToSetPerIteration[iteration - 1] - pixelsToSet)
        iteration++
        currentX += movementDistance
    }
    pixelsToRevertPerIteration[0] = pixelsToSetPerIteration.last() - pixelsToSetPerIteration[0]

    return PixelModificationLists(pixelsToSetPerIteration, pixelsToRevertPerIteration)
}

/**
 * Group pixels based on their distance from a central point.
 *
 * Used by animations such as Ripple.
 *
 * @param maximumDistance How far away from the center the animation can be
 * @param movementPerIteration How far to move away from (or towards) the center in each iteration
 * @return Lists of pixels to modify in each iteration as a [PixelModificationLists]
 */
fun AnimationManager.groupPixelsByDistance(
    center: Location,
    maximumDistance: AbsoluteDistance,
    movementPerIteration: Double,
): PixelModificationLists {
    require(movementPerIteration != 0.0) { "Movement per iteration cannot be 0" }

    val maxDistance = maximumDistance.maxDistance
    val locationManager = pixelLocationManager
    val pixelsToSetPerIteration: MutableList<List<Int>> = mutableListOf()
    val pixelsToRevertPerIteration: MutableList<List<Int>> = mutableListOf(listOf())
    val changedPixels: MutableList<PixelLocation> = mutableListOf()

    val movementDistance = abs(movementPerIteration)

    var iteration = 0
    if (movementPerIteration > 0.0) {
        var currentDistance = 0.0
        while (currentDistance < maxDistance) {
            val pixelsToSet: MutableList<Int> = mutableListOf()
            for (pixel in locationManager.pixelLocations) {
                if (pixel !in changedPixels && pixel.location.distanceFrom(center) <= currentDistance) {
                    pixelsToSet.add(pixel.index)
                    changedPixels.add(pixel)
                }
            }
            pixelsToSetPerIteration.add(pixelsToSet.toList())
            if (iteration > 0)
                pixelsToRevertPerIteration.add(pixelsToSetPerIteration[iteration - 1] - pixelsToSet)
            iteration++
            currentDistance += movementDistance
        }
    } else {
        var currentDistance = maxDistance
        while (currentDistance >= 0.0) {
            val pixelsToSet: MutableList<Int> = mutableListOf()
            for (pixel in locationManager.pixelLocations) {
                if (pixel !in changedPixels && pixel.location.distanceFrom(center) >= currentDistance) {
                    pixelsToSet.add(pixel.index)
                    changedPixels.add(pixel)
                }
            }
            pixelsToSetPerIteration.add(pixelsToSet.toList())
            if (iteration > 0)
                pixelsToRevertPerIteration.add(pixelsToSetPerIteration[iteration - 1] - pixelsToSet)
            iteration++
            currentDistance -= movementDistance
        }
    }
    pixelsToRevertPerIteration[0] = pixelsToSetPerIteration.last() - pixelsToSetPerIteration[0]

    return PixelModificationLists(pixelsToSetPerIteration, pixelsToRevertPerIteration)
}

/**
 * Group pixels based on their location along the X axis and distance from a line
 * after an offset and a rotation are performed
 *
 * @param line The line that will determine if a pixel is close enough to be affected
 * @param rotation How to rotate the animation
 * @param offset How to offset the animation
 * @param influenceDistance How far a pixel can be from the line before it doesn't get affected
 * @param movementPerIteration How far to move along the X axis in each iteration
 * @return Lists of pixels to modify in each iteration as a [PixelModificationLists]
 */
fun AnimationManager.groupPixelsAlongLine(
    line: Equation,
    rotation: RadiansRotation,
    offset: AbsoluteDistance,
    influenceDistance: Double,
    movementPerIteration: Double,
): PixelModificationLists {
    require(movementPerIteration != 0.0) { "Movement per iteration cannot be 0" }

    // Negatives used because we want it to appear like the animation moved, not the pixels
    val locationManager = PixelLocationManager(pixelLocationManager,
                                               offset = -offset,
                                               rotation = -rotation,
                                               invertX = movementPerIteration < 0.0)
    val pixelsToSetPerIteration: MutableList<List<Int>> = mutableListOf()
    val pixelsToRevertPerIteration: MutableList<List<Int>> = mutableListOf(listOf())

    val movementDistance = abs(movementPerIteration)

    var iteration = 0
    var currentX = locationManager.xMin
    while (currentX < locationManager.xMax + movementPerIteration) {
        val pixelsToSet: MutableList<Int> = mutableListOf()
        val currentLocation = Location(currentX, line.calculate(currentX))
        for (pixel in locationManager.pixelLocations) {
            if (pixel.location.distanceFrom(currentLocation) <= influenceDistance) {
                pixelsToSet.add(pixel.index)
            }
        }
        pixelsToSetPerIteration.add(pixelsToSet.toList())
        if (iteration > 0)
            pixelsToRevertPerIteration.add(pixelsToSetPerIteration[iteration - 1] - pixelsToSet)
        iteration++
        currentX += movementDistance
    }
    pixelsToRevertPerIteration[0] = pixelsToSetPerIteration.last() - pixelsToSetPerIteration[0]

    return PixelModificationLists(pixelsToSetPerIteration, pixelsToRevertPerIteration)
}

/**
 * Group pixels into groups spaced apart by [groupSpacing] based on their location along
 * the X axis and distance from a line after an offset and a rotation are performed
 *
 * @param line The line that will determine if a pixel is close enough to be affected
 * @param rotation How to rotate the animation
 * @param offset How to offset the animation
 * @param influenceDistance How far a pixel can be from the line before it doesn't get affected
 * @param movementPerIteration How far to move along the X axis in each iteration
 * @param groupSpacing How far apart along the X axis each group should be
 * @return Lists of pixels to modify in each iteration as a [PixelModificationLists]
 */
fun AnimationManager.groupGroupsOfPixelsAlongLine(
    line: Equation,
    rotation: RadiansRotation,
    offset: AbsoluteDistance,
    influenceDistance: Double,
    movementPerIteration: Double,
    groupSpacing: Double,
): PixelModificationLists {
    require(movementPerIteration != 0.0) { "Movement per iteration cannot be 0" }
    require(groupSpacing > 0.0) { "Group spacing cannot be less than or equal to 0" }

    // Negatives used because we want it to appear like the animation moved, not the pixels
    val locationManager = PixelLocationManager(pixelLocationManager,
                                               offset = -offset,
                                               rotation = -rotation,
                                               invertX = movementPerIteration < 0.0)
    val pixelsToSetPerIteration: MutableList<List<Int>> = mutableListOf()
    val pixelsToResetPerIteration: MutableList<List<Int>> = mutableListOf(listOf())

    val movementDistance = abs(movementPerIteration)

    var iteration = 0
    var currentBasePoint = locationManager.xMin
    while (currentBasePoint < locationManager.xMax + movementPerIteration) {
        val pixelsToSet: MutableList<Int> = mutableListOf()
        var currentX =
            currentBasePoint - (groupSpacing * (truncate((currentBasePoint - locationManager.xMin) / groupSpacing) + 1))
        while (currentX < locationManager.xMax + influenceDistance) {
            val currentLocation = Location(currentX, line.calculate(currentX))
            for (pixel in locationManager.pixelLocations) {
                if (pixel.location.distanceFrom(currentLocation) <= influenceDistance) {
                    pixelsToSet.add(pixel.index)
                }
            }
            currentX += groupSpacing
        }
        pixelsToSetPerIteration.add(pixelsToSet.toList())
        if (iteration > 0)
            pixelsToResetPerIteration.add(pixelsToSetPerIteration[iteration - 1] - pixelsToSet)
        iteration++
        currentBasePoint += movementDistance
    }
    pixelsToResetPerIteration[0] = pixelsToSetPerIteration.last() - pixelsToSetPerIteration[0]

    return PixelModificationLists(pixelsToSetPerIteration, pixelsToResetPerIteration)
}
