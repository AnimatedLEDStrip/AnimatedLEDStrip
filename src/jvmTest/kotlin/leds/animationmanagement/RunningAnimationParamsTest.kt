/*
 *  Copyright (c) 2018-2020 AnimatedLEDStrip
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package animatedledstrip.test.leds.animationmanagement

import animatedledstrip.animations.Direction
import animatedledstrip.leds.stripmanagement.Location
import animatedledstrip.test.newRunningAnimationParams
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll

class RunningAnimationParamsTest : StringSpec(
    {

        "with animation modification" {
            newRunningAnimationParams.withModifications().animation shouldBe ""

            checkAll<String> { a ->
                newRunningAnimationParams.withModifications(animation = a).animation shouldBe a
            }
        }

        "with colors modification" {
            // TODO
        }

        "with center modification" {
            newRunningAnimationParams.withModifications().center shouldBe 5

            checkAll<Int> { c ->
                newRunningAnimationParams.withModifications(center = Location(c)).center shouldBe c
            }
        }

        "with delay modification" {
            newRunningAnimationParams.withModifications().delay shouldBe 5

            checkAll<Long> { d ->
                newRunningAnimationParams.withModifications(delay = d).delay shouldBe d
            }
        }

        "with delayMod modification" {
            newRunningAnimationParams.withModifications().delayMod shouldBe 2.0

            checkAll<Double> { d ->
                newRunningAnimationParams.withModifications(delayMod = d).delayMod shouldBe d
            }
        }

        "with direction modification" {
            newRunningAnimationParams.withModifications().direction shouldBe Direction.FORWARD

            newRunningAnimationParams.withModifications(direction = Direction.FORWARD).direction shouldBe Direction.FORWARD

            newRunningAnimationParams.withModifications(direction = Direction.BACKWARD).direction shouldBe Direction.BACKWARD
        }

        "with distance modification" {
            newRunningAnimationParams.withModifications().distance shouldBe 15

            checkAll<Int> { d ->
                newRunningAnimationParams.withModifications(distance = d).distance shouldBe d
            }
        }

        "with id modification" {
            newRunningAnimationParams.withModifications().id shouldBe ""

            checkAll<String> { i ->
                newRunningAnimationParams.withModifications(id = i).id shouldBe i
            }
        }

        "with runCount modification" {
            newRunningAnimationParams.withModifications().runCount shouldBe 10

            checkAll<Int> { r ->
                newRunningAnimationParams.withModifications(runCount = r).runCount shouldBe r
            }
        }

        "with section modification" {
            newRunningAnimationParams.withModifications().section shouldBe ""

            checkAll<String> { s ->
                newRunningAnimationParams.withModifications(section = s).section shouldBe s
            }
        }

        "with spacing modification" {
            newRunningAnimationParams.withModifications().spacing shouldBe 2

            checkAll<Int> { s ->
                newRunningAnimationParams.withModifications(spacing = s).spacing shouldBe s
            }
        }

        "encode JSON" {
            // TODO
        }

        "decode JSON" {
            // TODO
        }

        "encode and decode JSON" {
            // TODO
        }
    }
)
