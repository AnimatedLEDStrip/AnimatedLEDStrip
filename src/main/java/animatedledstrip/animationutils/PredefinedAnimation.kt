package animatedledstrip.animationutils

import animatedledstrip.leds.AnimatedLEDStrip
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import kotlinx.coroutines.CoroutineScope

class PredefinedAnimation(
    info: AnimationInfo,
    val animation: (AnimatedLEDStrip.Section, AnimationData, CoroutineScope) -> Unit
) : Animation(info) {

    companion object {
        const val prefix = "PANM"

        object ExStrategy : ExclusionStrategy {
            override fun shouldSkipClass(p0: Class<*>?) = false

            override fun shouldSkipField(field: FieldAttributes?): Boolean {
                return when (field?.name) {
                    "animation" -> true
                    else -> false
                }
            }
        }
    }

    override val prefix = PredefinedAnimation.prefix

    override fun runAnimation(leds: AnimatedLEDStrip.Section, data: AnimationData, scope: CoroutineScope) =
        animation(leds, data, scope)

}