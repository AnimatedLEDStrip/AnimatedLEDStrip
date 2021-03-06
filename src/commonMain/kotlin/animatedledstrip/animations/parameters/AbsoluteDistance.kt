/*
 * Copyright (c) 2018-2021 AnimatedLEDStrip
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package animatedledstrip.animations.parameters

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A [Distance] with the exact distance in each direction
 */
@Serializable
@SerialName("AbsoluteDistance")
data class AbsoluteDistance(override val x: Double = 0.0, override val y: Double = 0.0, override val z: Double = 0.0) :
    Distance {
    constructor(x: Int = 0, y: Int = 0, z: Int = 0) : this(x.toDouble(), y.toDouble(), z.toDouble())
    constructor() : this(0.0, 0.0, 0.0)

    operator fun unaryMinus(): AbsoluteDistance = AbsoluteDistance(-x, -y, -z)

    operator fun div(divisor: Int): AbsoluteDistance = AbsoluteDistance(x / divisor, y / divisor, z / divisor)

    operator fun times(multiplier: Distance): AbsoluteDistance = when (multiplier) {
        is PercentDistance ->
            AbsoluteDistance(x * multiplier.x / 100.0,
                             y * multiplier.y / 100.0,
                             z * multiplier.z / 100.0)
        else ->
            AbsoluteDistance(x * multiplier.x,
                             y * multiplier.y,
                             z * multiplier.z)
    }

    companion object {
        val NO_DISTANCE: AbsoluteDistance
            get() = AbsoluteDistance(0.0, 0.0, 0.0)
    }
}
