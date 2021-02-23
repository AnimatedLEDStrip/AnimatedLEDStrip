/*
 * Copyright (c) 2018-2021 AnimatedLEDStrip
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package animatedledstrip.leds.colormanagement

import animatedledstrip.colors.PreparedColorContainer
import animatedledstrip.leds.animationmanagement.AnimationManager
import animatedledstrip.leds.sectionmanagement.SectionManager
import animatedledstrip.leds.stripmanagement.LEDStrip
import kotlinx.coroutines.delay

/* Set Pixel Fade Color */

/**
 * Set a pixel's fade color
 */
fun LEDStrip.setPixelFadeColor(pixel: Int, color: PreparedColorContainer): Unit =
    colorManager.setPixelColor(pixel, color, PixelColorType.FADE)

/**
 * Set a pixel's fade color
 */
fun LEDStrip.setPixelFadeColor(pixel: Int, color: Int): Unit =
    colorManager.setPixelColor(pixel, color, PixelColorType.FADE)

/**
 * Set multiple pixel's fade color
 */
fun LEDStrip.setPixelFadeColors(pixels: List<Int>, color: PreparedColorContainer) {
    for (pixel in pixels) setPixelFadeColor(pixel, color)
}

/**
 * Set multiple pixels' fade color
 */
fun LEDStrip.setPixelFadeColors(pixels: List<Int>, color: Int) {
    for (pixel in pixels) setPixelFadeColor(pixel, color)
}

/**
 * Set multiple pixels' fade color
 */
fun LEDStrip.setPixelFadeColors(pixels: IntRange, color: PreparedColorContainer): Unit =
    setPixelFadeColors(pixels.toList(), color)

/**
 * Set multiple pixels' fade color
 */
fun LEDStrip.setPixelFadeColors(pixels: IntRange, color: Int): Unit =
    setPixelFadeColors(pixels.toList(), color)


/**
 * Set a pixel's fade color
 */
fun SectionManager.setPixelFadeColor(pixel: Int, color: PreparedColorContainer): Unit =
    stripManager.setPixelFadeColor(getPhysicalIndex(pixel), color[pixel])

/**
 * Set a pixel's fade color
 */
fun SectionManager.setPixelFadeColor(pixel: Int, color: Int): Unit =
    stripManager.setPixelFadeColor(getPhysicalIndex(pixel), color)

/**
 * Set multiple pixels' fade color
 */
fun SectionManager.setPixelFadeColors(pixels: List<Int>, color: PreparedColorContainer) {
    for (pixel in pixels) setPixelFadeColor(pixel, color)
}

/**
 * Set multiple pixels' fade color
 */
fun SectionManager.setPixelFadeColors(pixels: List<Int>, color: Int) {
    for (pixel in pixels) setPixelFadeColor(pixel, color)
}

/**
 * Set multiple pixels' fade color
 */
fun SectionManager.setPixelFadeColors(pixels: IntRange, color: PreparedColorContainer): Unit =
    setPixelFadeColors(pixels.toList(), color)

/**
 * Set multiple pixels' fade color
 */
fun SectionManager.setPixelFadeColors(pixels: IntRange, color: Int): Unit =
    setPixelFadeColors(pixels.toList(), color)


/**
 * Set a pixel's fade color
 */
fun AnimationManager.setPixelFadeColor(pixel: Int, color: PreparedColorContainer): Unit =
    sectionManager.setPixelFadeColor(pixel, color)

/**
 * Set a pixel's fade color
 */
fun AnimationManager.setPixelFadeColor(pixel: Int, color: Int): Unit =
    sectionManager.setPixelFadeColor(pixel, color)

/**
 * Set multiple pixels' fade color
 */
fun AnimationManager.setPixelFadeColors(pixels: List<Int>, color: PreparedColorContainer): Unit =
    sectionManager.setPixelFadeColors(pixels, color)

/**
 * Set multiple pixels' fade color
 */
fun AnimationManager.setPixelFadeColors(pixels: List<Int>, color: Int): Unit =
    sectionManager.setPixelFadeColors(pixels, color)

/**
 * Set multiple pixels' fade color
 */
fun AnimationManager.setPixelFadeColors(pixels: IntRange, color: PreparedColorContainer): Unit =
    setPixelFadeColors(pixels.toList(), color)

/**
 * Set multiple pixels' fade color
 */
fun AnimationManager.setPixelFadeColors(pixels: IntRange, color: Int): Unit =
    setPixelFadeColors(pixels.toList(), color)


/* Set Pixel Prolonged Color */

/**
 * Set a pixel's prolonged color
 */
fun LEDStrip.setPixelProlongedColor(pixel: Int, color: PreparedColorContainer): Unit =
    colorManager.setPixelColor(pixel, color, PixelColorType.PROLONGED)

/**
 * Set a pixel's prolonged color
 */
