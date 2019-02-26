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


import animatedledstrip.animationutils.Animation
import animatedledstrip.animationutils.AnimationData
import animatedledstrip.animationutils.Direction
import animatedledstrip.animationutils.NonRepetitive
import animatedledstrip.colors.PreparedColorContainer
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
        imageDebugging: Boolean = false
) :
        LEDStrip(numLEDs, imageDebugging), AnimatedLEDStripInterface, SectionableLEDStrip {

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
         * @param destinationColor The color to end with
         * @param amountOfOverlay How much the pixel should fade in each iteration
         * @param delay Time in milliseconds between iterations
         */
        fun fade(destinationColor: Long, amountOfOverlay: Int = 25, delay: Int = 30) {
            val myName = Thread.currentThread().name
            owner = myName
            var i = 0
            while (getPixelColor(pixel) != destinationColor && i <= 40) {
                if (owner != myName) break
                setPixelColor(pixel, blend(getPixelColor(pixel), destinationColor, amountOfOverlay))
                delayBlocking(delay)
                i++
            }
        }
    }

    /**
     * Helper function for fading a pixel from its current color to `destinationColor`.
     *
     * @param pixel The pixel to be faded
     * @param destinationColor The color to fade to
     * @param amountOfOverlay How much the pixel should fade in each iteration
     * @param delay Time in milliseconds between iterations
     * @see FadePixel
     */
    fun fadePixel(pixel: Int, destinationColor: Long, amountOfOverlay: Int = 25, delay: Int = 30) {
        Logger.trace("Fading pixel $pixel to ${destinationColor.toString(16)}")
        fadeMap[pixel]?.fade(destinationColor, amountOfOverlay, delay)
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
        animation.color1 = animation.color1.prepare(animation.endPixel - animation.startPixel + 1,
                leadingZeros = animation.startPixel)
        animation.color2 = animation.color2.prepare(animation.endPixel - animation.startPixel + 1,
                leadingZeros = animation.startPixel)
        animation.color3 = animation.color3.prepare(animation.endPixel - animation.startPixel + 1,
                leadingZeros = animation.startPixel)
        animation.color4 = animation.color4.prepare(animation.endPixel - animation.startPixel + 1,
                leadingZeros = animation.startPixel)
        animation.color5 = animation.color5.prepare(animation.endPixel - animation.startPixel + 1,
                leadingZeros = animation.startPixel)

        when (animation.animation) {
            Animation.ALTERNATE -> alternate(animation)
            Animation.BOUNCE -> bounce(animation)
            Animation.BOUNCETOCOLOR -> bounceToColor(animation)
            Animation.COLOR -> setStripColor(animation.color1)
            Animation.MULTICOLOR -> setStripColor(animation.color1)
            Animation.MULTIPIXELRUN -> multiPixelRun(animation)
            Animation.MULTIPIXELRUNTOCOLOR -> multiPixelRunToColor(animation)
            Animation.PIXELMARATHON -> pixelMarathon(animation)
            Animation.PIXELRUN -> pixelRun(animation)
            Animation.PIXELRUNWITHTRAIL -> pixelRunWithTrail(animation)
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
     * Strip alternates between `color1` and `color2` at the specified rate
     * (delay between changes).
     */
    private val alternate = { animation: AnimationData ->
        setSectionColor(animation.startPixel, animation.endPixel, animation.color1)
        delayBlocking(animation.delay)
        setSectionColor(animation.startPixel, animation.endPixel, animation.color2)
        delayBlocking(animation.delay)
    }


    /**
     * Runs a Bounce animation.
     *
     * Similar to Bounce to Color but the ends fade to `color2` after being set
     * to `color1`.
     */
    private val bounce = { animation: AnimationData ->
        for (i in 0..((animation.endPixel - animation.startPixel) / 2)) {
            for (j in (i + animation.startPixel)..(animation.endPixel - i)) {
                runBlocking {
                    locks[j]!!.tryWithLock {
                        val originalColor: Long = getPixelColor(j)
                        setPixelColor(j, animation.color1)
                        delay(animation.delay)
                        setPixelColor(j, originalColor)
                    }
                }
            }
            setPixelColor(animation.endPixel - i, animation.color1)
            GlobalScope.launch(animationThreadPool) {
                val p = animation.endPixel - i
                fadePixel(p, animation.color2.color, 25, 50)
            }
            for (j in animation.endPixel - i - 1 downTo (i + animation.startPixel)) {
                runBlocking {
                    locks[j]!!.tryWithLock {
                        val originalColor: Long = getPixelColor(j)
                        setPixelColor(j, animation.color1)
                        delay(animation.delay)
                        setPixelColor(j, originalColor)
                    }
                }
            }
            setPixelColor(i, animation.color1)
            GlobalScope.launch(animationThreadPool) {
                fadePixel(i, animation.color2.color, 25, 50)
            }
        }
        if ((animation.endPixel - animation.startPixel) % 2 == 1) {
            setPixelColor((animation.endPixel - animation.startPixel) / 2 + animation.startPixel, animation.color1)
            GlobalScope.launch(animationThreadPool) {
                val p = (animation.endPixel - animation.startPixel) / 2 + animation.startPixel
                fadePixel(p, animation.color2.color, 25, 50)
            }
        }
    }


    /**
     * Runs a Bounce to Color animation.
     *
     * Pixel 'bounces' back and forth, leaving behind a pixel set to color1
     * at each end like Stack, eventually ending in the middle.
     */
    @NonRepetitive
    private val bounceToColor = { animation: AnimationData ->
        for (i in 0..((animation.endPixel - animation.startPixel) / 2)) {
            for (j in (animation.startPixel + i)..(animation.endPixel - i)) {
                val originalColor: Long = getPixelColor(j)
                setPixelColor(j, animation.color1)
                delayBlocking(animation.delay)
                setPixelColor(j, originalColor)
            }
            setPixelColor(animation.endPixel - i, animation.color1)
            for (j in animation.endPixel - i - 1 downTo (i + animation.startPixel)) {
                val originalColor: Long = getPixelColor(j)
                setPixelColor(j, animation.color1)
                delayBlocking(animation.delay)
                setPixelColor(j, originalColor)
            }
            setPixelColor(i, animation.color1)
        }
        if ((animation.endPixel - animation.startPixel) % 2 == 1) {
            setPixelColor((animation.endPixel - animation.startPixel) / 2 + animation.startPixel, animation.color1)
        }
    }

    // TODO: Add multiAlternate


    /**
     * Runs a Multi-Pixel Run animation.
     *
     * Similar to Pixel Run but with multiple LEDs at a specified spacing.
     */
    private val multiPixelRun = { animation: AnimationData ->
        when (animation.direction) {
            Direction.BACKWARD -> for (q in 0 until animation.spacing) {
                setStripColor(animation.color2)
                for (i in animation.startPixel..animation.endPixel step animation.spacing) {
                    if (i + (-(q - (animation.spacing - 1))) > animation.endPixel) continue
                    setPixelColor(
                            i + (-(q - (animation.spacing - 1))),
                            animation.color1
                    )
                }
                delayBlocking(animation.delay)
                for (i in animation.startPixel..animation.endPixel step animation.spacing) {
                    if (i + (-(q - (animation.spacing - 1))) > animation.endPixel) continue
                    setPixelColor(
                            i + (-(q - (animation.spacing - 1))),
                            animation.color2
                    )
                }
            }
            Direction.FORWARD -> for (q in animation.spacing - 1 downTo 0) {
                setStripColor(animation.color2)
                for (i in animation.startPixel..animation.endPixel step animation.spacing) {
                    if (i + (-(q - (animation.spacing - 1))) > animation.endPixel) continue
                    setPixelColor(
                            i + (-(q - (animation.spacing - 1))),
                            animation.color1
                    )
                }
                delayBlocking(animation.delay)
                for (i in animation.startPixel..animation.endPixel step animation.spacing) {
                    if (i + (-(q - (animation.spacing - 1))) > animation.endPixel) continue
                    setPixelColor(
                            i + (-(q - (animation.spacing - 1))),
                            animation.color2
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
                    setPixelColor(
                            i + (-(q - (animation.spacing - 1))),
                            animation.color1
                    )
                }
                delayBlocking(animation.delay)
            }
            Direction.FORWARD -> for (q in animation.spacing - 1 downTo 0) {
                for (i in animation.startPixel..animation.endPixel step animation.spacing) {
                    if (i + (-(q - (animation.spacing - 1))) > animation.endPixel) continue
                    setPixelColor(
                            i + (-(q - (animation.spacing - 1))),
                            animation.color1
                    )
                }
                delayBlocking(animation.delay)
            }
        }
    }


    private val pixelMarathon = { animation: AnimationData ->
        // TODO: Add pixelMarathon
    }


    /**
     * Runs a Pixel Run animation.
     *
     * The strip is set to `color2`, then a pixel 'runs' along the strip.
     * Similar to Multi-Pixel Run but with only one pixel.
     */
    private val pixelRun = { animation: AnimationData ->
        setStripColor(animation.color2)
        when (animation.direction) {
            Direction.FORWARD -> for (q in 0 until ledStrip.numLEDs) {
                runBlocking {
                    locks[q]!!.tryWithLock {
                        setPixelColor(q, animation.color1)
                        delay(animation.delay)
                        setPixelColor(q, animation.color2)
                    }
                }
            }
            Direction.BACKWARD -> for (q in ledStrip.numLEDs - 1 downTo 0) {
                runBlocking {
                    locks[q]!!.tryWithLock {
                        setPixelColor(q, animation.color1)
                        delay(animation.delay)
                        setPixelColor(q, animation.color2)
                    }
                }
            }
        }
    }


    /**
     * Runs a Pixel Run with Trail animation.
     *
     * Like a Pixel Run animation, but the 'running' pixel has a trail behind it
     * where the pixels fade from `color1` to `color2` over ~20 iterations.
     */
    private val pixelRunWithTrail = { animation: AnimationData ->
        when (animation.direction) {
            Direction.FORWARD -> for (q in animation.startPixel..animation.endPixel) {
                setPixelColor(q, animation.color1)
                GlobalScope.launch(animationThreadPool) {
                    fadePixel(q, animation.color2.color, 60, 25)
                }
                delayBlocking(animation.delay)
            }
            Direction.BACKWARD -> for (q in animation.endPixel downTo animation.startPixel) {
                setPixelColor(q, animation.color1)
                GlobalScope.launch(animationThreadPool) {
                    fadePixel(q, animation.color2.color, 60, 25)
                }
                delayBlocking(animation.delay)
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
                setStripColorWithOffset(animation.color1 as PreparedColorContainer, m - animation.startPixel)
                delayBlocking(animation.delay)
            }
            Direction.BACKWARD -> for (m in animation.startPixel..animation.endPixel) {
                setStripColorWithOffset(animation.color1 as PreparedColorContainer, m - animation.startPixel)
                delayBlocking(animation.delay)
            }
        }
    }


    /**
     * Runs a Smooth Fade animation.
     */
    private val smoothFade = { animation: AnimationData ->
        for (i in animation.startPixel..animation.endPixel) {
            setStripColor((animation.color1 as PreparedColorContainer)[i])
            delayBlocking(animation.delay)
        }

    }


    /**
     * Runs a Sparkle animation.
     *
     * Each LED is changed to `color1` for delay milliseconds before reverting
     * to its original color. A separate thread is created for each pixel. Each
     * thread saves its pixel's original color, then waits for 0-5 seconds
     * before sparkling its pixel.
     */
    private val sparkle = { animation: AnimationData ->
        val deferred = (animation.startPixel..animation.endPixel).map { n ->
            GlobalScope.async(sparkleThreadPool) {
                val originalColor = getPixelColor(n)
                delay((random() * 5000).toLong() % 4950)
                setPixelColor(n, animation.color1)
                delay(animation.delay)
                setPixelColor(n, originalColor)
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
     * Similar to Sparkle but pixels fade back to `color2`.
     */
    private val sparkleFade = { animation: AnimationData ->
        val deferred = (animation.startPixel..animation.endPixel).map { n ->
            GlobalScope.async(sparkleThreadPool) {
                delay((random() * 5000).toLong())
                setPixelColor(n, animation.color1)
                GlobalScope.launch(sparkleThreadPool) {
                    fadePixel(n, animation.color2.color, 25)
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
                setPixelColor(n, animation.color1)
                delay(animation.delay)
            }
        }
        runBlocking {
            deferred.awaitAll()
        }
        Unit        // Ensure sparkleToColor is of type (AnimationData) -> Unit
    }


    /**
     * TODO (Katie)
     */
    @NonRepetitive
    private val stack = { animation: AnimationData ->
        when (animation.direction) {
            Direction.FORWARD -> for (q in animation.endPixel downTo animation.startPixel) {
                var originalColor: Long
                for (i in animation.startPixel until q) {
                    runBlocking {
                        locks[i]!!.tryWithLock {
                            originalColor = getPixelColor(i)
                            setPixelColor(i, animation.color1)
                            delay(animation.delay)
                            setPixelColor(i, originalColor)
                        }
                    }
                }
                setPixelColor(q, animation.color1)
            }
            Direction.BACKWARD -> for (q in animation.startPixel..animation.endPixel) {
                var originalColor: Long
                for (i in animation.endPixel downTo q) {
                    runBlocking {
                        locks[i]!!.tryWithLock {
                            originalColor = getPixelColor(i)
                            setPixelColor(i, animation.color1)
                            delay(animation.delay)
                            setPixelColor(i, originalColor)
                        }
                    }
                }
                setPixelColor(q, animation.color1)
            }
        }
    }


    private val stackOverflow = { animation: AnimationData ->
        // TODO: Add stackOverflow
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
                setPixelColor(i, animation.color1)
                delayBlocking(animation.delay)
            }
            Direction.FORWARD -> for (i in animation.startPixel..animation.endPixel) {
                setPixelColor(i, animation.color1)
                delayBlocking(animation.delay)
            }
        }
    }
}
//    /**
//     * TODO (Will)
//     * @param point1
//     * @param point2
//     * @param color1
//     * @param color2
//     * @param delay
//     * @param delayMod
//     */
//    fun multiAlternate(
//        point1: Int,
//        point2: Int,
//        color1: ColorContainer,
//        color2: ColorContainer,
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
//            setSectionColor(endptA, endptB, color1)
//            delay((delay * delayMod).toInt())
//            setSectionColor(endptA, endptB, color2)
//            delay((delay * delayMod).toInt())
//        }
//        GlobalScope.launch(newSingleThreadContext("Thread ${Thread.currentThread().name}-2")) {
//            setSectionColor(endptC, endptD, color1)
//            delay((delay * delayMod).toInt())
//            setSectionColor(endptC, endptD, color2)
//            delay((delay * delayMod).toInt())
//        }
//        GlobalScope.launch(newSingleThreadContext("Thread ${Thread.currentThread().name}-3")) {
//            setSectionColor(endptB, endptC, color2)
//            delay((delay * delayMod).toInt())
//            setSectionColor(endptB, endptC, color1)
//            delay((delay * delayMod).toInt())
//        }
//    }
//
//
//
//    /**
//     * TODO(Katie)
//     * @param pixelColor1
//     * @param pixelColor2
//     * @param pixelColor3
//     * @param pixelColor4
//     * @param pixelColor5
//     * @param delay
//     */
//    fun pixelMarathon(
//        pixelColor1: ColorContainer,
//        pixelColor2: ColorContainer,
//        pixelColor3: ColorContainer,
//        pixelColor4: ColorContainer,
//        pixelColor5: ColorContainer,
//        delay: Int = PixelMarathon.delayDefault
//    ) {
//        // TODO: Change to use threads from animationThreadPool
//
//        GlobalScope.launch(newSingleThreadContext("Thread ${Thread.currentThread().name}-1")) {
//            pixelRun(Direction.FORWARD, pixelColor5, delay = delay)
//        }
//        delay(100)
//        GlobalScope.launch(newSingleThreadContext("Thread ${Thread.currentThread().name}-2")) {
//            pixelRun(Direction.BACKWARD, pixelColor4, delay = delay)
//        }
//        delay(200)
//        GlobalScope.launch(newSingleThreadContext("Thread ${Thread.currentThread().name}-1")) {
//            pixelRun(Direction.BACKWARD, pixelColor2, delay = delay)
//        }
//        delay(300)
//        GlobalScope.launch(newSingleThreadContext("Thread ${Thread.currentThread().name}-2")) {
//            pixelRun(Direction.FORWARD, pixelColor3, delay = delay)
//        }
//        delay(400)
//        GlobalScope.launch(newSingleThreadContext("Thread ${Thread.currentThread().name}-3")) {
//            pixelRun(Direction.FORWARD, pixelColor1, delay = delay)
//        }
//        delay(500)
//        GlobalScope.launch(newSingleThreadContext("Thread ${Thread.currentThread().name}-4")) {
//            pixelRun(Direction.FORWARD, pixelColor2, delay = delay)
//        }
//        delay(100)
//        GlobalScope.launch(newSingleThreadContext("Thread ${Thread.currentThread().name}-5")) {
//            pixelRun(Direction.BACKWARD, pixelColor5, delay = delay)
//        }
//        delay(200)
//        GlobalScope.launch(newSingleThreadContext("Thread ${Thread.currentThread().name}-6")) {
//            pixelRun(Direction.BACKWARD, pixelColor3, delay = delay)
//        }
//        delay(300)
//        GlobalScope.launch(newSingleThreadContext("Thread ${Thread.currentThread().name}-7")) {
//            pixelRun(Direction.FORWARD, pixelColor4, delay = delay)
//        }
//        delay(200)
//        GlobalScope.launch(newSingleThreadContext("Thread ${Thread.currentThread().name}-8")) {
//            pixelRun(Direction.BACKWARD, pixelColor1, delay = delay)
//        }
//    }
//
//    /**
//     * @param stackDirection
//     * @param colorValues1
//     * @param delay
//     * @param delayMod
//     */
//    fun stack(
//        stackDirection: Direction,
//        colorValues1: ColorContainer,
//        delay: Int = Stack.delayDefault,
//        delayMod: Double = 1.0,
//        startPixel: Int = 0,
//        endPixel: Int = numLEDs - 1
//    ) {
//        if (stackDirection == Direction.FORWARD) {
//            for (q in endPixel downTo startPixel) {
//                var originalColor: ColorContainer
//                for (i in startPixel until q) {
//                    runBlocking {
//                        locks[i]!!.tryWithLock {
//                            originalColor = getPixelColor(i)
//                            setPixelColor(i, colorValues1)
//                            show()
//                            delay((delay * delayMod).toInt())
//                            setPixelColor(i, originalColor)
//                        }
//                    }
//                }
//                setPixelColor(q, colorValues1)
//                show()
//            }
//        } else if (stackDirection == Direction.BACKWARD) {
//            for (q in startPixel..endPixel) {
//                var originalColor: ColorContainer
//                for (i in endPixel downTo q) {
//                    runBlocking {
//                        locks[i]!!.tryWithLock {
//                            originalColor = getPixelColor(i)
//                            setPixelColor(i, colorValues1)
//                            show()
//                            delay((delay * delayMod).toInt())
//                            setPixelColor(i, originalColor)
//                        }
//                    }
//                }
//                setPixelColor(q, colorValues1)
//                show()
//            }
//        }
//    }
//
//
//    /**
//     * TODO(Katie)
//     * @param stackColor1
//     * @param stackColor2
//     */
//    fun stackOverflow(stackColor1: ColorContainer, stackColor2: ColorContainer) {
//        // TODO: Change to use threads from animationThreadPool
//        GlobalScope.launch(newSingleThreadContext("Thread ${Thread.currentThread().name}-1")) {
//            stack(Direction.FORWARD, stackColor1, delay = 2)
//        }
//        GlobalScope.launch(newSingleThreadContext("Thread ${Thread.currentThread().name}-2")) {
//            stack(Direction.BACKWARD, stackColor2, delay = 2)
//        }
//    }
