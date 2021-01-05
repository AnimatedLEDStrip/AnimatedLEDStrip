package animatedledstrip.leds.stripmanagement

import animatedledstrip.animations.Distance
import kotlin.math.abs

class PixelLocationManager(ledLocations: List<Location>?, numLEDs: Int, include1D: Boolean = true) {
    val pixelLocations: List<PixelLocation>

    init {
        val tempLocationList = mutableListOf<PixelLocation>()
        val tempUsedLocationsList = mutableListOf<Location>()
        if (ledLocations == null && !include1D) {
            error("Must specify locations for pixels if not using 1D")
        } else if (ledLocations == null) {
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

    val xMin: Double = pixelLocations.map { it.location.x }.minOrNull() ?: 0.0
    val xMax: Double = pixelLocations.map { it.location.x }.maxOrNull() ?: 0.0
    val xAvg: Double = (xMin + xMax) / 2
    val yMin: Double = pixelLocations.map { it.location.y }.minOrNull() ?: 0.0
    val yMax: Double = pixelLocations.map { it.location.y }.maxOrNull() ?: 0.0
    val yAvg: Double = (yMin + yMax) / 2
    val zMin: Double = pixelLocations.map { it.location.z }.minOrNull() ?: 0.0
    val zMax: Double = pixelLocations.map { it.location.z }.maxOrNull() ?: 0.0
    val zAvg: Double = (zMin + zMax) / 2

    val defaultLocation = Location(xAvg, yAvg, zAvg)
    val defaultDistance = Distance(abs(xMin) + abs(xMax), abs(yMin) + abs(yMax), abs(zMin) + abs(zMax))
}
