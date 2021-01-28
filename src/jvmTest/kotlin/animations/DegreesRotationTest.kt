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

import animatedledstrip.animations.parameters.DegreesRotation
import animatedledstrip.animations.parameters.RadiansRotation
import animatedledstrip.animations.parameters.RotationAxis
import animatedledstrip.test.largeDoubleArb
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import kotlin.math.PI

class DegreesRotationTest : StringSpec(
    {
        "construction" {
            checkAll(largeDoubleArb,
                     largeDoubleArb,
                     largeDoubleArb,
                     Arb.list(Arb.enum<RotationAxis>(), 1..3)) { x, y, z, o ->
                val rot = DegreesRotation(x, y, z, o)
                rot.xRotation shouldBe x
                rot.yRotation shouldBe y
                rot.zRotation shouldBe z
                rot.rotationOrder.shouldContainExactly(o)
            }
        }

        "to radians rotation" {
            checkAll(largeDoubleArb,
                     largeDoubleArb,
                     largeDoubleArb,
                     Arb.list(Arb.enum<RotationAxis>(), 1..3)) { x, y, z, o ->
                val rot = DegreesRotation(x, y, z, o).toRadiansRotation()
                rot.xRotation shouldBe x * PI / 180
                rot.yRotation shouldBe y * PI / 180
                rot.zRotation shouldBe z * PI / 180
                rot.rotationOrder.shouldContainExactly(o)
                rot.shouldBeTypeOf<RadiansRotation>()
            }
        }
    }
)
