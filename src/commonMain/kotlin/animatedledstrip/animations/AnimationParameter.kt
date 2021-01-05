package animatedledstrip.animations

import kotlinx.serialization.Serializable

@Serializable
data class AnimationParameter<T>(
    val name: String,
    val description: String,
    val default: T? = null,
)
