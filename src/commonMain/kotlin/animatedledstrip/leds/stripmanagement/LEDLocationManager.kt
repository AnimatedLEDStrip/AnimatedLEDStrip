package animatedledstrip.leds.stripmanagement

class LEDLocationManager(val stripManager: LEDStrip) {
    val ledLocations: MutableMap<LEDLocation, Int> = mutableMapOf()

    init {
        if (stripManager.stripInfo.ledLocations == null && !stripManager.stripInfo.include1D)
            error("Must specify locations for pixels if not using 1D")
        else if (stripManager.stripInfo.ledLocations == null)
                    for (i in 0 until stripManager.stripInfo.numLEDs)
                        ledLocations[LEDLocation(i.toDouble(), 0.0, 0.0)] = i
        else
            stripManager.stripInfo.ledLocations.forEachIndexed { index, coords ->
                val location = LEDLocation(coords.first, coords.second, coords.third)
                require(location !in ledLocations.keys) { "Two pixels cannot have the same coordinates: ${location.coordinates} (pixels ${ledLocations[location]} and $index)"}
                ledLocations[location] = index
            }
    }

    val xMin = ledLocations.map { it.key.x }.minOrNull() ?: 0.0
    val xMax = ledLocations.map { it.key.x }.maxOrNull() ?: 0.0
    val yMin = ledLocations.map { it.key.y }.minOrNull() ?: 0.0
    val yMax = ledLocations.map { it.key.y }.maxOrNull() ?: 0.0
    val zMin = ledLocations.map { it.key.z }.minOrNull() ?: 0.0
    val zMax = ledLocations.map { it.key.z }.maxOrNull() ?: 0.0
}
