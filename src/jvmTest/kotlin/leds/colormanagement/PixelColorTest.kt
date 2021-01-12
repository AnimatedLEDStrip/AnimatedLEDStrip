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

package animatedledstrip.test.leds.colormanagement

import animatedledstrip.leds.colormanagement.PixelColor
import animatedledstrip.leds.colormanagement.PixelColorType
import animatedledstrip.leds.emulation.EmulatedWS281x
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

class PixelColorTest : StringSpec(
    {
        "pixel number" {
            checkAll<Int> { p ->
                PixelColor(p).pixelNumber shouldBe p
            }
        }

        "actual color" {
            val pixel = PixelColor(0)

            shouldThrow<IllegalArgumentException> {
                pixel.setColor(0xFF, PixelColorType.ACTUAL)
            }

            pixel.actualColor shouldBe 0
            pixel.fadeColor shouldBe -1
            pixel.prolongedColor shouldBe 0
            pixel.temporaryColor shouldBe -1
            pixel.getColor(PixelColorType.ACTUAL) shouldBe 0
            pixel.getColor(PixelColorType.FADE) shouldBe -1
            pixel.getColor(PixelColorType.PROLONGED) shouldBe 0
            pixel.getColor(PixelColorType.TEMPORARY) shouldBe -1
        }

        "fade color" {
            val pixel = PixelColor(0)

            checkAll<Int> { c ->
                pixel.setColor(c, PixelColorType.FADE)

                pixel.actualColor shouldBe 0
                pixel.fadeColor shouldBe c
                pixel.prolongedColor shouldBe 0
                pixel.temporaryColor shouldBe -1
                pixel.getColor(PixelColorType.ACTUAL) shouldBe 0
                pixel.getColor(PixelColorType.FADE) shouldBe c
                pixel.getColor(PixelColorType.PROLONGED) shouldBe 0
                pixel.getColor(PixelColorType.TEMPORARY) shouldBe -1
            }
        }

        "prolonged color" {
            val pixel = PixelColor(0)

            checkAll<Int> { c ->
                pixel.setColor(c, PixelColorType.PROLONGED)

                pixel.actualColor shouldBe 0
                pixel.fadeColor shouldBe -1
                pixel.prolongedColor shouldBe c
                pixel.temporaryColor shouldBe -1
                pixel.getColor(PixelColorType.ACTUAL) shouldBe 0
                pixel.getColor(PixelColorType.FADE) shouldBe -1
                pixel.getColor(PixelColorType.PROLONGED) shouldBe c
                pixel.getColor(PixelColorType.TEMPORARY) shouldBe -1
            }
        }

        "temporary color" {
            val pixel = PixelColor(0)

            checkAll<Int> { c ->
                pixel.setColor(c, PixelColorType.TEMPORARY)

                pixel.actualColor shouldBe 0
                pixel.fadeColor shouldBe -1
                pixel.prolongedColor shouldBe 0
                pixel.temporaryColor shouldBe c
                pixel.getColor(PixelColorType.ACTUAL) shouldBe 0
                pixel.getColor(PixelColorType.FADE) shouldBe -1
                pixel.getColor(PixelColorType.PROLONGED) shouldBe 0
                pixel.getColor(PixelColorType.TEMPORARY) shouldBe c
            }
        }

        "revert color" {
            val pixel1 = PixelColor(0)

            checkAll<Int> { c ->
                pixel1.setColor(c, PixelColorType.FADE)

                pixel1.actualColor shouldBe 0
                pixel1.fadeColor shouldBe c
                pixel1.prolongedColor shouldBe 0
                pixel1.temporaryColor shouldBe -1
                pixel1.getColor(PixelColorType.ACTUAL) shouldBe 0
                pixel1.getColor(PixelColorType.FADE) shouldBe c
                pixel1.getColor(PixelColorType.PROLONGED) shouldBe 0
                pixel1.getColor(PixelColorType.TEMPORARY) shouldBe -1

                pixel1.revertColor()

                pixel1.actualColor shouldBe 0
                pixel1.fadeColor shouldBe c
                pixel1.prolongedColor shouldBe 0
                pixel1.temporaryColor shouldBe -1
                pixel1.getColor(PixelColorType.ACTUAL) shouldBe 0
                pixel1.getColor(PixelColorType.FADE) shouldBe c
                pixel1.getColor(PixelColorType.PROLONGED) shouldBe 0
                pixel1.getColor(PixelColorType.TEMPORARY) shouldBe -1
            }

            val pixel2 = PixelColor(1)

            checkAll<Int> { c ->
                pixel2.setColor(c, PixelColorType.PROLONGED)

                pixel2.actualColor shouldBe 0
                pixel2.fadeColor shouldBe -1
                pixel2.prolongedColor shouldBe c
                pixel2.temporaryColor shouldBe -1
                pixel2.getColor(PixelColorType.ACTUAL) shouldBe 0
                pixel2.getColor(PixelColorType.FADE) shouldBe -1
                pixel2.getColor(PixelColorType.PROLONGED) shouldBe c
                pixel2.getColor(PixelColorType.TEMPORARY) shouldBe -1

                pixel2.revertColor()

                pixel2.actualColor shouldBe 0
                pixel2.fadeColor shouldBe -1
                pixel2.prolongedColor shouldBe c
                pixel2.temporaryColor shouldBe -1
                pixel2.getColor(PixelColorType.ACTUAL) shouldBe 0
                pixel2.getColor(PixelColorType.FADE) shouldBe -1
                pixel2.getColor(PixelColorType.PROLONGED) shouldBe c
                pixel2.getColor(PixelColorType.TEMPORARY) shouldBe -1
            }

            val pixel3 = PixelColor(2)

            checkAll<Int> { c ->
                pixel3.setColor(c, PixelColorType.TEMPORARY)

                pixel3.actualColor shouldBe 0
                pixel3.fadeColor shouldBe -1
                pixel3.prolongedColor shouldBe 0
                pixel3.temporaryColor shouldBe c
                pixel3.getColor(PixelColorType.ACTUAL) shouldBe 0
                pixel3.getColor(PixelColorType.FADE) shouldBe -1
                pixel3.getColor(PixelColorType.PROLONGED) shouldBe 0
                pixel3.getColor(PixelColorType.TEMPORARY) shouldBe c

                pixel3.revertColor()

                pixel3.actualColor shouldBe 0
                pixel3.fadeColor shouldBe -1
                pixel3.prolongedColor shouldBe 0
                pixel3.temporaryColor shouldBe -1
                pixel3.getColor(PixelColorType.ACTUAL) shouldBe 0
                pixel3.getColor(PixelColorType.FADE) shouldBe -1
                pixel3.getColor(PixelColorType.PROLONGED) shouldBe 0
                pixel3.getColor(PixelColorType.TEMPORARY) shouldBe -1
            }
        }

        "send color to strip" {
            val emulatedStrip = EmulatedWS281x(5)

            checkAll(Arb.int(), Arb.int(), Arb.int().filter { it != -1 }) { f, p, t ->
                val pixel = PixelColor(0)
                pixel.setColor(f, PixelColorType.FADE)
                pixel.setColor(p, PixelColorType.PROLONGED)
                pixel.setColor(t, PixelColorType.TEMPORARY)

                pixel.actualColor shouldBe 0
                pixel.fadeColor shouldBe f
                pixel.prolongedColor shouldBe p
                pixel.temporaryColor shouldBe t
                pixel.getColor(PixelColorType.ACTUAL) shouldBe 0
                pixel.getColor(PixelColorType.FADE) shouldBe f
                pixel.getColor(PixelColorType.PROLONGED) shouldBe p
                pixel.getColor(PixelColorType.TEMPORARY) shouldBe t

                pixel.sendColorToStrip(emulatedStrip, doFade = false)

                pixel.actualColor shouldBe t
                pixel.fadeColor shouldBe f
                pixel.prolongedColor shouldBe p
                pixel.temporaryColor shouldBe t
                pixel.getColor(PixelColorType.ACTUAL) shouldBe t
                pixel.getColor(PixelColorType.FADE) shouldBe f
                pixel.getColor(PixelColorType.PROLONGED) shouldBe p
                pixel.getColor(PixelColorType.TEMPORARY) shouldBe t

                emulatedStrip.getPixelColor(0) shouldBe t
            }

            checkAll(Arb.int().filter { it != -1 }, Arb.int()) { f, p ->
                val pixel = PixelColor(0)
                pixel.setColor(f, PixelColorType.FADE)
                pixel.setColor(p, PixelColorType.PROLONGED)

                pixel.actualColor shouldBe 0
                pixel.fadeColor shouldBe f
                pixel.prolongedColor shouldBe p
                pixel.temporaryColor shouldBe -1
                pixel.getColor(PixelColorType.ACTUAL) shouldBe 0
                pixel.getColor(PixelColorType.FADE) shouldBe f
                pixel.getColor(PixelColorType.PROLONGED) shouldBe p
                pixel.getColor(PixelColorType.TEMPORARY) shouldBe -1

                pixel.sendColorToStrip(emulatedStrip, doFade = false)

                pixel.actualColor shouldBe f
                pixel.fadeColor shouldBe f
                pixel.prolongedColor shouldBe p
                pixel.temporaryColor shouldBe -1
                pixel.getColor(PixelColorType.ACTUAL) shouldBe f
                pixel.getColor(PixelColorType.FADE) shouldBe f
                pixel.getColor(PixelColorType.PROLONGED) shouldBe p
                pixel.getColor(PixelColorType.TEMPORARY) shouldBe -1

                emulatedStrip.getPixelColor(0) shouldBe f
            }

            checkAll<Int> { p ->
                val pixel = PixelColor(0)
                pixel.setColor(p, PixelColorType.PROLONGED)

                pixel.actualColor shouldBe 0
                pixel.fadeColor shouldBe -1
                pixel.prolongedColor shouldBe p
                pixel.temporaryColor shouldBe -1
                pixel.getColor(PixelColorType.ACTUAL) shouldBe 0
                pixel.getColor(PixelColorType.FADE) shouldBe -1
                pixel.getColor(PixelColorType.PROLONGED) shouldBe p
                pixel.getColor(PixelColorType.TEMPORARY) shouldBe -1

                pixel.sendColorToStrip(emulatedStrip, doFade = false)

                pixel.actualColor shouldBe p
                pixel.fadeColor shouldBe -1
                pixel.prolongedColor shouldBe p
                pixel.temporaryColor shouldBe -1
                pixel.getColor(PixelColorType.ACTUAL) shouldBe p
                pixel.getColor(PixelColorType.FADE) shouldBe -1
                pixel.getColor(PixelColorType.PROLONGED) shouldBe p
                pixel.getColor(PixelColorType.TEMPORARY) shouldBe -1

                emulatedStrip.getPixelColor(0) shouldBe p
            }
        }

        "send color to strip with fade" {
            // TODO
        }
    }
)
