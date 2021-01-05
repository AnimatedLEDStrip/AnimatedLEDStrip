package animatedledstrip.animations

import kotlinx.serialization.Serializable
import kotlin.math.max

@Serializable
data class Distance(
    val x: Double = 0.0,
    val y: Double = 0.0,
    val z: Double = 0.0,
) {
    constructor(x: Int = 0, y: Int = 0, z: Int = 0) : this(x.toDouble(), y.toDouble(), z.toDouble())

    operator fun times(multiplier: Distance?): Distance? =
        if (multiplier == null) null
        else Distance(x * multiplier.x,
                      y * multiplier.y,
                      z * multiplier.z)

    val maxDistance: Double = max(x, max(y, z))
}
