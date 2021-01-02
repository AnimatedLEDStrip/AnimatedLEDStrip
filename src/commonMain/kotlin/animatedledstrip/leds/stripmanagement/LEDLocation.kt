package animatedledstrip.leds.stripmanagement

import animatedledstrip.communication.SendableData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("LEDLocation")
data class LEDLocation(
    val x: Double = 0.0,
    val y: Double = 0.0,
    val z: Double = 0.0,
) : SendableData {

    constructor(x: Int = 0, y: Int = 0, z: Int = 0) : this(x.toDouble(), y.toDouble(), z.toDouble())

    val coordinates: String = "$x, $y, $z"
    override fun toHumanReadableString(): String = coordinates
}
