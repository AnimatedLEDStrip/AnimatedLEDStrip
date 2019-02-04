package animatedledstrip.test

import animatedledstrip.leds.ColorContainer
import org.junit.Test
import kotlin.test.assertTrue

class ColorContainerTest {

    @Test
    fun testPrimaryConstructor() {
        val testCC = ColorContainer(255, 123, 80)

        assertTrue { testCC.r == 255 }
        assertTrue { testCC.g == 123 }
        assertTrue { testCC.b == 80 }
    }

    @Test
    fun testCCConstructor() {
        val cc = ColorContainer(255, 123, 80)
        val testCC = ColorContainer(cc)

        assertTrue { testCC.r == 255 }
        assertTrue { testCC.g == 123 }
        assertTrue { testCC.b == 80 }
    }

    @Test
    fun testLongConstructor() {
        val testCC = ColorContainer(0xFF7B50)

        assertTrue { testCC.r == 255 }
        assertTrue { testCC.g == 123 }
        assertTrue { testCC.b == 80 }
    }

    @Test
    fun testR() {
        val testCC = ColorContainer(0)
        testCC.r = 255

        assertTrue { testCC.r == 255 }
    }

    @Test
    fun testG() {
        val testCC = ColorContainer(0)
        testCC.g = 255

        assertTrue { testCC.g == 255 }
    }

    @Test
    fun testB() {
        val testCC = ColorContainer(0)
        testCC.b = 255

        assertTrue { testCC.b == 255 }
    }

    @Test
    fun testHex() {
        val testCC = ColorContainer(0xFF7B50)

        assertTrue { testCC.hex == 0xFF7B50L }
    }

    @Test
    fun testHexString() {
        val testCC = ColorContainer(0xFF7B50)

        assertTrue { testCC.hexString == "FF7B50" }
    }

    @Test
    fun testSetRGB() {
        val testCC = ColorContainer(0)
        testCC.setRGB(255, 123, 80)

        assertTrue { testCC.r == 255 }
        assertTrue { testCC.g == 123 }
        assertTrue { testCC.b == 80 }
    }

    @Test
    fun testToColor() {
        val testCC = ColorContainer(0xFF7B50)

        assertTrue { testCC.toColor().red == 1.0 }
        assertTrue { testCC.toColor().green == 0.48235294222831726 }
        assertTrue { testCC.toColor().blue == 0.3137255012989044 }

    }

    @Test
    fun testInvert() {
        val testCC = ColorContainer(0xFF7B50)

        assertTrue { testCC.invert() == ColorContainer(0x0084AF) }
    }

    @Test
    fun testGrayscale() {
        val testCC = ColorContainer(0xFF7B51)

        assertTrue { testCC.grayscale() == 0x99L }
    }

    @Test
    fun testEquals() {
        val testCC = ColorContainer(0xFF7B50)

        assertTrue { testCC == ColorContainer(0xFF7B50) }
    }
}