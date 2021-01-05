package animatedledstrip.animations

import kotlinx.serialization.Serializable

@Serializable
class Equation(
    val coefficients: List<Double>,
) {
    constructor(vararg coefficients: Double) : this(coefficients.toList())

    companion object {
        val default = Equation()
    }
}