fun LEDStrip.setPixelProlongedColor(pixel: Int, color: Int): Unit =
    colorManager.setPixelColor(pixel, color, PixelColorType.PROLONGED)

/**
 * Set multiple pixels' prolonged color
 */
fun LEDStrip.setPixelProlongedColors(pixels: List<Int>, color: PreparedColorContainer) {
    for (pixel in pixels) setPixelProlongedColor(pixel, color)
}

/**
 * Set multiple pixels' prolonged color
 */
fun LEDStrip.setPixelProlongedColors(pixels: List<Int>, color: Int) {
    for (pixel in pixels) setPixelProlongedColor(pixel, color)
}

/**
 * Set multiple pixels' prolonged color
 */
fun LEDStrip.setPixelProlongedColors(pixels: IntRange, color: PreparedColorContainer): Unit =
    setPixelProlongedColors(pixels.toList(), color)

/**
 * Set multiple pixels' prolonged color
 */
fun LEDStrip.setPixelProlongedColors(pixels: IntRange, color: Int): Unit =
    setPixelProlongedColors(pixels.toList(), color)


/**
 * Set a pixel's prolonged color
 */
fun SectionManager.setPixelProlongedColor(pixel: Int, color: PreparedColorContainer): Unit =
    stripManager.setPixelProlongedColor(getPhysicalIndex(pixel), color[pixel])

/**
 * Set a pixel's prolonged color
 */
fun SectionManager.setPixelProlongedColor(pixel: Int, color: Int): Unit =
    stripManager.setPixelProlongedColor(getPhysicalIndex(pixel), color)

/**
 * Set multiple pixels' prolonged color
 */
fun SectionManager.setPixelProlongedColors(pixels: List<Int>, color: PreparedColorContainer) {
    for (pixel in pixels) setPixelProlongedColor(pixel, color)
}

/**
 * Set multiple pixels' prolonged color
 */
fun SectionManager.setPixelProlongedColors(pixels: List<Int>, color: Int) {
    for (pixel in pixels) setPixelProlongedColor(pixel, color)
}

/**
 * Set multiple pixels' prolonged color
 */
fun SectionManager.setPixelProlongedColors(pixels: IntRange, color: PreparedColorContainer): Unit =
    setPixelProlongedColors(pixels.toList(), color)

/**
 * Set multiple pixels' prolonged color
 */
fun SectionManager.setPixelProlongedColors(pixels: IntRange, color: Int): Unit =
    setPixelProlongedColors(pixels.toList(), color)


/**
 * Set a pixel's prolonged color
 */
fun AnimationManager.setPixelProlongedColor(pixel: Int, color: PreparedColorContainer): Unit =
    sectionManager.setPixelProlongedColor(pixel, color)

/**
 * Set a pixel's prolonged color
 */
fun AnimationManager.setPixelProlongedColor(pixel: Int, color: Int): Unit =
    sectionManager.setPixelProlongedColor(pixel, color)

/**
 * Set multiple pixels' prolonged color
 */
fun AnimationManager.setPixelProlongedColors(pixels: List<Int>, color: PreparedColorContainer): Unit =
    sectionManager.setPixelProlongedColors(pixels, color)

/**
 * Set multiple pixels' prolonged color
 */
fun AnimationManager.setPixelProlongedColors(pixels: List<Int>, color: Int): Unit =
    sectionManager.setPixelProlongedColors(pixels, color)

/**
 * Set multiple pixels' prolonged color
 */
fun AnimationManager.setPixelProlongedColors(pixels: IntRange, color: PreparedColorContainer): Unit =
    setPixelProlongedColors(pixels.toList(), color)

/**
 * Set multiple pixels' prolonged color
 */
fun AnimationManager.setPixelProlongedColors(pixels: IntRange, color: Int): Unit =
    setPixelProlongedColors(pixels.toList(), color)


/* Set Pixel Temporary Color */

/**
 * Set a pixel's temporary color
 */
fun LEDStrip.setPixelTemporaryColor(pixel: Int, color: PreparedColorContainer): Unit =
    colorManager.setPixelColor(pixel, color, PixelColorType.TEMPORARY)

/**
 * Set a pixel's temporary color
 */
fun LEDStrip.setPixelTemporaryColor(pixel: Int, color: Int): Unit =
    colorManager.setPixelColor(pixel, color, PixelColorType.TEMPORARY)

/**
 * Set multiple pixels' temporary color
 */
fun LEDStrip.setPixelTemporaryColors(pixels: List<Int>, color: PreparedColorContainer) {
    for (pixel in pixels) setPixelTemporaryColor(pixel, color)
}

/**
 * Set multiple pixels' temporary color
 */
fun LEDStrip.setPixelTemporaryColors(pixels: List<Int>, color: Int) {
    for (pixel in pixels) setPixelTemporaryColor(pixel, color)
}

