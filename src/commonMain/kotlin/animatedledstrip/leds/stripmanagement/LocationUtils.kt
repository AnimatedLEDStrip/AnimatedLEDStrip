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

package animatedledstrip.leds.stripmanagement

import animatedledstrip.leds.animationmanagement.AnimationManager
import kotlin.math.cos
import kotlin.math.sin

/**
 * Transform a location of a pixel around the Z and X axes.
 *
 * Adapted from Stand-up Maths' video
 * (https://youtu.be/TvlpIojusBE?t=1078)
 *
 * @param zRotation How far to rotate around Z axis (in radians)
 * @param xRotation How far to rotate around X axis (in radians)
 * @return A new [Location]
 */
fun PixelLocation.transform(zRotation: Double, xRotation: Double): Location {
    val x1 = location.x
    val y1 = location.y
    val z1 = location.z

    // Rotate around z axis with matrix
    // | cos(zRotation) -sin(zRotation) 0 |
    // | sin(zRotation) cos(zRotation)  0 |
    // | 0              0               1 |
    val x2 = x1 * cos(zRotation) + y1 * sin(zRotation) + z1 * 0
    val y2 = x1 * -sin(zRotation) + y1 * cos(zRotation) + z1 * 0
    val z2 = x1 * 0 + y1 * 0 + z1 * 1

    // Rotate around x axis with matrix
    // | 1 0              0               |
    // | 0 cos(xRotation) -sin(xRotation) |
    // | 0 sin(xRotation) cos(xRotation)  |
    val x3 = x2 * 1 + y2 * 0 + z2 * 0
    val y3 = x2 * 0 + y2 * cos(xRotation) + z2 * sin(xRotation)
    val z3 = x2 * 0 + y2 * -sin(xRotation) + z2 * cos(xRotation)

    return Location(x3, y3, z3)
}

fun AnimationManager.transformLocations(zRotation: Double, xRotation: Double): List<Location> =
    sectionManager.stripManager.pixelLocationManager.pixelLocations.map { it.transform(zRotation, xRotation) }
