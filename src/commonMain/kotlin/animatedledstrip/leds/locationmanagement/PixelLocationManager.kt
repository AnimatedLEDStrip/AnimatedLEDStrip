/*
 * Copyright (c) 2018-2022 AnimatedLEDStrip
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
import animatedledstrip.animations.parameters.RadiansRotation
import animatedledstrip.leds.animationmanagement.randomDouble
import animatedledstrip.utils.Logger
import kotlin.math.abs

class PixelLocationManager(ledLocations: List<Location>?, val numLEDs: Int) {
    constructor(
        oldLocations: PixelLocationManager,
        offset: AbsoluteDistance = AbsoluteDistance(0, 0, 0),
        rotation: RadiansRotation = RadiansRotation(0.0, 0.0, 0.0, listOf()),
        invertX: Boolean = false,
    ) : this(oldLocations.pixelLocations.map { it.location }.transformLocations(offset, rotation).invertXIf(invertX),
             oldLocations.numLEDs)

    val pixelLocations: List<PixelLocation>

    init {
        val tempLocationList = mutableListOf<PixelLocation>()
        val tempUsedLocationsList = mutableListOf<Location>()
        if (ledLocations == null) {
            Logger.w("Pixel Location Manager: No LED locations defined, assuming LEDs are in a one dimensional strip with equal spacing")
            for (i in 0 until numLEDs) {
                tempLocationList.add(PixelLocation(i, Location(i.toDouble())))
            }
        } else {
            ledLocations.forEachIndexed { index, coords ->
                require(coords !in tempUsedLocationsList) { "Two pixels cannot have the same coordinates: ${coords.coordinates} (pixels ${tempLocationList.find { it.location == coords }!!.index} and $index)" }
                tempLocationList.add(PixelLocation(index, coords))
                tempUsedLocationsList.add(coords)
            }
        }

        pixelLocations = tempLocationList.toList()
    }

    val xMin: Double = pixelLocations.minOfOrNull { it.location.x } ?: 0.0
    val xMax: Double = pixelLocations.maxOfOrNull { it.location.x } ?: 0.0
    val xAvg: Double = (xMin + xMax) / 2
    val yMin: Double = pixelLocations.minOfOrNull { it.location.y } ?: 0.0
    val yMax: Double = pixelLocations.maxOfOrNull { it.location.y } ?: 0.0
    val yAvg: Double = (yMin + yMax) / 2
    val zMin: Double = pixelLocations.minOfOrNull { it.location.z } ?: 0.0
    val zMax: Double = pixelLocations.maxOfOrNull { it.location.z } ?: 0.0
    val zAvg: Double = (zMin + zMax) / 2

    val defaultLocation = Location(xAvg, yAvg, zAvg)
    val maximumDistance = AbsoluteDistance(abs(xMin) + abs(xMax), abs(yMin) + abs(yMax), abs(zMin) + abs(zMax))

    fun randomLocation(): Location =
        Location(
            abs(xMin - xMax) * randomDouble() + xMin,
            abs(yMin - yMax) * randomDouble() + yMin,
            abs(zMin - zMax) * randomDouble() + zMin
        )
}
