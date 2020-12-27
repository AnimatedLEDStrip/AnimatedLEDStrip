package animatedledstrip.clients

import animatedledstrip.utils.SendableData
import kotlinx.serialization.Serializable

@Serializable
data class ClientParams(
    var sendDefinedAnimationInfoOnConnection: Boolean = true,
    var sendRunningAnimationInfoOnConnection: Boolean = true,
    var sendSectionInfoOnConnection: Boolean = true,
    var sendStripInfoOnConnection: Boolean = true,
    var sendAnimationStart: MessageFrequency = MessageFrequency.IMMEDIATE,
    var sendAnimationEnd: MessageFrequency = MessageFrequency.IMMEDIATE,
    var sendSectionCreation: MessageFrequency = MessageFrequency.IMMEDIATE,
    var sendLogs: Boolean = false,
) : SendableData {

    override fun toHumanReadableString(): String {
        TODO("not implemented")
    }
}
