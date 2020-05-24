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

package animatedledstrip.test

import animatedledstrip.animationutils.*
import animatedledstrip.colors.ColorContainer
import animatedledstrip.utils.toColorContainer
import org.junit.Test
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.*

class AnimationDataTest {

    @Test
    fun testAnimation() {
        val testAnimation = AnimationData()

        assertTrue { testAnimation.animation == "Color" }

        testAnimation.animation = "Bounce"
        assertTrue { testAnimation.animation == "Bounce" }

        testAnimation.animation("Splat")
        assertTrue { testAnimation.animation == "Splat" }
    }

    @Test
    fun testColors() {
        val testAnimation = AnimationData()

        testAnimation.color(0xFF)
        assertTrue { testAnimation.colors[0] == ColorContainer(0xFF) }

        testAnimation.color(ColorContainer(0xFFFF))
        assertTrue { testAnimation.colors[0] == ColorContainer(0xFFFF) }

        testAnimation.color(0xFFFFFFL)
        assertTrue { testAnimation.colors[0] == ColorContainer(0xFFFFFF) }

        testAnimation.color("0xFF00")
        assertTrue { testAnimation.colors[0] == ColorContainer(0xFF00) }

        testAnimation.color(0xFF, index = 3)
        assertTrue { testAnimation.colors[3] == ColorContainer(0xFF) }
        assertTrue { testAnimation.colors[2] == ColorContainer(0x0) }

        assertFailsWith<IllegalArgumentException> { testAnimation.color(0.0) }

        testAnimation.color0(0xFFFF)
        assertTrue { testAnimation.colors[0] == ColorContainer(0xFFFF) }

        testAnimation.color1(0xFFFF)
        assertTrue { testAnimation.colors[1] == ColorContainer(0xFFFF) }

        testAnimation.color2(0xFFFF)
        assertTrue { testAnimation.colors[2] == ColorContainer(0xFFFF) }

        testAnimation.color3(0xFFFF)
        assertTrue { testAnimation.colors[3] == ColorContainer(0xFFFF) }

        testAnimation.color4(0xFFFF)
        assertTrue { testAnimation.colors[4] == ColorContainer(0xFFFF) }

        testAnimation.addColor(0xFFFFFF)
        assertTrue { testAnimation.colors[5] == ColorContainer(0xFFFFFF) }

        testAnimation.addColors(ColorContainer(0xFFFF), ColorContainer(0xFF00FF))
        assertTrue { testAnimation.colors[6] == ColorContainer(0xFFFF) }
        assertTrue { testAnimation.colors[7] == ColorContainer(0xFF00FF) }

        testAnimation.addColors(0xFF88L, 0xFFL)
        assertTrue { testAnimation.colors[8] == ColorContainer(0xFF88) }
        assertTrue { testAnimation.colors[9] == ColorContainer(0xFF) }

        testAnimation.addColors(0xFF, 0xFF00)
        assertTrue { testAnimation.colors[10] == ColorContainer(0xFF) }
        assertTrue { testAnimation.colors[11] == ColorContainer(0xFF00) }

        testAnimation.addColors("0xFF", "0x88")
        assertTrue { testAnimation.colors[12] == ColorContainer(0xFF) }
        assertTrue { testAnimation.colors[13] == ColorContainer(0x88) }

        testAnimation.addColors(listOf(0xAA, "0xFFFF", 0xFF00FFL, ColorContainer(0x8888)))
        assertTrue { testAnimation.colors[14] == ColorContainer(0xAA) }
        assertTrue { testAnimation.colors[15] == ColorContainer(0xFFFF) }
        assertTrue { testAnimation.colors[16] == ColorContainer(0xFF00FF) }
        assertTrue { testAnimation.colors[17] == ColorContainer(0x8888) }

        assertFailsWith<IllegalArgumentException> { testAnimation.addColors(listOf<Int>()) }
        assertFailsWith<IllegalArgumentException> { testAnimation.addColors(listOf<Int?>(null)) }
        assertFailsWith<IllegalArgumentException> { testAnimation.addColors(listOf(0.0)) }
        assertFailsWith<NumberFormatException> { testAnimation.addColors(listOf("0XG")) }

    }

