package animatedledstrip.leds.colormanagement

import animatedledstrip.colors.PreparedColorContainer
import animatedledstrip.leds.animationmanagement.AnimationManager
import animatedledstrip.leds.animationmanagement.RunningAnimationParams
import animatedledstrip.leds.sectionmanagement.SectionManager
import animatedledstrip.leds.stripmanagement.LEDStrip
import kotlinx.coroutines.delay

val LEDStripColorManager.pixelActualColorList: List<Int>
    get() = pixelColors.map { it.actualColor }

val LEDStripColorManager.pixelFadeColorList: List<Int>
    get() = pixelColors.map { it.fadeColor }

/**
 * Get the prolonged colors of all pixels as a `List<Long>`
 */
val LEDStripColorManager.pixelProlongedColorList: List<Int>
    get() = pixelColors.map { it.prolongedColor }

/**
 * Get the temporary colors of all pixels as a `List<Long>`
 */
val LEDStripColorManager.pixelTemporaryColorList: List<Int>
    get() = pixelColors.map { it.temporaryColor }

val LEDStrip.pixelActualColorList: List<Int>
    get() = colorManager.pixelActualColorList

val LEDStrip.pixelFadeColorList: List<Int>
    get() = colorManager.pixelFadeColorList

val LEDStrip.pixelProlongedColorList: List<Int>
    get() = colorManager.pixelProlongedColorList

val LEDStrip.pixelTemporaryColorList: List<Int>
    get() = colorManager.pixelTemporaryColorList


val SectionManager.pixelActualColorList: List<Int>
    get() = stripManager.pixelActualColorList.slice(getPhysicalIndex(startPixel)..getPhysicalIndex(endPixel))

val SectionManager.pixelFadeColorList: List<Int>
    get() = stripManager.pixelFadeColorList.slice(getPhysicalIndex(startPixel)..getPhysicalIndex(endPixel))

val SectionManager.pixelProlongedColorList: List<Int>
    get() = stripManager.pixelProlongedColorList.slice(getPhysicalIndex(startPixel)..getPhysicalIndex(endPixel))

val SectionManager.pixelTemporaryColorList: List<Int>
    get() = stripManager.pixelTemporaryColorList.slice(getPhysicalIndex(startPixel)..getPhysicalIndex(endPixel))


/* Set Pixel Fade Color */

fun LEDStrip.setPixelFadeColor(pixel: Int, color: PreparedColorContainer): Unit =
    colorManager.setPixelColor(pixel, color, PixelColorType.FADE)

fun LEDStrip.setPixelFadeColor(pixel: Int, color: Int): Unit =
    colorManager.setPixelColor(pixel, color, PixelColorType.FADE)

fun LEDStrip.setPixelFadeColors(pixels: List<Int>, color: PreparedColorContainer) {
    for (pixel in pixels) setPixelFadeColor(pixel, color)
}

fun LEDStrip.setPixelFadeColors(pixels: List<Int>, color: Int) {
    for (pixel in pixels) setPixelFadeColor(pixel, color)
}

fun LEDStrip.setPixelFadeColors(pixels: IntRange, color: PreparedColorContainer): Unit =
    setPixelFadeColors(pixels.toList(), color)

fun LEDStrip.setPixelFadeColors(pixels: IntRange, color: Int): Unit =
    setPixelFadeColors(pixels.toList(), color)


fun SectionManager.setPixelFadeColor(pixel: Int, color: PreparedColorContainer): Unit =
    stripManager.setPixelFadeColor(getPhysicalIndex(pixel), color)

fun SectionManager.setPixelFadeColor(pixel: Int, color: Int): Unit =
    stripManager.setPixelFadeColor(getPhysicalIndex(pixel), color)

fun SectionManager.setPixelFadeColors(pixels: List<Int>, color: PreparedColorContainer) {
    for (pixel in pixels) setPixelFadeColor(getPhysicalIndex(pixel), color)
}

fun SectionManager.setPixelFadeColors(pixels: List<Int>, color: Int) {
    for (pixel in pixels) setPixelFadeColor(getPhysicalIndex(pixel), color)
}

