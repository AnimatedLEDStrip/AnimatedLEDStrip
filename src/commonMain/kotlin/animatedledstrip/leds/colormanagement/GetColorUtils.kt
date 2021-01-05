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
import animatedledstrip.leds.animationmanagement.RunningAnimationParams
import animatedledstrip.leds.sectionmanagement.SectionManager
import animatedledstrip.leds.stripmanagement.LEDStrip

/**
 * A list of the actual colors for the strip
 */
val LEDStripColorManager.pixelActualColorList: List<Int>
    get() = pixelColors.map { it.actualColor }

/**
 * A list of the fade colors for the strip
 */
val LEDStripColorManager.pixelFadeColorList: List<Int>
    get() = pixelColors.map { it.fadeColor }

/**
 * A list of the prolonged colors for the strip
 */
val LEDStripColorManager.pixelProlongedColorList: List<Int>
    get() = pixelColors.map { it.prolongedColor }

/**
 * A list of the temporary colors for the strip
 */
val LEDStripColorManager.pixelTemporaryColorList: List<Int>
    get() = pixelColors.map { it.temporaryColor }

/**
 * A list of the actual colors for the strip
 */
val LEDStrip.pixelActualColorList: List<Int>
    get() = colorManager.pixelActualColorList

/**
 * A list of the fade colors for the strip
 */
val LEDStrip.pixelFadeColorList: List<Int>
    get() = colorManager.pixelFadeColorList

/**
 * A list of the prolonged colors for the strip
 */
val LEDStrip.pixelProlongedColorList: List<Int>
    get() = colorManager.pixelProlongedColorList

/**
 * A list of the temporary colors for the strip
 */
val LEDStrip.pixelTemporaryColorList: List<Int>
    get() = colorManager.pixelTemporaryColorList

/**
 * A list of the actual colors for the section
 */
val SectionManager.pixelActualColorList: List<Int>
    get() = stripManager.pixelActualColorList.slice(startPixel..endPixel)

/**
 * A list of the fade colors for the section
 */
val SectionManager.pixelFadeColorList: List<Int>
    get() = stripManager.pixelFadeColorList.slice(startPixel..endPixel)

/**
 * A list of the prolonged colors for the section
 */
val SectionManager.pixelProlongedColorList: List<Int>
    get() = stripManager.pixelProlongedColorList.slice(startPixel..endPixel)

/**
 * A list of the temporary colors for the section
 */
val SectionManager.pixelTemporaryColorList: List<Int>
    get() = stripManager.pixelTemporaryColorList.slice(startPixel..endPixel)

/**
 * @return A [CurrentStripColor] with the strip's current state that can be sent to a client
 */
fun LEDStrip.currentStripColor(): CurrentStripColor = CurrentStripColor(pixelActualColorList)

/* Get Pixel Actual Color */

/**
 * @return The pixel's actual color
 */
fun LEDStrip.getPixelActualColor(pixel: Int): Int =
    colorManager.getPixelColor(pixel, PixelColorType.ACTUAL)

/**
 * @return The pixel's actual color, or null if the index was invalid
 */
fun LEDStrip.getPixelActualColorOrNull(pixel: Int): Int? =
    try {
        getPixelActualColor(pixel)
    } catch (e: IllegalArgumentException) {
        null
    }

/**
 * @return The pixel's actual color
 */
fun SectionManager.getPixelActualColor(pixel: Int): Int =
    stripManager.getPixelActualColor(getPhysicalIndex(pixel))

/**
 * @return The pixel's actual color, or null if the index was invalid
 */
fun SectionManager.getPixelActualColorOrNull(pixel: Int): Int? =
    stripManager.getPixelActualColorOrNull(getPhysicalIndex(pixel))

/**
 * @return The pixel's actual color
 */
fun AnimationManager.getPixelActualColor(pixel: Int): Int =
    sectionManager.getPixelActualColor(pixel)

/**
 * @return The pixel's actual color, or null if the index was invalid
 */
fun AnimationManager.getPixelActualColorOrNull(pixel: Int): Int? =
    sectionManager.getPixelActualColorOrNull(pixel)


/* Get Pixel Fade Color */

/**
 * @return The pixel's fade color
 */
