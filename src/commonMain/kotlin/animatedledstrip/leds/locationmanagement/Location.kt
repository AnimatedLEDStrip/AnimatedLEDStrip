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
import kotlinx.serialization.Serializable
import kotlin.math.pow

/**
 * A location in 3D space
 *
 * @property x X coordinate
 * @property y Y coordinate
 * @property z Z coordinate
 */
@Serializable
data class Location(
    val x: Double = 0.0,
    val y: Double = 0.0,
    val z: Double = 0.0,
) {
    constructor(x: Int = 0, y: Int = 0, z: Int = 0) : this(x.toDouble(), y.toDouble(), z.toDouble())

    val coordinates: String
        get() = "$x, $y, $z"

    fun distanceFrom(other: Location): Double = ((x - other.x).pow(2) +
                                                 (y - other.y).pow(2) +
                                                 (z - other.z).pow(2)).pow(0.5)

    fun offsetBy(offset: AbsoluteDistance): Location = Location(x + offset.x,
                                                                y + offset.y,
                                                                z + offset.z)

    companion object {
        /**
         * Used by animation defaults to specify that the location should be the
         * center of all LED locations
         */
        val CENTER: Location? = null
    }
}