fun SectionManager.setPixelFadeColors(pixels: IntRange, color: PreparedColorContainer): Unit =
    setPixelFadeColors(pixels.toList(), color)

fun SectionManager.setPixelFadeColors(pixels: IntRange, color: Int): Unit =
    setPixelFadeColors(pixels.toList(), color)


fun AnimationManager.setPixelFadeColor(pixel: Int, color: PreparedColorContainer): Unit =
    sectionManager.setPixelFadeColor(pixel, color)

fun AnimationManager.setPixelFadeColor(pixel: Int, color: Int): Unit =
    sectionManager.setPixelFadeColor(pixel, color)

fun AnimationManager.setPixelFadeColors(pixels: List<Int>, color: PreparedColorContainer): Unit =
    sectionManager.setPixelFadeColors(pixels, color)

fun AnimationManager.setPixelFadeColors(pixels: List<Int>, color: Int): Unit =
    sectionManager.setPixelFadeColors(pixels, color)

fun AnimationManager.setPixelFadeColors(pixels: IntRange, color: PreparedColorContainer): Unit =
    setPixelFadeColors(pixels.toList(), color)

fun AnimationManager.setPixelFadeColors(pixels: IntRange, color: Int): Unit =
    setPixelFadeColors(pixels.toList(), color)


/* Set Pixel Prolonged Color */

fun LEDStrip.setPixelProlongedColor(pixel: Int, color: PreparedColorContainer): Unit =
    colorManager.setPixelColor(pixel, color, PixelColorType.PROLONGED)

fun LEDStrip.setPixelProlongedColor(pixel: Int, color: Int): Unit =
    colorManager.setPixelColor(pixel, color, PixelColorType.PROLONGED)

fun LEDStrip.setPixelProlongedColors(pixels: List<Int>, color: PreparedColorContainer) {
    for (pixel in pixels) setPixelProlongedColor(pixel, color)
}

fun LEDStrip.setPixelProlongedColors(pixels: List<Int>, color: Int) {
    for (pixel in pixels) setPixelProlongedColor(pixel, color)
}

fun LEDStrip.setPixelProlongedColors(pixels: IntRange, color: PreparedColorContainer): Unit =
    setPixelProlongedColors(pixels.toList(), color)

fun LEDStrip.setPixelProlongedColors(pixels: IntRange, color: Int): Unit =
    setPixelProlongedColors(pixels.toList(), color)


fun SectionManager.setPixelProlongedColor(pixel: Int, color: PreparedColorContainer): Unit =
    stripManager.setPixelProlongedColor(getPhysicalIndex(pixel), color)

fun SectionManager.setPixelProlongedColor(pixel: Int, color: Int): Unit =
    stripManager.setPixelProlongedColor(getPhysicalIndex(pixel), color)

fun SectionManager.setPixelProlongedColors(pixels: List<Int>, color: PreparedColorContainer) {
    for (pixel in pixels) setPixelProlongedColor(getPhysicalIndex(pixel), color)
}

fun SectionManager.setPixelProlongedColors(pixels: List<Int>, color: Int) {
    for (pixel in pixels) setPixelProlongedColor(getPhysicalIndex(pixel), color)
}

fun SectionManager.setPixelProlongedColors(pixels: IntRange, color: PreparedColorContainer): Unit =
    setPixelProlongedColors(pixels.toList(), color)

fun SectionManager.setPixelProlongedColors(pixels: IntRange, color: Int): Unit =
    setPixelProlongedColors(pixels.toList(), color)


fun AnimationManager.setPixelProlongedColor(pixel: Int, color: PreparedColorContainer): Unit =
    sectionManager.setPixelProlongedColor(pixel, color)

fun AnimationManager.setPixelProlongedColor(pixel: Int, color: Int): Unit =
    sectionManager.setPixelProlongedColor(pixel, color)

fun AnimationManager.setPixelProlongedColors(pixels: List<Int>, color: PreparedColorContainer): Unit =
    sectionManager.setPixelProlongedColors(pixels, color)

fun AnimationManager.setPixelProlongedColors(pixels: List<Int>, color: Int): Unit =
    sectionManager.setPixelProlongedColors(pixels, color)

