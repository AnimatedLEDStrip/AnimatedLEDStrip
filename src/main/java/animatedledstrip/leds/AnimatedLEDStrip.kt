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
import animatedledstrip.leds.sections.SectionableLEDStrip
import animatedledstrip.utils.delayBlocking
import kotlinx.coroutines.*
import org.pmw.tinylog.Logger
import java.lang.Math.random
import kotlin.math.max
import kotlin.math.min

/**
 * A subclass of [LEDStrip] adding concurrent animations.
 *
 * @param numLEDs Number of LEDs in the strip
 * @param imageDebugging Enable image debugging
 * @param fileName The file to write image debugging outputs to
 * @param rendersBeforeSave How many renders to perform between
 * image debugging saves
 */
abstract class AnimatedLEDStrip(
    numLEDs: Int,
    imageDebugging: Boolean = false,
    fileName: String? = null,
    rendersBeforeSave: Int = 1000
) : LEDStrip(numLEDs, imageDebugging, fileName, rendersBeforeSave), AnimatedLEDStripInterface, SectionableLEDStrip {

    /**
     * A pool of threads to be used for animations that spawn new sub-threads
     * (with the exception of sparkle-type animations, those use the
     * [sparkleThreadPool]).
     */
    @Suppress("EXPERIMENTAL_API_USAGE")
    val parallelAnimationThreadPool =
        newFixedThreadPoolContext(2 * numLEDs, "Parallel Animation Pool")

    /**
     * A pool of threads to be used for sparkle-type animations due to the
     * number of threads a concurrent sparkle animation uses. This prevents
     * memory leaks caused by the overhead associated with creating new threads.
     */
    @Suppress("EXPERIMENTAL_API_USAGE")
    val sparkleThreadPool =
        newFixedThreadPoolContext(numLEDs + 1, "Sparkle Pool")


    private val pixelSetLists = mutableMapOf<Triple<Int, Int, Int>, MutableList<MutableList<Int>>>()


    /**
     * Map containing custom animations.
     */
    private val customAnimationMap = mutableMapOf<String, (AnimationData) -> Unit>()


    /**
     * Run an animation.
     *
     * @param animation An [AnimationData] instance with details about the
     * animation to run
     */
    override fun run(animation: AnimationData) {
        animation.prepare(this)
        Logger.trace("Starting $animation")
//        animation.endPixel = when (animation.endPixel) {
//            -1 -> numLEDs - 1
//            else -> animation.endPixel
//        }
//
//        animation.center = when (animation.center) {
//            -1 -> numLEDs / 2
//            else -> animation.center
//        }
//
//        animation.distance = when (animation.distance) {
//            -1 -> numLEDs
//            else -> animation.distance
//        }
//
//        if (animation.colors.isEmpty()) animation.color(CCBlack)
//
//        animation.pCols = mutableListOf()
//        animation.colors.forEach {
//            animation.pCols.add(
//                it.prepare(
//                    animation.endPixel - animation.startPixel + 1,
//                    leadingZeros = animation.startPixel
//                )
//            )
//        }
//
//        for (i in animation.colors.size until (animation.animation.infoOrNull()?.numColors ?: 0)) {
//            animation.pCols.add(
//                CCBlack.prepare(
//                    animation.endPixel - animation.startPixel + 1,
//                    leadingZeros = animation.startPixel
//                )
//            )
//        }

        @Suppress("EXPERIMENTAL_API_USAGE", "DEPRECATION")
        when (animation.animation) {
            Animation.ALTERNATE -> alternate(animation)
            Animation.BOUNCE -> bounce(animation)
            Animation.BOUNCETOCOLOR -> bounceToColor(animation)
            Animation.CATTOY -> catToy(animation)
            Animation.COLOR -> setStripColor(animation.pCols[0])
            Animation.METEOR -> meteor(animation)
            Animation.MULTICOLOR -> Logger.warn("MultiColor is deprecated. Use Color")
            Animation.MULTIPIXELRUN -> multiPixelRun(animation)
            Animation.MULTIPIXELRUNTOCOLOR -> multiPixelRunToColor(animation)
            Animation.PIXELMARATHON -> pixelMarathon(animation)
            Animation.PIXELRUN -> pixelRun(animation)
            Animation.RIPPLE -> ripple(animation)
            Animation.SMOOTHCHASE -> smoothChase(animation)
            Animation.SMOOTHFADE -> smoothFade(animation)
            Animation.SPARKLE -> sparkle(animation)
            Animation.SPARKLEFADE -> sparkleFade(animation)
            Animation.SPARKLETOCOLOR -> sparkleToColor(animation)
            Animation.SPLAT -> splat(animation)
            Animation.STACK -> stack(animation)
            Animation.STACKOVERFLOW -> stackOverflow(animation)
            Animation.WIPE -> wipe(animation)
            Animation.CUSTOMANIMATION, Animation.CUSTOMREPETITIVEANIMATION -> runCustomAnimation(animation)
            else -> Logger.warn("Animation ${animation.animation} not supported by AnimatedLEDStrip")
        }
    }

    /**
     * Start another animation in a separate coroutine.
     *
     * @param animation The animation to run
     * @param pool The pool of threads to start the animation in
     * @return The `Job` associated with the new coroutine
     */
    fun runParallel(
        animation: AnimationData,
        pool: ExecutorCoroutineDispatcher = parallelAnimationThreadPool
    ): Job {
        return GlobalScope.launch(pool) {
            run(animation)
        }
    }

    /**
     * Run a custom animation. Identify the animation using the ID parameter in
     * the AnimationData instance.
     *
     * @param animation The AnimationData instance to use in the animation
     */
    private fun runCustomAnimation(animation: AnimationData) {
        customAnimationMap[animation.id]?.invoke(animation)
    }

    /**
     * Runs an Alternate animation.
     *
     * Strip alternates between `pCols[0]` and `pCols[1]` at the specified rate
     * (delay between changes).
     */
    private val alternate: (AnimationData) -> Unit = { animation: AnimationData ->
        setSectionColor(animation.startPixel, animation.endPixel, animation.pCols[0])
        delayBlocking(animation.delay)
        setSectionColor(animation.startPixel, animation.endPixel, animation.pCols[1])
        delayBlocking(animation.delay)
    }


    /**
     * Runs a Bounce animation.
     *
     * Similar to Bounce to Color but the ends fade to `pCols[1]` after being set
     * to `pCols[0]`.
     */
    private val bounce: (AnimationData) -> Unit = { animation: AnimationData ->
        for (i in 0..((animation.endPixel - animation.startPixel) / 2)) {
            iterateOver(animation.startPixel + i..animation.endPixel - i) {
                setPixelAndRevertAfterDelay(it, animation.pCols[0], animation.delay)
            }
            setAndFadePixel(
                pixel = animation.endPixel - i,
                color = animation.pCols[0],
                amountOfOverlay = 25,
                delay = 50,
                context = parallelAnimationThreadPool
            )
            iterateOver(animation.endPixel - i downTo animation.startPixel + i) {
                setPixelAndRevertAfterDelay(it, animation.pCols[0], animation.delay)
            }
            setAndFadePixel(
                pixel = animation.startPixel + i,
                color = animation.pCols[0],
                amountOfOverlay = 25,
                delay = 50,
                context = parallelAnimationThreadPool
            )
        }
        if ((animation.endPixel - animation.startPixel) % 2 == 1) {
            setAndFadePixel(
                pixel = (animation.endPixel - animation.startPixel) / 2 + animation.startPixel,
                color = animation.pCols[0],
                amountOfOverlay = 25,
                delay = 50,
                context = parallelAnimationThreadPool
            )
        }
    }


    /**
     * Runs a Bounce to Color animation.
     *
     * Pixel 'bounces' back and forth, leaving behind a pixel set to `pCols[0]`
     * at each end like Stack, eventually ending in the middle.
     */
    @NonRepetitive
    private val bounceToColor: (AnimationData) -> Unit = { animation: AnimationData ->
        for (i in 0..((animation.endPixel - animation.startPixel) / 2)) {
            for (j in (animation.startPixel + i)..(animation.endPixel - i))
                setPixelAndRevertAfterDelay(j, animation.pCols[0], animation.delay)
            setProlongedPixelColor(animation.endPixel - i, animation.pCols[0])

            for (j in animation.endPixel - i - 1 downTo (i + animation.startPixel))
                setPixelAndRevertAfterDelay(j, animation.pCols[0], animation.delay)
            setProlongedPixelColor(animation.startPixel + i, animation.pCols[0])
        }
        if ((animation.endPixel - animation.startPixel) % 2 == 1) {
            setProlongedPixelColor(
                (animation.endPixel - animation.startPixel) / 2 + animation.startPixel,
                animation.pCols[0]
            )
        }
    }

    @ExperimentalAnimation
    private val catToy: (AnimationData) -> Unit = { animation: AnimationData ->

        val pixel1 = randomPixelIn(animation)
        val pixel2 = randomPixelIn(animation.startPixel, pixel1)
        val pixel3 = randomPixelIn(pixel2, animation.endPixel)
        val pixel4 = randomPixelIn(animation.startPixel, pixel3)
        val pixel5 = randomPixelIn(pixel4, animation.endPixel)

        run(
            animation.copy(
                animation = Animation.PIXELRUN,
                endPixel = pixel1,
                direction = Direction.FORWARD
            )
        )
        setPixelColor(pixel1, animation.colors[0])
        delayBlocking((random() * 2500).toInt())
        revertPixel(pixel1)
        run(
            animation.copy(
                animation = Animation.PIXELRUN,
                startPixel = pixel2,
                endPixel = pixel1,
                direction = Direction.BACKWARD
            )
        )
        setPixelColor(pixel2, animation.colors[0])
        delayBlocking((random() * 2500).toInt())
        revertPixel(pixel2)
        run(
            animation.copy(
                animation = Animation.PIXELRUN,
                startPixel = pixel2,
                endPixel = pixel3,
                direction = Direction.FORWARD
            )
        )
        setPixelColor(pixel3, animation.colors[0])
        delayBlocking((random() * 2500).toInt())
        revertPixel(pixel3)
        run(
            animation.copy(
                animation = Animation.PIXELRUN,
                startPixel = pixel4,
                endPixel = pixel3,
                direction = Direction.BACKWARD
            )
        )
        setPixelColor(pixel4, animation.colors[0])
        delayBlocking((random() * 2500).toInt())
        revertPixel(pixel4)
        run(
            animation.copy(
                animation = Animation.PIXELRUN,
                startPixel = pixel4,
                endPixel = pixel5,
                direction = Direction.FORWARD
            )
        )
        setPixelColor(pixel5, animation.colors[0])
        delayBlocking((random() * 2500).toInt())
        revertPixel(pixel5)
        run(
            animation.copy(
                animation = Animation.PIXELRUN,
                endPixel = pixel5,
                direction = Direction.BACKWARD
            )
        )

    }


    /**
     * Runs a Meteor animation.
     *
     * Like a Pixel Run animation, but the 'running' pixel has a trail behind it
     * where the pixels fade back from `pCols[0]` over ~20 iterations.
     */
    private val meteor: (AnimationData) -> Unit = { animation: AnimationData ->
        when (animation.direction) {
            Direction.FORWARD -> iterateOverPixels(animation) {
                setAndFadePixel(
                    pixel = it,
                    color = animation.pCols[0],
                    amountOfOverlay = 60,
                    delay = 25,
                    context = parallelAnimationThreadPool
                )
                delayBlocking(animation.delay)
            }
            Direction.BACKWARD -> iterateOverPixelsReverse(animation) {
                setAndFadePixel(
                    pixel = it,
                    color = animation.pCols[0],
                    amountOfOverlay = 60,
                    delay = 25,
                    context = parallelAnimationThreadPool
                )
                delayBlocking(animation.delay)
            }
        }
    }

    /**
     * Runs a Multi-Pixel Run animation.
     * TODO: Fix flickering
     * Similar to Pixel Run but with multiple LEDs at a specified spacing.
     */
    private val multiPixelRun: (AnimationData) -> Unit = { animation: AnimationData ->
        val pixelSets: MutableList<MutableList<Int>> =
            pixelSetLists[Triple(animation.startPixel, animation.endPixel, animation.spacing)] ?: run {
                val temp = mutableListOf<MutableList<Int>>()
                for (q in 0 until animation.spacing) {
                    val list = mutableListOf<Int>()
                    for (i in animation.startPixel..animation.endPixel step animation.spacing) {
                        if (i + (-(q - (animation.spacing - 1))) > animation.endPixel) continue
                        else list += i + (-(q - (animation.spacing - 1)))
                    }
                    temp += list
                }
                pixelSetLists[Triple(animation.startPixel, animation.endPixel, animation.spacing)] = temp
                temp
            }
        when (animation.direction) {
            Direction.BACKWARD -> {
                revertPixels(pixelSets[animation.spacing - 1])
                for (q in 0..animation.spacing - 2) {
                    setPixelColors(pixelSets[q], animation.pCols[0])
                    delayBlocking(animation.delay)
                    revertPixels(pixelSets[q])
                }
                setPixelColors(pixelSets[animation.spacing - 1], animation.pCols[0])
                delayBlocking(animation.delay)
            }
            Direction.FORWARD -> {
                revertPixels(pixelSets[0])
                for (q in animation.spacing - 1 downTo 1) {
                    setPixelColors(pixelSets[q], animation.pCols[0])
                    delayBlocking(animation.delay)
                    revertPixels(pixelSets[q])
//                    setPixelColors(pixelSets[q % animation.spacing], animation.pCols[0])
//                    revertPixels(pixelSets[q - 1])
//                    delayBlocking(animation.delay)
                }
                setPixelColors(pixelSets[0], animation.pCols[0])
                delayBlocking(animation.delay)
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
    private val multiPixelRunToColor: (AnimationData) -> Unit = { animation: AnimationData ->
        when (animation.direction) {
            Direction.BACKWARD -> for (q in 0 until animation.spacing) {
                for (i in animation.startPixel..animation.endPixel step animation.spacing) {
                    if (i + (-(q - (animation.spacing - 1))) > animation.endPixel) continue
                    setProlongedPixelColor(
                        i + (-(q - (animation.spacing - 1))),
                        animation.pCols[0]
                    )
                }
                delayBlocking(animation.delay)
            }
            Direction.FORWARD -> for (q in animation.spacing - 1 downTo 0) {
                for (i in animation.startPixel..animation.endPixel step animation.spacing) {
                    if (i + (-(q - (animation.spacing - 1))) > animation.endPixel) continue
                    setProlongedPixelColor(
                        i + (-(q - (animation.spacing - 1))),
                        animation.pCols[0]
                    )
                }
                delayBlocking(animation.delay)
            }
        }
    }


    // TODO: Documentation
    private val pixelMarathon: (AnimationData) -> Unit = { animation: AnimationData ->
        val baseAnimation = AnimationData().animation(Animation.PIXELRUN)
            .direction(animation.direction).delay(animation.delay)

        runParallel(baseAnimation.copy().color(animation.pCols[4]))
        delayBlocking((random() * 500).toInt())

        runParallel(baseAnimation.copy().color(animation.pCols[3]))
        delayBlocking((random() * 500).toInt())

        runParallel(baseAnimation.copy().color(animation.pCols[1]))
        delayBlocking((random() * 500).toInt())

        runParallel(baseAnimation.copy().color(animation.pCols[2]))
        delayBlocking((random() * 500).toInt())

        runParallel(baseAnimation.copy().color(animation.pCols[0]))
        delayBlocking((random() * 500).toInt())

        runParallel(baseAnimation.copy().color(animation.pCols[1]))
        delayBlocking((random() * 500).toInt())

        runParallel(baseAnimation.copy().color(animation.pCols[4]))
        delayBlocking((random() * 500).toInt())

        runParallel(baseAnimation.copy().color(animation.pCols[2]))
        delayBlocking((random() * 500).toInt())

        runParallel(baseAnimation.copy().color(animation.pCols[3]))
        delayBlocking((random() * 500).toInt())

        runParallel(baseAnimation.copy().color(animation.pCols[0]))
        delayBlocking((random() * 500).toInt())
    }


    /**
     * Runs a Pixel Run animation.
     *
     * A pixel colored with `pCols[0]` 'runs' along the strip.
     * Similar to Multi-Pixel Run but with only one pixel.
     */
    private val pixelRun: (AnimationData) -> Unit = { animation: AnimationData ->
        when (animation.direction) {
            Direction.FORWARD -> for (q in animation.startPixel..animation.endPixel)
                setPixelAndRevertAfterDelay(q, animation.pCols[0], animation.delay)
            Direction.BACKWARD -> for (q in animation.endPixel downTo animation.startPixel)
                setPixelAndRevertAfterDelay(q, animation.pCols[0], animation.delay)
        }
    }


    /**
     * Runs a Ripple animation.
     *
     * Starts two Meteor animations running in opposite directions
     * from `center`, stopping after travelling `distance` or at
     * the end of the strip, whichever comes first. Does not wait
     * for the Meteor animations to be complete before returning,
     * giving the ripple-like appearance when run continuously.
     */
    @Radial
    private val ripple: (AnimationData) -> Unit = { animation: AnimationData ->
        val baseAnimation = AnimationData()
            .animation(Animation.METEOR)
            .color(animation.pCols[0])
            .delay(animation.delay)

        runParallel(
            baseAnimation.copy(
                startPixel = animation.center,
                endPixel = min(animation.center + animation.distance, animation.endPixel),
                direction = Direction.FORWARD
            )
        )
        runParallel(
            baseAnimation.copy(
                startPixel = max(animation.center - animation.distance, animation.startPixel),
                endPixel = animation.center,
                direction = Direction.BACKWARD
            )
        )
        delayBlocking(animation.delay * 20)
    }


    /**
     * Runs a Smooth Chase animation.
     *
     * PreparedColorContainer and setStripColorWithOffset are used to 'move' the
     * colors down the strip.
     *
     * The prepare function from ColorContainer is used to create a collection
     * of colors for the strip:
     * *The palette colors are spread out along the strip at approximately equal
     * intervals. All pixels between these 'pure' pixels are a blend of the
     * colors of the two nearest pure pixels. The blend ratio is determined by the
     * location of the pixel relative to the nearest pure pixels.*
     *
     * Each pixel is set to its respective color in the PreparedColorContainer.
     * Then, if the direction is [Direction].`FORWARD`,
     * each pixel is set to `pCols[0][i + 1]`, then `pCols[0][i + 2]`, etc.
     * to create the illusion that the animation is 'moving'. If the direction is
     * [Direction].`BACKWARD`, the same happens but with indices `i`, `i-1`, `i-2`, etc.
     */
    private val smoothChase: (AnimationData) -> Unit = { animation: AnimationData ->
        when (animation.direction) {
            Direction.FORWARD -> for (m in animation.endPixel downTo animation.startPixel) {
                setStripColorWithOffset(animation.pCols[0], m - animation.startPixel)
                delayBlocking(animation.delay)
            }
            Direction.BACKWARD -> for (m in animation.startPixel..animation.endPixel) {
                setStripColorWithOffset(animation.pCols[0], m - animation.startPixel)
                delayBlocking(animation.delay)
            }
        }
    }


    /**
     * Runs a Smooth Fade animation.
     *
     * Like a Smooth Chase animation, but the whole strip is the same color
     * while fading through the PreparedColorContainer.
     */
    private val smoothFade: (AnimationData) -> Unit = { animation: AnimationData ->
        for (i in animation.startPixel..animation.endPixel) {
            setStripColor(animation.pCols[0][i])
            delayBlocking(animation.delay)
        }
    }


    /**
     * Runs a Sparkle animation.
     *
     * Each LED is changed to `pCols[0]` for delay milliseconds before reverting
     * to its original color. A separate thread is created for each pixel. Each
     * thread saves its pixel's original color, then waits for 0-5 seconds
     * before sparkling its pixel.
     */
    private val sparkle: (AnimationData) -> Unit = { animation: AnimationData ->
        val deferred = (animation.startPixel..animation.endPixel).map { n ->
            GlobalScope.async(sparkleThreadPool) {
                delay((random() * 5000).toLong() % 4950)
                setPixelAndRevertAfterDelay(n, animation.pCols[0], animation.delay)
            }
        }
        runBlocking {
            deferred.awaitAll()
        }
        Unit        // Ensure sparkle is of type (AnimationData) -> Unit
    }


    /**
     * Runs a Sparkle Fade animation.
     *
     * Similar to Sparkle but pixels fade back to `pCols[0]`.
     */
    private val sparkleFade: (AnimationData) -> Unit = { animation: AnimationData ->
        val deferred = (animation.startPixel..animation.endPixel).map { n ->
            GlobalScope.async(sparkleThreadPool) {
                delay((random() * 5000).toLong())
                setAndFadePixel(
                    pixel = n,
                    color = animation.pCols[0],
                    amountOfOverlay = 25,
                    context = sparkleThreadPool
                )
                delay(animation.delay)
            }
        }
        runBlocking {
            deferred.awaitAll()
        }
        Unit        // Ensure sparkleFade is of type (AnimationData) -> Unit
    }

    /**
     * Runs a Sparkle To Color animation.
     *
     * Very similar to the Sparkle animation, but the LEDs are not reverted to their
     * original color after the sparkle. A separate thread is created for each
     * pixel. Each thread waits for 0-5 seconds before sparkling its pixel.
     */
    @NonRepetitive
    private val sparkleToColor: (AnimationData) -> Unit = { animation: AnimationData ->
        val deferred = (animation.startPixel..animation.endPixel).map { n ->
            GlobalScope.async(sparkleThreadPool) {
                delay((random() * 5000).toLong() % 4950)
                setProlongedPixelColor(n, animation.pCols[0])
                delay(animation.delay)
            }
        }
        runBlocking {
            deferred.awaitAll()
        }
        Unit        // Ensure sparkleToColor is of type (AnimationData) -> Unit
    }


    /**
     * Runs a Splat animation.
     *
     * Similar to a Ripple but the pixels don't fade back.
     * Runs two Wipe animations in opposite directions starting
     * from `center`, stopping after travelling `distance` or at
     * the end of the strip, whichever comes first.
     */
    @NonRepetitive
    @Radial
    private val splat: (AnimationData) -> Unit = { animation: AnimationData ->
        val baseAnimation = AnimationData()
            .animation(Animation.WIPE)
            .color(animation.pCols[0])
            .delay(animation.delay)

        runParallelAndJoin(
            baseAnimation.copy(
                startPixel = animation.center,
                endPixel = min(animation.center + animation.distance, animation.endPixel),
                direction = Direction.FORWARD
            ),
            baseAnimation.copy(
                startPixel = max(animation.center - animation.distance, animation.startPixel),
                endPixel = animation.center,
                direction = Direction.BACKWARD
            )
        )
    }


    // TODO: documentation
    @NonRepetitive
    private val stack: (AnimationData) -> Unit = { animation: AnimationData ->
        when (animation.direction) {
            Direction.FORWARD -> for (q in animation.endPixel downTo animation.startPixel) {
                for (i in animation.startPixel until q)
                    setPixelAndRevertAfterDelay(i, animation.pCols[0], animation.delay)
                setProlongedPixelColor(q, animation.pCols[0])
            }
            Direction.BACKWARD -> for (q in animation.startPixel..animation.endPixel) {
                for (i in animation.endPixel downTo q)
                    setPixelAndRevertAfterDelay(i, animation.pCols[0], animation.delay)
                setProlongedPixelColor(q, animation.pCols[0])
            }
        }
    }


    // TODO: documentation
    @NonRepetitive
    private val stackOverflow: (AnimationData) -> Unit = { animation: AnimationData ->
        val baseAnimation = AnimationData()
            .animation(Animation.STACK)
            .delay(animation.delay)

        runParallelAndJoin(
            baseAnimation.copy(
                colors = listOf(animation.pCols[0]),
                direction = Direction.FORWARD
            ),
            baseAnimation.copy(
                colors = listOf(animation.pCols[1]),
                direction = Direction.BACKWARD
            )
        )
    }


    /**
     * Runs a Wipe animation.
     *
     * Similar to a Pixel Run animation, but the pixels do not revert to their
     * original color.
     */
    @NonRepetitive
    private val wipe: (AnimationData) -> Unit = { animation: AnimationData ->
        when (animation.direction) {
            Direction.FORWARD -> iterateOverPixels(animation) {
                setProlongedPixelColor(it, animation.pCols[0])
                delayBlocking(animation.delay)
            }
            Direction.BACKWARD -> iterateOverPixelsReverse(animation) {
                setProlongedPixelColor(it, animation.pCols[0])
                delayBlocking(animation.delay)
            }
        }
    }
}
//    /**
//     * TODO (Will)
//     * @param point1
//     * @param point2
//     * @param color0
//     * @param color0
//     * @param delay
//     * @param delayMod
//     */
//    fun multiAlternate(
//        point1: Int,
//        point2: Int,
//        color0: ColorContainer,
//        color0: ColorContainer,
//        delay: Int = 1000,
//        delayMod: Double = 1.0
//    ) {
//        val endptA = 0
//        val endptB: Int
//        val endptC: Int
//        val endptD = numLEDs - 1
//
//        if (point1 <= point2 && point1 > endptA && point2 < endptD) {
//            endptB = point1
//            endptC = point2
//        } else if (point2 > endptA && point1 < endptD) {
//            endptB = point2
//            endptC = point1
//        } else {
//            endptB = numLEDs / 3
//            endptC = (numLEDs * 2 / 3) - 1
//        }
//
//        // TODO: Change to use threads from parallelAnimationThreadPool
//        GlobalScope.launch(newSingleThreadContext("Thread ${Thread.currentThread().name}-1")) {
//            setSectionColor(endptA, endptB, color0)
//            delay((delay * delayMod).toInt())
//            setSectionColor(endptA, endptB, color0)
//            delay((delay * delayMod).toInt())
//        }
//        GlobalScope.launch(newSingleThreadContext("Thread ${Thread.currentThread().name}-2")) {
//            setSectionColor(endptC, endptD, color0)
//            delay((delay * delayMod).toInt())
//            setSectionColor(endptC, endptD, color0)
//            delay((delay * delayMod).toInt())
//        }
//        GlobalScope.launch(newSingleThreadContext("Thread ${Thread.currentThread().name}-3")) {
//            setSectionColor(endptB, endptC, color0)
//            delay((delay * delayMod).toInt())
//            setSectionColor(endptB, endptC, color0)
//            delay((delay * delayMod).toInt())
//        }
//    }

