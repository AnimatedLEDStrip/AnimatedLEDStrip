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

import animatedledstrip.animations.AnimationParameter
import animatedledstrip.test.distanceArb
import animatedledstrip.test.locationArb
import animatedledstrip.test.rotationArb
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class AnimationParameterTest : StringSpec(
    {
        "construction" {
            checkAll(Arb.string(), Arb.string(), Arb.int()) { n, d, def ->
                val param = AnimationParameter(n, d, def)
                param.name shouldBe n
                param.description shouldBe d
                param.default shouldBe def
            }
            checkAll(Arb.string(), Arb.string(), Arb.double()) { n, d, def ->
                val param = AnimationParameter(n, d, def)
                param.name shouldBe n
                param.description shouldBe d
                param.default shouldBe def
            }
            checkAll(Arb.string(), Arb.string(), Arb.string()) { n, d, def ->
                val param = AnimationParameter(n, d, def)
                param.name shouldBe n
                param.description shouldBe d
                param.default shouldBe def
            }
            checkAll(Arb.string(), Arb.string(), locationArb) { n, d, def ->
                val param = AnimationParameter(n, d, def)
                param.name shouldBe n
                param.description shouldBe d
                param.default shouldBe def
            }
            checkAll(Arb.string(), Arb.string(), distanceArb) { n, d, def ->
                val param = AnimationParameter(n, d, def)
                param.name shouldBe n
                param.description shouldBe d
                param.default shouldBe def
            }
            checkAll(Arb.string(), Arb.string(), rotationArb) { n, d, def ->
                val param = AnimationParameter(n, d, def)
                param.name shouldBe n
                param.description shouldBe d
                param.default shouldBe def
            }
        }
    }
)
