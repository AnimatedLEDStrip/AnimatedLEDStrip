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
import org.junit.Ignore
import org.junit.Test
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
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
    @Ignore
    fun testColorList() {
        TODO()
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

        assertFails {
            AnimationData(mapOf("Color1" to 0xFFL))
            Unit
        }

        val a1 = AnimationData(mapOf("Animation" to Animation.ALTERNATE))
        assertTrue { a1.animation == Animation.ALTERNATE }
        val a2 = AnimationData(mapOf("Animation" to Animation.BOUNCE))
        assertTrue { a2.animation == Animation.BOUNCE }
        val a3 = AnimationData(mapOf("Animation" to Animation.BOUNCETOCOLOR))
        assertTrue { a3.animation == Animation.BOUNCETOCOLOR }
        val a4 = AnimationData(mapOf("Animation" to Animation.COLOR))
        assertTrue { a4.animation == Animation.COLOR }
        val a5 = AnimationData(mapOf("Animation" to Animation.MULTICOLOR))
        assertTrue { a5.animation == Animation.MULTICOLOR }
        val a6 = AnimationData(mapOf("Animation" to Animation.MULTIPIXELRUN))
        assertTrue { a6.animation == Animation.MULTIPIXELRUN }
        val a7 = AnimationData(mapOf("Animation" to Animation.MULTIPIXELRUNTOCOLOR))
        assertTrue { a7.animation == Animation.MULTIPIXELRUNTOCOLOR }
        val a8 = AnimationData(mapOf("Animation" to Animation.PIXELMARATHON))
        assertTrue { a8.animation == Animation.PIXELMARATHON }
        val a9 = AnimationData(mapOf("Animation" to Animation.PIXELRUN))
        assertTrue { a9.animation == Animation.PIXELRUN }
        val a10 = AnimationData(mapOf("Animation" to Animation.PIXELRUNWITHTRAIL))
        assertTrue { a10.animation == Animation.PIXELRUNWITHTRAIL }
        val a11 = AnimationData(mapOf("Animation" to Animation.SMOOTHCHASE))
        assertTrue { a11.animation == Animation.SMOOTHCHASE }
        val a12 = AnimationData(mapOf("Animation" to Animation.SMOOTHFADE))
        assertTrue { a12.animation == Animation.SMOOTHFADE }
        val a13 = AnimationData(mapOf("Animation" to Animation.SPARKLE))
        assertTrue { a13.animation == Animation.SPARKLE }
        val a14 = AnimationData(mapOf("Animation" to Animation.SPARKLEFADE))
        assertTrue { a14.animation == Animation.SPARKLEFADE }
        val a15 = AnimationData(mapOf("Animation" to Animation.SPARKLETOCOLOR))
        assertTrue { a15.animation == Animation.SPARKLETOCOLOR }
        val a16 = AnimationData(mapOf("Animation" to Animation.STACK))
        assertTrue { a16.animation == Animation.STACK }
        val a17 = AnimationData(mapOf("Animation" to Animation.STACKOVERFLOW))
        assertTrue { a17.animation == Animation.STACKOVERFLOW }
        val a18 = AnimationData(mapOf("Animation" to Animation.WIPE))
        assertTrue { a18.animation == Animation.WIPE }
        val a19 = AnimationData(mapOf("Animation" to Animation.CUSTOMANIMATION))
        assertTrue { a19.animation == Animation.CUSTOMANIMATION }
        val a20 = AnimationData(mapOf("Animation" to Animation.CUSTOMREPETITIVEANIMATION))
        assertTrue { a20.animation == Animation.CUSTOMREPETITIVEANIMATION }

        val as1 = AnimationData(mapOf("Animation" to "ALT"))
        assertTrue { as1.animation == Animation.ALTERNATE }

        val as2 = AnimationData(mapOf("Animation" to "COL"))
        assertTrue { as2.animation == Animation.COLOR }

        val as3 = AnimationData(mapOf("Animation" to "MCOL"))
        assertTrue { as3.animation == Animation.MULTICOLOR }

        assertFails {
            AnimationData(mapOf("Animation" to "ABC"))
        }

        assertFails {
            AnimationData(mapOf("Animation" to 10))
        }

        val c0 = AnimationData(mapOf("Animation" to Animation.WIPE))
        assertTrue { c0.color1 == ColorContainer(0) }
        assertTrue { c0.color2 == ColorContainer(0) }
        assertTrue { c0.color3 == ColorContainer(0) }
        assertTrue { c0.color4 == ColorContainer(0) }
        assertTrue { c0.color5 == ColorContainer(0) }

        val c11 = AnimationData(mapOf("Animation" to Animation.WIPE, "Color1" to 0xFFFFL))
        assertTrue { c11.color1 == ColorContainer(0xFFFF) }

        val c21 = AnimationData(mapOf("Animation" to Animation.WIPE, "Color2" to 0xFFFFL))
        assertTrue { c21.color2 == ColorContainer(0xFFFF) }

        val c31 = AnimationData(mapOf("Animation" to Animation.WIPE, "Color3" to 0xFFFFL))
        assertTrue { c31.color3 == ColorContainer(0xFFFF) }

        val c41 = AnimationData(mapOf("Animation" to Animation.WIPE, "Color4" to 0xFFFFL))
        assertTrue { c41.color4 == ColorContainer(0xFFFF) }

        val c51 = AnimationData(mapOf("Animation" to Animation.WIPE, "Color5" to 0xFFFFL))
        assertTrue { c51.color5 == ColorContainer(0xFFFF) }

//        val c12 = AnimationData(mapOf("Animation" to Animation.WIPE, "Color1" to ColorContainer(0xFFFF)))
//        assertTrue { c12.color1 == ColorContainer(0xFFFF) }
//
//        val c22 = AnimationData(mapOf("Animation" to Animation.WIPE, "Color2" to ColorContainer(0xFFFF)))
//        assertTrue { c22.color2 == ColorContainer(0xFFFF) }
//
//        val c32 = AnimationData(mapOf("Animation" to Animation.WIPE, "Color3" to ColorContainer(0xFFFF)))
//        assertTrue { c32.color3 == ColorContainer(0xFFFF) }
//
//        val c42 = AnimationData(mapOf("Animation" to Animation.WIPE, "Color4" to ColorContainer(0xFFFF)))
//        assertTrue { c42.color4 == ColorContainer(0xFFFF) }
//
//        val c52 = AnimationData(mapOf("Animation" to Animation.WIPE, "Color5" to ColorContainer(0xFFFF)))
//        assertTrue { c52.color5 == ColorContainer(0xFFFF) }


        val c1 = AnimationData(mapOf("Animation" to Animation.STACK, "Continuous" to true))
        assertTrue { c1.continuous }

        val c2 = AnimationData(mapOf("Animation" to Animation.STACK, "Continuous" to false))
        assertFalse { c2.continuous }


        val dm1 = AnimationData(mapOf("Animation" to Animation.SPARKLE))
        assertTrue { dm1.delayMod == 1.0 }

        val dm2 = AnimationData(mapOf("Animation" to Animation.SPARKLE, "DelayMod" to 2.0))
        assertTrue { dm2.delayMod == 2.0 }


        val d1 = AnimationData(mapOf("Animation" to Animation.PIXELRUN))
        assertTrue { d1.direction == Direction.FORWARD }

        val d2 = AnimationData(mapOf("Animation" to Animation.PIXELRUN, "Direction" to Direction.FORWARD))
        assertTrue { d2.direction == Direction.FORWARD }

        val d3 = AnimationData(mapOf("Animation" to Animation.PIXELRUN, "Direction" to 'F'))
        assertTrue { d3.direction == Direction.FORWARD }

        val d4 = AnimationData(mapOf("Animation" to Animation.PIXELRUN, "Direction" to 'f'))
        assertTrue { d4.direction == Direction.FORWARD }

        val d5 = AnimationData(mapOf("Animation" to Animation.PIXELRUN, "Direction" to Direction.BACKWARD))
        assertTrue { d5.direction == Direction.BACKWARD }

        val d6 = AnimationData(mapOf("Animation" to Animation.PIXELRUN, "Direction" to 'B'))
        assertTrue { d6.direction == Direction.BACKWARD }

        val d7 = AnimationData(mapOf("Animation" to Animation.PIXELRUN, "Direction" to 'b'))
        assertTrue { d7.direction == Direction.BACKWARD }

        assertFails {
            AnimationData(mapOf("Animation" to Animation.PIXELRUN, "Direction" to 'G'))
        }


        val ep1 = AnimationData(mapOf("Animation" to Animation.BOUNCE))
        assertTrue { ep1.endPixel == 0 }

        val ep2 = AnimationData(mapOf("Animation" to Animation.BOUNCE, "EndPixel" to 15))
        assertTrue { ep2.endPixel == 15 }


        val i1 = AnimationData(mapOf("Animation" to Animation.MULTIPIXELRUN))
        assertTrue { i1.id == "" }

        val i2 = AnimationData(mapOf("Animation" to Animation.MULTIPIXELRUN, "ID" to "TEST"))
        assertTrue { i2.id == "TEST" }


        val s1 = AnimationData(mapOf("Animation" to Animation.MULTIPIXELRUNTOCOLOR))
        assertTrue { s1.spacing == 3 }

        val s2 = AnimationData(mapOf("Animation" to Animation.MULTIPIXELRUNTOCOLOR, "Spacing" to 5))
        assertTrue { s2.spacing == 5 }


        val sp1 = AnimationData(mapOf("Animation" to Animation.SMOOTHCHASE))
        assertTrue { sp1.startPixel == 0 }

        val sp2 = AnimationData(mapOf("Animation" to Animation.SMOOTHCHASE, "StartPixel" to 10))
        assertTrue { sp2.startPixel == 10 }
    }

    @Test
    @Ignore
    fun testToString() {
        TODO()
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
//        assertTrue { testAnimation["Color1"] is Long }
//        assertTrue { testAnimation["Color1"] == 0xFFL }
//        assertTrue { testAnimation["Color2"] is Long }
//        assertTrue { testAnimation["Color2"] == 0xFFL }
//        assertTrue { testAnimation["Color3"] is Long }
//        assertTrue { testAnimation["Color3"] == 0xFFL }
//        assertTrue { testAnimation["Color4"] is Long }
//        assertTrue { testAnimation["Color4"] == 0xFFL }
//        assertTrue { testAnimation["Color5"] is Long }
//        assertTrue { testAnimation["Color5"] == 0xFFL }
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