/**
 * Set multiple pixels' temporary color
 */
fun LEDStrip.setPixelTemporaryColors(pixels: IntRange, color: PreparedColorContainer): Unit =
    setPixelTemporaryColors(pixels.toList(), color)

/**
 * Set multiple pixels' temporary color
 */
fun LEDStrip.setPixelTemporaryColors(pixels: IntRange, color: Int): Unit =
    setPixelTemporaryColors(pixels.toList(), color)


/**
 * Set a pixel's temporary color
 */
fun SectionManager.setPixelTemporaryColor(pixel: Int, color: PreparedColorContainer): Unit =
    stripManager.setPixelTemporaryColor(getPhysicalIndex(pixel), color[pixel])

/**
 * Set a pixel's temporary color
 */
fun SectionManager.setPixelTemporaryColor(pixel: Int, color: Int): Unit =
    stripManager.setPixelTemporaryColor(getPhysicalIndex(pixel), color)

/**
 * Set multiple pixels' temporary color
 */
fun SectionManager.setPixelTemporaryColors(pixels: List<Int>, color: PreparedColorContainer) {
    for (pixel in pixels) setPixelTemporaryColor(pixel, color)
}

/**
 * Set multiple pixels' temporary color
 */
fun SectionManager.setPixelTemporaryColors(pixels: List<Int>, color: Int) {
    for (pixel in pixels) setPixelTemporaryColor(pixel, color)
}

/**
 * Set multiple pixels' temporary color
 */
fun SectionManager.setPixelTemporaryColors(pixels: IntRange, color: PreparedColorContainer): Unit =
    setPixelTemporaryColors(pixels.toList(), color)

/**
 * Set multiple pixels' temporary color
 */
fun SectionManager.setPixelTemporaryColors(pixels: IntRange, color: Int): Unit =
    setPixelTemporaryColors(pixels.toList(), color)


/**
 * Set a pixel's temporary color
 */
fun AnimationManager.setPixelTemporaryColor(pixel: Int, color: PreparedColorContainer): Unit =
    sectionManager.setPixelTemporaryColor(pixel, color)

/**
 * Set a pixel's temporary color
 */
fun AnimationManager.setPixelTemporaryColor(pixel: Int, color: Int): Unit =
    sectionManager.setPixelTemporaryColor(pixel, color)

/**
 * Set multiple pixels' temporary color
 */
fun AnimationManager.setPixelTemporaryColors(pixels: List<Int>, color: PreparedColorContainer): Unit =
    sectionManager.setPixelTemporaryColors(pixels, color)

/**
 * Set multiple pixels' temporary color
 */
fun AnimationManager.setPixelTemporaryColors(pixels: List<Int>, color: Int): Unit =
    sectionManager.setPixelTemporaryColors(pixels, color)

/**
 * Set multiple pixels' temporary color
 */
fun AnimationManager.setPixelTemporaryColors(pixels: IntRange, color: PreparedColorContainer): Unit =
    setPixelTemporaryColors(pixels.toList(), color)

/**
 * Set multiple pixels' temporary color
 */
fun AnimationManager.setPixelTemporaryColors(pixels: IntRange, color: Int): Unit =
    setPixelTemporaryColors(pixels.toList(), color)

/**
 * Set a pixel's temporary color and revert (reset its temporary color) after a delay
 */
suspend fun AnimationManager.setPixelAndRevertAfterDelay(pixel: Int, color: PreparedColorContainer, delay: Long) {
    setPixelTemporaryColor(pixel, color)
    delay(delay)
    revertPixel(pixel)
}


/* Revert Pixel */

/**
 * Revert a pixel's color (reset its temporary color)
 */
fun LEDStrip.revertPixel(pixel: Int): Unit = colorManager.revertPixel(pixel)

/**
 * Revert multiple pixels' color (reset their temporary colors)
 */
fun LEDStrip.revertPixels(pixels: List<Int>) {
    for (pixel in pixels) revertPixel(pixel)
}

/**
 * Revert multiple pixels' color (reset their temporary colors)
 */
fun LEDStrip.revertPixels(pixels: IntRange): Unit =
    revertPixels(pixels.toList())


/**
 * Revert a pixel's color (reset its temporary color)
 */
fun SectionManager.revertPixel(pixel: Int): Unit =
    stripManager.revertPixel(getPhysicalIndex(pixel))

/**
 * Revert multiple pixels' color (reset their temporary colors)
 */
fun SectionManager.revertPixels(pixels: List<Int>) {
    for (pixel in pixels) revertPixel(pixel)
}

/**
 * Revert multiple pixels' color (reset their temporary colors)
 */
fun SectionManager.revertPixels(pixels: IntRange): Unit =
    revertPixels(pixels.toList())


/**
 * Revert a pixel's color (reset its temporary color)
 */
