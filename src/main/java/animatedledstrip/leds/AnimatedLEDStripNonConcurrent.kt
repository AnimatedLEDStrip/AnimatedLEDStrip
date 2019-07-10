package animatedledstrip.leds

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
import animatedledstrip.colors.ccpresets.CCBlack
import animatedledstrip.leds.sections.SectionableLEDStrip
import animatedledstrip.utils.blend
import animatedledstrip.utils.delayBlocking
import org.pmw.tinylog.Logger


/**
 * A subclass of [LEDStripNonConcurrent] adding animations
 *
 * @param numLEDs Number of LEDs in the strip
 */
abstract class AnimatedLEDStripNonConcurrent(numLEDs: Int) :
        LEDStripNonConcurrent(numLEDs), AnimatedLEDStripInterface, SectionableLEDStrip {


    /**
     * Array used for shuffle animation.
     */
    private var shuffleArray = mutableListOf<Int>()

    init {
        for (i in 0 until numLEDs) shuffleArray.add(i)      // Initialize shuffleArray
    }

    /**
     * Run an animation.
     *
     * @param animation An [AnimationData] instance with details about the
     * animation to run
     */
    override fun run(animation: AnimationData) {
        animation.endPixel = when (animation.endPixel) {
            0 -> numLEDs - 1
            else -> animation.endPixel
        }

        animation.pCols = mutableListOf()
        animation.colors.forEach {
            animation.pCols.add(it.prepare(animation.endPixel - animation.startPixel + 1,
                    leadingZeros = animation.startPixel))
        }

        for (i in animation.colors.size..(animationInfoMap[animation.animation]?.numColors ?: 0)) {
            animation.pCols.add(CCBlack.prepare(animation.endPixel - animation.startPixel + 1,
                    leadingZeros = animation.startPixel))
        }

        when (animation.animation) {
            Animation.ALTERNATE -> alternate(animation)
            Animation.BOUNCETOCOLOR -> bounceToColor(animation)
            Animation.COLOR -> setStripColor(animation.pCols[0])
            Animation.MULTICOLOR -> setStripColor(animation.pCols[0])
            Animation.MULTIPIXELRUN -> multiPixelRun(animation)
            Animation.MULTIPIXELRUNTOCOLOR -> multiPixelRunToColor(animation)
            Animation.PIXELRUN -> pixelRun(animation)
            Animation.PIXELRUNWITHTRAIL -> pixelRunWithTrail(animation)
            Animation.SMOOTHCHASE -> smoothChase(animation)
            Animation.SPARKLE -> sparkle(animation)
            Animation.SPARKLETOCOLOR -> sparkleToColor(animation)
            Animation.STACK -> stack(animation)
            Animation.WIPE -> wipe(animation)
            else -> Logger.warn("Animation ${animation.animation} not supported by AnimatedLEDStripNonConcurrent")
        }
    }


    /**
     * Runs an Alternate animation.
     *
     * Strip alternates between `pCols[0]` and `pCols[1]` at the specified rate (delay between changes).
     */
    private val alternate = { animation: AnimationData ->
        setSectionColor(animation.startPixel, animation.endPixel, animation.pCols[0])
        delayBlocking(animation.delay)
        setSectionColor(animation.startPixel, animation.endPixel, animation.pCols[1])
        delayBlocking(animation.delay)
    }


    /**
     * Runs a Bounce to Color animation.
     *
     * Pixel 'bounces' back and forth, leaving behind a pixel set to `pCols[0]`
     * at each end like Stack, eventually ending in the middle.
     */
    @NonRepetitive
    private val bounceToColor = { animation: AnimationData ->
        for (i in 0..((animation.endPixel - animation.startPixel) / 2)) {
            for (j in (animation.startPixel + i)..(animation.endPixel - i)) {
                setPixelColor(j, animation.pCols[0])
                show()
                delayBlocking(animation.delay)
                setPixelColor(j, CCBlack)
                show()
            }
            setPixelColor(animation.endPixel - i, animation.pCols[0])
            for (j in animation.endPixel - i - 1 downTo (i + animation.startPixel)) {
                setPixelColor(j, animation.pCols[0])
                show()
                delayBlocking(animation.delay)
                setPixelColor(j, CCBlack)
                show()
            }
            setPixelColor(i, animation.pCols[0])
            show()
        }
        if ((animation.endPixel - animation.startPixel) % 2 == 1) {
            setPixelColor((animation.endPixel - animation.startPixel) / 2 + animation.startPixel, animation.pCols[0])
            show()
        }
    }


    /**
     * Runs a Multi-Pixel Run animation.
     *
     * Similar to Pixel Run but with multiple LEDs at a specified spacing.
     */
    private val multiPixelRun = { animation: AnimationData ->
        when (animation.direction) {
            Direction.BACKWARD -> for (q in 0 until animation.spacing) {
                setStripColor(animation.pCols[1])
                for (i in animation.startPixel..animation.endPixel step animation.spacing) {
                    if (i + (-(q - (animation.spacing - 1))) > animation.endPixel) continue
                    setPixelColor(
                            i + (-(q - (animation.spacing - 1))),
                            animation.pCols[0]
                    )
                }
                show()
                delayBlocking(animation.delay)
                for (i in animation.startPixel..animation.endPixel step animation.spacing) {
                    if (i + (-(q - (animation.spacing - 1))) > animation.endPixel) continue
                    setPixelColor(
                            i + (-(q - (animation.spacing - 1))),
                            animation.pCols[1]
                    )
                }
                show()
            }
            Direction.FORWARD -> for (q in animation.spacing - 1 downTo 0) {
                setStripColor(animation.pCols[1])
                for (i in animation.startPixel..animation.endPixel step animation.spacing) {
                    if (i + (-(q - (animation.spacing - 1))) > animation.endPixel) continue
                    setPixelColor(
                            i + (-(q - (animation.spacing - 1))),
                            animation.pCols[0]
                    )
                }
                show()
                delayBlocking(animation.delay)
                for (i in animation.startPixel..animation.endPixel step animation.spacing) {
                    if (i + (-(q - (animation.spacing - 1))) > animation.endPixel) continue
                    setPixelColor(
                            i + (-(q - (animation.spacing - 1))),
                            animation.pCols[1]
                    )
                }
                show()
            }
        }
    }


    /**
     * Runs a Multi-Pixel Run To Color animation.
     *
     * Similar to Multi-Pixel Run but LEDs do not revert back to their original
     * color.
     */
    @NonRepetitive
    private val multiPixelRunToColor = { animation: AnimationData ->
        when (animation.direction) {
            Direction.BACKWARD -> for (q in 0 until animation.spacing) {
                for (i in animation.startPixel..animation.endPixel step animation.spacing) {
                    if (i + (-(q - (animation.spacing - 1))) > animation.endPixel) continue
                    setPixelColor(
                            i + (-(q - (animation.spacing - 1))),
                            animation.pCols[0]
                    )
                }
                show()
                delayBlocking(animation.delay)
            }
            Direction.FORWARD -> for (q in animation.spacing - 1 downTo 0) {
                for (i in animation.startPixel..animation.endPixel step animation.spacing) {
                    if (i + (-(q - (animation.spacing - 1))) > animation.endPixel) continue
                    setPixelColor(
                            i + (-(q - (animation.spacing - 1))),
                            animation.pCols[0]
                    )
                }
                show()
                delayBlocking(animation.delay)
            }
        }
    }


    /**
     * Runs a Pixel Run animation.
     *
     * The strip is set to `pCols[0]`, then a pixel 'runs' along the strip.
     * Similar to Multi-Pixel Run but with only one pixel.
     */
    private val pixelRun = { animation: AnimationData ->
        setStripColor(animation.pCols[1])
        when (animation.direction) {
            Direction.FORWARD -> for (q in 0 until ledStrip.numLEDs) {
                setPixelColor(q, animation.pCols[0])
                show()
                delayBlocking(animation.delay)
                setPixelColor(q, animation.pCols[1])
                show()
            }
            Direction.BACKWARD -> for (q in ledStrip.numLEDs - 1 downTo 0) {
                setPixelColor(q, animation.pCols[0])
                show()
                delayBlocking(animation.delay)
                setPixelColor(q, animation.pCols[1])
                show()
            }
        }
    }


    /**
     * Runs a Pixel Run with Trail animation.
     *
     * Like a Pixel Run animation, but the 'running' pixel has a trail behind it
     * where the pixels fade from `pCols[1]` to `pCols[0]`. Note: the end of the strip
     * might remain lit with the tail after the animation completes unless if
     * another Pixel Run with Trail is run.
     */
    private val pixelRunWithTrail = { animation: AnimationData ->
        when (animation.direction) {
            Direction.FORWARD -> for (q in animation.startPixel..animation.endPixel) {
                for (i in animation.startPixel until animation.endPixel) {
                    setPixelColor(i, blend(getPixelColor(i), animation.pCols[1].color, 60))
                }
                setPixelColor(q, animation.pCols[0])
                show()
                delayBlocking(animation.delay)
            }
            Direction.BACKWARD -> for (q in animation.endPixel downTo animation.startPixel) {
                for (i in animation.startPixel until animation.endPixel) {
                    setPixelColor(i, blend(getPixelColor(i), animation.pCols[1].color, 60))
                }
                setPixelColor(q, animation.pCols[0])
                show()
                delayBlocking(animation.delay)
            }
        }
    }


    /**
     * TODO: Update
     * Runs a Smooth Chase animation.
     *
     * The [colorsFromPalette] function is used to create a collection of colors
     * for the strip:
     * *The palette colors are spread out along the strip at approximately equal
     * intervals. All pixels between these 'pure' pixels are a blend of the
     * colors of the two nearest pure pixels. The blend ratio is determined by the
     * location of the pixel relative to the nearest pure pixels.*
     *
     * The collection created, `palette2`, is a map of integers to `ColorContainer`s
     * where each integer is a pixel index. Each pixel is set to `palette2[i]`,
     * where `i` is the pixel index. Then, if the direction is [Direction].`FORWARD`,
     * each pixel is set to `palette2[i + 1]`, then `palette[i + 2]`, etc.
     * to create the illusion that the animation is 'moving'. If the direction is
     * [Direction].`BACKWARD`, the same happens but with indices `i`, `i-1`, `i-2`, etc.
     * The index is found with `(i + a) % s`, where `i` is the pixel index, `a` is the
     * offset for this iteration and `s` is the number of pixels in the strip.
     */
    @Suppress("KDocUnresolvedReference")
    private val smoothChase = { animation: AnimationData ->
        when (animation.direction) {
            Direction.FORWARD -> for (m in animation.startPixel..animation.endPixel) {
                setStripColorWithOffset(animation.pCols[0], m - animation.startPixel)
                show()
                delayBlocking(animation.delay)
            }
            Direction.BACKWARD -> for (m in animation.endPixel downTo animation.startPixel) {
                setStripColorWithOffset(animation.pCols[0], m - animation.startPixel)
                show()
                delayBlocking(animation.delay)
            }
        }
    }


    /**
     * Runs a Sparkle animation.
     *
     * Each LED is changed to `pCols[0]` for delay milliseconds before reverting
     * to its original color. At the beginning, shuffleArray is shuffled, then
     * the LEDs are sparkled in the order given in shuffleArray.
     */
    private val sparkle = { animation: AnimationData ->
        var originalColor: Long
        shuffleArray.shuffle()
        for (i in animation.startPixel..animation.endPixel) {
            originalColor = getPixelColor(shuffleArray[i])
            setPixelColor(shuffleArray[i], animation.pCols[0])
            show()
            delayBlocking(animation.delay)
            setPixelColor(shuffleArray[i], originalColor)
            show()
        }
    }


    /**
     * Runs a Sparkle To Color animation.
     *
     * Very similar to the Sparkle animation, but the LEDs are not reverted to their
     * original color after the sparkle. At the beginning, `shuffleArray` is shuffled, then
     * the LEDs are sparkled in the order given in `shuffleArray`.
     */
    private val sparkleToColor = { animation: AnimationData ->
        shuffleArray.shuffle()
        for (i in animation.startPixel..animation.endPixel) {
            setPixelColor(shuffleArray[i], animation.pCols[0])
            show()
            delayBlocking(animation.delay)
        }
    }


    /**
     * TODO (Katie)
     */
    private val stack = { animation: AnimationData ->
        when (animation.direction) {
            Direction.FORWARD -> for (q in animation.endPixel downTo animation.startPixel) {
                var originalColor: Long
                for (i in animation.startPixel until q) {
                    originalColor = getPixelColor(i)
                    setPixelColor(i, animation.pCols[0])
                    show()
                    delayBlocking(animation.delay)
                    setPixelColor(i, originalColor)
                }
                setPixelColor(q, animation.pCols[0])
                show()
            }
            Direction.BACKWARD -> for (q in animation.startPixel..animation.endPixel) {
                var originalColor: Long
                for (i in animation.endPixel downTo q) {
                    originalColor = getPixelColor(i)
                    setPixelColor(i, animation.pCols[0])
                    show()
                    delayBlocking(animation.delay)
                    setPixelColor(i, originalColor)
                }
                setPixelColor(q, animation.pCols[0])
                show()
            }
        }
    }


    /**
     * Runs a Wipe animation.
     *
     * Similar to a Pixel Run animation, but the pixels do not revert to their
     * original color.
     */
    @NonRepetitive
    private val wipe = { animation: AnimationData ->
        when (animation.direction) {
            Direction.BACKWARD -> for (i in animation.endPixel downTo animation.startPixel) {
                setPixelColor(i, animation.pCols[0])
                show()
                delayBlocking(animation.delay)
            }
            Direction.FORWARD -> for (i in animation.startPixel..animation.endPixel) {
                setPixelColor(i, animation.pCols[0])
                show()
                delayBlocking(animation.delay)
            }
        }
    }
}
