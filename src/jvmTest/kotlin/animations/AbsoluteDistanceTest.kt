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

package animatedledstrip.test.animations

import animatedledstrip.animations.parameters.AbsoluteDistance
import animatedledstrip.test.largeDoubleArb
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlin.math.max

class AbsoluteDistanceTest : StringSpec(
    {
        "double constructor" {
            checkAll(largeDoubleArb, largeDoubleArb, largeDoubleArb) { x, y, z ->
                val distance = AbsoluteDistance(x, y, z)
                distance.x shouldBe x
                distance.y shouldBe y
                distance.z shouldBe z
            }
        }

        "int constructor" {
            checkAll(Arb.int(), Arb.int(), Arb.int()) { x, y, z ->
                val distance = AbsoluteDistance(x, y, z)
                distance.x shouldBe x.toDouble()
                distance.y shouldBe y.toDouble()
                distance.z shouldBe z.toDouble()
            }
        }

        "coordinates" {
            checkAll(largeDoubleArb, largeDoubleArb, largeDoubleArb) { x, y, z ->
                AbsoluteDistance(x, y, z).coordinates shouldBe "$x, $y, $z"
            }
        }

        "max distance" {
            checkAll(largeDoubleArb, largeDoubleArb, largeDoubleArb) { x, y, z ->
                AbsoluteDistance(x, y, z).maxDistance shouldBe max(x, max(y, z))
                AbsoluteDistance(x, y, z).maxDistance shouldBe max(max(x, y), z)
            }
        }
    }
)