fun AnimationManager.revertPixel(pixel: Int): Unit =
    sectionManager.revertPixel(pixel)

/**
 * Revert multiple pixels' color (reset their temporary colors)
 */
fun AnimationManager.revertPixels(pixels: List<Int>): Unit =
    sectionManager.revertPixels(pixels)

/**
 * Revert multiple pixels' color (reset their temporary colors)
 */
fun AnimationManager.revertPixels(pixels: IntRange): Unit =
    revertPixels(pixels.toList())


/* Set Strip Fade Color */

/**
 * Set the strip's fade color
 */
fun LEDStrip.setStripFadeColor(color: PreparedColorContainer) {
    for (i in pixelIndices) setPixelFadeColor(i, color)
}

/**
 * Set the strip's fade color
 */
fun LEDStrip.setStripFadeColor(color: Int) {
    for (i in pixelIndices) setPixelFadeColor(i, color)
}

/**
 * Set the section's fade color
 */
fun SectionManager.setStripFadeColor(color: PreparedColorContainer) {
    for (i in pixels.indices) setPixelFadeColor(i, color)
}

/**
 * Set the section's fade color
 */
fun SectionManager.setStripFadeColor(color: Int) {
    for (i in pixels.indices) setPixelFadeColor(i, color)
}

/**
 * Set the strip's/section's fade color
 */
fun AnimationManager.setStripFadeColor(color: PreparedColorContainer): Unit =
    sectionManager.setStripFadeColor(color)

/**
 * Set the strip's/section's fade color
 */
fun AnimationManager.setStripFadeColor(color: Int): Unit =
    sectionManager.setStripFadeColor(color)


/* Set Strip Prolonged Color */

/**
 * Set the strip's prolonged color
 */
fun LEDStrip.setStripProlongedColor(color: PreparedColorContainer) {
    for (i in pixelIndices) setPixelProlongedColor(i, color)
}

/**
 * Set the strip's prolonged color
 */
fun LEDStrip.setStripProlongedColor(color: Int) {
    for (i in pixelIndices) setPixelProlongedColor(i, color)
}

/**
 * Set the section's prolonged color
 */
fun SectionManager.setStripProlongedColor(color: PreparedColorContainer) {
    for (i in pixels.indices) setPixelProlongedColor(i, color)
}

/**
 * Set the section's prolonged color
 */
fun SectionManager.setStripProlongedColor(color: Int) {
    for (i in pixels.indices) setPixelProlongedColor(i, color)
}

/**
 * Set the strip's/section's prolonged color
 */
fun AnimationManager.setStripProlongedColor(color: PreparedColorContainer): Unit =
    sectionManager.setStripProlongedColor(color)

/**
 * Set the strip's/section's prolonged color
 */
fun AnimationManager.setStripProlongedColor(color: Int): Unit =
    sectionManager.setStripProlongedColor(color)


/* Set Strip Temporary Color */

/**
 * Set the strip's temporary color
 */
fun LEDStrip.setStripTemporaryColor(color: PreparedColorContainer) {
    for (i in pixelIndices) setPixelTemporaryColor(i, color)
}

/**
 * Set the strip's temporary color
 */
fun LEDStrip.setStripTemporaryColor(color: Int) {
    for (i in pixelIndices) setPixelTemporaryColor(i, color)
}

/**
 * Set the section's temporary color
 */
fun SectionManager.setStripTemporaryColor(color: PreparedColorContainer) {
    for (i in pixels.indices) setPixelTemporaryColor(i, color)
}

/**
 * Set the section's temporary color
 */
fun SectionManager.setStripTemporaryColor(color: Int) {
    for (i in pixels.indices) setPixelTemporaryColor(i, color)
}

/**
 * Set the strip's/section's temporary color
 */
fun AnimationManager.setStripTemporaryColor(color: PreparedColorContainer): Unit =
    sectionManager.setStripTemporaryColor(color)

/**
 * Set the strip's/section's temporary color
 */
fun AnimationManager.setStripTemporaryColor(color: Int): Unit =
    sectionManager.setStripTemporaryColor(color)

/* Clear */

/**
 * Clear the strip (set all pixels to 0)
 */
fun LEDStrip.clear() {
    for (i in pixelIndices) {
        setPixelProlongedColor(i, 0)
        setPixelFadeColor(i, -1)
        setPixelTemporaryColor(i, -1)
    }
}

/**
 * Clear the section (set all pixels to 0)
 */
fun SectionManager.clear() {
    for (i in pixels.indices) {
        setPixelProlongedColor(i, 0)
        setPixelFadeColor(i, -1)
        setPixelTemporaryColor(i, -1)
    }
}

/**
 * Clear the strip/section (set all pixels to 0)
 */
fun AnimationManager.clear(): Unit = sectionManager.clear()
