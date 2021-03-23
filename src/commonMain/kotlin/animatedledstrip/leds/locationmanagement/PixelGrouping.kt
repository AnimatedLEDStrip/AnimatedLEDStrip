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

import animatedledstrip.animations.parameters.AbsoluteDistance
import animatedledstrip.animations.parameters.Equation
import animatedledstrip.animations.parameters.RadiansRotation
import animatedledstrip.leds.animationmanagement.AnimationManager
import animatedledstrip.leds.animationmanagement.PixelModificationLists
import kotlin.math.abs
import kotlin.math.truncate

/**
 * Group pixels based on their location along the X axis after a transformation is performed.
 *
 * Used by animations such as Plane Run, Wave, and Wipe.
 *
 * @param rotation How to rotate the animation
 * @param movementPerIteration How far to move along the X axis in each iteration
 * @return Lists of pixels to modify in each iteration as a [PixelModificationLists]
 */
fun AnimationManager.groupPixelsByXLocation(
    offset: AbsoluteDistance,
    rotation: RadiansRotation,
    movementPerIteration: Double,
): PixelModificationLists {
    require(movementPerIteration != 0.0) { "Movement per iteration cannot be 0" }

    // Negative offset and rotation used because we want it to appear like the animation moved, not the pixels
    val locationManager = PixelLocationManager(pixelLocationManager,
                                               offset = -offset,
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
    pixelsToRevertPerIteration.add(pixelsToSetPerIteration.last() - pixelsToSetPerIteration[0])

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
    pixelsToRevertPerIteration.add(pixelsToSetPerIteration.last() - pixelsToSetPerIteration[0])

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
    pixelsToRevertPerIteration.add(pixelsToSetPerIteration.last() - pixelsToSetPerIteration[0])

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
    pixelsToResetPerIteration.add(pixelsToSetPerIteration.last() - pixelsToSetPerIteration[0])

    return PixelModificationLists(pixelsToSetPerIteration, pixelsToResetPerIteration)
}