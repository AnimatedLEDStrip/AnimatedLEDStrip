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
import kotlin.math.pow

/**
 * An equation of the form ax^0 + bx^1 ... mx^n.
 * Each coefficient corresponds with x to the index of that coefficient,
 * i.e. the coefficient at index 2 would be associated with x^2.
 */
@Serializable
@SerialName("Equation")
data class Equation(
    val coefficients: List<Double> = listOf(),
) {
    constructor(vararg coefficients: Double) : this(coefficients.toList())

    fun calculate(value: Double): Double =
        coefficients.reduceIndexedOrNull { power, acc, c ->
            acc + (value.pow(power) * c)
        } ?: 0.0
}