fun AnimationManager.setPixelProlongedColors(pixels: IntRange, color: PreparedColorContainer): Unit =
    setPixelProlongedColors(pixels.toList(), color)

fun AnimationManager.setPixelProlongedColors(pixels: IntRange, color: Int): Unit =
    setPixelProlongedColors(pixels.toList(), color)


/* Set Pixel Temporary Color */

fun LEDStrip.setPixelTemporaryColor(pixel: Int, color: PreparedColorContainer): Unit =
    colorManager.setPixelColor(pixel, color, PixelColorType.TEMPORARY)

fun LEDStrip.setPixelTemporaryColor(pixel: Int, color: Int): Unit =
    colorManager.setPixelColor(pixel, color, PixelColorType.TEMPORARY)

fun LEDStrip.setPixelTemporaryColors(pixels: List<Int>, color: PreparedColorContainer) {
    for (pixel in pixels) setPixelTemporaryColor(pixel, color)
}

fun LEDStrip.setPixelTemporaryColors(pixels: List<Int>, color: Int) {
    for (pixel in pixels) setPixelTemporaryColor(pixel, color)
}

fun LEDStrip.setPixelTemporaryColors(pixels: IntRange, color: PreparedColorContainer): Unit =
    setPixelTemporaryColors(pixels.toList(), color)

fun LEDStrip.setPixelTemporaryColors(pixels: IntRange, color: Int): Unit =
    setPixelTemporaryColors(pixels.toList(), color)


fun SectionManager.setPixelTemporaryColor(pixel: Int, color: PreparedColorContainer): Unit =
    stripManager.setPixelTemporaryColor(getPhysicalIndex(pixel), color)

fun SectionManager.setPixelTemporaryColor(pixel: Int, color: Int): Unit =
    stripManager.setPixelTemporaryColor(getPhysicalIndex(pixel), color)

fun SectionManager.setPixelTemporaryColors(pixels: List<Int>, color: PreparedColorContainer) {
    for (pixel in pixels) setPixelTemporaryColor(getPhysicalIndex(pixel), color)
}

fun SectionManager.setPixelTemporaryColors(pixels: List<Int>, color: Int) {
    for (pixel in pixels) setPixelTemporaryColor(getPhysicalIndex(pixel), color)
}

fun SectionManager.setPixelTemporaryColors(pixels: IntRange, color: PreparedColorContainer): Unit =
    setPixelTemporaryColors(pixels.toList(), color)

fun SectionManager.setPixelTemporaryColors(pixels: IntRange, color: Int): Unit =
    setPixelTemporaryColors(pixels.toList(), color)


fun AnimationManager.setPixelTemporaryColor(pixel: Int, color: PreparedColorContainer): Unit =
    sectionManager.setPixelTemporaryColor(pixel, color)

fun AnimationManager.setPixelTemporaryColor(pixel: Int, color: Int): Unit =
    sectionManager.setPixelTemporaryColor(pixel, color)

fun AnimationManager.setPixelTemporaryColors(pixels: List<Int>, color: PreparedColorContainer): Unit =
    sectionManager.setPixelTemporaryColors(pixels, color)

fun AnimationManager.setPixelTemporaryColors(pixels: List<Int>, color: Int): Unit =
    sectionManager.setPixelTemporaryColors(pixels, color)

fun AnimationManager.setPixelTemporaryColors(pixels: IntRange, color: PreparedColorContainer): Unit =
    setPixelTemporaryColors(pixels.toList(), color)

fun AnimationManager.setPixelTemporaryColors(pixels: IntRange, color: Int): Unit =
    setPixelTemporaryColors(pixels.toList(), color)

suspend fun AnimationManager.setPixelAndRevertAfterDelay(pixel: Int, color: PreparedColorContainer, delay: Long) {
    setPixelTemporaryColor(pixel, color)
    delay(delay)
    revertPixel(pixel)
}


/* Revert Pixel */

fun LEDStrip.revertPixel(pixel: Int): Unit = colorManager.revertPixel(pixel)

