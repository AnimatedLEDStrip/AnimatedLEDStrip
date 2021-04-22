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

package animatedledstrip.test.leds.locationmanagement

import animatedledstrip.leds.locationmanagement.Location
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import kotlin.math.pow

class LocationTest : StringSpec(
    {
        "double constructor" {
            checkAll<Double, Double, Double>(15) { x, y, z ->
                val newLocation = Location(x, y, z)
                newLocation.x shouldBe x
                newLocation.y shouldBe y
                newLocation.z shouldBe z
            }
            val newLocation = Location(x = 0.0)
            newLocation.y shouldBe 0.0
            newLocation.z shouldBe 0.0
            val newLocation2 = Location(y = 0.0)
            newLocation2.x shouldBe 0.0
        }

        "int constructor" {
            checkAll<Int, Int, Int> { x, y, z ->
                val newLocation = Location(x, y, z)
                newLocation.x shouldBe x.toDouble()
                newLocation.y shouldBe y.toDouble()
                newLocation.z shouldBe z.toDouble()
            }
        }

        "coordinates" {
            checkAll<Double, Double, Double>(15) { x, y, z ->
                Location(x, y, z).coordinates shouldBe "$x, $y, $z"
            }
        }

        "distanceFrom" {
            checkAll<Double, Double, Double, Double, Double, Double>(15) { x1, y1, z1, x2, y2, z2 ->
                val loc1 = Location(x1, y1, z1)
                val loc2 = Location(x2, y2, z2)
                loc1.distanceFrom(loc2) shouldBe ((loc1.x - loc2.x).pow(2) +
                        (loc1.y - loc2.y).pow(2) +
                        (loc1.z - loc2.z).pow(2)).pow(0.5)
                loc1.distanceFrom(loc2) shouldBe loc2.distanceFrom(loc1)
            }
        }
    }
)
