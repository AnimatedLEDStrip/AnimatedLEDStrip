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


import animatedledstrip.ccpresets.*
import org.pmw.tinylog.Logger


/**
 * A subclass of [LEDStripNonConcurrent] adding animations
 *
 * @param numLEDs Number of LEDs in the strip
 * @param pin GPIO pin connected for signal
 * @param emulated Is this strip real or emulated?
 */
abstract class AnimatedLEDStripNonConcurrent(numLEDs: Int, pin: Int, emulated: Boolean = false) :
    LEDStripNonConcurrent(numLEDs, pin, emulated) {


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
    fun run(animation: AnimationData) {
        animation.endPixel = when (animation.endPixel) {
            0 -> numLEDs - 1
            else -> animation.endPixel
        }
        when (animation.animation) {
            Animation.ALTERNATE -> alternate(animation)
            Animation.BOUNCETOCOLOR -> bounceToColor(animation)
            Animation.COLOR -> setStripColor(animation.color1)
            Animation.MULTICOLOR -> setStripColorWithGradient(animation.colorList)
            Animation.MULTIPIXELRUN -> multiPixelRun(animation)
            Animation.MULTIPIXELRUNTOCOLOR -> multiPixelRunToColor(animation)
            Animation.PIXELRUN -> pixelRun(animation)
            Animation.SMOOTHCHASE -> smoothChase(animation)
            Animation.SPARKLE -> sparkle(animation)
            Animation.SPARKLETOCOLOR -> sparkleToColor(animation)
            Animation.STACK -> stack(animation)
            Animation.WIPE -> wipe(animation)
            else -> Logger.warn("Animation ${animation.animation} not supported by AnimatedLEDStripNonConcurrent")
        }
    }


    private val delayMod = 1.0      // TODO: Remove delayMod from animations as it is implemented in AnimationData


    /**
     * Runs an Alternate animation.
     *
     * Strip alternates between `color1` and `color2` at the specified rate (delay between changes).
     */
    private val alternate = { animation: AnimationData ->
        val startPixel = animation.startPixel
        val endPixel = animation.endPixel
        val colorValues1 = animation.color1
        val colorValues2 = animation.color2
        val delay = animation.delay

        setSectionColor(startPixel, endPixel, colorValues1)
        delay((delay * delayMod).toInt())
        setSectionColor(startPixel, endPixel, colorValues2)
        delay((delay * delayMod).toInt())
    }


    /**
     * Runs a Bounce to Color animation.
     *
     * Pixel 'bounces' back and forth, leaving behind a pixel set to `color1`
     * at each end like Stack, eventually ending in the middle.
     */
    @NonRepetitive
    private val bounceToColor = { animation: AnimationData ->
        for (i in 0..((animation.endPixel - animation.startPixel) / 2)) {
            for (j in (animation.startPixel + i)..(animation.endPixel - i)) {
                setPixelColor(j, animation.color1)
                show()
                delay(animation.delay)
                setPixelColor(j, CCBlack)
                show()
            }
            setPixelColor(animation.endPixel - i, animation.color1)
            for (j in animation.endPixel - i - 1 downTo (i + animation.startPixel)) {
                setPixelColor(j, animation.color1)
                show()
                delay(animation.delay)
                setPixelColor(j, CCBlack)
                show()
            }
            setPixelColor(i, animation.color1)
            show()
        }
        if ((animation.endPixel - animation.startPixel) % 2 == 1) {
            setPixelColor((animation.endPixel - animation.startPixel) / 2 + animation.startPixel, animation.color1)
            show()
        }
    }


    /**
     * Runs a Multi-Pixel Run animation.
     *
     * Similar to Pixel Run but with multiple leds at a specified spacing.
     */
    private val multiPixelRun = { animation: AnimationData ->
        val chaseDirection = animation.direction
        val spacing = animation.spacing
        val colorValues1 = animation.color1
        val colorValues2 = animation.color2
        val delay = animation.delay
        val startPixel = animation.startPixel
        val endPixel = animation.endPixel

        when (chaseDirection) {
            Direction.BACKWARD -> for (q in 0 until spacing) {
                setStripColor(colorValues2)
                for (i in startPixel..endPixel step spacing) setPixelColor(
                    i + (-(q - (spacing - 1))),
                    colorValues1
                )
                show()
                delay((delay * delayMod).toInt())
                for (i in startPixel..endPixel step spacing) setPixelColor(
                    i + (-(q - (spacing - 1))),
                    colorValues2
                )
                show()
            }
            Direction.FORWARD -> for (q in spacing - 1 downTo 0) {
                setStripColor(colorValues2)
                for (i in startPixel..endPixel step spacing) setPixelColor(
                    i + (-(q - (spacing - 1))),
                    colorValues1
                )
                show()
                delay((delay * delayMod).toInt())
                for (i in startPixel..endPixel step spacing) setPixelColor(
                    i + (-(q - (spacing - 1))),
                    colorValues2
                )
                show()
            }
        }
    }


    /**
     * Runs a Multi-Pixel Run To Color animation.
     *
     * Similar to Multi-Pixel Run but leds do not revert back to their original
     * color.
     */
    @NonRepetitive
    private val multiPixelRunToColor = { animation: AnimationData ->
        val chaseDirection = animation.direction
        val spacing = animation.spacing
        val destinationColor = animation.color1
        val startPixel = animation.startPixel
        val endPixel = animation.endPixel
        val delay = animation.delay

        when (chaseDirection) {
            Direction.BACKWARD -> for (q in 0 until spacing) {
                for (i in startPixel..endPixel step spacing) setPixelColor(
                    i + (-(q - (spacing - 1))),
                    destinationColor
                )
                show()
                delay((delay * delayMod).toInt())
            }
            Direction.FORWARD -> for (q in spacing - 1 downTo 0) {
                for (i in startPixel..endPixel step spacing) setPixelColor(
                    i + (-(q - (spacing - 1))),
                    destinationColor
                )
                show()
                delay((delay * delayMod).toInt())
            }
        }
    }


    /**
     * Runs a Pixel Run animation.
     *
     * The strip is set to `color2`, then a pixel 'runs' along the strip.
     * Similar to Multi-Pixel Run but with only one pixel.
     */
    private val pixelRun = { animation: AnimationData ->
        val colorValues1 = animation.color1
        val colorValues2 = animation.color2
        val movementDirection = animation.direction
        val delay = animation.delay

        setStripColor(colorValues2)
        when (movementDirection) {
            Direction.FORWARD -> for (q in 0 until ledStrip.numLEDs) {
                setPixelColor(q, colorValues1)
                show()
                delay((delay * delayMod).toInt())
                setPixelColor(q, colorValues2)
                show()
            }
            Direction.BACKWARD -> for (q in ledStrip.numLEDs - 1 downTo 0) {
                setPixelColor(q, colorValues1)
                show()
                delay((delay * delayMod).toInt())
                setPixelColor(q, colorValues2)
                show()
            }
        }
    }


    /**
     * Runs a Pixel Run with Trail animation.
     *
     * Like a Pixel Run animation, but the 'running' pixel has a trail behind it
     * where the pixels fade from `color1` to `color2`. Note: the end of the strip
     * might remain lit with the tail after the animation completes unless if
     * another Pixel Run with Trail is run.
     */
    private val pixelRunWithTrail = { animation: AnimationData ->
        val colorValues1 = animation.color1
        val colorValues2 = animation.color2
        val movementDirection = animation.direction
        val startPixel = animation.startPixel
        val endPixel = animation.endPixel
        val delay = animation.delay

        when (movementDirection) {
            Direction.FORWARD -> for (q in startPixel..endPixel) {
                for (i in startPixel until endPixel) {
                    setPixelColor(i, blend(getPixelColor(i), colorValues2, 60))
                }
                setPixelColor(q, colorValues1)
                show()
                delay(delay)
            }
            Direction.BACKWARD -> for (q in endPixel downTo startPixel) {
                for (i in startPixel until endPixel) {
                    setPixelColor(i, blend(getPixelColor(i), colorValues2, 60))
                }
                setPixelColor(q, colorValues1)
                show()
                delay(delay)
            }
        }
    }


    /**
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
        val colorList = animation.colorList
        val movementDirection = animation.direction
        val startPixel = animation.startPixel
        val endPixel = animation.endPixel
        val delay = animation.delay

        val palette = colorsFromPalette(colorList, numLEDs)

        when (movementDirection) {
            Direction.FORWARD -> for (m in startPixel..endPixel) {
                setStripColorWithPalette(palette, m)
                show()
                delay((delay * delayMod).toInt())
            }
            Direction.BACKWARD -> for (m in endPixel downTo startPixel) {
                setStripColorWithPalette(palette, m)
                show()
                delay((delay * delayMod).toInt())
            }
        }
    }


    /**
     * Runs a Sparkle animation.
     *
     * Each LED is changed to `color1` for delay milliseconds before reverting
     * to its original color. At the beginning, shuffleArray is shuffled, then
     * the LEDs are sparkled in the order given in shuffleArray.
     */
    private val sparkle = { animation: AnimationData ->
        val sparkleColor = animation.color1
        val delay = animation.delay
        val startPixel = animation.startPixel
        val endPixel = animation.endPixel

        var originalColor: ColorContainer
        shuffleArray.shuffle()
        for (i in startPixel..endPixel) {
            originalColor = getPixelColor(shuffleArray[i])
            setPixelColor(shuffleArray[i], sparkleColor)
            show()
            delay(delay)
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
        val destinationColor = animation.color1
        val delay = animation.delay
        val startPixel = animation.startPixel
        val endPixel = animation.endPixel


        // TODO: Refactor to only run on specified part of the strip
        shuffleArray.shuffle()
        for (i in 0 until ledStrip.numLEDs) {
            setPixelColor(shuffleArray[i], destinationColor)
            show()
            delay(delay)
        }
    }


    /**
     * TODO (Katie)
     */
    private val stack = { animation: AnimationData ->
        val colorValues1 = animation.color1
        val stackDirection = animation.direction
        val delay = animation.delay
        val startPixel = animation.startPixel
        val endPixel = animation.endPixel

        when (stackDirection) {
            Direction.FORWARD -> for (q in endPixel downTo startPixel) {
                var originalColor: ColorContainer
                for (i in startPixel until q) {
                    originalColor = getPixelColor(i)
                    setPixelColor(i, colorValues1)
                    show()
                    delay((delay * delayMod).toInt())
                    setPixelColor(i, originalColor)
                }
                setPixelColor(q, colorValues1)
                show()
            }
            Direction.BACKWARD -> for (q in startPixel..endPixel) {
                var originalColor: ColorContainer
                for (i in endPixel downTo q) {
                    originalColor = getPixelColor(i)
                    setPixelColor(i, colorValues1)
                    show()
                    delay((delay * delayMod).toInt())
                    setPixelColor(i, originalColor)
                }
                setPixelColor(q, colorValues1)
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
        val colorValues = animation.color1
        val wipeDirection = animation.direction
        val delay = animation.delay
        val startPixel = animation.startPixel
        val endPixel = animation.endPixel

        when (wipeDirection) {
            Direction.BACKWARD -> for (i in endPixel downTo startPixel) {
                setPixelColor(i, colorValues)
                show()
                delay((delay * delayMod).toInt())
            }
            Direction.FORWARD -> for (i in startPixel..endPixel) {
                setPixelColor(i, colorValues)
                show()
                delay((delay * delayMod).toInt())
            }
        }
    }
}