fun LEDStrip.revertPixels(pixels: List<Int>) {
    for (pixel in pixels) revertPixel(pixel)
}

fun LEDStrip.revertPixels(pixels: IntRange): Unit =
    revertPixels(pixels.toList())


fun SectionManager.revertPixel(pixel: Int): Unit =
    stripManager.revertPixel(getPhysicalIndex(pixel))

fun SectionManager.revertPixels(pixels: List<Int>) {
    for (pixel in pixels) revertPixel(getPhysicalIndex(pixel))
}

fun SectionManager.revertPixels(pixels: IntRange): Unit =
    revertPixels(pixels.toList())


fun AnimationManager.revertPixel(pixel: Int): Unit =
    sectionManager.revertPixel(pixel)

fun AnimationManager.revertPixels(pixels: List<Int>): Unit =
    sectionManager.revertPixels(pixels)

fun AnimationManager.revertPixels(pixels: IntRange): Unit =
    revertPixels(pixels.toList())


/* Set Strip Fade Color */

fun LEDStrip.setStripFadeColor(color: PreparedColorContainer) {
    for (i in validIndices) setPixelFadeColor(i, color)
}

fun LEDStrip.setStripFadeColor(color: Int) {
    for (i in validIndices) setPixelFadeColor(i, color)
}

fun SectionManager.setStripFadeColor(color: PreparedColorContainer) {
    for (i in validIndices) setPixelFadeColor(i, color)
}

fun SectionManager.setStripFadeColor(color: Int) {
    for (i in validIndices) setPixelFadeColor(i, color)
}

fun AnimationManager.setStripFadeColor(color: PreparedColorContainer): Unit = sectionManager.setStripFadeColor(color)

fun AnimationManager.setStripFadeColor(color: Int): Unit = sectionManager.setStripFadeColor(color)


/* Set Strip Prolonged Color */

fun LEDStrip.setStripProlongedColor(color: PreparedColorContainer) {
    for (i in validIndices) setPixelProlongedColor(i, color)
}

fun LEDStrip.setStripProlongedColor(color: Int) {
    for (i in validIndices) setPixelProlongedColor(i, color)
}

fun SectionManager.setStripProlongedColor(color: PreparedColorContainer) {
    for (i in validIndices) setPixelProlongedColor(i, color)
}

fun SectionManager.setStripProlongedColor(color: Int) {
    for (i in validIndices) setPixelProlongedColor(i, color)
}

fun AnimationManager.setStripProlongedColor(color: PreparedColorContainer): Unit =
    sectionManager.setStripProlongedColor(color)

fun AnimationManager.setStripProlongedColor(color: Int): Unit = sectionManager.setStripProlongedColor(color)


/* Set Strip Temporary Color */

fun LEDStrip.setStripTemporaryColor(color: PreparedColorContainer) {
    for (i in validIndices) setPixelTemporaryColor(i, color)
}

fun LEDStrip.setStripTemporaryColor(color: Int) {
    for (i in validIndices) setPixelTemporaryColor(i, color)
}

fun SectionManager.setStripTemporaryColor(color: PreparedColorContainer) {
    for (i in validIndices) setPixelTemporaryColor(i, color)
}

fun SectionManager.setStripTemporaryColor(color: Int) {
    for (i in validIndices) setPixelTemporaryColor(i, color)
}

fun AnimationManager.setStripTemporaryColor(color: PreparedColorContainer): Unit =
    sectionManager.setStripTemporaryColor(color)

fun AnimationManager.setStripTemporaryColor(color: Int): Unit = sectionManager.setStripTemporaryColor(color)

/* Clear */

fun LEDStrip.clear() {
    for (i in validIndices) {
        setPixelProlongedColor(i, 0)
        setPixelFadeColor(i, -1)
        setPixelTemporaryColor(i, -1)
    }
}

fun SectionManager.clear() {
    for (i in validIndices) {
        setPixelProlongedColor(i, 0)
        setPixelFadeColor(i, -1)
        setPixelTemporaryColor(i, -1)
    }
}

fun AnimationManager.clear(): Unit = sectionManager.clear()

/* Get Pixel Actual Color */

