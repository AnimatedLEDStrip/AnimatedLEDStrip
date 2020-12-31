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
import animatedledstrip.leds.colormanagement.*
import animatedledstrip.leds.emulation.createNewEmulatedStrip
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll

class SetColorUtilsTests : StringSpec(
    {
        val ledStrip = createNewEmulatedStrip(50).apply {
            renderer.stopRendering()
        }

        afterSpec {
            ledStrip.renderer.close()
        }

        "set pixel fade color" {
            checkAll(Arb.int(0..49), Arb.list(Arb.int(0..0xFFFFFF))) { p, c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                ledStrip.setPixelFadeColor(p, pCC)
                ledStrip.colorManager.pixelColors[p].fadeColor shouldBe pCC[p]
            }

            checkAll(Arb.int(0..49), Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.setPixelFadeColor(p, c)
                ledStrip.colorManager.pixelColors[p].fadeColor shouldBe c
            }

            checkAll(Arb.list(Arb.int(0..49)),
                     Arb.list(Arb.int(0..0xFFFFFF))) { p, c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                ledStrip.setPixelFadeColors(p, pCC)
                for (pixel in p)
                    ledStrip.colorManager.pixelColors[pixel].fadeColor shouldBe pCC[pixel]
            }

            checkAll(Arb.list(Arb.int(0..49)),
                     Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.setPixelFadeColors(p, c)
                for (pixel in p)
                    ledStrip.colorManager.pixelColors[pixel].fadeColor shouldBe c
            }

            checkAll(Arb.list(Arb.int(0..49), 2..2),
                     Arb.list(Arb.int(0..0xFFFFFF))) { p, c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                ledStrip.setPixelFadeColors(IntRange(p[0], p[1]), pCC)
                for (pixel in IntRange(p[0], p[1]))
                    ledStrip.colorManager.pixelColors[pixel].fadeColor shouldBe pCC[pixel]
            }

            checkAll(Arb.list(Arb.int(0..49), 2..2).filter { it[0] <= it[1] },
                     Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.setPixelFadeColors(IntRange(p[0], p[1]), c)
                for (pixel in IntRange(p[0], p[1]))
                    ledStrip.colorManager.pixelColors[pixel].fadeColor shouldBe c
            }


            checkAll(Arb.int(0..49), Arb.list(Arb.int(0..0xFFFFFF))) { p, c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                ledStrip.sectionManager.setPixelFadeColor(p, pCC)
                ledStrip.colorManager.pixelColors[p].fadeColor shouldBe pCC[p]
            }

            checkAll(Arb.int(0..49), Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.sectionManager.setPixelFadeColor(p, c)
                ledStrip.colorManager.pixelColors[p].fadeColor shouldBe c
            }

            checkAll(Arb.list(Arb.int(0..49)),
                     Arb.list(Arb.int(0..0xFFFFFF))) { p, c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                ledStrip.sectionManager.setPixelFadeColors(p, pCC)
                for (pixel in p) ledStrip.colorManager.pixelColors[pixel].fadeColor shouldBe pCC[pixel]
            }

            checkAll(Arb.list(Arb.int(0..49)),
                     Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.sectionManager.setPixelFadeColors(p, c)
                for (pixel in p) ledStrip.colorManager.pixelColors[pixel].fadeColor shouldBe c
            }

            checkAll(Arb.list(Arb.int(0..49), 2..2),
                     Arb.list(Arb.int(0..0xFFFFFF))) { p, c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                ledStrip.sectionManager.setPixelFadeColors(IntRange(p[0], p[1]), pCC)
                for (pixel in IntRange(p[0], p[1]))
                    ledStrip.colorManager.pixelColors[pixel].fadeColor shouldBe pCC[pixel]
            }

            checkAll(Arb.list(Arb.int(0..49), 2..2).filter { it[0] <= it[1] },
                     Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.sectionManager.setPixelFadeColors(IntRange(p[0], p[1]), c)
                for (pixel in IntRange(p[0], p[1]))
                    ledStrip.colorManager.pixelColors[pixel].fadeColor shouldBe c
            }


            val testSection = ledStrip.sectionManager.createSection("testf", 5, 39)

            checkAll(Arb.int(0..34), Arb.list(Arb.int(0..0xFFFFFF))) { p, c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(35)
                testSection.setPixelFadeColor(p, pCC)
                ledStrip.colorManager.pixelColors[p + 5].fadeColor shouldBe pCC[p]
            }

            checkAll(Arb.int(0..34), Arb.int(0..0xFFFFFF)) { p, c ->
                testSection.setPixelFadeColor(p, c)
                ledStrip.colorManager.pixelColors[p + 5].fadeColor shouldBe c
            }

            checkAll(Arb.list(Arb.int(0..34)),
                     Arb.list(Arb.int(0..0xFFFFFF)).filter { it.isNotEmpty() }) { p, c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(35)
                testSection.setPixelFadeColors(p, pCC)
                for (pixel in p)
                    ledStrip.colorManager.pixelColors[pixel + 5].fadeColor shouldBe pCC[pixel]
            }

            checkAll(Arb.list(Arb.int(0..34)),
                     Arb.int(0..0xFFFFFF)) { p, c ->
                testSection.setPixelFadeColors(p, c)
                for (pixel in p)
                    ledStrip.colorManager.pixelColors[pixel + 5].fadeColor shouldBe c
            }

            checkAll(Arb.list(Arb.int(0..34), 2..2),
                     Arb.list(Arb.int(0..0xFFFFFF))) { p, c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(35)
                testSection.setPixelFadeColors(IntRange(p[0], p[1]), pCC)
                for (pixel in IntRange(p[0], p[1]))
                    ledStrip.colorManager.pixelColors[pixel + 5].fadeColor shouldBe pCC[pixel]
            }

            checkAll(Arb.list(Arb.int(0..34), 2..2).filter { it[0] <= it[1] },
                     Arb.int(0..0xFFFFFF)) { p, c ->
                testSection.setPixelFadeColors(IntRange(p[0], p[1]), c)
                for (pixel in IntRange(p[0], p[1]))
                    ledStrip.colorManager.pixelColors[pixel + 5].fadeColor shouldBe c
            }


            checkAll(Arb.int(0..49), Arb.list(Arb.int(0..0xFFFFFF))) { p, c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                ledStrip.animationManager.setPixelFadeColor(p, pCC)
                ledStrip.colorManager.pixelColors[p].fadeColor shouldBe pCC[p]
            }

            checkAll(Arb.int(0..49), Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.animationManager.setPixelFadeColor(p, c)
                ledStrip.colorManager.pixelColors[p].fadeColor shouldBe c
            }

            checkAll(Arb.list(Arb.int(0..49)),
                     Arb.list(Arb.int(0..0xFFFFFF))) { p, c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                ledStrip.animationManager.setPixelFadeColors(p, pCC)
                for (pixel in p) ledStrip.colorManager.pixelColors[pixel].fadeColor shouldBe pCC[pixel]
            }

            checkAll(Arb.list(Arb.int(0..49)),
                     Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.animationManager.setPixelFadeColors(p, c)
                for (pixel in p) ledStrip.colorManager.pixelColors[pixel].fadeColor shouldBe c
            }

            checkAll(Arb.list(Arb.int(0..49), 2..2),
                     Arb.list(Arb.int(0..0xFFFFFF))) { p, c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                ledStrip.animationManager.setPixelFadeColors(IntRange(p[0], p[1]), pCC)
                for (pixel in IntRange(p[0], p[1]))
                    ledStrip.colorManager.pixelColors[pixel].fadeColor shouldBe pCC[pixel]
            }

            checkAll(Arb.list(Arb.int(0..49), 2..2).filter { it[0] <= it[1] },
                     Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.animationManager.setPixelFadeColors(IntRange(p[0], p[1]), c)
                for (pixel in IntRange(p[0], p[1]))
                    ledStrip.colorManager.pixelColors[pixel].fadeColor shouldBe c
            }
        }

        "set pixel prolonged color" {
            checkAll(Arb.int(0..49), Arb.list(Arb.int(0..0xFFFFFF))) { p, c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                ledStrip.setPixelProlongedColor(p, pCC)
                ledStrip.colorManager.pixelColors[p].prolongedColor shouldBe pCC[p]
            }

            checkAll(Arb.int(0..49), Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.setPixelProlongedColor(p, c)
                ledStrip.colorManager.pixelColors[p].prolongedColor shouldBe c
            }

            checkAll(Arb.list(Arb.int(0..49)),
                     Arb.list(Arb.int(0..0xFFFFFF))) { p, c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                ledStrip.setPixelProlongedColors(p, pCC)
                for (pixel in p)
                    ledStrip.colorManager.pixelColors[pixel].prolongedColor shouldBe pCC[pixel]
            }

            checkAll(Arb.list(Arb.int(0..49)),
                     Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.setPixelProlongedColors(p, c)
                for (pixel in p)
                    ledStrip.colorManager.pixelColors[pixel].prolongedColor shouldBe c
            }

            checkAll(Arb.list(Arb.int(0..49), 2..2),
                     Arb.list(Arb.int(0..0xFFFFFF))) { p, c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                ledStrip.setPixelProlongedColors(IntRange(p[0], p[1]), pCC)
                for (pixel in IntRange(p[0], p[1]))
                    ledStrip.colorManager.pixelColors[pixel].prolongedColor shouldBe pCC[pixel]
            }

            checkAll(Arb.list(Arb.int(0..49), 2..2).filter { it[0] <= it[1] },
                     Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.setPixelProlongedColors(IntRange(p[0], p[1]), c)
                for (pixel in IntRange(p[0], p[1]))
                    ledStrip.colorManager.pixelColors[pixel].prolongedColor shouldBe c
            }


            checkAll(Arb.int(0..49), Arb.list(Arb.int(0..0xFFFFFF))) { p, c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                ledStrip.sectionManager.setPixelProlongedColor(p, pCC)
                ledStrip.colorManager.pixelColors[p].prolongedColor shouldBe pCC[p]
            }

            checkAll(Arb.int(0..49), Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.sectionManager.setPixelProlongedColor(p, c)
                ledStrip.colorManager.pixelColors[p].prolongedColor shouldBe c
            }

            checkAll(Arb.list(Arb.int(0..49)),
                     Arb.list(Arb.int(0..0xFFFFFF))) { p, c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                ledStrip.sectionManager.setPixelProlongedColors(p, pCC)
                for (pixel in p) ledStrip.colorManager.pixelColors[pixel].prolongedColor shouldBe pCC[pixel]
            }

            checkAll(Arb.list(Arb.int(0..49)),
                     Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.sectionManager.setPixelProlongedColors(p, c)
                for (pixel in p) ledStrip.colorManager.pixelColors[pixel].prolongedColor shouldBe c
            }

            checkAll(Arb.list(Arb.int(0..49), 2..2),
                     Arb.list(Arb.int(0..0xFFFFFF))) { p, c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                ledStrip.sectionManager.setPixelProlongedColors(IntRange(p[0], p[1]), pCC)
                for (pixel in IntRange(p[0], p[1]))
                    ledStrip.colorManager.pixelColors[pixel].prolongedColor shouldBe pCC[pixel]
            }

            checkAll(Arb.list(Arb.int(0..49), 2..2).filter { it[0] <= it[1] },
                     Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.sectionManager.setPixelProlongedColors(IntRange(p[0], p[1]), c)
                for (pixel in IntRange(p[0], p[1]))
                    ledStrip.colorManager.pixelColors[pixel].prolongedColor shouldBe c
            }


            val testSection = ledStrip.sectionManager.createSection("testp", 5, 39)

            checkAll(Arb.int(0..34), Arb.list(Arb.int(0..0xFFFFFF))) { p, c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(35)
                testSection.setPixelProlongedColor(p, pCC)
                ledStrip.colorManager.pixelColors[p + 5].prolongedColor shouldBe pCC[p]
            }

            checkAll(Arb.int(0..34), Arb.int(0..0xFFFFFF)) { p, c ->
                testSection.setPixelProlongedColor(p, c)
                ledStrip.colorManager.pixelColors[p + 5].prolongedColor shouldBe c
            }

            checkAll(Arb.list(Arb.int(0..34)),
                     Arb.list(Arb.int(0..0xFFFFFF)).filter { it.isNotEmpty() }) { p, c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(35)
                testSection.setPixelProlongedColors(p, pCC)
                for (pixel in p)
                    ledStrip.colorManager.pixelColors[pixel + 5].prolongedColor shouldBe pCC[pixel]
            }

            checkAll(Arb.list(Arb.int(0..34)),
                     Arb.int(0..0xFFFFFF)) { p, c ->
                testSection.setPixelProlongedColors(p, c)
                for (pixel in p)
                    ledStrip.colorManager.pixelColors[pixel + 5].prolongedColor shouldBe c
            }

            checkAll(Arb.list(Arb.int(0..34), 2..2),
                     Arb.list(Arb.int(0..0xFFFFFF))) { p, c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(35)
                testSection.setPixelProlongedColors(IntRange(p[0], p[1]), pCC)
                for (pixel in IntRange(p[0], p[1]))
                    ledStrip.colorManager.pixelColors[pixel + 5].prolongedColor shouldBe pCC[pixel]
            }

            checkAll(Arb.list(Arb.int(0..34), 2..2).filter { it[0] <= it[1] },
                     Arb.int(0..0xFFFFFF)) { p, c ->
                testSection.setPixelProlongedColors(IntRange(p[0], p[1]), c)
                for (pixel in IntRange(p[0], p[1]))
                    ledStrip.colorManager.pixelColors[pixel + 5].prolongedColor shouldBe c
            }


            checkAll(Arb.int(0..49), Arb.list(Arb.int(0..0xFFFFFF))) { p, c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                ledStrip.animationManager.setPixelProlongedColor(p, pCC)
                ledStrip.colorManager.pixelColors[p].prolongedColor shouldBe pCC[p]
            }

            checkAll(Arb.int(0..49), Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.animationManager.setPixelProlongedColor(p, c)
                ledStrip.colorManager.pixelColors[p].prolongedColor shouldBe c
            }

            checkAll(Arb.list(Arb.int(0..49)),
                     Arb.list(Arb.int(0..0xFFFFFF))) { p, c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                ledStrip.animationManager.setPixelProlongedColors(p, pCC)
                for (pixel in p) ledStrip.colorManager.pixelColors[pixel].prolongedColor shouldBe pCC[pixel]
            }

            checkAll(Arb.list(Arb.int(0..49)),
                     Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.animationManager.setPixelProlongedColors(p, c)
                for (pixel in p) ledStrip.colorManager.pixelColors[pixel].prolongedColor shouldBe c
            }

            checkAll(Arb.list(Arb.int(0..49), 2..2),
                     Arb.list(Arb.int(0..0xFFFFFF))) { p, c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                ledStrip.animationManager.setPixelProlongedColors(IntRange(p[0], p[1]), pCC)
                for (pixel in IntRange(p[0], p[1]))
                    ledStrip.colorManager.pixelColors[pixel].prolongedColor shouldBe pCC[pixel]
            }

            checkAll(Arb.list(Arb.int(0..49), 2..2).filter { it[0] <= it[1] },
                     Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.animationManager.setPixelProlongedColors(IntRange(p[0], p[1]), c)
                for (pixel in IntRange(p[0], p[1]))
                    ledStrip.colorManager.pixelColors[pixel].prolongedColor shouldBe c
            }
        }

        "set pixel temporary color" {
            checkAll(Arb.int(0..49), Arb.list(Arb.int(0..0xFFFFFF))) { p, c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                ledStrip.setPixelTemporaryColor(p, pCC)
                ledStrip.colorManager.pixelColors[p].temporaryColor shouldBe pCC[p]
            }

            checkAll(Arb.int(0..49), Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.setPixelTemporaryColor(p, c)
                ledStrip.colorManager.pixelColors[p].temporaryColor shouldBe c
            }

            checkAll(Arb.list(Arb.int(0..49)),
                     Arb.list(Arb.int(0..0xFFFFFF))) { p, c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                ledStrip.setPixelTemporaryColors(p, pCC)
                for (pixel in p)
                    ledStrip.colorManager.pixelColors[pixel].temporaryColor shouldBe pCC[pixel]
            }

            checkAll(Arb.list(Arb.int(0..49)),
                     Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.setPixelTemporaryColors(p, c)
                for (pixel in p)
                    ledStrip.colorManager.pixelColors[pixel].temporaryColor shouldBe c
            }

            checkAll(Arb.list(Arb.int(0..49), 2..2),
                     Arb.list(Arb.int(0..0xFFFFFF))) { p, c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                ledStrip.setPixelTemporaryColors(IntRange(p[0], p[1]), pCC)
                for (pixel in IntRange(p[0], p[1]))
                    ledStrip.colorManager.pixelColors[pixel].temporaryColor shouldBe pCC[pixel]
            }

            checkAll(Arb.list(Arb.int(0..49), 2..2).filter { it[0] <= it[1] },
                     Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.setPixelTemporaryColors(IntRange(p[0], p[1]), c)
                for (pixel in IntRange(p[0], p[1]))
                    ledStrip.colorManager.pixelColors[pixel].temporaryColor shouldBe c
            }


            checkAll(Arb.int(0..49), Arb.list(Arb.int(0..0xFFFFFF))) { p, c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                ledStrip.sectionManager.setPixelTemporaryColor(p, pCC)
                ledStrip.colorManager.pixelColors[p].temporaryColor shouldBe pCC[p]
            }

            checkAll(Arb.int(0..49), Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.sectionManager.setPixelTemporaryColor(p, c)
                ledStrip.colorManager.pixelColors[p].temporaryColor shouldBe c
            }

            checkAll(Arb.list(Arb.int(0..49)),
                     Arb.list(Arb.int(0..0xFFFFFF))) { p, c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                ledStrip.sectionManager.setPixelTemporaryColors(p, pCC)
                for (pixel in p) ledStrip.colorManager.pixelColors[pixel].temporaryColor shouldBe pCC[pixel]
            }

            checkAll(Arb.list(Arb.int(0..49)),
                     Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.sectionManager.setPixelTemporaryColors(p, c)
                for (pixel in p) ledStrip.colorManager.pixelColors[pixel].temporaryColor shouldBe c
            }

            checkAll(Arb.list(Arb.int(0..49), 2..2),
                     Arb.list(Arb.int(0..0xFFFFFF))) { p, c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                ledStrip.sectionManager.setPixelTemporaryColors(IntRange(p[0], p[1]), pCC)
                for (pixel in IntRange(p[0], p[1]))
                    ledStrip.colorManager.pixelColors[pixel].temporaryColor shouldBe pCC[pixel]
            }

            checkAll(Arb.list(Arb.int(0..49), 2..2).filter { it[0] <= it[1] },
                     Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.sectionManager.setPixelTemporaryColors(IntRange(p[0], p[1]), c)
                for (pixel in IntRange(p[0], p[1]))
                    ledStrip.colorManager.pixelColors[pixel].temporaryColor shouldBe c
            }


            val testSection = ledStrip.sectionManager.createSection("testt", 5, 39)

            checkAll(Arb.int(0..34), Arb.list(Arb.int(0..0xFFFFFF))) { p, c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(35)
                testSection.setPixelTemporaryColor(p, pCC)
                ledStrip.colorManager.pixelColors[p + 5].temporaryColor shouldBe pCC[p]
            }

            checkAll(Arb.int(0..34), Arb.int(0..0xFFFFFF)) { p, c ->
                testSection.setPixelTemporaryColor(p, c)
                ledStrip.colorManager.pixelColors[p + 5].temporaryColor shouldBe c
            }

            checkAll(Arb.list(Arb.int(0..34)),
                     Arb.list(Arb.int(0..0xFFFFFF)).filter { it.isNotEmpty() }) { p, c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(35)
                testSection.setPixelTemporaryColors(p, pCC)
                for (pixel in p)
                    ledStrip.colorManager.pixelColors[pixel + 5].temporaryColor shouldBe pCC[pixel]
            }

            checkAll(Arb.list(Arb.int(0..34)),
                     Arb.int(0..0xFFFFFF)) { p, c ->
                testSection.setPixelTemporaryColors(p, c)
                for (pixel in p)
                    ledStrip.colorManager.pixelColors[pixel + 5].temporaryColor shouldBe c
            }

            checkAll(Arb.list(Arb.int(0..34), 2..2),
                     Arb.list(Arb.int(0..0xFFFFFF))) { p, c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(35)
                testSection.setPixelTemporaryColors(IntRange(p[0], p[1]), pCC)
                for (pixel in IntRange(p[0], p[1]))
                    ledStrip.colorManager.pixelColors[pixel + 5].temporaryColor shouldBe pCC[pixel]
            }

            checkAll(Arb.list(Arb.int(0..34), 2..2).filter { it[0] <= it[1] },
                     Arb.int(0..0xFFFFFF)) { p, c ->
                testSection.setPixelTemporaryColors(IntRange(p[0], p[1]), c)
                for (pixel in IntRange(p[0], p[1]))
                    ledStrip.colorManager.pixelColors[pixel + 5].temporaryColor shouldBe c
            }


            checkAll(Arb.int(0..49), Arb.list(Arb.int(0..0xFFFFFF))) { p, c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                ledStrip.animationManager.setPixelTemporaryColor(p, pCC)
                ledStrip.colorManager.pixelColors[p].temporaryColor shouldBe pCC[p]
            }

            checkAll(Arb.int(0..49), Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.animationManager.setPixelTemporaryColor(p, c)
                ledStrip.colorManager.pixelColors[p].temporaryColor shouldBe c
            }

            checkAll(Arb.list(Arb.int(0..49)),
                     Arb.list(Arb.int(0..0xFFFFFF))) { p, c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                ledStrip.animationManager.setPixelTemporaryColors(p, pCC)
                for (pixel in p) ledStrip.colorManager.pixelColors[pixel].temporaryColor shouldBe pCC[pixel]
            }

            checkAll(Arb.list(Arb.int(0..49)),
                     Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.animationManager.setPixelTemporaryColors(p, c)
                for (pixel in p) ledStrip.colorManager.pixelColors[pixel].temporaryColor shouldBe c
            }

            checkAll(Arb.list(Arb.int(0..49), 2..2),
                     Arb.list(Arb.int(0..0xFFFFFF))) { p, c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                ledStrip.animationManager.setPixelTemporaryColors(IntRange(p[0], p[1]), pCC)
                for (pixel in IntRange(p[0], p[1]))
                    ledStrip.colorManager.pixelColors[pixel].temporaryColor shouldBe pCC[pixel]
            }

            checkAll(Arb.list(Arb.int(0..49), 2..2).filter { it[0] <= it[1] },
                     Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.animationManager.setPixelTemporaryColors(IntRange(p[0], p[1]), c)
                for (pixel in IntRange(p[0], p[1]))
                    ledStrip.colorManager.pixelColors[pixel].temporaryColor shouldBe c
            }
        }

        "revert pixel" {
            checkAll(Arb.int(0..49), Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.setPixelTemporaryColor(p, c)
                ledStrip.colorManager.pixelColors[p].temporaryColor shouldBe c
                ledStrip.revertPixel(p)
                ledStrip.colorManager.pixelColors[p].temporaryColor shouldBe -1
            }

            checkAll(Arb.list(Arb.int(0..49)), Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.setPixelTemporaryColors(p, c)
                for (pixel in p)
                    ledStrip.colorManager.pixelColors[pixel].temporaryColor shouldBe c
                ledStrip.revertPixels(p)
                for (pixel in p)
                    ledStrip.colorManager.pixelColors[pixel].temporaryColor shouldBe -1
            }

            checkAll(Arb.list(Arb.int(0..49), 2..2).filter { it[0] <= it[1] },
                     Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.setPixelTemporaryColors(IntRange(p[0], p[1]), c)
                for (pixel in IntRange(p[0], p[1]))
                    ledStrip.colorManager.pixelColors[pixel].temporaryColor shouldBe c
                ledStrip.revertPixels(IntRange(p[0], p[1]))
                for (pixel in IntRange(p[0], p[1]))
                    ledStrip.colorManager.pixelColors[pixel].temporaryColor shouldBe -1
            }


            checkAll(Arb.int(0..49), Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.sectionManager.setPixelTemporaryColor(p, c)
                ledStrip.colorManager.pixelColors[p].temporaryColor shouldBe c
                ledStrip.sectionManager.revertPixel(p)
                ledStrip.colorManager.pixelColors[p].temporaryColor shouldBe -1
            }

            checkAll(Arb.list(Arb.int(0..49)), Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.sectionManager.setPixelTemporaryColors(p, c)
                for (pixel in p)
                    ledStrip.colorManager.pixelColors[pixel].temporaryColor shouldBe c
                ledStrip.sectionManager.revertPixels(p)
                for (pixel in p)
                    ledStrip.colorManager.pixelColors[pixel].temporaryColor shouldBe -1
            }

            checkAll(Arb.list(Arb.int(0..49), 2..2).filter { it[0] <= it[1] },
                     Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.sectionManager.setPixelTemporaryColors(IntRange(p[0], p[1]), c)
                for (pixel in IntRange(p[0], p[1]))
                    ledStrip.colorManager.pixelColors[pixel].temporaryColor shouldBe c
                ledStrip.sectionManager.revertPixels(IntRange(p[0], p[1]))
                for (pixel in IntRange(p[0], p[1]))
                    ledStrip.colorManager.pixelColors[pixel].temporaryColor shouldBe -1
            }


            val testSection = ledStrip.sectionManager.createSection("testr", 5, 39)

            checkAll(Arb.int(0..34), Arb.int(0..0xFFFFFF)) { p, c ->
                testSection.setPixelTemporaryColor(p, c)
                ledStrip.colorManager.pixelColors[p + 5].temporaryColor shouldBe c
                testSection.revertPixel(p)
                ledStrip.colorManager.pixelColors[p + 5].temporaryColor shouldBe -1
            }

            checkAll(Arb.list(Arb.int(0..34)),
                     Arb.int(0..0xFFFFFF)) { p, c ->
                testSection.setPixelTemporaryColors(p, c)
                for (pixel in p)
                    ledStrip.colorManager.pixelColors[pixel + 5].temporaryColor shouldBe c
                testSection.revertPixels(p)
                for (pixel in p)
                    ledStrip.colorManager.pixelColors[pixel + 5].temporaryColor shouldBe -1
            }

            checkAll(Arb.list(Arb.int(0..34), 2..2).filter { it[0] <= it[1] },
                     Arb.int(0..0xFFFFFF)) { p, c ->
                testSection.setPixelTemporaryColors(IntRange(p[0], p[1]), c)
                for (pixel in IntRange(p[0], p[1]))
                    ledStrip.colorManager.pixelColors[pixel + 5].temporaryColor shouldBe c
                testSection.revertPixels(IntRange(p[0], p[1]))
                for (pixel in IntRange(p[0], p[1]))
                    ledStrip.colorManager.pixelColors[pixel + 5].temporaryColor shouldBe -1
            }


            checkAll(Arb.int(0..49), Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.animationManager.setPixelTemporaryColor(p, c)
                ledStrip.colorManager.pixelColors[p].temporaryColor shouldBe c
                ledStrip.animationManager.revertPixel(p)
                ledStrip.colorManager.pixelColors[p].temporaryColor shouldBe -1
            }

            checkAll(Arb.list(Arb.int(0..49)),
                     Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.animationManager.setPixelTemporaryColors(p, c)
                for (pixel in p) ledStrip.colorManager.pixelColors[pixel].temporaryColor shouldBe c
                ledStrip.animationManager.revertPixels(p)
                for (pixel in p) ledStrip.colorManager.pixelColors[pixel].temporaryColor shouldBe -1
            }

            checkAll(Arb.list(Arb.int(0..49), 2..2).filter { it[0] <= it[1] },
                     Arb.int(0..0xFFFFFF)) { p, c ->
                ledStrip.animationManager.setPixelTemporaryColors(IntRange(p[0], p[1]), c)
                for (pixel in IntRange(p[0], p[1]))
                    ledStrip.colorManager.pixelColors[pixel].temporaryColor shouldBe c
                ledStrip.animationManager.revertPixels(IntRange(p[0], p[1]))
                for (pixel in IntRange(p[0], p[1]))
                    ledStrip.colorManager.pixelColors[pixel].temporaryColor shouldBe -1
            }
        }

        "set strip fade color" {
            checkAll(Arb.list(Arb.int(0..0xFFFFFF))) { c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                ledStrip.setStripFadeColor(pCC)
                for (pixel in ledStrip.validIndices)
                    ledStrip.colorManager.pixelColors[pixel].fadeColor shouldBe pCC[pixel]
            }

            checkAll(Arb.int(0..0xFFFFFF)) { c ->
                ledStrip.setStripFadeColor(c)
                for (pixel in ledStrip.validIndices)
                    ledStrip.colorManager.pixelColors[pixel].fadeColor shouldBe c
            }


            checkAll(Arb.list(Arb.int(0..0xFFFFFF))) { c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                ledStrip.sectionManager.setStripFadeColor(pCC)
                for (pixel in ledStrip.validIndices)
                    ledStrip.colorManager.pixelColors[pixel].fadeColor shouldBe pCC[pixel]
            }

            checkAll(Arb.int(0..0xFFFFFF)) { c ->
                ledStrip.sectionManager.setStripFadeColor(c)
                for (pixel in ledStrip.validIndices)
                    ledStrip.colorManager.pixelColors[pixel].fadeColor shouldBe c
            }


            val testSection = ledStrip.sectionManager.createSection("testsf", 5, 39)

            checkAll(Arb.list(Arb.int(0..0xFFFFFF))) { c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                testSection.setStripFadeColor(pCC)
                for (pixel in testSection.validIndices)
                    ledStrip.colorManager.pixelColors[pixel + 5].fadeColor shouldBe pCC[pixel]
            }

            checkAll(Arb.int(0..0xFFFFFF)) { c ->
                testSection.setStripFadeColor(c)
                for (pixel in testSection.validIndices)
                    ledStrip.colorManager.pixelColors[pixel + 5].fadeColor shouldBe c
            }


            checkAll(Arb.list(Arb.int(0..0xFFFFFF))) { c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                ledStrip.animationManager.setStripFadeColor(pCC)
                for (pixel in ledStrip.validIndices)
                    ledStrip.colorManager.pixelColors[pixel].fadeColor shouldBe pCC[pixel]
            }

            checkAll(Arb.int(0..0xFFFFFF)) { c ->
                ledStrip.animationManager.setStripFadeColor(c)
                for (pixel in ledStrip.validIndices)
                    ledStrip.colorManager.pixelColors[pixel].fadeColor shouldBe c
            }
        }

        "set strip prolonged color" {
            checkAll(Arb.list(Arb.int(0..0xFFFFFF))) { c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                ledStrip.setStripProlongedColor(pCC)
                for (pixel in ledStrip.validIndices)
                    ledStrip.colorManager.pixelColors[pixel].prolongedColor shouldBe pCC[pixel]
            }

            checkAll(Arb.int(0..0xFFFFFF)) { c ->
                ledStrip.setStripProlongedColor(c)
                for (pixel in ledStrip.validIndices)
                    ledStrip.colorManager.pixelColors[pixel].prolongedColor shouldBe c
            }


            checkAll(Arb.list(Arb.int(0..0xFFFFFF))) { c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                ledStrip.sectionManager.setStripProlongedColor(pCC)
                for (pixel in ledStrip.validIndices)
                    ledStrip.colorManager.pixelColors[pixel].prolongedColor shouldBe pCC[pixel]
            }

            checkAll(Arb.int(0..0xFFFFFF)) { c ->
                ledStrip.sectionManager.setStripProlongedColor(c)
                for (pixel in ledStrip.validIndices)
                    ledStrip.colorManager.pixelColors[pixel].prolongedColor shouldBe c
            }


            val testSection = ledStrip.sectionManager.createSection("testsp", 5, 39)

            checkAll(Arb.list(Arb.int(0..0xFFFFFF))) { c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                testSection.setStripProlongedColor(pCC)
                for (pixel in testSection.validIndices)
                    ledStrip.colorManager.pixelColors[pixel + 5].prolongedColor shouldBe pCC[pixel]
            }

            checkAll(Arb.int(0..0xFFFFFF)) { c ->
                testSection.setStripProlongedColor(c)
                for (pixel in testSection.validIndices)
                    ledStrip.colorManager.pixelColors[pixel + 5].prolongedColor shouldBe c
            }


            checkAll(Arb.list(Arb.int(0..0xFFFFFF))) { c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                ledStrip.animationManager.setStripProlongedColor(pCC)
                for (pixel in ledStrip.validIndices)
                    ledStrip.colorManager.pixelColors[pixel].prolongedColor shouldBe pCC[pixel]
            }

            checkAll(Arb.int(0..0xFFFFFF)) { c ->
                ledStrip.animationManager.setStripProlongedColor(c)
                for (pixel in ledStrip.validIndices)
                    ledStrip.colorManager.pixelColors[pixel].prolongedColor shouldBe c
            }
        }

        "set strip temporary color" {
            checkAll(Arb.list(Arb.int(0..0xFFFFFF))) { c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                ledStrip.setStripTemporaryColor(pCC)
                for (pixel in ledStrip.validIndices)
                    ledStrip.colorManager.pixelColors[pixel].temporaryColor shouldBe pCC[pixel]
            }

            checkAll(Arb.int(0..0xFFFFFF)) { c ->
                ledStrip.setStripTemporaryColor(c)
                for (pixel in ledStrip.validIndices)
                    ledStrip.colorManager.pixelColors[pixel].temporaryColor shouldBe c
            }


            checkAll(Arb.list(Arb.int(0..0xFFFFFF))) { c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                ledStrip.sectionManager.setStripTemporaryColor(pCC)
                for (pixel in ledStrip.validIndices)
                    ledStrip.colorManager.pixelColors[pixel].temporaryColor shouldBe pCC[pixel]
            }

            checkAll(Arb.int(0..0xFFFFFF)) { c ->
                ledStrip.sectionManager.setStripTemporaryColor(c)
                for (pixel in ledStrip.validIndices)
                    ledStrip.colorManager.pixelColors[pixel].temporaryColor shouldBe c
            }


            val testSection = ledStrip.sectionManager.createSection("testst", 5, 39)

            checkAll(Arb.list(Arb.int(0..0xFFFFFF))) { c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                testSection.setStripTemporaryColor(pCC)
                for (pixel in testSection.validIndices)
                    ledStrip.colorManager.pixelColors[pixel + 5].temporaryColor shouldBe pCC[pixel]
            }

            checkAll(Arb.int(0..0xFFFFFF)) { c ->
                testSection.setStripTemporaryColor(c)
                for (pixel in testSection.validIndices)
                    ledStrip.colorManager.pixelColors[pixel + 5].temporaryColor shouldBe c
            }


            checkAll(Arb.list(Arb.int(0..0xFFFFFF))) { c ->
                val pCC = ColorContainer(c.toMutableList()).prepare(50)
                ledStrip.animationManager.setStripTemporaryColor(pCC)
                for (pixel in ledStrip.validIndices)
                    ledStrip.colorManager.pixelColors[pixel].temporaryColor shouldBe pCC[pixel]
            }

            checkAll(Arb.int(0..0xFFFFFF)) { c ->
                ledStrip.animationManager.setStripTemporaryColor(c)
                for (pixel in ledStrip.validIndices)
                    ledStrip.colorManager.pixelColors[pixel].temporaryColor shouldBe c
            }
        }

        "clear" {
            checkAll(Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF)) { c1, c2, c3 ->
                ledStrip.setStripFadeColor(c1)
                ledStrip.setStripProlongedColor(c2)
                ledStrip.setStripTemporaryColor(c3)
                for (pixel in ledStrip.validIndices) {
                    ledStrip.colorManager.pixelColors[pixel].fadeColor shouldBe c1
                    ledStrip.colorManager.pixelColors[pixel].prolongedColor shouldBe c2
                    ledStrip.colorManager.pixelColors[pixel].temporaryColor shouldBe c3
                }

                ledStrip.clear()
                for (pixel in ledStrip.validIndices) {
                    ledStrip.colorManager.pixelColors[pixel].fadeColor shouldBe -1
                    ledStrip.colorManager.pixelColors[pixel].prolongedColor shouldBe 0
                    ledStrip.colorManager.pixelColors[pixel].temporaryColor shouldBe -1
                }
            }

            checkAll(Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF)) { c1, c2, c3 ->
                ledStrip.sectionManager.setStripFadeColor(c1)
                ledStrip.sectionManager.setStripProlongedColor(c2)
                ledStrip.sectionManager.setStripTemporaryColor(c3)
                for (pixel in ledStrip.validIndices) {
                    ledStrip.colorManager.pixelColors[pixel].fadeColor shouldBe c1
                    ledStrip.colorManager.pixelColors[pixel].prolongedColor shouldBe c2
                    ledStrip.colorManager.pixelColors[pixel].temporaryColor shouldBe c3
                }

                ledStrip.sectionManager.clear()
                for (pixel in ledStrip.validIndices) {
                    ledStrip.colorManager.pixelColors[pixel].fadeColor shouldBe -1
                    ledStrip.colorManager.pixelColors[pixel].prolongedColor shouldBe 0
                    ledStrip.colorManager.pixelColors[pixel].temporaryColor shouldBe -1
                }
            }

            val testSection = ledStrip.sectionManager.createSection("testc", 5, 39)

            checkAll(Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF)) { c1, c2, c3 ->
                testSection.setStripFadeColor(c1)
                testSection.setStripProlongedColor(c2)
                testSection.setStripTemporaryColor(c3)
                for (pixel in testSection.validIndices) {
                    ledStrip.colorManager.pixelColors[pixel + 5].fadeColor shouldBe c1
                    ledStrip.colorManager.pixelColors[pixel + 5].prolongedColor shouldBe c2
                    ledStrip.colorManager.pixelColors[pixel + 5].temporaryColor shouldBe c3
                }

                testSection.clear()
                for (pixel in testSection.validIndices) {
                    ledStrip.colorManager.pixelColors[pixel + 5].fadeColor shouldBe -1
                    ledStrip.colorManager.pixelColors[pixel + 5].prolongedColor shouldBe 0
                    ledStrip.colorManager.pixelColors[pixel + 5].temporaryColor shouldBe -1
                }
            }

            checkAll(Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF),
                     Arb.int(0..0xFFFFFF)) { c1, c2, c3 ->
                ledStrip.animationManager.setStripFadeColor(c1)
                ledStrip.animationManager.setStripProlongedColor(c2)
                ledStrip.animationManager.setStripTemporaryColor(c3)
                for (pixel in ledStrip.validIndices) {
                    ledStrip.colorManager.pixelColors[pixel].fadeColor shouldBe c1
                    ledStrip.colorManager.pixelColors[pixel].prolongedColor shouldBe c2
                    ledStrip.colorManager.pixelColors[pixel].temporaryColor shouldBe c3
                }

                ledStrip.animationManager.clear()
                for (pixel in ledStrip.validIndices) {
                    ledStrip.colorManager.pixelColors[pixel].fadeColor shouldBe -1
                    ledStrip.colorManager.pixelColors[pixel].prolongedColor shouldBe 0
                    ledStrip.colorManager.pixelColors[pixel].temporaryColor shouldBe -1
                }
            }
        }
    }
)
