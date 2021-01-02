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

package animatedledstrip.test.communication

import animatedledstrip.animations.Direction
import animatedledstrip.animations.definedAnimationNames
import animatedledstrip.colors.toColorContainer
import animatedledstrip.leds.animationmanagement.AnimationToRunParams
import animatedledstrip.leds.stripmanagement.LEDLocation
import animatedledstrip.leds.stripmanagement.StripInfo
import io.kotest.core.spec.style.StringSpec
import kotlin.test.assertTrue

class SendableDataTests : StringSpec(
    {
//        "animation data json" {
//            val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip
//
//            val testData1 = AnimationToRunParams().animation("Stack")
//                .color(ColorContainer(0xFF, 0xFFFF).prepare(5), index = 0)
//                .color(0xFF, index = 1)
//                .color(0xFF, index = 2)
//                .color(0xFF, index = 3)
//                .color(0xFF, index = 4)
//                .delay(50)
//                .direction(Direction.FORWARD)
//                .id("TEST")
//                .spacing(5)
//                .prepare(testLEDs)
//
//            val testBytes = testData1.json()
//            val testData2 = testBytes.toUTF8(testBytes.size).jsonToAnimationData()
//            assertTrue { testData1 == testData2 }
//        }

        "animation data to human readable string".config(enabled = false) {
            assertTrue {
                AnimationToRunParams().toHumanReadableString() ==
                        """
                        AnimationData for 
                          animation: Color
                          animatedledstrip.colors: []
                          center: -1
                          delay: 50
                          delayMod: 1.0
                          direction: FORWARD
                          distance: -1
                          runCount: 1
                          section: 
                          spacing: 3
                        End AnimationData
                    """.trimIndent()
            }

            assertTrue {
                AnimationToRunParams(
                    animation = "Bounce",
                    colors = mutableListOf(0xFF.toColorContainer()),
                    center = LEDLocation(30),
                    delay = 10,
                    delayMod = 2.0,
                    direction = Direction.BACKWARD,
                    distance = 50,
                    id = "test",
                    runCount = 2,
                    section = "section",
                    spacing = 4,
                ).toHumanReadableString() ==
                        """
                        AnimationData for test
                          animation: Bounce
                          animatedledstrip.colors: [ff]
                          center: 30
                          delay: 10
                          delayMod: 2.0
                          direction: BACKWARD
                          distance: 50
                          runCount: 2
                          section: section
                          spacing: 4
                        End AnimationData
                    """.trimIndent()
            }
        }

//        "animation info JSON" {
//            val info1 = fireworks.info
//            val infoBytes = info1.json()
//
//            val info2 = infoBytes.toUTF8(infoBytes.size).jsonToAnimationInfo()
//
//            assertTrue { info1 == info2 }
//        }

//        "section to human readable string" {
//            val section1 = EmulatedAnimatedLEDStrip(50).createSection("Test", 5, 10)
//            assertTrue {
//                section1.toHumanReadableString() ==
//                        """
//                        Section Info
//                          name: Test
//                          numLEDs: 6
//                          startPixel: 5
//                          endPixel: 10
//                          physicalStart: 5
//                        End Info
//                    """.trimIndent()
//            }
//        }

//        "strip info JSON" {
//            val info1 = StripInfo(
//                numLEDs = 10,
//                pin = 15,
//                imageDebugging = true,
//                fileName = "test.csv",
//                rendersBeforeSave = 100,
//                threadCount = 200,
//            )
//            val infoBytes = info1.json()
//
//            val info2 = infoBytes.toUTF8(infoBytes.size).jsonToStripInfo()
//
//            assertTrue { info1 == info2 }
//        }

        "strip info to human readable string" {
            val info1 = StripInfo(
                numLEDs = 10,
                pin = 15,
                imageDebugging = true,
                fileName = "test.csv",
                rendersBeforeSave = 100,
            )
            assertTrue {
                info1.toHumanReadableString() ==
                        """
                        Strip Info
                          numLEDs: 10
                          pin: 15
                          imageDebugging: true
                          fileName: test.csv
                          rendersBeforeSave: 100
                          threadCount: 200
                          supportedAnimations: $definedAnimationNames
                        End Strip Info
                    """.trimIndent()
            }
        }
    })