fun LEDStrip.getPixelActualColor(pixel: Int): Int =
    colorManager.getPixelColor(pixel, PixelColorType.ACTUAL)

fun LEDStrip.getPixelActualColorOrNull(pixel: Int): Int? =
    try {
        getPixelActualColor(pixel)
    } catch (e: IllegalArgumentException) {
        null
    }

fun SectionManager.getPixelActualColor(pixel: Int): Int =
    stripManager.getPixelActualColor(getPhysicalIndex(pixel))

fun SectionManager.getPixelActualColorOrNull(pixel: Int): Int? =
    stripManager.getPixelActualColorOrNull(getPhysicalIndex(pixel))

fun AnimationManager.getPixelActualColor(pixel: Int): Int =
    sectionManager.getPixelActualColor(pixel)

fun AnimationManager.getPixelActualColorOrNull(pixel: Int): Int? =
    sectionManager.getPixelActualColorOrNull(pixel)


/* Get Pixel Fade Color */

fun LEDStrip.getPixelFadeColor(pixel: Int): Int =
    colorManager.getPixelColor(pixel, PixelColorType.FADE)

fun LEDStrip.getPixelFadeColorOrNull(pixel: Int): Int? =
    try {
        getPixelFadeColor(pixel)
    } catch (e: IllegalArgumentException) {
        null
    }

fun SectionManager.getPixelFadeColor(pixel: Int): Int =
    stripManager.getPixelFadeColor(getPhysicalIndex(pixel))

fun SectionManager.getPixelFadeColorOrNull(pixel: Int): Int? =
    stripManager.getPixelFadeColorOrNull(getPhysicalIndex(pixel))

fun AnimationManager.getPixelFadeColor(pixel: Int): Int =
    sectionManager.getPixelFadeColor(pixel)

fun AnimationManager.getPixelFadeColorOrNull(pixel: Int): Int? =
    sectionManager.getPixelFadeColorOrNull(pixel)


/* Get Pixel Prolonged Color */

fun LEDStrip.getPixelProlongedColor(pixel: Int): Int =
    colorManager.getPixelColor(pixel, PixelColorType.PROLONGED)

fun LEDStrip.getPixelProlongedColorOrNull(pixel: Int): Int? =
    try {
        getPixelProlongedColor(pixel)
    } catch (e: IllegalArgumentException) {
        null
    }

fun SectionManager.getPixelProlongedColor(pixel: Int): Int =
    stripManager.getPixelProlongedColor(getPhysicalIndex(pixel))

fun SectionManager.getPixelProlongedColorOrNull(pixel: Int): Int? =
    stripManager.getPixelProlongedColorOrNull(getPhysicalIndex(pixel))

fun AnimationManager.getPixelProlongedColor(pixel: Int): Int =
    sectionManager.getPixelProlongedColor(pixel)

fun AnimationManager.getPixelProlongedColorOrNull(pixel: Int): Int? =
    sectionManager.getPixelProlongedColorOrNull(pixel)


/* Get Pixel Temporary Color */

fun LEDStrip.getPixelTemporaryColor(pixel: Int): Int =
    colorManager.getPixelColor(pixel, PixelColorType.TEMPORARY)

fun LEDStrip.getPixelTemporaryColorOrNull(pixel: Int): Int? =
    try {
        getPixelTemporaryColor(pixel)
    } catch (e: IllegalArgumentException) {
        null
    }

fun SectionManager.getPixelTemporaryColor(pixel: Int): Int =
    stripManager.getPixelTemporaryColor(getPhysicalIndex(pixel))

fun SectionManager.getPixelTemporaryColorOrNull(pixel: Int): Int? =
    stripManager.getPixelTemporaryColorOrNull(getPhysicalIndex(pixel))

fun AnimationManager.getPixelTemporaryColor(pixel: Int): Int =
    sectionManager.getPixelTemporaryColor(pixel)

fun AnimationManager.getPixelTemporaryColorOrNull(pixel: Int): Int? =
    sectionManager.getPixelTemporaryColorOrNull(pixel)

fun RunningAnimationParams.randomColor(): PreparedColorContainer = colors.random()