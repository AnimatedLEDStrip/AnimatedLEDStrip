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

import animatedledstrip.leds.animationmanagement.AnimationManager
import kotlin.math.cos
import kotlin.math.sin

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
 * Transform a location of a pixel around the Z and X axes.
 *
 * Adapted from Matt Parker's video
 * (https://youtu.be/TvlpIojusBE?t=1078)
 *
 * @param zRotation How far to rotate around Z axis (in radians)
 * @param xRotation How far to rotate around X axis (in radians)
 * @return A new [Location]
 */
fun PixelLocation.transform(zRotation: Double, xRotation: Double): Location =
    location.zRotate(zRotation).xRotate(xRotation)

fun AnimationManager.transformLocations(zRotation: Double, xRotation: Double): List<Location> =
    sectionManager.stripManager.pixelLocationManager.pixelLocations.map { it.transform(zRotation, xRotation) }