    @Test
    fun testCenter() {
        val testAnimation = AnimationData()

        testAnimation.center = 10
        assertTrue { testAnimation.center == 10 }

        testAnimation.center(15)
        assertTrue { testAnimation.center == 15 }
    }

    @Test
    fun testContinuous() {
        val testAnimation = AnimationData()
        assertNull(testAnimation.continuous)

        testAnimation.continuous(true)
        assertTrue { testAnimation.continuous == true }

        testAnimation.continuous(false)
        assertFalse { testAnimation.continuous == true }
    }

    @Test
    fun testDelay() {
        loadPredefinedAnimations(this::class.java.classLoader)
        awaitPredefinedAnimationsLoaded()
        val testAnimation = AnimationData()

        assertTrue { DEFAULT_DELAY == 50L }

        testAnimation.delay = 15
        assertTrue { testAnimation.delay == 15L }

        testAnimation.delay(30)
        assertTrue { testAnimation.delay == 30L }

        testAnimation.delay(40L)
        assertTrue { testAnimation.delay == 40L }

        assertTrue { findAnimation("Bounce")?.info?.delayDefault == 10L }
        testAnimation.animation = "Bounce"

        testAnimation.delay = -1
        assertTrue { testAnimation.delay == 10L }

        testAnimation.delay = -2
        assertTrue { testAnimation.delay == -2L }

        testAnimation.delay = 0
        assertTrue { testAnimation.delay == 10L }

        testAnimation.delay = 1
        assertTrue { testAnimation.delay == 1L }
    }

    @Test
    fun testDelayMod() {
        val testAnimation = AnimationData()

        assertTrue { testAnimation.delayMod == 1.0 }

        testAnimation.delayMod = 2.5
        assertTrue { testAnimation.delayMod == 2.5 }

        testAnimation.delayMod(0.5)
        assertTrue { testAnimation.delayMod == 0.5 }

        assertTrue { testAnimation.animation == "Color" }
        assertTrue { testAnimation.delay == 25L }
    }

    @Test
    fun testDirection() {
        val testAnimation = AnimationData()

        assertTrue { testAnimation.direction == Direction.FORWARD }

        testAnimation.direction = Direction.FORWARD
        assertTrue { testAnimation.direction == Direction.FORWARD }

        testAnimation.direction = Direction.BACKWARD
        assertTrue { testAnimation.direction == Direction.BACKWARD }

        testAnimation.direction(Direction.FORWARD)
        assertTrue { testAnimation.direction == Direction.FORWARD }

        testAnimation.direction(Direction.BACKWARD)
        assertTrue { testAnimation.direction == Direction.BACKWARD }

        testAnimation.direction('F')
        assertTrue { testAnimation.direction == Direction.FORWARD }

        testAnimation.direction('B')
        assertTrue { testAnimation.direction == Direction.BACKWARD }

        testAnimation.direction('f')
        assertTrue { testAnimation.direction == Direction.FORWARD }

        testAnimation.direction('b')
        assertTrue { testAnimation.direction == Direction.BACKWARD }

        assertFails {
            testAnimation.direction('G')
        }
    }

    @Test
    fun testDistance() {
        val testAnimation = AnimationData()

        testAnimation.distance = 50
        assertTrue { testAnimation.distance == 50 }

        testAnimation.distance(35)
        assertTrue { testAnimation.distance == 35 }
    }

    @Test
    fun testID() {
        val testAnimation = AnimationData()

        assertTrue { testAnimation.id == "" }

        testAnimation.id = "TEST"
        assertTrue { testAnimation.id == "TEST" }

        testAnimation.id("TEST2")
        assertTrue { testAnimation.id == "TEST2" }
    }

    @Test
    fun testSection() {
        val testAnimation = AnimationData()

        assertTrue { testAnimation.section == "" }

        testAnimation.section = "test"
        assertTrue { testAnimation.section == "test" }

        testAnimation.section("section")
        assertTrue { testAnimation.section == "section" }
    }

