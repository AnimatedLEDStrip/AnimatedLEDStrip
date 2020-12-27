package animatedledstrip.leds.colormanagement

import animatedledstrip.utils.SendableData
import kotlinx.serialization.Serializable

@Serializable
data class CurrentStripColor(val color: List<Int>) : SendableData {
    override fun toHumanReadableString(): String {
        TODO("not implemented")
    }
}
