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

package animatedledstrip.test.animationutils

import animatedledstrip.animations.Animation
import animatedledstrip.animations.ParamUsage
import animatedledstrip.animations.predefined.*
import animatedledstrip.utils.decodeJson
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import kotlin.test.assertTrue

class AnimationInfoTest : StringSpec(
    {
        "constructor" {
            val info = Animation.AnimationInfo(
                name = "Test",
                abbr = "TST",
                description = "A test animation",
                signatureFile = "sig.png",
                runCountDefault = 1,
                minimumColors = 4,
                unlimitedColors = true,
                center = ParamUsage.USED,
                delay = ParamUsage.NOTUSED,
                direction = ParamUsage.NOTUSED,
                distance = ParamUsage.USED,
                spacing = ParamUsage.USED,
                delayDefault = 5,
                distanceDefault = 40,
                spacingDefault = 4,
            )

            info.name shouldBe "Test"
            info.abbr shouldBe "TST"
            info.description shouldBe "A test animation"
            info.signatureFile shouldBe "sig.png"
            info.minimumColors shouldBe 4
            info.unlimitedColors.shouldBeTrue()
            info.center shouldBe ParamUsage.USED
            info.delay shouldBe ParamUsage.NOTUSED
            info.direction shouldBe ParamUsage.NOTUSED
            info.distance shouldBe ParamUsage.USED
            info.spacing shouldBe ParamUsage.USED
            info.delayDefault shouldBe 5L
            info.distanceDefault shouldBe 40
            info.spacingDefault shouldBe 4
        }

        "presets to human readable strings" {
            alternate.info.toHumanReadableString() shouldBe
                """
                    Animation Info
                      name: Alternate
                      abbr: ALT
                      runCountDefault: -1
                      minimum colors: 2
                      unlimited colors: true
                      center: NOTUSED
                      delay: USED (1000)
                      direction: NOTUSED
                      distance: NOTUSED
                      spacing: NOTUSED
                    End Info
                """.trimIndent()
            assertTrue {
                bounce.info.toHumanReadableString() ==
                        """
                        Animation Info
                          name: Bounce
                          abbr: BNC
                          runCountDefault: -1
                          minimum colors: 1
                          unlimited colors: false
                          center: NOTUSED
                          delay: USED (10)
                          direction: NOTUSED
                          distance: NOTUSED
                          spacing: NOTUSED
                        End Info
                    """.trimIndent()
            }
            assertTrue {
                bounceToColor.info.toHumanReadableString() ==
                        """
                        Animation Info
                          name: Bounce to Color
                          abbr: BTC
                          runCountDefault: 1
                          minimum colors: 1
                          unlimited colors: false
                          center: NOTUSED
                          delay: USED (5)
                          direction: NOTUSED
                          distance: NOTUSED
                          spacing: NOTUSED
                        End Info
                    """.trimIndent()
            }
            assertTrue {
                bubbleSort.info.toHumanReadableString() ==
                        """
                        Animation Info
                          name: Bubble Sort
                          abbr: BST
                          runCountDefault: 1
                          minimum colors: 1
                          unlimited colors: false
                          center: NOTUSED
                          delay: USED (5)
                          direction: NOTUSED
                          distance: NOTUSED
                          spacing: NOTUSED
                        End Info
                    """.trimIndent()
            }
            assertTrue {
                catToy.info.toHumanReadableString() ==
                        """
                        Animation Info
                          name: Cat Toy
                          abbr: CAT
                          runCountDefault: -1
                          minimum colors: 1
                          unlimited colors: false
                          center: NOTUSED
                          delay: USED (5)
                          direction: NOTUSED
                          distance: NOTUSED
                          spacing: NOTUSED
                        End Info
                    """.trimIndent()
            }
            assertTrue {
                catToyToColor.info.toHumanReadableString() ==
                        """
                        Animation Info
                          name: Cat Toy to Color
                          abbr: CTC
                          runCountDefault: 1
                          minimum colors: 1
                          unlimited colors: false
                          center: NOTUSED
                          delay: USED (5)
                          direction: NOTUSED
                          distance: NOTUSED
                          spacing: NOTUSED
                        End Info
                    """.trimIndent()
            }
            assertTrue {
                color.info.toHumanReadableString() ==
                        """
                        Animation Info
                          name: Color
                          abbr: COL
                          runCountDefault: 1
                          minimum colors: 1
                          unlimited colors: false
                          center: NOTUSED
                          delay: NOTUSED
                          direction: NOTUSED
                          distance: NOTUSED
                          spacing: NOTUSED
                        End Info
                    """.trimIndent()
            }
            assertTrue {
                fadeToColor.info.toHumanReadableString() ==
                        """
                        Animation Info
                          name: Fade to Color
                          abbr: FTC
                          runCountDefault: 1
                          minimum colors: 1
                          unlimited colors: false
                          center: NOTUSED
                          delay: USED (30)
                          direction: NOTUSED
                          distance: NOTUSED
                          spacing: NOTUSED
                        End Info
                    """.trimIndent()
            }
            assertTrue {
                fireworks.info.toHumanReadableString() ==
                        """
                        Animation Info
                          name: Fireworks
                          abbr: FWK
                          runCountDefault: -1
                          minimum colors: 1
                          unlimited colors: true
                          center: NOTUSED
                          delay: USED (30)
                          direction: NOTUSED
                          distance: USED (20)
                          spacing: NOTUSED
                        End Info
                    """.trimIndent()
            }
            assertTrue {
                mergeSortParallel.info.toHumanReadableString() ==
                        """
                        Animation Info
                          name: Merge Sort (Parallel)
                          abbr: MSP
                          runCountDefault: 1
                          minimum colors: 1
                          unlimited colors: false
                          center: NOTUSED
                          delay: USED (50)
                          direction: NOTUSED
                          distance: NOTUSED
                          spacing: NOTUSED
                        End Info
                    """.trimIndent()
            }
            assertTrue {
                mergeSortSequential.info.toHumanReadableString() ==
                        """
                        Animation Info
                          name: Merge Sort (Sequential)
                          abbr: MSS
                          runCountDefault: 1
                          minimum colors: 1
                          unlimited colors: false
                          center: NOTUSED
                          delay: USED (25)
                          direction: NOTUSED
                          distance: NOTUSED
                          spacing: NOTUSED
                        End Info
                    """.trimIndent()
            }
            assertTrue {
                meteor.info.toHumanReadableString() ==
                        """
                        Animation Info
                          name: Meteor
                          abbr: MET
                          runCountDefault: -1
                          minimum colors: 1
                          unlimited colors: false
                          center: NOTUSED
                          delay: USED (10)
                          direction: USED
                          distance: NOTUSED
                          spacing: NOTUSED
                        End Info
                    """.trimIndent()
            }
            assertTrue {
                multiPixelRun.info.toHumanReadableString() ==
                        """
                        Animation Info
                          name: Multi Pixel Run
                          abbr: MPR
                          runCountDefault: -1
                          minimum colors: 1
                          unlimited colors: false
                          center: NOTUSED
                          delay: USED (100)
                          direction: USED
                          distance: NOTUSED
                          spacing: USED (3)
                        End Info
                    """.trimIndent()
            }
            assertTrue {
                multiPixelRunToColor.info.toHumanReadableString() ==
                        """
                        Animation Info
                          name: Multi Pixel Run to Color
                          abbr: MTC
                          runCountDefault: 1
                          minimum colors: 1
                          unlimited colors: false
                          center: NOTUSED
                          delay: USED (150)
                          direction: USED
                          distance: NOTUSED
                          spacing: USED (3)
                        End Info
                    """.trimIndent()
            }
            assertTrue {
                pixelMarathon.info.toHumanReadableString() ==
                        """
                        Animation Info
                          name: Pixel Marathon
                          abbr: PXM
                          runCountDefault: -1
                          minimum colors: 1
                          unlimited colors: true
                          center: NOTUSED
                          delay: USED (8)
                          direction: USED
                          distance: NOTUSED
                          spacing: NOTUSED
                        End Info
                    """.trimIndent()
            }
            assertTrue {
                pixelRun.info.toHumanReadableString() ==
                        """
                        Animation Info
                          name: Pixel Run
                          abbr: PXR
                          runCountDefault: -1
                          minimum colors: 1
                          unlimited colors: false
                          center: NOTUSED
                          delay: USED (10)
                          direction: USED
                          distance: NOTUSED
                          spacing: NOTUSED
                        End Info
                    """.trimIndent()
            }
            assertTrue {
                ripple.info.toHumanReadableString() ==
                        """
                        Animation Info
                          name: Ripple
                          abbr: RPL
                          runCountDefault: -1
                          minimum colors: 1
                          unlimited colors: false
                          center: USED
                          delay: USED (30)
                          direction: NOTUSED
                          distance: USED (whole strip)
                          spacing: NOTUSED
                        End Info
                    """.trimIndent()
            }
            assertTrue {
                smoothChase.info.toHumanReadableString() ==
                        """
                        Animation Info
                          name: Smooth Chase
                          abbr: SCH
                          runCountDefault: -1
                          minimum colors: 1
                          unlimited colors: false
                          center: NOTUSED
                          delay: USED (50)
                          direction: USED
                          distance: NOTUSED
                          spacing: NOTUSED
                        End Info
                    """.trimIndent()
            }
            assertTrue {
                smoothFade.info.toHumanReadableString() ==
                        """
                        Animation Info
                          name: Smooth Fade
                          abbr: SMF
                          runCountDefault: -1
                          minimum colors: 1
                          unlimited colors: false
                          center: NOTUSED
                          delay: USED (50)
                          direction: NOTUSED
                          distance: NOTUSED
                          spacing: NOTUSED
                        End Info
                    """.trimIndent()
            }
            assertTrue {
                sparkle.info.toHumanReadableString() ==
                        """
                        Animation Info
                          name: Sparkle
                          abbr: SPK
                          runCountDefault: -1
                          minimum colors: 1
                          unlimited colors: false
                          center: NOTUSED
                          delay: USED (50)
                          direction: NOTUSED
                          distance: NOTUSED
                          spacing: NOTUSED
                        End Info
                    """.trimIndent()
            }
            assertTrue {
                sparkleFade.info.toHumanReadableString() ==
                        """
                        Animation Info
                          name: Sparkle Fade
                          abbr: SPF
                          runCountDefault: -1
                          minimum colors: 1
                          unlimited colors: false
                          center: NOTUSED
                          delay: USED (50)
                          direction: NOTUSED
                          distance: NOTUSED
                          spacing: NOTUSED
                        End Info
                    """.trimIndent()
            }
            assertTrue {
                sparkleToColor.info.toHumanReadableString() ==
                        """
                        Animation Info
                          name: Sparkle to Color
                          abbr: STC
                          runCountDefault: 1
                          minimum colors: 1
                          unlimited colors: false
                          center: NOTUSED
                          delay: USED (50)
                          direction: NOTUSED
                          distance: NOTUSED
                          spacing: NOTUSED
                        End Info
                    """.trimIndent()
            }
            assertTrue {
                splat.info.toHumanReadableString() ==
                        """
                        Animation Info
                          name: Splat
                          abbr: SPT
                          runCountDefault: 1
                          minimum colors: 1
                          unlimited colors: false
                          center: USED
                          delay: USED (5)
                          direction: NOTUSED
                          distance: USED (whole strip)
                          spacing: NOTUSED
                        End Info
                    """.trimIndent()
            }
            assertTrue {
                stack.info.toHumanReadableString() ==
                        """
                        Animation Info
                          name: Stack
                          abbr: STK
                          runCountDefault: 1
                          minimum colors: 1
                          unlimited colors: false
                          center: NOTUSED
                          delay: USED (10)
                          direction: USED
                          distance: NOTUSED
                          spacing: NOTUSED
                        End Info
                    """.trimIndent()
            }
            assertTrue {
                stackOverflow.info.toHumanReadableString() ==
                        """
                        Animation Info
                          name: Stack Overflow
                          abbr: STO
                          runCountDefault: -1
                          minimum colors: 2
                          unlimited colors: false
                          center: NOTUSED
                          delay: USED (2)
                          direction: NOTUSED
                          distance: NOTUSED
                          spacing: NOTUSED
                        End Info
                    """.trimIndent()
            }
            assertTrue {
                wipe.info.toHumanReadableString() ==
                        """
                        Animation Info
                          name: Wipe
                          abbr: WIP
                          runCountDefault: 1
                          minimum colors: 1
                          unlimited colors: false
                          center: NOTUSED
                          delay: USED (10)
                          direction: USED
                          distance: NOTUSED
                          spacing: NOTUSED
                        End Info
                    """.trimIndent()
            }

        }

        "decode JSON" {
            val json =
                """{"type":"animatedledstrip.animationutils.Animation.AnimationInfo", "name":"Alternate","abbr":"ALT","description":"A description","signatureFile":"alternate.png","runCountDefault":1,"minimumColors":2,"unlimitedColors":true,"center":"NOTUSED","delay":"USED","direction":"NOTUSED","distance":"NOTUSED","spacing":"NOTUSED","delayDefault":1000,"distanceDefault":20,"spacingDefault":3}"""

            val correctData = Animation.AnimationInfo(name = "Alternate",
                                                      abbr = "ALT",
                                                      description = "A description",
                                                      signatureFile = "alternate.png",
                                                      runCountDefault = 1,
                                                      minimumColors = 2,
                                                      unlimitedColors = true,
                                                      center = ParamUsage.NOTUSED,
                                                      delay = ParamUsage.USED,
                                                      direction = ParamUsage.NOTUSED,
                                                      distance = ParamUsage.NOTUSED,
                                                      spacing = ParamUsage.NOTUSED,
                                                      delayDefault = 1000,
                                                      distanceDefault = 20,
                                                      spacingDefault = 3)

            json.decodeJson() as Animation.AnimationInfo shouldBe correctData
        }
    })