    @Test
    fun testSpacing() {
        loadPredefinedAnimations(this::class.java.classLoader)
        awaitPredefinedAnimationsLoaded()
        val testAnimation = AnimationData()

        assertTrue { DEFAULT_SPACING == 3 }
        assertTrue { testAnimation.spacing == DEFAULT_SPACING }

        testAnimation.spacing = 5
        assertTrue { testAnimation.spacing == 5 }

        testAnimation.spacing(10)
        assertTrue { testAnimation.spacing == 10 }

        assertTrue { findAnimation("MultiPixelRun")?.info?.spacingDefault == 3 }
        testAnimation.animation = "MultiPixelRun"

        testAnimation.spacing = -1
        assertTrue { testAnimation.spacing == 3 }

        testAnimation.spacing = -2
        assertTrue { testAnimation.spacing == -2 }

        testAnimation.spacing = 0
        assertTrue { testAnimation.spacing == 3 }

        testAnimation.spacing = 1
        assertTrue { testAnimation.spacing == 1 }
    }

    @Test
    fun testSpeed() {
        val testAnimation = AnimationData()

        testAnimation.speed(AnimationSpeed.FAST)
        assertTrue { testAnimation.delayMod == 2.0 }

        testAnimation.speed(AnimationSpeed.SLOW)
        assertTrue { testAnimation.delayMod == 0.5 }

        testAnimation.speed(AnimationSpeed.DEFAULT)
        assertTrue { testAnimation.delayMod == 1.0 }
    }

    @Test
    fun testEquals() {
        val testAnimation1 = AnimationData()
            .animation("Stack")
            .color0(0xFFFF)
            .color1(0xFFFFFF)
            .delay(10)
            .direction(Direction.BACKWARD)
            .spacing(4)

        val testAnimation2 = AnimationData().apply {
            animation = "sta ck"
            addColor(0xFFFF)
            addColor(0xFFFFFF)
            delay = 10
            direction = Direction.BACKWARD
            spacing = 4
        }

        assertTrue { testAnimation1 == testAnimation1 }
        assertTrue { testAnimation1 == testAnimation2 }
        @Suppress("ReplaceCallWithBinaryOperator")
        assertFalse { testAnimation1.equals(0xFF) }
    }

    @Test
    fun testCopy() {
        val testAnimation1 = AnimationData()
            .animation("Stack")
            .color0(0xFFFF)
            .color1(0xFFFFFF)
            .delay(10)
            .direction(Direction.BACKWARD)
            .spacing(4)

        val testAnimation2 = testAnimation1.copy()
        assertFalse { testAnimation1 === testAnimation2 }
        assertTrue { testAnimation1 == testAnimation2 }

        val testAnimation3 = testAnimation1.copy(
            animation = "Color",
            colors = listOf(0xFF.toColorContainer()),
            center = 30,
            continuous = false,
            delay = 10,
            delayMod = 2.0,
            direction = Direction.FORWARD,
            distance = 50,
            id = "test",
            section = "section",
            spacing = 4
        )

        assertFalse { testAnimation1 === testAnimation3 }
        assertFalse { testAnimation1 == testAnimation3 }
    }

    @Test
    fun testHashCode() {
        AnimationData().hashCode()
        AnimationData(continuous = true).hashCode()
    }

    @Test
    fun testToString() {
        assertTrue {
            AnimationData().toString() ==
                    "AnimationData(animation=Color, colors=[], center=-1, continuous=null, delay=50, " +
                    "delayMod=1.0, direction=FORWARD, distance=-1, id=, section=, spacing=3)"
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
            ).toString() ==
                    "AnimationData(animation=Bounce, colors=[ff], center=30, continuous=false, delay=10, " +
                    "delayMod=2.0, direction=BACKWARD, distance=50, id=test, section=section, spacing=4)"
        }
    }

    @Test
    fun testToHumanReadableString() {
        assertTrue {
            AnimationData().toHumanReadableString() ==
                    """
                        AnimationData :
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
                        AnimationData test:
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
    fun testIsSerializable() {
        val testAnimation = AnimationData().animation("Stack")
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
        val fileOut = FileOutputStream("animation.ser")
        val out = ObjectOutputStream(fileOut)
        out.writeObject(testAnimation)
        out.close()
        fileOut.close()
        val fileIn = FileInputStream("animation.ser")
        val input = ObjectInputStream(fileIn)
        val testAnimation2: AnimationData = input.readObject() as AnimationData
        input.close()
        fileIn.close()
        assertTrue { testAnimation == testAnimation2 }
        Files.delete(Paths.get("animation.ser"))
    }
}