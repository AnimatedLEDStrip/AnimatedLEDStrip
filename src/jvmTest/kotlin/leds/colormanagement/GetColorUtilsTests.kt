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

package animatedledstrip.test.leds.colormanagement

import animatedledstrip.colors.ColorContainer
import animatedledstrip.leds.animationmanagement.AnimationToRunParams
import animatedledstrip.leds.animationmanagement.addColor
import animatedledstrip.leds.animationmanagement.animation
import animatedledstrip.leds.colormanagement.*
import animatedledstrip.leds.emulation.createNewEmulatedStrip
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll

class GetColorUtilsTests : StringSpec(
    {
        val listTestStrip = createNewEmulatedStrip(10)

        afterSpec {
            listTestStrip.renderer.close()
        }

        "pixel actual color list" {
            listTestStrip.colorManager.pixelActualColorList shouldBe listOf(0, 0, 0, 0, 0,
                                                                            0, 0, 0, 0, 0)
            listTestStrip.pixelActualColorList shouldBe listTestStrip.colorManager.pixelActualColorList
            listTestStrip.sectionManager.pixelActualColorList shouldBe listTestStrip.colorManager.pixelActualColorList
            listTestStrip.sectionManager.createSection("test1", 3, 6).pixelActualColorList shouldBe listOf(0, 0, 0, 0)
        }

        "pixel fade color list" {
            listTestStrip.colorManager.pixelFadeColorList shouldBe listOf(-1, -1, -1, -1, -1,
                                                                          -1, -1, -1, -1, -1)
            listTestStrip.pixelFadeColorList shouldBe listTestStrip.colorManager.pixelFadeColorList
            listTestStrip.sectionManager.pixelFadeColorList shouldBe listTestStrip.colorManager.pixelFadeColorList
            listTestStrip.sectionManager.createSection("test2", 3, 6).pixelFadeColorList shouldBe listOf(-1, -1, -1, -1)
        }

        "pixel prolonged color list" {
            listTestStrip.colorManager.pixelProlongedColorList shouldBe listOf(0, 0, 0, 0, 0,
                                                                               0, 0, 0, 0, 0)
            listTestStrip.pixelProlongedColorList shouldBe listTestStrip.colorManager.pixelProlongedColorList
            listTestStrip.sectionManager.pixelProlongedColorList shouldBe listTestStrip.colorManager.pixelProlongedColorList
            listTestStrip.sectionManager.createSection("test3", 3, 6).pixelProlongedColorList shouldBe listOf(0, 0, 0, 0)
        }

        "pixel temporary color list" {
            listTestStrip.colorManager.pixelTemporaryColorList shouldBe listOf(-1, -1, -1, -1, -1,
                                                                               -1, -1, -1, -1, -1)
            listTestStrip.pixelTemporaryColorList shouldBe listTestStrip.colorManager.pixelTemporaryColorList
            listTestStrip.sectionManager.pixelTemporaryColorList shouldBe listTestStrip.colorManager.pixelTemporaryColorList
            listTestStrip.sectionManager.createSection("test4", 3, 6)
                .pixelTemporaryColorList shouldBe listOf(-1, -1, -1, -1)
        }

        "current strip color" {
            listTestStrip.currentStripColor() shouldBe CurrentStripColor(listTestStrip.pixelActualColorList)
        }


        val ledStrip = createNewEmulatedStrip(50).apply {
            renderer.stopRendering()
        }

        afterSpec {
            ledStrip.renderer.close()
        }

        "get pixel actual color" {
            checkAll(Arb.int(0..49), Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.setPixelTemporaryColor(p, c)
                ledStrip.colorManager.pixelColors[p].sendColorToStrip(ledStrip.nativeLEDStrip, false)
                ledStrip.colorManager.pixelColors[p].actualColor shouldBe c
                ledStrip.getPixelActualColor(p) shouldBe c
                ledStrip.getPixelActualColorOrNull(p) shouldBe c
                ledStrip.sectionManager.getPixelActualColor(p) shouldBe c
                ledStrip.sectionManager.getPixelActualColorOrNull(p) shouldBe c
                ledStrip.animationManager.getPixelActualColor(p) shouldBe c
                ledStrip.animationManager.getPixelActualColorOrNull(p) shouldBe c
            }

            checkAll(Arb.int().filter { it !in 0..49 }) { p ->
                shouldThrow<IllegalArgumentException> {
                    ledStrip.getPixelActualColor(p)
                }
                ledStrip.getPixelActualColorOrNull(p) shouldBe null
                shouldThrow<IllegalArgumentException> {
                    ledStrip.sectionManager.getPixelActualColor(p)
                }
                ledStrip.sectionManager.getPixelActualColorOrNull(p) shouldBe null
                shouldThrow<IllegalArgumentException> {
                    ledStrip.animationManager.getPixelActualColor(p)
                }
                ledStrip.animationManager.getPixelActualColorOrNull(p) shouldBe null
            }
        }

        "get pixel fade color" {
            checkAll(Arb.int(0..49), Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.setPixelFadeColor(p, c)
                ledStrip.colorManager.pixelColors[p].fadeColor shouldBe c
                ledStrip.getPixelFadeColor(p) shouldBe c
                ledStrip.getPixelFadeColorOrNull(p) shouldBe c
            }

            checkAll(Arb.int().filter { it !in 0..49 }) { p ->
                shouldThrow<IllegalArgumentException> {
                    ledStrip.getPixelFadeColor(p)
                }
                ledStrip.getPixelFadeColorOrNull(p) shouldBe null
            }
        }

        "get pixel prolonged color" {
            checkAll(Arb.int(0..49), Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.setPixelProlongedColor(p, c)
                ledStrip.colorManager.pixelColors[p].prolongedColor shouldBe c
                ledStrip.getPixelProlongedColor(p) shouldBe c
                ledStrip.getPixelProlongedColorOrNull(p) shouldBe c
            }

            checkAll(Arb.int().filter { it !in 0..49 }) { p ->
                shouldThrow<IllegalArgumentException> {
                    ledStrip.getPixelProlongedColor(p)
                }
                ledStrip.getPixelProlongedColorOrNull(p) shouldBe null
            }
        }

        "get pixel temporary color" {
            checkAll(Arb.int(0..49), Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.setPixelTemporaryColor(p, c)
                ledStrip.colorManager.pixelColors[p].temporaryColor shouldBe c
                ledStrip.getPixelTemporaryColor(p) shouldBe c
                ledStrip.getPixelTemporaryColorOrNull(p) shouldBe c
            }

            checkAll(Arb.int().filter { it !in 0..49 }) { p ->
                shouldThrow<IllegalArgumentException> {
                    ledStrip.getPixelTemporaryColor(p)
                }
                ledStrip.getPixelTemporaryColorOrNull(p) shouldBe null
            }
        }

        "random color" {
            checkAll(Arb.list(Arb.list(Arb.int(0..0xFFFFFF)))) { c ->
                val params = AnimationToRunParams().animation("Color")
                for (l in c) params.addColor(ColorContainer(l.toMutableList()))
                val prepParams = params.prepare(ledStrip.sectionManager.fullStripSection)
                prepParams.randomColor() shouldBeIn prepParams.colors
            }
        }
    }
)