fun LEDStrip.getPixelFadeColor(pixel: Int): Int =
    colorManager.getPixelColor(pixel, PixelColorType.FADE)

/**
 * @return The pixel's fade color, or null if the index was invalid
 */
fun LEDStrip.getPixelFadeColorOrNull(pixel: Int): Int? =
    try {
        getPixelFadeColor(pixel)
    } catch (e: IllegalArgumentException) {
        null
    }

/**
 * @return The pixel's fade color
 */
fun SectionManager.getPixelFadeColor(pixel: Int): Int =
    stripManager.getPixelFadeColor(getPhysicalIndex(pixel))

/**
 * @return The pixel's fade color, or null if the index was invalid
 */
fun SectionManager.getPixelFadeColorOrNull(pixel: Int): Int? =
    stripManager.getPixelFadeColorOrNull(getPhysicalIndex(pixel))

/**
 * @return The pixel's fade color
 */
fun AnimationManager.getPixelFadeColor(pixel: Int): Int =
    sectionManager.getPixelFadeColor(pixel)

/**
 * @return The pixel's fade color, or null if the index was invalid
 */
fun AnimationManager.getPixelFadeColorOrNull(pixel: Int): Int? =
    sectionManager.getPixelFadeColorOrNull(pixel)


/* Get Pixel Prolonged Color */

/**
 * @return The pixel's prolonged color
 */
fun LEDStrip.getPixelProlongedColor(pixel: Int): Int =
    colorManager.getPixelColor(pixel, PixelColorType.PROLONGED)

/**
 * @return The pixel's prolonged color, or null if the index was invalid
 */
fun LEDStrip.getPixelProlongedColorOrNull(pixel: Int): Int? =
    try {
        getPixelProlongedColor(pixel)
    } catch (e: IllegalArgumentException) {
        null
    }

/**
 * @return The pixel's prolonged color
 */
fun SectionManager.getPixelProlongedColor(pixel: Int): Int =
    stripManager.getPixelProlongedColor(getPhysicalIndex(pixel))

/**
 * @return The pixel's prolonged color, or null if the index was invalid
 */
fun SectionManager.getPixelProlongedColorOrNull(pixel: Int): Int? =
    stripManager.getPixelProlongedColorOrNull(getPhysicalIndex(pixel))

/**
 * @return The pixel's prolonged color
 */
fun AnimationManager.getPixelProlongedColor(pixel: Int): Int =
    sectionManager.getPixelProlongedColor(pixel)

/**
 * @return The pixel's prolonged color, or null if the index was invalid
 */
fun AnimationManager.getPixelProlongedColorOrNull(pixel: Int): Int? =
    sectionManager.getPixelProlongedColorOrNull(pixel)


/* Get Pixel Temporary Color */

/**
 * @return The pixel's temporary color
 */
fun LEDStrip.getPixelTemporaryColor(pixel: Int): Int =
    colorManager.getPixelColor(pixel, PixelColorType.TEMPORARY)

/**
 * @return The pixel's temporary color, or null if the index was invalid
 */
fun LEDStrip.getPixelTemporaryColorOrNull(pixel: Int): Int? =
    try {
        getPixelTemporaryColor(pixel)
    } catch (e: IllegalArgumentException) {
        null
    }

/**
 * @return The pixel's temporary color
 */
fun SectionManager.getPixelTemporaryColor(pixel: Int): Int =
    stripManager.getPixelTemporaryColor(getPhysicalIndex(pixel))

/**
 * @return The pixel's temporary color, or null if the index was invalid
 */
fun SectionManager.getPixelTemporaryColorOrNull(pixel: Int): Int? =
    stripManager.getPixelTemporaryColorOrNull(getPhysicalIndex(pixel))

/**
 * @return The pixel's temporary color
 */
fun AnimationManager.getPixelTemporaryColor(pixel: Int): Int =
    sectionManager.getPixelTemporaryColor(pixel)

/**
 * @return The pixel's temporary color, or null if the index was invalid
 */
fun AnimationManager.getPixelTemporaryColorOrNull(pixel: Int): Int? =
    sectionManager.getPixelTemporaryColorOrNull(pixel)

/**
 * @return A random color from `colors`
 */
fun RunningAnimationParams.randomColor(): PreparedColorContainer = colors.random()
