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

import animatedledstrip.animations.AbsoluteDistance
import animatedledstrip.animations.Distance
import animatedledstrip.animations.PercentDistance
import animatedledstrip.test.largeDoubleArb
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.double
import io.kotest.property.checkAll

class DistanceTest : StringSpec(
    {
        "absolute times absolute" {
            checkAll(largeDoubleArb,
                     largeDoubleArb,
                     largeDoubleArb,
                     largeDoubleArb,
                     largeDoubleArb,
                     largeDoubleArb) { x1, y1, z1, x2, y2, z2 ->
                val res = AbsoluteDistance(x1, y1, z1) * AbsoluteDistance(x2, y2, z2)
                res.shouldNotBeNull()
                res.x shouldBe x1 * x2
                res.y shouldBe y1 * y2
                res.z shouldBe z1 * z2
            }
        }

        "absolute times percent" {
            checkAll(largeDoubleArb,
                     largeDoubleArb,
                     largeDoubleArb,
                     Arb.double(),
                     Arb.double(),
                     Arb.double()) { x1, y1, z1, x2, y2, z2 ->
                val res1 = AbsoluteDistance(x1, y1, z1) * PercentDistance(x2, y2, z2)
                res1.shouldNotBeNull()
                res1.x shouldBe x1 * x2
                res1.y shouldBe y1 * y2
                res1.z shouldBe z1 * z2
                val res2 = PercentDistance(x2, y2, z2) * AbsoluteDistance(x1, y1, z1)
                res2.shouldNotBeNull()
                res2.x shouldBe x1 * x2
                res2.y shouldBe y1 * y2
                res2.z shouldBe z1 * z2
                res1 shouldBe res2
            }
        }

        "percent times percent" {
            checkAll(Arb.double(),
                     Arb.double(),
                     Arb.double(),
                     Arb.double(),
                     Arb.double(),
                     Arb.double()) { x1, y1, z1, x2, y2, z2 ->
                val res = PercentDistance(x1, y1, z1) * PercentDistance(x2, y2, z2)
                res.shouldNotBeNull()
                res.x shouldBe x1 * x2
                res.y shouldBe y1 * y2
                res.z shouldBe z1 * z2
            }
        }

        "absolute times null" {
            checkAll(largeDoubleArb, largeDoubleArb, largeDoubleArb) { x, y, z ->
                val res = AbsoluteDistance(x, y, z) * null as Distance?
                res.shouldBeNull()
            }
        }

        "percent times null" {
            checkAll(Arb.double(), Arb.double(), Arb.double()) { x, y, z ->
                val res = PercentDistance(x, y, z) * null as Distance?
                res.shouldBeNull()
            }
        }
    }
)
