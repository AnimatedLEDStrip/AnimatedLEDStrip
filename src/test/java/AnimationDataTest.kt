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


import animatedledstrip.ccpresets.CCBlack
import animatedledstrip.leds.Animation
import animatedledstrip.leds.AnimationData
import animatedledstrip.leds.ColorContainer
import animatedledstrip.leds.Direction
import org.junit.Test
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

        assertTrue { testAnimation.color1 == CCBlack }
        assertTrue { testAnimation.color2 == CCBlack }
        assertTrue { testAnimation.color3 == CCBlack }
        assertTrue { testAnimation.color4 == CCBlack }
        assertTrue { testAnimation.color5 == CCBlack }

        testAnimation.color(0xFF)
        assertTrue { testAnimation.color1 == ColorContainer(0xFF) }

        testAnimation.color(ColorContainer(0xFFFF))
        assertTrue { testAnimation.color1 == ColorContainer(0xFFFF) }

        testAnimation.color(0xFFFFFFL)
        assertTrue { testAnimation.color1 == ColorContainer(0xFFFFFF) }

        testAnimation.color("0xFF00")
        assertTrue { testAnimation.color1 == ColorContainer(0xFF00) }

        testAnimation.color1(0xFF)
        assertTrue { testAnimation.color1 == ColorContainer(0xFF) }

        testAnimation.color1(ColorContainer(0xFFFF))
        assertTrue { testAnimation.color1 == ColorContainer(0xFFFF) }

        testAnimation.color1(0xFFFFFFL)
        assertTrue { testAnimation.color1 == ColorContainer(0xFFFFFF) }

        testAnimation.color1("0xFF00")
        assertTrue { testAnimation.color1 == ColorContainer(0xFF00) }


        testAnimation.color2(0xFF)
        assertTrue { testAnimation.color2 == ColorContainer(0xFF) }

        testAnimation.color2(ColorContainer(0xFFFF))
        assertTrue { testAnimation.color2 == ColorContainer(0xFFFF) }

        testAnimation.color2(0xFFFFFFL)
        assertTrue { testAnimation.color2 == ColorContainer(0xFFFFFF) }

        testAnimation.color2("0xFF00")
        assertTrue { testAnimation.color2 == ColorContainer(0xFF00) }


        testAnimation.color3(0xFF)
        assertTrue { testAnimation.color3 == ColorContainer(0xFF) }

        testAnimation.color3(ColorContainer(0xFFFF))
        assertTrue { testAnimation.color3 == ColorContainer(0xFFFF) }

        testAnimation.color3(0xFFFFFFL)
        assertTrue { testAnimation.color3 == ColorContainer(0xFFFFFF) }

        testAnimation.color3("0xFF00")
        assertTrue { testAnimation.color3 == ColorContainer(0xFF00) }


        testAnimation.color4(0xFF)
        assertTrue { testAnimation.color4 == ColorContainer(0xFF) }

        testAnimation.color4(ColorContainer(0xFFFF))
        assertTrue { testAnimation.color4 == ColorContainer(0xFFFF) }

        testAnimation.color4(0xFFFFFFL)
        assertTrue { testAnimation.color4 == ColorContainer(0xFFFFFF) }

        testAnimation.color4("0xFF00")
        assertTrue { testAnimation.color4 == ColorContainer(0xFF00) }


        testAnimation.color5(0xFF)
        assertTrue { testAnimation.color5 == ColorContainer(0xFF) }

        testAnimation.color5(ColorContainer(0xFFFF))
        assertTrue { testAnimation.color5 == ColorContainer(0xFFFF) }

        testAnimation.color5(0xFFFFFFL)
        assertTrue { testAnimation.color5 == ColorContainer(0xFFFFFF) }

        testAnimation.color5("0xFF00")
        assertTrue { testAnimation.color5 == ColorContainer(0xFF00) }
    }

    @Test
    fun testColorList() {
        // TODO()
    }

    @Test
    fun testDelay() {
        val testAnimation = AnimationData().animation(Animation.MULTICOLOR)

        assertTrue { testAnimation.delay == 50L }

        testAnimation.delay = 15
        assertTrue { testAnimation.delay == 15L }

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
    fun testStartPixel() {
        val testAnimation = AnimationData()

        assertTrue { testAnimation.startPixel == 0 }

        testAnimation.startPixel = 50
        assertTrue { testAnimation.startPixel == 50 }

        testAnimation.startPixel(100)
        assertTrue { testAnimation.startPixel == 100 }
    }

    @Test
    fun testMapConstructor() {
        // TODO()
    }

    @Test
    fun testToString() {
        // TODO()
    }

    @Test
    fun testToMap() {
        val testAnimation = AnimationData().animation(Animation.STACK)
                .color1(0xFF)
                .color2(0xFF)
                .color3(0xFF)
                .color4(0xFF)
                .color5(0xFF)
                .colorList(listOf(0xFF))
                .continuous(true)
                .delay(50)
                .direction(Direction.FORWARD)
                .id("TEST")
                .spacing(5)
                .toMap()
        assertTrue { testAnimation["Animation"] is Animation }
        assertTrue { testAnimation["Animation"] == Animation.STACK }
        assertTrue { testAnimation["Color1"] is Long }
        assertTrue { testAnimation["Color1"] == 0xFFL }
        assertTrue { testAnimation["Color2"] is Long }
        assertTrue { testAnimation["Color2"] == 0xFFL }
        assertTrue { testAnimation["Color3"] is Long }
        assertTrue { testAnimation["Color3"] == 0xFFL }
        assertTrue { testAnimation["Color4"] is Long }
        assertTrue { testAnimation["Color4"] == 0xFFL }
        assertTrue { testAnimation["Color5"] is Long }
        assertTrue { testAnimation["Color5"] == 0xFFL }
        assertTrue { testAnimation["ColorList"] is List<*> }
//        assertTrue { (testAnimation["ColorList"] as List<*>).size != 0 }  // TODO: Fix test
        assertTrue { testAnimation["Continuous"] is Boolean }
        assertTrue { testAnimation["Continuous"] == true }
        assertTrue { testAnimation["Delay"] is Long }
        assertTrue { testAnimation["Delay"] == 50L }
        assertTrue { testAnimation["DelayMod"] is Double }
        assertTrue { testAnimation["DelayMod"] == 1.0 }
        assertTrue { testAnimation["Direction"] is Char }
        assertTrue { testAnimation["Direction"] == 'F' }
        assertTrue { testAnimation["EndPixel"] is Int }
        assertTrue { testAnimation["EndPixel"] == 0 }
        assertTrue { testAnimation["ID"] is String }
        assertTrue { testAnimation["ID"] == "TEST" }
        assertTrue { testAnimation["Spacing"] is Int }
        assertTrue { testAnimation["Spacing"] == 5 }
        assertTrue { testAnimation["StartPixel"] is Int }
        assertTrue { testAnimation["StartPixel"] == 0 }
    }
}