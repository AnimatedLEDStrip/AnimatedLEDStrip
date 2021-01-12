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
 *
 */

package animatedledstrip.test.leds.colormanagement

import animatedledstrip.colors.ColorContainer
import animatedledstrip.leds.colormanagement.LEDStripColorManager
import animatedledstrip.leds.colormanagement.PixelColorType
import animatedledstrip.leds.emulation.createNewEmulatedStrip
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll

class LEDStripColorManagerTest : StringSpec(
    {
        val ledStrip = createNewEmulatedStrip(50)

        afterSpec {
            ledStrip.renderer.close()
        }

        "set pixel color" {
            val colorManager = LEDStripColorManager(ledStrip)

            checkAll(Arb.int(0..49), Arb.list(Arb.int(0..0xFFFFFF))) { p, c ->
                colorManager.setPixelColor(p, ColorContainer(c.toMutableList()).prepare(50), PixelColorType.FADE)
                colorManager.pixelColors[p].fadeColor shouldBe ColorContainer(c.toMutableList()).prepare(50)[p]
            }

            checkAll(Arb.int(0..49), Arb.list(Arb.int(0..0xFFFFFF))) { p, c ->
                colorManager.setPixelColor(p, ColorContainer(c.toMutableList()).prepare(50), PixelColorType.PROLONGED)
                colorManager.pixelColors[p].prolongedColor shouldBe ColorContainer(c.toMutableList()).prepare(50)[p]
            }

            checkAll(Arb.int(0..49), Arb.list(Arb.int(0..0xFFFFFF))) { p, c ->
                colorManager.setPixelColor(p, ColorContainer(c.toMutableList()).prepare(50), PixelColorType.TEMPORARY)
                colorManager.pixelColors[p].temporaryColor shouldBe ColorContainer(c.toMutableList()).prepare(50)[p]
            }


            checkAll(Arb.int(0..49), Arb.int(0..0xFFFFFF)) { p, c ->
                colorManager.setPixelColor(p, c, PixelColorType.FADE)
                colorManager.pixelColors[p].fadeColor shouldBe c
            }

            checkAll(Arb.int(0..49), Arb.int(0..0xFFFFFF)) { p, c ->
                colorManager.setPixelColor(p, c, PixelColorType.PROLONGED)
                colorManager.pixelColors[p].prolongedColor shouldBe c
            }

            checkAll(Arb.int(0..49), Arb.int(0..0xFFFFFF)) { p, c ->
                colorManager.setPixelColor(p, c, PixelColorType.TEMPORARY)
                colorManager.pixelColors[p].temporaryColor shouldBe c
            }

            checkAll(Arb.int().filter { it !in 0..49 }, Arb.int(0..0xFFFFFF)) { p, c ->
                shouldThrow<IllegalArgumentException> {
                    colorManager.setPixelColor(p, c, PixelColorType.ACTUAL)
                }
                shouldThrow<IllegalArgumentException> {
                    colorManager.setPixelColor(p, c, PixelColorType.FADE)
                }
                shouldThrow<IllegalArgumentException> {
                    colorManager.setPixelColor(p, c, PixelColorType.PROLONGED)
                }
                shouldThrow<IllegalArgumentException> {
                    colorManager.setPixelColor(p, c, PixelColorType.TEMPORARY)
                }
            }
        }

        "revert pixel" {
            val colorManager = LEDStripColorManager(ledStrip)

            checkAll(Arb.int(0..49), Arb.int(0..0xFFFFFF)) { p, c ->
                colorManager.setPixelColor(p, c, PixelColorType.TEMPORARY)
                colorManager.pixelColors[p].temporaryColor shouldBe c
                colorManager.revertPixel(p)
                colorManager.pixelColors[p].temporaryColor shouldBe -1
            }
        }

        "get pixel color" {
            val colorManager = LEDStripColorManager(ledStrip)

            checkAll(Arb.int(0..49), Arb.int(0..0xFFFFFF)) { p, c ->
                colorManager.setPixelColor(p, c, PixelColorType.FADE)
                colorManager.getPixelColor(p, PixelColorType.FADE) shouldBe c
                colorManager.pixelColors[p].fadeColor shouldBe c
            }

            checkAll(Arb.int(0..49), Arb.int(0..0xFFFFFF)) { p, c ->
                colorManager.setPixelColor(p, c, PixelColorType.PROLONGED)
                colorManager.getPixelColor(p, PixelColorType.PROLONGED) shouldBe c
                colorManager.pixelColors[p].prolongedColor shouldBe c
            }

            checkAll(Arb.int(0..49), Arb.int(0..0xFFFFFF)) { p, c ->
                colorManager.setPixelColor(p, c, PixelColorType.TEMPORARY)
                colorManager.getPixelColor(p, PixelColorType.TEMPORARY) shouldBe c
                colorManager.pixelColors[p].temporaryColor shouldBe c
            }
        }
    }
)
