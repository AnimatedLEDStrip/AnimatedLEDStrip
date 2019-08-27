package animatedledstrip.test

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


import animatedledstrip.animationutils.*
import animatedledstrip.colors.ColorContainer
import org.junit.Test
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue


class AnimationDataTest {

    @Test
    fun testAnimation() {
        val testAnimation = AnimationData()

        assertFailsWith(UninitializedPropertyAccessException::class) {
            testAnimation.animation
        }

        testAnimation.animation = Animation.BOUNCE
        assertTrue { testAnimation.animation == Animation.BOUNCE }

        testAnimation.animation(Animation.MULTICOLOR)
        assertTrue { testAnimation.animation == Animation.MULTICOLOR }
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
    }

    @Test
    fun testDelay() {
        val testAnimation = AnimationData().animation(Animation.MULTICOLOR)

        assertTrue { testAnimation.delay == 50L }

        testAnimation.delay = 15
        assertTrue { testAnimation.delay == 15L }
        assertTrue { testAnimation.speed == AnimationSpeed.DEFAULT }

        testAnimation.delay(30)
        assertTrue { testAnimation.delay == 30L }

        testAnimation.delay(40L)
        assertTrue { testAnimation.delay == 40L }
    }

    @Test
    fun testDelayMod() {
        val testAnimation = AnimationData()

        assertTrue { testAnimation.delayMod == 1.0 }

        testAnimation.delayMod = 2.5
        assertTrue { testAnimation.delayMod == 2.5 }
        assertTrue { testAnimation.speed == AnimationSpeed.CUSTOM }

        testAnimation.delayMod(0.5)
        assertTrue { testAnimation.delayMod == 0.5 }
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
    fun testEndPixel() {
        val testAnimation = AnimationData()

        assertTrue { testAnimation.endPixel == 0 }

        testAnimation.endPixel = 50
        assertTrue { testAnimation.endPixel == 50 }

        testAnimation.endPixel(100)
        assertTrue { testAnimation.endPixel == 100 }
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
    fun testSpeed() {
        val testAnimation = AnimationData()

        assertTrue { testAnimation.speed == AnimationSpeed.DEFAULT }

        testAnimation.speed = AnimationSpeed.FAST
        assertTrue { testAnimation.speed == AnimationSpeed.FAST }
        assertTrue { testAnimation.delayMod == 2.0 }

        testAnimation.speed(AnimationSpeed.SLOW)
        assertTrue { testAnimation.speed == AnimationSpeed.SLOW }
        assertTrue { testAnimation.delayMod == 0.5 }
    }

    @Test
    fun testStartPixel() {
        val testAnimation = AnimationData()

        assertTrue { testAnimation.startPixel == 0 }

        testAnimation.startPixel = 50
        assertTrue { testAnimation.startPixel == 50 }

        testAnimation.startPixel(100)
        assertTrue { testAnimation.startPixel == 100 }
    }

    @Test
    fun testEquals() {

        val testAnimation1 = AnimationData()
            .animation(Animation.STACK)
            .color0(0xFFFF)
            .color1(0xFFFFFF)
            .delay(10)
            .direction(Direction.BACKWARD)
            .spacing(4)

        val testAnimation2 = AnimationData().apply {
            animation = Animation.STACK
            addColor(0xFFFF)
            addColor(0xFFFFFF)
            delay = 10
            direction = Direction.BACKWARD
            spacing = 4
        }

        assertTrue { testAnimation1 == testAnimation2 }
    }

    @Test
    fun testIsSerializable() {
        val testAnimation = AnimationData().animation(Animation.STACK)
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