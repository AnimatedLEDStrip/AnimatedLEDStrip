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

package animatedledstrip.test.leds.emulation

import animatedledstrip.leds.emulation.EmulatedWS281x
import animatedledstrip.leds.stripmanagement.StripInfo
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

class EmulatedWS281xTest : StringSpec(
    {
        "strip info constructor" {
            checkAll(15, Arb.int(0..50000)) { n ->
                val newEmulation = EmulatedWS281x(StripInfo(numLEDs = n))
                newEmulation.numLEDs shouldBe n
                newEmulation.ledArray.size shouldBe n
            }
        }

        "num LEDs constructor" {
            checkAll(15, Arb.int(0..50000)) { n ->
                val newEmulation = EmulatedWS281x(n)
                newEmulation.numLEDs shouldBe n
                newEmulation.ledArray.size shouldBe n
            }
        }

        "get pixel color" {
            val newEmulation = EmulatedWS281x(50)
            checkAll(Arb.int(0..49), Arb.int(0..0xFFFFFF)) { p, c ->
                newEmulation.ledArray[p] = c
                newEmulation.getPixelColor(p) shouldBe c
            }
        }

        "set pixel color" {
            val newEmulation = EmulatedWS281x(50)
            checkAll(Arb.int(0..49), Arb.int(0..0xFFFFFF)) { p, c ->
                newEmulation.setPixelColor(p, c)
                newEmulation.ledArray[p] shouldBe c
            }
        }
    }
)
