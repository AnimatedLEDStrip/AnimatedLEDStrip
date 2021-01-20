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
 * Transform a location of a pixel around the Z and X axes.
 *
 * Adapted from Matt Parker's video
 * (https://youtu.be/TvlpIojusBE?t=1078)
 *
 * @param zRotation How far to rotate around Z axis (in radians)
 * @param xRotation How far to rotate around X axis (in radians)
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

fun List<Location>.transformLocations(offset: AbsoluteDistance, rotation: RadiansRotation): List<Location> =
    map { it.transform(offset, rotation) }

val AnimationManager.pixelLocationManager: PixelLocationManager
    get() = sectionManager.stripManager.pixelLocationManager

data class PixelsToModify(
    val setRevertList: List<Pair<Int, Int>>,
    val setPixels: List<Int>,
    val revertPixels: List<Int>,
)

data class PixelModificationLists(
    val modLists: List<PixelsToModify>,
)

fun AnimationManager.groupPixelsByXLocation(
    rotation: RadiansRotation,
    movementPerIteration: Double,
): List<List<Int>> {
    val locationManager = PixelLocationManager(pixelLocationManager, rotation = rotation)
    val pixelsToModifyPerIteration: MutableList<MutableList<Int>> = mutableListOf()
    val changedPixels: MutableList<PixelLocation> = mutableListOf()

    var iteration = 0
    var currentX = locationManager.xMin
    while (currentX < locationManager.xMax + movementPerIteration) {
        pixelsToModifyPerIteration.add(mutableListOf())
        for (pixel in locationManager.pixelLocations) {
            if (pixel !in changedPixels && pixel.location.x <= currentX) {
                pixelsToModifyPerIteration[iteration].add(pixel.index)
                changedPixels.add(pixel)
            }
        }
        iteration++
        currentX += movementPerIteration
    }

    return pixelsToModifyPerIteration.map { it.toList() }
}

fun AnimationManager.groupPixelsByDistance(
    center: Location,
    maximumDistance: AbsoluteDistance,
    movementPerIteration: Double,
): List<List<Int>> {
    val maxDistance = maximumDistance.maxDistance
    val locationManager = pixelLocationManager
    val pixelsToModifyPerIteration: MutableList<MutableList<Int>> = mutableListOf()
    val changedPixels: MutableList<PixelLocation> = mutableListOf()

    var iteration = 0
    var currentDistance = 0.0
    while (currentDistance < maxDistance) {
        pixelsToModifyPerIteration.add(mutableListOf())
        for (pixel in locationManager.pixelLocations) {
            if (pixel !in changedPixels && pixel.location.distanceFrom(center) <= currentDistance) {
                pixelsToModifyPerIteration[iteration].add(pixel.index)
                changedPixels.add(pixel)
            }
        }
        iteration++
        currentDistance += movementPerIteration
    }

    return pixelsToModifyPerIteration.map { it.toList() }
}

fun AnimationManager.groupPixelsAlongLine(
    line: Equation,
    rotation: RadiansRotation,
    offset: AbsoluteDistance,
    influenceDistance: Double,
    movementPerIteration: Double,
): List<List<Int>> {
    // Negatives used because we want it to appear like the animation moved, not the pixels
    val locationManager = PixelLocationManager(pixelLocationManager, offset = -offset, rotation = -rotation)
    val pixelsToModifyPerIteration: MutableList<MutableList<Int>> = mutableListOf()

    var iteration = 0
    var currentX = locationManager.xMin
    while (currentX < locationManager.xMax + movementPerIteration) {
        pixelsToModifyPerIteration.add(mutableListOf())
        val currentLocation = Location(currentX, line.calculate(currentX))
        for (pixel in locationManager.pixelLocations) {
            if (pixel.location.distanceFrom(currentLocation) <= influenceDistance) {
                pixelsToModifyPerIteration[iteration].add(pixel.index)
            }
        }
        iteration++
        currentX += movementPerIteration
    }

    return pixelsToModifyPerIteration.map { it.toList() }
}

fun AnimationManager.groupGroupsOfPixelsAlongLine(
    line: Equation,
    rotation: RadiansRotation,
    offset: AbsoluteDistance,
    influenceDistance: Double,
    movementPerIteration: Double,
    groupSpacing: Double,
): PixelModificationLists {
    // Negatives used because we want it to appear like the animation moved, not the pixels
    val locationManager = PixelLocationManager(pixelLocationManager, offset = -offset, rotation = -rotation)
    val pixelsToSetPerIteration: MutableList<List<Int>> = mutableListOf()
    val pixelsToResetPerIteration: MutableList<List<Int>> = mutableListOf(listOf())

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
        currentBasePoint += movementPerIteration
    }
    pixelsToResetPerIteration[0] = pixelsToSetPerIteration.last() - pixelsToSetPerIteration[0]

    return PixelModificationLists(pixelsToSetPerIteration.mapIndexed { index: Int, list: List<Int> ->
        val revertList = pixelsToResetPerIteration[index]
        val setRevertList = list.zip(revertList)
        PixelsToModify(setRevertList, list.drop(setRevertList.size), revertList.drop(setRevertList.size))
    })
}
