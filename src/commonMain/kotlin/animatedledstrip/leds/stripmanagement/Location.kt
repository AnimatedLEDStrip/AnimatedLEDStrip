package animatedledstrip.leds.stripmanagement

import kotlinx.serialization.Serializable
import kotlin.math.pow

@Serializable
data class Location(
    val x: Double = 0.0,
    val y: Double = 0.0,
    val z: Double = 0.0,
) {

    constructor(x: Int = 0, y: Int = 0, z: Int = 0) : this(x.toDouble(), y.toDouble(), z.toDouble())

    val coordinates: String = "$x, $y, $z"

    fun distanceFrom(other: Location): Double = ((x - other.x).pow(2) +
                                                 (y - other.y).pow(2) +
                                                 (z - other.z).pow(2)).pow(0.5)
}
