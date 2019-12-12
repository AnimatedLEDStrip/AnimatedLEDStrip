/*
 *  Copyright (c) 2019 AnimatedLEDStrip
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

import animatedledstrip.animationutils.Animation
import animatedledstrip.animationutils.AnimationData
import animatedledstrip.animationutils.animation
import animatedledstrip.animationutils.color
import animatedledstrip.colors.ColorContainer
import animatedledstrip.colors.ccpresets.CCBlack
import animatedledstrip.colors.ccpresets.CCBlue
import animatedledstrip.leds.*
import animatedledstrip.leds.emulated.EmulatedAnimatedLEDStrip
import animatedledstrip.utils.delayBlocking
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LEDStripTest {

    @Test
    fun testSetPixelColor() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        checkAllPixels(testLEDs, 0)
        checkAllProlongedPixels(testLEDs, 0)

        // prolonged default

        // setPixelColor with ColorContainer
        testLEDs.setPixelColor(10, ColorContainer(0xFF))

        assertTrue { testLEDs.getPixelColor(10, prolonged = false) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(10, prolonged = true) == 0L }

        // setPixelColor with Long
        testLEDs.setPixelColor(10, 0xFF0000)

        assertTrue { testLEDs.getPixelColor(10, prolonged = false) == 0xFF0000L }
        assertTrue { testLEDs.getPixelColor(10, prolonged = true) == 0L }


        // prolonged = false

        // setPixelColor with ColorContainer
        testLEDs.setPixelColor(10, ColorContainer(0xFF), prolonged = false)

        assertTrue { testLEDs.getPixelColor(10, prolonged = false) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(10, prolonged = true) == 0L }

        // setPixelColor with Long
        testLEDs.setPixelColor(10, 0xFF0000, prolonged = false)

        assertTrue { testLEDs.getPixelColor(10, prolonged = false) == 0xFF0000L }
        assertTrue { testLEDs.getPixelColor(10, prolonged = true) == 0L }


        // prolonged = true

        // setPixelColor with ColorContainer
        testLEDs.setPixelColor(10, ColorContainer(0xFF), prolonged = true)

        assertTrue { testLEDs.getPixelColor(10, prolonged = false) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(10, prolonged = true) == 0xFFL }

        // setPixelColor with Long
        testLEDs.setPixelColor(10, 0xFF0000, prolonged = true)

        assertTrue { testLEDs.getPixelColor(10, prolonged = false) == 0xFF0000L }
        assertTrue { testLEDs.getPixelColor(10, prolonged = true) == 0xFF0000L }


        testLEDs.setPixelColors(listOf(10, 11, 12), 0L, prolonged = true)   // reset pixels

        // Confirm successful reset
        checkPixels(10..12, testLEDs, 0)
        checkProlongedPixels(10..12, testLEDs, 0)


        // Index operators

        // vararg set with ColorContainer
        testLEDs[10, 11, 12] = ColorContainer(0xFFFF)

        assertTrue { testLEDs.getPixelColor(10, prolonged = false) == 0xFFFFL }
        assertTrue { testLEDs.getPixelColor(11, prolonged = false) == 0xFFFFL }
        assertTrue { testLEDs.getPixelColor(12, prolonged = false) == 0xFFFFL }
        assertTrue { testLEDs.getPixelColor(10, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(11, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(12, prolonged = true) == 0L }

        // IntRange set with ColorContainer
        testLEDs[10..12] = ColorContainer(0xFF00FF)

        assertTrue { testLEDs.getPixelColor(10, prolonged = false) == 0xFF00FFL }
        assertTrue { testLEDs.getPixelColor(11, prolonged = false) == 0xFF00FFL }
        assertTrue { testLEDs.getPixelColor(12, prolonged = false) == 0xFF00FFL }
        assertTrue { testLEDs.getPixelColor(10, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(11, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(12, prolonged = true) == 0L }

        // vararg set with Long
        testLEDs[10, 11, 12] = 0xFF00

        assertTrue { testLEDs.getPixelColor(10, prolonged = false) == 0xFF00L }
        assertTrue { testLEDs.getPixelColor(11, prolonged = false) == 0xFF00L }
        assertTrue { testLEDs.getPixelColor(12, prolonged = false) == 0xFF00L }
        assertTrue { testLEDs.getPixelColor(10, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(11, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(12, prolonged = true) == 0L }

        // IntRange set with Long
        testLEDs[10..12] = 0xFF0000

        assertTrue { testLEDs.getPixelColor(10, prolonged = false) == 0xFF0000L }
        assertTrue { testLEDs.getPixelColor(11, prolonged = false) == 0xFF0000L }
        assertTrue { testLEDs.getPixelColor(12, prolonged = false) == 0xFF0000L }
        assertTrue { testLEDs.getPixelColor(10, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(11, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(12, prolonged = true) == 0L }

        assertFailsWith<IllegalArgumentException> { testLEDs.setPixelColor(50, CCBlack) }
    }

    @Test
    fun testSetPixelColors() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        checkAllPixels(testLEDs, 0)
        checkAllProlongedPixels(testLEDs, 0)

        // prolonged default

        // setPixelColors with List and ColorContainer
        testLEDs.setPixelColors(listOf(10, 15, 20), ColorContainer(0xFF))

        assertTrue { testLEDs.getPixelColor(10, prolonged = false) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(15, prolonged = false) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(20, prolonged = false) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(18, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(10, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(15, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(20, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(18, prolonged = true) == 0L }

        // setPixelColors with List and Long
        testLEDs.setPixelColors(listOf(12, 17, 24), 0xFF00)

        assertTrue { testLEDs.getPixelColor(12, prolonged = false) == 0xFF00L }
        assertTrue { testLEDs.getPixelColor(17, prolonged = false) == 0xFF00L }
        assertTrue { testLEDs.getPixelColor(24, prolonged = false) == 0xFF00L }
        assertTrue { testLEDs.getPixelColor(21, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(12, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(17, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(24, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(21, prolonged = true) == 0L }

        // setPixelColors with IntRange and ColorContainer
        testLEDs.setPixelColors(30..40, ColorContainer(0xFF))

        assertTrue { testLEDs.getPixelColor(0, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(29, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(30, prolonged = false) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(40, prolonged = false) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(41, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(45, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(0, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(29, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(30, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(40, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(41, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(45, prolonged = true) == 0L }

        // setPixelColors with IntRange and Long
        testLEDs.setPixelColors(30..40, 0xFF00)

        assertTrue { testLEDs.getPixelColor(0, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(29, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(30, prolonged = false) == 0xFF00L }
        assertTrue { testLEDs.getPixelColor(40, prolonged = false) == 0xFF00L }
        assertTrue { testLEDs.getPixelColor(41, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(45, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(0, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(29, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(30, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(40, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(41, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(45, prolonged = true) == 0L }


        // prolonged = false

        // setPixelColors with ColorContainer
        testLEDs.setPixelColors(listOf(10, 15, 20), ColorContainer(0xFF), prolonged = false)

        assertTrue { testLEDs.getPixelColor(10, prolonged = false) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(15, prolonged = false) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(20, prolonged = false) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(18, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(10, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(15, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(20, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(18, prolonged = true) == 0L }

        // setPixelColors with Long
        testLEDs.setPixelColors(listOf(12, 17, 24), 0xFF00, prolonged = false)

        assertTrue { testLEDs.getPixelColor(12, prolonged = false) == 0xFF00L }
        assertTrue { testLEDs.getPixelColor(17, prolonged = false) == 0xFF00L }
        assertTrue { testLEDs.getPixelColor(24, prolonged = false) == 0xFF00L }
        assertTrue { testLEDs.getPixelColor(21, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(12, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(17, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(24, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(21, prolonged = true) == 0L }

        // setPixelColors with IntRange and ColorContainer
        testLEDs.setPixelColors(30..40, ColorContainer(0xFF), prolonged = false)

        assertTrue { testLEDs.getPixelColor(0, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(29, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(30, prolonged = false) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(40, prolonged = false) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(41, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(45, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(0, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(29, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(30, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(40, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(41, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(45, prolonged = true) == 0L }

        // setPixelColors with IntRange and Long
        testLEDs.setPixelColors(30..40, 0xFF00, prolonged = false)

        assertTrue { testLEDs.getPixelColor(0, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(29, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(30, prolonged = false) == 0xFF00L }
        assertTrue { testLEDs.getPixelColor(40, prolonged = false) == 0xFF00L }
        assertTrue { testLEDs.getPixelColor(41, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(45, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(0, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(29, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(30, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(40, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(41, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(45, prolonged = true) == 0L }


        // prolonged = true

        // setPixelColors with ColorContainer
        testLEDs.setPixelColors(listOf(10, 15, 20), ColorContainer(0xFF), prolonged = true)

        assertTrue { testLEDs.getPixelColor(10, prolonged = false) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(15, prolonged = false) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(20, prolonged = false) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(18, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(10, prolonged = true) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(15, prolonged = true) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(20, prolonged = true) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(18, prolonged = true) == 0L }

        // setPixelColors with Long
        testLEDs.setPixelColors(listOf(12, 17, 24), 0xFF00, prolonged = true)

        assertTrue { testLEDs.getPixelColor(12, prolonged = false) == 0xFF00L }
        assertTrue { testLEDs.getPixelColor(17, prolonged = false) == 0xFF00L }
        assertTrue { testLEDs.getPixelColor(24, prolonged = false) == 0xFF00L }
        assertTrue { testLEDs.getPixelColor(21, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(12, prolonged = true) == 0xFF00L }
        assertTrue { testLEDs.getPixelColor(17, prolonged = true) == 0xFF00L }
        assertTrue { testLEDs.getPixelColor(24, prolonged = true) == 0xFF00L }
        assertTrue { testLEDs.getPixelColor(21, prolonged = true) == 0L }

        // setPixelColors with IntRange and ColorContainer
        testLEDs.setPixelColors(30..40, ColorContainer(0xFF), prolonged = true)

        assertTrue { testLEDs.getPixelColor(0, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(29, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(30, prolonged = false) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(40, prolonged = false) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(41, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(45, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(0, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(29, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(30, prolonged = true) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(40, prolonged = true) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(41, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(45, prolonged = true) == 0L }

        // setPixelColors with IntRange and Long
        testLEDs.setPixelColors(30..40, 0xFF00, prolonged = true)

        assertTrue { testLEDs.getPixelColor(0, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(29, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(30, prolonged = false) == 0xFF00L }
        assertTrue { testLEDs.getPixelColor(40, prolonged = false) == 0xFF00L }
        assertTrue { testLEDs.getPixelColor(41, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(45, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(0, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(29, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(30, prolonged = true) == 0xFF00L }
        assertTrue { testLEDs.getPixelColor(40, prolonged = true) == 0xFF00L }
        assertTrue { testLEDs.getPixelColor(41, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(45, prolonged = true) == 0L }
    }

    @Test
    fun testSetSectionColor() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        checkAllPixels(testLEDs, 0)
        checkAllProlongedPixels(testLEDs, 0)

        // prolonged default

        // setSectionColor with ColorContainer
        testLEDs.setSectionColor(15, 30, ColorContainer(0xFF))

        assertTrue { testLEDs.getPixelColor(0, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(14, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(15, prolonged = false) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(30, prolonged = false) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(31, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(45, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(0, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(14, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(15, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(30, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(31, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(45, prolonged = true) == 0L }

        // setSectionColor with Long
        testLEDs.setSectionColor(15, 30, 0xFFFF)

        assertTrue { testLEDs.getPixelColor(0, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(14, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(15, prolonged = false) == 0xFFFFL }
        assertTrue { testLEDs.getPixelColor(30, prolonged = false) == 0xFFFFL }
        assertTrue { testLEDs.getPixelColor(31, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(45, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(0, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(14, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(15, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(30, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(31, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(45, prolonged = true) == 0L }

        // setSectionColor with ColorContainer
        testLEDs.setSectionColor(15..30, ColorContainer(0xFF))

        assertTrue { testLEDs.getPixelColor(0, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(14, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(15, prolonged = false) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(30, prolonged = false) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(31, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(45, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(0, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(14, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(15, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(30, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(31, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(45, prolonged = true) == 0L }

        // setSectionColor with Long
        testLEDs.setSectionColor(15..30, 0xFFFF)

        assertTrue { testLEDs.getPixelColor(0, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(14, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(15, prolonged = false) == 0xFFFFL }
        assertTrue { testLEDs.getPixelColor(30, prolonged = false) == 0xFFFFL }
        assertTrue { testLEDs.getPixelColor(31, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(45, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(0, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(14, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(15, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(30, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(31, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(45, prolonged = true) == 0L }

        // prolonged = false

        // setSectionColor with ColorContainer
        testLEDs.setSectionColor(15, 30, ColorContainer(0xFF), prolonged = false)

        assertTrue { testLEDs.getPixelColor(0, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(14, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(15, prolonged = false) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(30, prolonged = false) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(31, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(45, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(0, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(14, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(15, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(30, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(31, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(45, prolonged = true) == 0L }


        // setSectionColor with Long
        testLEDs.setSectionColor(15, 30, 0xFFFF, prolonged = false)

        assertTrue { testLEDs.getPixelColor(0, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(14, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(15, prolonged = false) == 0xFFFFL }
        assertTrue { testLEDs.getPixelColor(30, prolonged = false) == 0xFFFFL }
        assertTrue { testLEDs.getPixelColor(31, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(45, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(0, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(14, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(15, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(30, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(31, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(45, prolonged = true) == 0L }


        // prolonged = true

        // setSectionColor with ColorContainer
        testLEDs.setSectionColor(15, 30, ColorContainer(0xFF), prolonged = true)

        assertTrue { testLEDs.getPixelColor(0, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(14, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(15, prolonged = false) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(30, prolonged = false) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(31, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(45, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(0, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(14, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(15, prolonged = true) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(30, prolonged = true) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(31, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(45, prolonged = true) == 0L }


        // setSectionColor with Long
        testLEDs.setSectionColor(15, 30, 0xFFFF, prolonged = true)

        assertTrue { testLEDs.getPixelColor(0, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(14, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(15, prolonged = false) == 0xFFFFL }
        assertTrue { testLEDs.getPixelColor(30, prolonged = false) == 0xFFFFL }
        assertTrue { testLEDs.getPixelColor(31, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(45, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(0, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(14, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(15, prolonged = true) == 0xFFFFL }
        assertTrue { testLEDs.getPixelColor(30, prolonged = true) == 0xFFFFL }
        assertTrue { testLEDs.getPixelColor(31, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(45, prolonged = true) == 0L }
    }

    @Test
    fun testSetStripColor() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        checkAllPixels(testLEDs, 0)
        checkAllProlongedPixels(testLEDs, 0)

        // prolonged default

        // setStripColor with ColorContainer
        testLEDs.setStripColor(ColorContainer(0xFF))

        checkAllPixels(testLEDs, 0xFF)
        checkAllProlongedPixels(testLEDs, 0)


        // setStripColor with Long
        testLEDs.setStripColor(0xFFFF)

        checkAllPixels(testLEDs, 0xFFFF)
        checkAllProlongedPixels(testLEDs, 0)


        // color property set
        testLEDs.color = ColorContainer(0xFF0000)
        checkAllPixels(testLEDs, 0xFF0000)
        checkAllProlongedPixels(testLEDs, 0)

        // color property get
        assertFailsWith(IllegalStateException::class) {
            testLEDs.color
        }


        // prolonged false

        // setStripColor with ColorContainer
        testLEDs.setStripColor(ColorContainer(0xFF), prolonged = false)

        checkAllPixels(testLEDs, 0xFF)
        checkAllProlongedPixels(testLEDs, 0)


        // setStripColor with Long
        testLEDs.setStripColor(0xFFFF, prolonged = false)

        checkAllPixels(testLEDs, 0xFFFF)
        checkAllProlongedPixels(testLEDs, 0)


        // prolonged = true

        // setStripColor with ColorContainer
        testLEDs.setStripColor(ColorContainer(0xFF), prolonged = true)

        checkAllPixels(testLEDs, 0xFF)
        checkAllProlongedPixels(testLEDs, 0xFFL)


        // setStripColor with Long
        testLEDs.setStripColor(0xFFFF, prolonged = true)

        checkAllPixels(testLEDs, 0xFFFF)
        checkAllProlongedPixels(testLEDs, 0xFFFFL)
    }

    @Test
    fun testGetPixelColor() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.setPixelColor(15, 0xFF, prolonged = false)

        assertTrue { testLEDs.getPixelColor(15) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(15, prolonged = false) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(15, prolonged = true) == 0L }

        testLEDs.setPixelColor(15, 0xFF00, prolonged = true)

        assertTrue { testLEDs.getPixelColor(15) == 0xFF00L }
        assertTrue { testLEDs.getPixelColor(15, prolonged = false) == 0xFF00L }
        assertTrue { testLEDs.getPixelColor(15, prolonged = true) == 0xFF00L }

        assertFailsWith<IllegalArgumentException> { testLEDs.getPixelColor(50) }
    }

    @Test
    fun testGetPixelColorOrNull() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.setPixelColor(15, 0xFF, prolonged = true)

        assertTrue { testLEDs.getPixelColorOrNull(15) == 0xFFL }
        assertTrue { testLEDs.getPixelColorOrNull(15, prolonged = false) == 0xFFL }
        assertTrue { testLEDs.getPixelColorOrNull(15, prolonged = true) == 0xFFL }

        assertNull(testLEDs.getPixelColorOrNull(50))
    }

    @Test
    fun testGetPixelHexString() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)
        testLEDs.setPixelColor(15, 0xFF, prolonged = true)

        assertTrue { testLEDs.getPixelHexString(15) == "ff" }
        assertTrue { testLEDs.getPixelHexString(15, prolonged = false) == "ff" }
        assertTrue { testLEDs.getPixelHexString(15, prolonged = true) == "ff" }
    }

    @Test
    fun testSectionCreator() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        val testSection = LEDStrip.SectionCreator.new(15, 40, testLEDs)

        checkAllPixels(testLEDs, 0)
        checkAllProlongedPixels(testLEDs, 0)

        testSection.addAnimation(AnimationData().animation(Animation.COLOR).color(0xFF))

        assertTrue { testLEDs.getPixelColor(0, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(14, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(15, prolonged = false) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(30, prolonged = false) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(40, prolonged = false) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(41, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(45, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(0, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(14, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(15, prolonged = true) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(30, prolonged = true) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(40, prolonged = true) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(41, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(45, prolonged = true) == 0L }

        testLEDs.setStripColor(0, prolonged = true)

        val testSection2 = LEDStrip.SectionCreator.new(10..25, testLEDs)

        checkAllPixels(testLEDs, 0)
        checkAllProlongedPixels(testLEDs, 0)

        testSection2.addAnimation(AnimationData().animation(Animation.COLOR).color(0xFF))

        assertTrue { testLEDs.getPixelColor(0, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(9, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(10, prolonged = false) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(20, prolonged = false) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(25, prolonged = false) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(26, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(45, prolonged = false) == 0L }
        assertTrue { testLEDs.getPixelColor(0, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(9, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(10, prolonged = true) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(20, prolonged = true) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(25, prolonged = true) == 0xFFL }
        assertTrue { testLEDs.getPixelColor(26, prolonged = true) == 0L }
        assertTrue { testLEDs.getPixelColor(45, prolonged = true) == 0L }
    }

    @Test
    fun testSectionRun() = runBlocking {
        val testLEDs = EmulatedAnimatedLEDStrip(50)
        val testSection = LEDStrip.SectionCreator.new(10..25, testLEDs)

        testSection.addAnimation(AnimationData().animation(Animation.COLOR).color(0xFF))

        assertTrue { testLEDs[0] == 0L }
        assertTrue { testLEDs[9] == 0L }
        assertTrue { testLEDs[10] == 0xFFL }
        assertTrue { testLEDs[15] == 0xFFL }
        assertTrue { testLEDs[25] == 0xFFL }
        assertTrue { testLEDs[26] == 0L }
        assertTrue { testLEDs[45] == 0L }

        testSection.addAnimation(AnimationData().animation(Animation.STACK).color(0xFFFF))?.join()

        assertTrue { testLEDs[0] == 0L }
        assertTrue { testLEDs[9] == 0L }
        assertTrue { testLEDs[10] == 0xFFFFL }
        assertTrue { testLEDs[15] == 0xFFFFL }
        assertTrue { testLEDs[25] == 0xFFFFL }
        assertTrue { testLEDs[26] == 0L }
        assertTrue { testLEDs[45] == 0L }

        testSection.addAnimation(AnimationData().animation(Animation.WIPE).color(0xFF00))?.join()

        assertTrue { testLEDs[0] == 0L }
        assertTrue { testLEDs[9] == 0L }
        assertTrue { testLEDs[10] == 0xFF00L }
        assertTrue { testLEDs[15] == 0xFF00L }
        assertTrue { testLEDs[25] == 0xFF00L }
        assertTrue { testLEDs[26] == 0L }
        assertTrue { testLEDs[45] == 0L }

        testSection.addAnimation(AnimationData().animation(Animation.BOUNCETOCOLOR).color(0xFF00FF))?.join()

        assertTrue { testLEDs[0] == 0L }
        assertTrue { testLEDs[9] == 0L }
        assertTrue { testLEDs[10] == 0xFF00FFL }
        assertTrue { testLEDs[15] == 0xFF00FFL }
        assertTrue { testLEDs[25] == 0xFF00FFL }
        assertTrue { testLEDs[26] == 0L }
        assertTrue { testLEDs[45] == 0L }

        testSection.addAnimation(AnimationData().animation(Animation.MULTIPIXELRUNTOCOLOR).color(0xFF0000))?.join()

        assertTrue { testLEDs[0] == 0L }
        assertTrue { testLEDs[9] == 0L }
        assertTrue { testLEDs[10] == 0xFF0000L }
        assertTrue { testLEDs[15] == 0xFF0000L }
        assertTrue { testLEDs[25] == 0xFF0000L }
        assertTrue { testLEDs[26] == 0L }
        assertTrue { testLEDs[45] == 0L }

        testSection.addAnimation(AnimationData().animation(Animation.SPARKLETOCOLOR).color(0xFFFFFF))?.join()

        assertTrue { testLEDs[0] == 0L }
        assertTrue { testLEDs[9] == 0L }
        assertTrue { testLEDs[10] == 0xFFFFFFL }
        assertTrue { testLEDs[15] == 0xFFFFFFL }
        assertTrue { testLEDs[25] == 0xFFFFFFL }
        assertTrue { testLEDs[26] == 0L }
        assertTrue { testLEDs[45] == 0L }
    }


    @Test
    fun testFadePixel() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        testLEDs.fadePixel(50, CCBlue.color.toInt())
    }

    @Test
    fun testToggleRender() {
        val testLEDs = EmulatedAnimatedLEDStrip(50)

        delayBlocking(1000)
        assertTrue { testLEDs.rendering }
        testLEDs.toggleRender()
        delayBlocking(1000)
        assertFalse { testLEDs.rendering }
        testLEDs.toggleRender()
        delayBlocking(1000)
        assertTrue { testLEDs.rendering }
    }

    @Test
    fun testImageDebugging() {
        // Test file name
        val info1 = StripInfo(numLEDs = 50, imageDebugging = true, fileName = "test1.csv")
        val leds1 = EmulatedAnimatedLEDStrip(info1)
        delayBlocking(5000)
        leds1.toggleRender()
        delayBlocking(1000)
        assertTrue { Files.exists(Paths.get("test1.csv")) }
        Files.delete(Paths.get("test1.csv"))

        // Test file name without .csv extension
        val info2 = StripInfo(numLEDs = 50, imageDebugging = true, fileName = "test2")
        val leds2 = EmulatedAnimatedLEDStrip(info2)
        delayBlocking(5000)
        leds2.toggleRender()
        delayBlocking(1000)
        assertTrue { Files.exists(Paths.get("test2.csv")) }
        Files.delete(Paths.get("test2.csv"))

        // Test saving renders to file
        val info3 = StripInfo(numLEDs = 50, imageDebugging = true, fileName = "test3.csv", rendersBeforeSave = 100)
        val leds3 = EmulatedAnimatedLEDStrip(info3)
        delayBlocking(10000)
        leds3.toggleRender()
        delayBlocking(1000)
        Files.delete(Paths.get("test3.csv"))
    }
}