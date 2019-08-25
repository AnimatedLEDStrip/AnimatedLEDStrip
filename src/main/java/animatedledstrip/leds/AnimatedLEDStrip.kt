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
import animatedledstrip.utils.tryWithLock
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import org.pmw.tinylog.Logger
import java.lang.Math.random
import javax.script.CompiledScript
import javax.script.ScriptContext
import javax.script.ScriptEngine

/**
 * A subclass of [LEDStrip] adding animations.
 *
 * @param numLEDs Number of LEDs in the strip
 */
abstract class AnimatedLEDStrip(
        numLEDs: Int,
        imageDebugging: Boolean = false,
        fileName: String? = null
) : LEDStrip(numLEDs, imageDebugging, fileName), AnimatedLEDStripInterface, SectionableLEDStrip {

    /**
     * Map containing Mutex instances for locking access to each led while it is
     * being used.
     */
    val locks = mutableMapOf<Int, Mutex>()


    /**
     * A pool of threads to be used for animations that spawn new sub-threads
     * (with the exception of sparkle-type animations, those use the
     * [sparkleThreadPool]).
     */
    @Suppress("EXPERIMENTAL_API_USAGE")
    val animationThreadPool =
            newFixedThreadPoolContext(2 * numLEDs, "Animation Pool")

    /**
     * A pool of threads to be used for sparkle-type animations due to the
     * number of threads a concurrent sparkle animation uses. This prevents
     * memory leaks caused by the overhead associated with creating new threads.
     */
    @Suppress("EXPERIMENTAL_API_USAGE")
    val sparkleThreadPool =
            newFixedThreadPoolContext(numLEDs + 1, "Sparkle Pool")


    /**
     * Map of pixel indices to `FadePixel` instances.
     */
    private val fadeMap = mutableMapOf<Int, FadePixel>()

    /**
     * Helper class for fading a pixel from one color to another.
     *
     * @property pixel The pixel associated with this instance
     */
    inner class FadePixel(private val pixel: Int) {
        /**
         * Which thread is currently fading a pixel?
         * Used so another thread can take over mid-fade if necessary.
         */
        var owner = ""


        /**
         * Fade a pixel from its current color to `destinationColor`.
         *
         * Blends the current color with `destinationColor` using [blend] every
         * `delay` milliseconds until the pixel reaches `destinationColor` or 40
         * iterations have passed, whichever comes first.
         *
         * @param amountOfOverlay How much the pixel should fade in each iteration
         * @param delay Time in milliseconds between iterations
         */
        fun fade(amountOfOverlay: Int = 25, delay: Int = 30) {
            val myName = Thread.currentThread().name
            owner = myName
            var i = 0
            while (getActualPixelColor(pixel) != prolongedColors[pixel] && i <= 40) {
                if (owner != myName) break
                setPixelColor(pixel, blend(getActualPixelColor(pixel), prolongedColors[pixel], amountOfOverlay))
                delayBlocking(delay)
                i++
            }
            revertPixel(pixel)
        }
    }

    /**
     * Helper function for fading a pixel from its current color to `destinationColor`.
     *
     * @param pixel The pixel to be faded
     * @param amountOfOverlay How much the pixel should fade in each iteration
     * @param delay Time in milliseconds between iterations
     * @see FadePixel
     */
    fun fadePixel(pixel: Int, amountOfOverlay: Int = 25, delay: Int = 30) {
        Logger.trace("Fading pixel $pixel")
        fadeMap[pixel]?.fade(amountOfOverlay =  amountOfOverlay, delay = delay)
        Logger.trace("Fade of pixel $pixel complete")
    }


    /**
     * Map containing compiled animations.
     *
     * NOTE: Must include the animatedledstrip-custom-animations library and
     * call setupCustomAnimations() first. Use addCustomAnimation(String,
     * String) to add a custom animation.
     */
    val customAnimationMap = mutableMapOf<String, CompiledScript>()


    /**
     * The compiler used when a custom animation is sent.
     *
     * NOTE: Must include the animatedledstrip-custom-animations library and
     * call setupCustomAnimations() first.
     */
    lateinit var customAnimationCompiler: ScriptEngine

    init {
        for (i in 0 until numLEDs) {
            locks += Pair(i, Mutex())        // Initialize locks map
            fadeMap += Pair(i, FadePixel(i))
        }
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
            Animation.BOUNCE -> bounce(animation)
            Animation.BOUNCETOCOLOR -> bounceToColor(animation)
            Animation.COLOR -> setStripColor(animation.pCols[0])
            Animation.METEOR -> meteor(animation)
            Animation.MULTICOLOR -> setStripColor(animation.pCols[0])
            Animation.MULTIPIXELRUN -> multiPixelRun(animation)
            Animation.MULTIPIXELRUNTOCOLOR -> multiPixelRunToColor(animation)
            Animation.PIXELMARATHON -> pixelMarathon(animation)
            Animation.PIXELRUN -> pixelRun(animation)
            Animation.PIXELRUNWITHTRAIL -> Logger.warn("PixelRunWithTrail is deprecated. Use Meteor")
            Animation.SMOOTHCHASE -> smoothChase(animation)
            Animation.SMOOTHFADE -> smoothFade(animation)
            Animation.SPARKLE -> sparkle(animation)
            Animation.SPARKLEFADE -> sparkleFade(animation)
            Animation.SPARKLETOCOLOR -> sparkleToColor(animation)
            Animation.STACK -> stack(animation)
            Animation.STACKOVERFLOW -> stackOverflow(animation)
            Animation.WIPE -> wipe(animation)
            Animation.CUSTOMANIMATION, Animation.CUSTOMREPETITIVEANIMATION -> runCustomAnimation(animation)
            else -> Logger.warn("Animation ${animation.animation} not supported by AnimatedLEDStrip")
        }
    }


    /**
     * Run a custom animation. Identify the animation using the ID parameter in
     * the AnimationData instance.
     *
     * @param animation The AnimationData instance to use in the animation
     */
    private fun runCustomAnimation(animation: AnimationData) {
        try {
            customAnimationCompiler.getBindings(ScriptContext.ENGINE_SCOPE).apply {
                put("animation", animation)
            }
            customAnimationMap[animation.id]?.eval()
        } catch (e: UninitializedPropertyAccessException) {
            Logger.error("""Custom animations not initialized:
                |   - Include animatedledstrip-custom-animations in your project
                |   - Call extension function setupCustomAnimations()
            """.trimMargin())
            throw UninitializedPropertyAccessException()
        }
    }


    /**
     * Runs an Alternate animation.
     *
     * Strip alternates between `pCols[0]` and `pCols[1]` at the specified rate
     * (delay between changes).
     */
    private val alternate = { animation: AnimationData ->
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
    private val bounce = { animation: AnimationData ->
        for (i in 0..((animation.endPixel - animation.startPixel) / 2)) {
            for (j in (i + animation.startPixel)..(animation.endPixel - i)) {
                runBlocking {
                    locks[j]!!.tryWithLock {
                        setPixelColor(j, animation.pCols[0])
                        delay(animation.delay)
                        revertPixel(j)
                    }
                }
            }
            setPixelColor(animation.endPixel - i, animation.pCols[0])
            GlobalScope.launch(animationThreadPool) {
                val p = animation.endPixel - i
                fadePixel(p, amountOfOverlay = 25, delay = 50)
            }
            for (j in animation.endPixel - i - 1 downTo (i + animation.startPixel)) {
                runBlocking {
                    locks[j]!!.tryWithLock {
                        setPixelColor(j, animation.pCols[0])
                        delay(animation.delay)
                        revertPixel(j)
                    }
                }
            }
            setPixelColor(i, animation.pCols[0])
            GlobalScope.launch(animationThreadPool) {
                fadePixel(i, 25, 50)
            }
        }
        if ((animation.endPixel - animation.startPixel) % 2 == 1) {
            setPixelColor((animation.endPixel - animation.startPixel) / 2 + animation.startPixel, animation.pCols[0])
            GlobalScope.launch(animationThreadPool) {
                val p = (animation.endPixel - animation.startPixel) / 2 + animation.startPixel
                fadePixel(p, 25, 50)
            }
        }
    }


    /**
     * Runs a Bounce to Color animation.
     *
     * Pixel 'bounces' back and forth, leaving behind a pixel set to pCols[0]
     * at each end like Stack, eventually ending in the middle.
     */
    @NonRepetitive
    private val bounceToColor = { animation: AnimationData ->
        for (i in 0..((animation.endPixel - animation.startPixel) / 2)) {
            for (j in (animation.startPixel + i)..(animation.endPixel - i)) {
                setPixelColor(j, animation.pCols[0])
                delayBlocking(animation.delay)
                revertPixel(j)
            }
            setProlongedPixelColor(animation.endPixel - i, animation.pCols[0])
            for (j in animation.endPixel - i - 1 downTo (i + animation.startPixel)) {
                setPixelColor(j, animation.pCols[0])
                delayBlocking(animation.delay)
                revertPixel(j)
            }
            setProlongedPixelColor(i, animation.pCols[0])
        }
        if ((animation.endPixel - animation.startPixel) % 2 == 1) {
            setProlongedPixelColor((animation.endPixel - animation.startPixel) / 2 + animation.startPixel, animation.pCols[0])
        }
    }

    // TODO: Add multiAlternate


    /**
     * Runs a Meteor animation.
     *
     * Like a Pixel Run animation, but the 'running' pixel has a trail behind it
     * where the pixels fade back from `pCols[0]` over ~20 iterations.
     */
    private val meteor = { animation: AnimationData ->
        when (animation.direction) {
            Direction.FORWARD -> for (q in animation.startPixel..animation.endPixel) {
                setPixelColor(q, animation.pCols[0])
                GlobalScope.launch(animationThreadPool) {
                    fadePixel(q, 60, 25)
                }
                delayBlocking(animation.delay)
            }
            Direction.BACKWARD -> for (q in animation.endPixel downTo animation.startPixel) {
                setPixelColor(q, animation.pCols[0])
                GlobalScope.launch(animationThreadPool) {
                    fadePixel(q, 60, 25)
                }
                delayBlocking(animation.delay)
            }
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
                delayBlocking(animation.delay)
                for (i in animation.startPixel..animation.endPixel step animation.spacing) {
                    if (i + (-(q - (animation.spacing - 1))) > animation.endPixel) continue
                    setPixelColor(
                            i + (-(q - (animation.spacing - 1))),
                            animation.pCols[1]
                    )
                }
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
                delayBlocking(animation.delay)
                for (i in animation.startPixel..animation.endPixel step animation.spacing) {
                    if (i + (-(q - (animation.spacing - 1))) > animation.endPixel) continue
                    setPixelColor(
                            i + (-(q - (animation.spacing - 1))),
                            animation.pCols[1]
                    )
                }
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


    // TODO: test
    private val pixelMarathon = { animation: AnimationData ->

        GlobalScope.launch(animationThreadPool) {
            run(AnimationData().animation(Animation.PIXELRUN).color(animation.pCols[4]))
        }
        delayBlocking((random() * 500).toInt())
        GlobalScope.launch(animationThreadPool) {
            run(AnimationData().animation(Animation.PIXELRUN).color(animation.pCols[3]))
        }
        delayBlocking((random() * 500).toInt())
        GlobalScope.launch(animationThreadPool) {
            run(AnimationData().animation(Animation.PIXELRUN).color(animation.pCols[1]))
        }
        delayBlocking((random() * 500).toInt())
        GlobalScope.launch(animationThreadPool) {
            run(AnimationData().animation(Animation.PIXELRUN).color(animation.pCols[2]))
        }
        delayBlocking((random() * 500).toInt())
        GlobalScope.launch(animationThreadPool) {
            run(AnimationData().animation(Animation.PIXELRUN).color(animation.pCols[0]))
        }
        delayBlocking((random() * 500).toInt())
        GlobalScope.launch(animationThreadPool) {
            run(AnimationData().animation(Animation.PIXELRUN).color(animation.pCols[1]))
        }
        delayBlocking((random() * 500).toInt())
        GlobalScope.launch(animationThreadPool) {
            run(AnimationData().animation(Animation.PIXELRUN).color(animation.pCols[4]))
        }
        delayBlocking((random() * 500).toInt())
        GlobalScope.launch(animationThreadPool) {
            run(AnimationData().animation(Animation.PIXELRUN).color(animation.pCols[2]))
        }
        delayBlocking((random() * 500).toInt())
        GlobalScope.launch(animationThreadPool) {
            run(AnimationData().animation(Animation.PIXELRUN).color(animation.pCols[3]))
        }
        delayBlocking((random() * 500).toInt())
        GlobalScope.launch(animationThreadPool) {
            run(AnimationData().animation(Animation.PIXELRUN).color(animation.pCols[0]))
        }
        delayBlocking((random() * 500).toInt())

    }


    /**
     * Runs a Pixel Run animation.
     *
     * The strip is set to `pCols[1]`, then a pixel 'runs' along the strip.
     * Similar to Multi-Pixel Run but with only one pixel.
     */
    private val pixelRun = { animation: AnimationData ->
        when (animation.direction) {
            Direction.FORWARD -> for (q in 0 until ledStrip.numLEDs) {
                runBlocking {
                    locks[q]!!.tryWithLock {
                        setPixelColor(q, animation.pCols[0])
                        delay(animation.delay)
                        revertPixel(q)
                    }
                }
            }
            Direction.BACKWARD -> for (q in ledStrip.numLEDs - 1 downTo 0) {
                runBlocking {
                    locks[q]!!.tryWithLock {
                        setPixelColor(q, animation.pCols[0])
                        delay(animation.delay)
                        revertPixel(q)
                    }
                }
            }
        }
    }


    /**
     * TODO: Update
     * Runs a Smooth Chase animation.
     *
     * The prepare function is used to create a collection of colors
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
     */
    private val smoothFade = { animation: AnimationData ->
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
    private val sparkle = { animation: AnimationData ->
        val deferred = (animation.startPixel..animation.endPixel).map { n ->
            GlobalScope.async(sparkleThreadPool) {
                delay((random() * 5000).toLong() % 4950)
                setPixelColor(n, animation.pCols[0])
                delay(animation.delay)
                revertPixel(n)
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
    private val sparkleFade = { animation: AnimationData ->
        val deferred = (animation.startPixel..animation.endPixel).map { n ->
            GlobalScope.async(sparkleThreadPool) {
                delay((random() * 5000).toLong())
                setPixelColor(n, animation.pCols[0])
                GlobalScope.launch(sparkleThreadPool) {
                    fadePixel(n, 25)
                }
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
    private val sparkleToColor = { animation: AnimationData ->
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
     * TODO: documentation
     */
    @NonRepetitive
    private val stack = { animation: AnimationData ->
        when (animation.direction) {
            Direction.FORWARD -> for (q in animation.endPixel downTo animation.startPixel) {
                for (i in animation.startPixel until q) {
                    runBlocking {
                        locks[i]!!.tryWithLock {
                            setPixelColor(i, animation.pCols[0])
                            delay(animation.delay)
                            revertPixel(i)
                        }
                    }
                }
                setProlongedPixelColor(q, animation.pCols[0])
            }
            Direction.BACKWARD -> for (q in animation.startPixel..animation.endPixel) {
                for (i in animation.endPixel downTo q) {
                    runBlocking {
                        locks[i]!!.tryWithLock {
                            setPixelColor(i, animation.pCols[0])
                            delay(animation.delay)
                            revertPixel(i)
                        }
                    }
                }
                setProlongedPixelColor(q, animation.pCols[0])
            }
        }
    }


    // TODO: documentation
    @NonRepetitive
    private val stackOverflow = { animation: AnimationData ->
        val forwardThread = GlobalScope.launch(animationThreadPool) {
            run(AnimationData().animation(Animation.STACK).color(animation.pCols[0]).direction(Direction.FORWARD).delay(2))
        }
        val backwardThread = GlobalScope.launch(animationThreadPool) {
            run(AnimationData().animation(Animation.STACK).color(animation.pCols[1]).direction(Direction.BACKWARD).delay(2))
        }
        runBlocking {
            joinAll(forwardThread, backwardThread)
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
                setProlongedPixelColor(i, animation.pCols[0])
                delayBlocking(animation.delay)
            }
            Direction.FORWARD -> for (i in animation.startPixel..animation.endPixel) {
                setProlongedPixelColor(i, animation.pCols[0])
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
//        // TODO: Change to use threads from animationThreadPool
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

