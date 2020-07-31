/*
 *  Copyright (c) 2020 AnimatedLEDStrip
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

package animatedledstrip.test

import animatedledstrip.animationutils.*
import animatedledstrip.animationutils.predefined.fireworks
import animatedledstrip.colors.ColorContainer
import animatedledstrip.leds.StripInfo
import animatedledstrip.leds.emulated.EmulatedAnimatedLEDStrip
import animatedledstrip.utils.*
import org.junit.Test
import kotlin.test.assertTrue

class SendableDataTests {

    @Test
    fun testAnimationDataJson() {
        val testLEDs = EmulatedAnimatedLEDStrip(50).wholeStrip

        val testData1 = AnimationData().animation("Stack")
            .color(ColorContainer(0xFF, 0xFFFF).prepare(5), index = 0)
            .color(0xFF, index = 1)
            .color(0xFF, index = 2)
            .color(0xFF, index = 3)
            .color(0xFF, index = 4)
            .continuous(true)
            .delay(50)
            .direction(Direction.FORWARD)
            .id("TEST")
            .spacing(5)
            .prepare(testLEDs)

        val testBytes = testData1.json()
        val testData2 = testBytes.toUTF8(testBytes.size).jsonToAnimationData()
        assertTrue { testData1 == testData2 }
    }

    @Test
    fun testAnimationDataToHumanReadableString() {
        assertTrue {
            AnimationData().toHumanReadableString() ==
                    """
                        AnimationData for 
                          animation: Color
                          colors: []
                          center: -1
                          continuous: null
                          delay: 50
                          delayMod: 1.0
                          direction: FORWARD
                          distance: -1
                          section: 
                          spacing: 3
                        End AnimationData
                    """.trimIndent()
        }

        assertTrue {
            AnimationData(
                animation = "Bounce",
                colors = listOf(0xFF.toColorContainer()),
                center = 30,
                continuous = false,
                delay = 10,
                delayMod = 2.0,
                direction = Direction.BACKWARD,
                distance = 50,
                id = "test",
                section = "section",
                spacing = 4
            ).toHumanReadableString() ==
                    """
                        AnimationData for test
                          animation: Bounce
                          colors: [ff]
                          center: 30
                          continuous: false
                          delay: 10
                          delayMod: 2.0
                          direction: BACKWARD
                          distance: 50
                          section: section
                          spacing: 4
                        End AnimationData
                    """.trimIndent()
        }
    }

    @Test
    fun testAnimationInfoJson() {
        val info1 = fireworks.info
        val infoBytes = info1.json()

        val info2 = infoBytes.toUTF8(infoBytes.size).jsonToAnimationInfo()

        assertTrue { info1 == info2 }
    }


    @Test
    fun testEndAnimationJson() {
        val end1 = EndAnimation(id = "test")
        val endBytes = end1.json()

        val end2 = endBytes.toUTF8(endBytes.size).jsonToEndAnimation()

        assertTrue { end1 == end2 }
    }

    @Test
    fun testEndAnimationToHumanReadableString() {
        assertTrue { EndAnimation("15235").toHumanReadableString() == "End of animation 15235" }
    }

    @Test
    fun testSectionJson() {
        val section1 = EmulatedAnimatedLEDStrip(50).createSection("Test", 5, 10)
        val sectionBytes = section1.json()

        val section2 = sectionBytes.toUTF8(sectionBytes.size).jsonToSection()

        assertTrue { section1.startPixel == section2.startPixel }
        assertTrue { section1.endPixel == section2.endPixel }
        assertTrue { section1.physicalStart == section2.physicalStart }
        assertTrue { section1.numLEDs == section2.numLEDs }
    }

    @Test
    fun testSectionToHumanReadableString() {
        val section1 = EmulatedAnimatedLEDStrip(50).createSection("Test", 5, 10)
        assertTrue {
            section1.toHumanReadableString() ==
                    """
                        Section Info
                          name: Test
                          numLEDs: 6
                          startPixel: 5
                          endPixel: 10
                          physicalStart: 5
                        End Info
                    """.trimIndent()
        }
    }

    @Test
    fun testStripInfoJson() {
        val info1 = StripInfo(
            numLEDs = 10,
            pin = 15,
            imageDebugging = true,
            fileName = "test.csv",
            rendersBeforeSave = 100,
            threadCount = 200
        )
        val infoBytes = info1.json()

        val info2 = infoBytes.toUTF8(infoBytes.size).jsonToStripInfo()

        assertTrue { info1 == info2 }
    }

    @Test
    fun testStripInfoToHumanReadableString() {
        val info1 = StripInfo(
            numLEDs = 10,
            pin = 15,
            imageDebugging = true,
            fileName = "test.csv",
            rendersBeforeSave = 100,
            threadCount = 200
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
}