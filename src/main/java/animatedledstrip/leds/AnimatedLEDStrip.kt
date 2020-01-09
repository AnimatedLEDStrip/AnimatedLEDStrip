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

package animatedledstrip.leds

import animatedledstrip.animationutils.*
import animatedledstrip.utils.delayBlocking
import kotlinx.coroutines.*
import org.pmw.tinylog.Logger
import java.lang.Math.random
import kotlin.math.max
import kotlin.math.min

/**
 * A subclass of [LEDStrip] adding animations.
 *
 * @param stripInfo Information about this strip, such as number of
 * LEDs, etc.
 */
abstract class AnimatedLEDStrip(
    stripInfo: StripInfo
) : LEDStrip(stripInfo) {


    /* Thread pools */

    /**
     * Multiplier used when calculating thread pool sizes
     */
    private val threadCount: Int = stripInfo.threadCount ?: 100

    /**
     * A pool of threads used to run animations.
     * (Animations spawned by other animations use the [parallelAnimationThreadPool])
     */
    @Suppress("EXPERIMENTAL_API_USAGE")
    val animationThreadPool =
        newFixedThreadPoolContext(threadCount, "Animation Pool")

    /**
     * A pool of threads to be used for animations that spawn new sub-threads
     * (with the exception of sparkle-type animations, those use the
     * [sparkleThreadPool]).
     */
    @Suppress("EXPERIMENTAL_API_USAGE")
    val parallelAnimationThreadPool =
        newFixedThreadPoolContext(3 * threadCount, "Parallel Animation Pool")

    /**
     * A pool of threads to be used for sparkle-type animations due to the
     * number of threads a concurrent sparkle animation uses. This prevents
     * memory leaks caused by the overhead associated with creating new threads.
     */
    @Suppress("EXPERIMENTAL_API_USAGE")
    val sparkleThreadPool =
        newFixedThreadPoolContext(numLEDs + 1, "Sparkle Pool")


    // TODO: Remove when multiPixelRun is refactored
    private val pixelSetLists = mutableMapOf<Triple<Int, Int, Int>, MutableList<MutableList<Int>>>()


    /* Track running animations */

    /**
     * Class for tracking a currently running animation
     *
     * @property animation An `AnimationData` instance with the properties of the animation
     * @property id The string ID representing the animation
     * @property job The `Job` that is running the animation
     */
    data class RunningAnimation(
        val animation: AnimationData,
        val id: String,
        val job: Job
    ) {
        /**
         * Cancel the coroutine running the animation
         * (in other words, end the animation after its current iteration is complete)
         */
        internal fun cancel(message: String, cause: Throwable? = null) = job.cancel(message, cause)
    }

    /**
     * Map containing all `RunningAnimation` instances
     */
    val runningAnimations = RunningAnimationMap()


    /* Define custom animations */

    /**
     * Map containing defined custom animations.
     */
    private val customAnimationMap =
        mutableMapOf<String, (AnimationData, CoroutineScope) -> Unit>()

    // TODO: Add addCustomAnimation function


    /* Add and remove/end animations */

    /**
     * Add a new animation
     *
     * @param animId Optional `String` parameter for setting the ID of the animation.
     * If not set, ID will be set to a random number between 0 and 100000000.
     */
    fun addAnimation(animation: AnimationData, animId: String? = null): RunningAnimation? {
        return if (animation.animation == Animation.ENDANIMATION) {
            // Special "Animation" type that the client sends to end an animation
            endAnimation(animation)
            null
        } else {
            val id = animId ?: (random() * 100000000).toInt().toString()
            animation.id = id
            val job = run(animation)
            Logger.trace(job)
            if (job != null) {
                runningAnimations[id] = RunningAnimation(animation, id, job)
            }
            // Will return null if job was null because runningAnimations[id] would not have been set
            return runningAnimations[id]
        }
    }

    /**
     * End animation by ID
     */
    fun endAnimation(id: String) {
        runningAnimations[id]?.endAnimation()
            ?: run {
                Logger.warn { "Animation $id not running" }
                runningAnimations.remove(id)
                return
            }
    }

    /**
     * End animation by ID (by extracting ID from the `AnimationData` instance)
     */
    fun endAnimation(animation: AnimationData?) {
        endAnimation(animation?.id ?: return)
    }


    /* Callbacks */

    /**
     * Callback run before the first iteration of the animation
     */
    var startAnimationCallback: ((AnimationData) -> Any?)? = null

    /**
     * Callback run after the last iteration of the animation (but before the
     * animation is remove from `runningAnimations`)
     */
    var endAnimationCallback: ((AnimationData) -> Any?)? = null


    /* Run animations */

    /**
     * Run an animation
     *
     * @param animation An [AnimationData] instance with details about the
     * animation to run
     * @param threadPool Optionally specify the thread pool to run the new
     * animation in
     * @param scope Optionally specify the parent `CoroutineScope` for the
     * coroutine that will run the animation
     */
    internal fun run(
        animation: AnimationData,
        threadPool: ExecutorCoroutineDispatcher = animationThreadPool,
        scope: CoroutineScope = GlobalScope,
        subAnimation: Boolean = false
    ): Job? {
        animation.prepare(this)
        Logger.trace("Starting $animation")

        val animationFunction: (AnimationData, CoroutineScope) -> Unit = when (animation.animation) {
            Animation.ALTERNATE -> alternate
            Animation.BOUNCE -> bounce
            Animation.BOUNCETOCOLOR -> bounceToColor
            Animation.CATTOY -> catToy
            Animation.CATTOYTOCOLOR -> catToyToColor
            Animation.COLOR -> run {
                setStripColor(animation.pCols[0], prolonged = true)
                null
            }
            Animation.FIREWORKS -> fireworks
            Animation.METEOR -> meteor
            Animation.MULTIPIXELRUN -> multiPixelRun
            Animation.MULTIPIXELRUNTOCOLOR -> multiPixelRunToColor
            Animation.PIXELMARATHON -> pixelMarathon
            Animation.PIXELRUN -> pixelRun
            Animation.RIPPLE -> ripple
            Animation.SMOOTHCHASE -> smoothChase
            Animation.SMOOTHFADE -> smoothFade
            Animation.SPARKLE -> sparkle
            Animation.SPARKLEFADE -> sparkleFade
            Animation.SPARKLETOCOLOR -> sparkleToColor
            Animation.SPLAT -> splat
            Animation.STACK -> stack
            Animation.STACKOVERFLOW -> stackOverflow
            Animation.WIPE -> wipe
            Animation.CUSTOMANIMATION, Animation.CUSTOMREPETITIVEANIMATION ->
                customAnimationMap[animation.id] ?: run {
                    Logger.warn("Custom animation ${animation.id} not found")
                    null
                }
            else -> run {
                Logger.warn("Animation ${animation.animation} not supported by AnimatedLEDStrip")
                null
            }
        } ?: return null

        return scope.launch(threadPool) {
            if (!subAnimation) startAnimationCallback?.invoke(animation)

            val isContinuous = animation.isContinuous()
            do {
                Logger.trace("Run ${animation.id}: $isActive $isContinuous")
                animationFunction.invoke(animation, this)
            } while (isActive && isContinuous)

            if (!subAnimation) {
                endAnimationCallback?.invoke(animation)
                runningAnimations.remove(animation.id)
            }
        }
    }

    /**
     * Start an animation in a child coroutine.
     *
     * Note: Animation is not run continuously unless if [continuous] is `true`
     * (even if the AnimationData instance has `continuous` as true).
     *
     * @param scope The parent `CoroutineScope` for the coroutine that will
     * run the animation
     * @param pool The pool of threads to start the animation in
     * @return The `Job` associated with the new coroutine
     */
    fun runParallel(
        animation: AnimationData,
        scope: CoroutineScope,
        pool: ExecutorCoroutineDispatcher = parallelAnimationThreadPool,
        continuous: Boolean = false
    ): Job? {
        return run(
            animation.copy(continuous = continuous),
            threadPool = pool,
            scope = scope,
            subAnimation = true
        )
    }

    /**
     * Start an animation and wait for it to complete.
     *
     * Note: Animation is not run continuously unless if [continuous] is `true`
     * (even if the AnimationData instance has `continuous` as true).
     */
    fun runSequential(animation: AnimationData, continuous: Boolean = false) = runBlocking {
        val job = run(
            animation.copy(continuous = continuous),
            threadPool = parallelAnimationThreadPool,
            scope = this,
            subAnimation = true
        )
        job?.join()
        Unit
    }


    /* Animation definitions */

    /**
     * Runs an Alternate animation.
     *
     * Strip alternates between `pCols[0]` and `pCols[1]`, delaying `delay`
     * milliseconds between changes.
     */
    private val alternate: (AnimationData, CoroutineScope) -> Unit = { animation, _ ->
        setSectionColor(animation.startPixel, animation.endPixel, animation.pCols[0], prolonged = true)
        delayBlocking(animation.delay)
        setSectionColor(animation.startPixel, animation.endPixel, animation.pCols[1], prolonged = true)
        delayBlocking(animation.delay)
    }


    /**
     * Runs a Bounce animation.
     *
     * Similar to Bounce to Color but the pixels at the end of each bounce
     * fade back to their prolonged color after being set from `pCols[0]`.
     * Note that this animation has a quadratic time complexity, meaning it
     * gets very long very quickly.
     */
    private val bounce: (AnimationData, CoroutineScope) -> Unit = { animation, _ ->
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
     * Pixel 'bounces' back and forth, leaving behind a pixel set from
     * `pCols[0]` at each end like Stack, eventually ending in the middle.
     * Note that this animation has a quadratic time complexity, meaning it
     * gets very long very quickly.
     */
    @NonRepetitive
    private val bounceToColor: (AnimationData, CoroutineScope) -> Unit = { animation, _ ->
        for (i in 0..((animation.endPixel - animation.startPixel) / 2)) {
            for (j in (animation.startPixel + i)..(animation.endPixel - i))
                setPixelAndRevertAfterDelay(j, animation.pCols[0], animation.delay)
            setPixelColor(animation.endPixel - i, animation.pCols[0], prolonged = true)

            for (j in animation.endPixel - i - 1 downTo (i + animation.startPixel))
                setPixelAndRevertAfterDelay(j, animation.pCols[0], animation.delay)
            setPixelColor(animation.startPixel + i, animation.pCols[0], prolonged = true)
        }
        if ((animation.endPixel - animation.startPixel) % 2 == 1) {
            setPixelColor(
                (animation.endPixel - animation.startPixel) / 2 + animation.startPixel,
                animation.pCols[0],
                prolonged = true
            )
        }
    }

    /**
     * Runs a Cat Toy animation.
     *
     * Entertain your cat with a pixel running back and forth to random locations,
     * waiting for up to 5 seconds between movements.
     */
    private val catToy: (AnimationData, CoroutineScope) -> Unit = { animation, _ ->
        val pixel1 = randomPixelIn(animation)
        val pixel2 = randomPixelIn(animation.startPixel, pixel1)
        val pixel3 = randomPixelIn(pixel2, animation.endPixel)
        val pixel4 = randomPixelIn(animation.startPixel, pixel3)
        val pixel5 = randomPixelIn(pixel4, animation.endPixel)

        runSequential(
            animation.copy(
                animation = Animation.PIXELRUN,
                endPixel = pixel1,
                direction = Direction.FORWARD
            )
        )
        setPixelColor(pixel1, animation.colors[0])
        delayBlocking((random() * 2500).toLong())
        revertPixel(pixel1)
        runSequential(
            animation.copy(
                animation = Animation.PIXELRUN,
                startPixel = pixel2,
                endPixel = pixel1,
                direction = Direction.BACKWARD
            )
        )
        setPixelColor(pixel2, animation.colors[0])
        delayBlocking((random() * 2500).toLong())
        revertPixel(pixel2)
        runSequential(
            animation.copy(
                animation = Animation.PIXELRUN,
                startPixel = pixel2,
                endPixel = pixel3,
                direction = Direction.FORWARD
            )
        )
        setPixelColor(pixel3, animation.colors[0])
        delayBlocking((random() * 2500).toLong())
        revertPixel(pixel3)
        runSequential(
            animation.copy(
                animation = Animation.PIXELRUN,
                startPixel = pixel4,
                endPixel = pixel3,
                direction = Direction.BACKWARD
            )
        )
        setPixelColor(pixel4, animation.colors[0])
        delayBlocking((random() * 2500).toLong())
        revertPixel(pixel4)
        runSequential(
            animation.copy(
                animation = Animation.PIXELRUN,
                startPixel = pixel4,
                endPixel = pixel5,
                direction = Direction.FORWARD
            )
        )
        setPixelColor(pixel5, animation.colors[0])
        delayBlocking((random() * 2500).toLong())
        revertPixel(pixel5)
        runSequential(
            animation.copy(
                animation = Animation.PIXELRUN,
                endPixel = pixel5,
                direction = Direction.BACKWARD
            )
        )
    }

    /**
     * Runs a Cat Toy animation that fills in.
     *
     * Entertain your cat or kids with a pixel running back and forth to random locations and filling the entire strip with color,
     * waiting for up to 5 seconds between movements.
     */
    @NonRepetitive
    private val catToyToColor: (AnimationData, CoroutineScope) -> Unit = { animation, _ ->
        val pixels = indices.shuffled()
        var oldPixel = 0
        for (newPixel in pixels) {
            runSequential(
                    animation.copy(
                            animation = Animation.PIXELRUN,
                            endPixel = max(oldPixel, newPixel),
                            startPixel = min(oldPixel, newPixel),
                            direction = if (oldPixel > newPixel) {Direction.BACKWARD}
                            else {Direction.FORWARD}
                    )
            )
            setPixelColor(newPixel, animation.colors[0], prolonged = true)
            delayBlocking((random() * 2500).toLong())
            oldPixel=newPixel
        }
    }

    private val fireworks: (AnimationData, CoroutineScope) -> Unit = {animation, _ ->
        val centerPix = indices.shuffled()

        for (newCenter in centerPix) {
            runSequential(
                animation.copy(
                    animation = Animation.RIPPLE,
                    center = newCenter
                )
            )
            delayBlocking(animation.delay * 40)

        }
    }

    /**
     * Runs a Meteor animation.
     *
     * Like a Pixel Run animation, but the 'running' pixel has a trail behind it
     * where the pixels fade back from `pCols[0]`.
     */
    private val meteor: (AnimationData, CoroutineScope) -> Unit = { animation, _ ->
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
    private val multiPixelRun: (AnimationData, CoroutineScope) -> Unit = { animation, _ ->
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
     * Similar to Multi-Pixel Run but LEDs do not revert back to their prolonged
     * color.
     */
    @NonRepetitive
    private val multiPixelRunToColor: (AnimationData, CoroutineScope) -> Unit = { animation, _ ->
        when (animation.direction) {
            Direction.BACKWARD -> for (q in 0 until animation.spacing) {
                for (i in animation.startPixel..animation.endPixel step animation.spacing) {
                    if (i + (-(q - (animation.spacing - 1))) > animation.endPixel) continue
                    setPixelColor(
                        i + (-(q - (animation.spacing - 1))),
                        animation.pCols[0],
                        prolonged = true
                    )
                }
                delayBlocking(animation.delay)
            }
            Direction.FORWARD -> for (q in animation.spacing - 1 downTo 0) {
                for (i in animation.startPixel..animation.endPixel step animation.spacing) {
                    if (i + (-(q - (animation.spacing - 1))) > animation.endPixel) continue
                    setPixelColor(
                        i + (-(q - (animation.spacing - 1))),
                        animation.pCols[0],
                        prolonged = true
                    )
                }
                delayBlocking(animation.delay)
            }
        }
    }


    /**
     * Runs a Pixel Marathon animation.
     *
     * Watch 10 pixels (2 of each color per iteration) race each other
     * along the strip (or strip section).
     */
    private val pixelMarathon: (AnimationData, CoroutineScope) -> Unit = { animation, scope ->
        val baseAnimation = AnimationData().animation(Animation.PIXELRUN)
            .direction(animation.direction).delay(animation.delay)

        runParallel(baseAnimation.copy().color(animation.pCols[4]), scope = scope)
        delayBlocking((random() * 500).toLong())

        runParallel(baseAnimation.copy().color(animation.pCols[3]), scope = scope)
        delayBlocking((random() * 500).toLong())

        runParallel(baseAnimation.copy().color(animation.pCols[1]), scope = scope)
        delayBlocking((random() * 500).toLong())

        runParallel(baseAnimation.copy().color(animation.pCols[2]), scope = scope)
        delayBlocking((random() * 500).toLong())

        runParallel(baseAnimation.copy().color(animation.pCols[0]), scope = scope)
        delayBlocking((random() * 500).toLong())

        runParallel(baseAnimation.copy().color(animation.pCols[1]), scope = scope)
        delayBlocking((random() * 500).toLong())

        runParallel(baseAnimation.copy().color(animation.pCols[4]), scope = scope)
        delayBlocking((random() * 500).toLong())

        runParallel(baseAnimation.copy().color(animation.pCols[2]), scope = scope)
        delayBlocking((random() * 500).toLong())

        runParallel(baseAnimation.copy().color(animation.pCols[3]), scope = scope)
        delayBlocking((random() * 500).toLong())

        runParallel(baseAnimation.copy().color(animation.pCols[0]), scope = scope)
        delayBlocking((random() * 500).toLong())
    }


    /**
     * Runs a Pixel Run animation.
     *
     * A pixel colored from `pCols[0]` runs along the strip.
     * Similar to Multi-Pixel Run but with only one pixel.
     */
    private val pixelRun: (AnimationData, CoroutineScope) -> Unit = { animation, _ ->
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
     * from `center`, stopping after traveling `distance` or at
     * the end of the strip/section, whichever comes first. Does not wait
     * for the Meteor animations to be complete before returning,
     * giving a ripple-like appearance when run continuously.
     */
    @Radial
    private val ripple: (AnimationData, CoroutineScope) -> Unit = { animation, scope ->
        val baseAnimation = AnimationData()
            .animation(Animation.METEOR)
            .color(animation.pCols[0])
            .delay(animation.delay)

        runParallel(
            baseAnimation.copy(
                startPixel = animation.center,
                endPixel = min(animation.center + animation.distance, animation.endPixel),
                direction = Direction.FORWARD
            ),
            scope = scope
        )
        runParallel(
            baseAnimation.copy(
                startPixel = max(animation.center - animation.distance, animation.startPixel),
                endPixel = animation.center,
                direction = Direction.BACKWARD
            ),
            scope = scope
        )
        delayBlocking(animation.delay * 20)
    }


    /**
     * Runs a Smooth Chase animation.
     *
     * Each pixel is set to its respective color in `pCols[0]`. Then, if the
     * direction is [Direction].`FORWARD`, each pixel is set to
     * `pCols[0][i + 1]`, then `pCols[0][i + 2]`, etc. to create the illusion
     * that the animation is 'moving'. If the direction is
     * [Direction].`BACKWARD`, the same happens but with indices `i`, `i-1`,
     * `i-2`, etc.
     */
    private val smoothChase: (AnimationData, CoroutineScope) -> Unit = { animation, _ ->
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
     * while fading through `pCols[0]`.
     */
    private val smoothFade: (AnimationData, CoroutineScope) -> Unit = { animation, _ ->
        for (i in animation.startPixel..animation.endPixel) {
            setStripColor(animation.pCols[0][i])
            delayBlocking(animation.delay)
        }
    }


    /**
     * Runs a Sparkle animation.
     *
     * Each LED is changed to `pCols[0]` for delay milliseconds before reverting
     * to its prolonged color. A separate thread is created for each pixel. Each
     * thread waits up to 5 seconds before sparkling its pixel.
     */
    private val sparkle: (AnimationData, CoroutineScope) -> Unit = { animation, scope ->
        val deferred = (animation.startPixel..animation.endPixel).map { n ->
            scope.async(sparkleThreadPool) {
                delayBlocking((random() * 5000).toLong() % 4950)
                setPixelAndRevertAfterDelay(n, animation.pCols[0], animation.delay)
            }
        }
        runBlocking {
            deferred.awaitAll()
        }
        Unit        // Ensure sparkle returns Unit
    }


    /**
     * Runs a Sparkle Fade animation.
     *
     * Similar to Sparkle but pixels fade back to their prolonged color.
     */
    private val sparkleFade: (AnimationData, CoroutineScope) -> Unit = { animation, scope ->
        val deferred = (animation.startPixel..animation.endPixel).map { n ->
            scope.async(sparkleThreadPool) {
                delayBlocking((random() * 5000).toLong())
                setAndFadePixel(
                    pixel = n,
                    color = animation.pCols[0],
                    amountOfOverlay = 25,
                    context = sparkleThreadPool
                )
                delayBlocking(animation.delay)
            }
        }
        runBlocking {
            deferred.awaitAll()
        }
        Unit        // Ensure sparkleFade returns Unit
    }

    /**
     * Runs a Sparkle To Color animation.
     *
     * Very similar to the Sparkle animation, but the LEDs are not reverted to their
     * prolonged color after the sparkle. (Their prolonged color is changed as well).
     * A separate thread is created for each pixel. Each thread waits up to 5 seconds
     * before sparkling its pixel.
     */
    @NonRepetitive
    private val sparkleToColor: (AnimationData, CoroutineScope) -> Unit = { animation, scope ->
        val deferred = (animation.startPixel..animation.endPixel).map { n ->
            scope.async(sparkleThreadPool) {
                delayBlocking((random() * 5000).toLong() % 4950)
                setPixelColor(n, animation.pCols[0], prolonged = true)
                delayBlocking(animation.delay)
            }
        }
        runBlocking {
            deferred.awaitAll()
        }
        Unit        // Ensure sparkleToColor returns Unit
    }


    /**
     * Runs a Splat animation.
     *
     * Similar to a Ripple but the pixels don't fade back.
     * Runs two Wipe animations in opposite directions starting
     * from `center`, stopping after traveling `distance` or at
     * the end of the strip/section, whichever comes first.
     */
    @NonRepetitive
    @Radial
    private val splat: (AnimationData, CoroutineScope) -> Unit = { animation, scope ->
        val baseAnimation = AnimationData()
            .animation(Animation.WIPE)
            .color(animation.pCols[0])
            .delay(animation.delay)

        runParallelAndJoin(
            scope,
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


    /**
     * Runs a Stack animation.
     *
     * Pixels are run from one end of the strip/section to the other,
     * 'stacking' up. Each pixel has to travel a shorter distance than
     * the last. Note that this animation has a quadratic time complexity,
     * meaning it gets very long very quickly.
     */
    @NonRepetitive
    private val stack: (AnimationData, CoroutineScope) -> Unit = { animation, _ ->
        when (animation.direction) {
            Direction.FORWARD -> for (q in animation.endPixel downTo animation.startPixel) {
                for (i in animation.startPixel until q)
                    setPixelAndRevertAfterDelay(i, animation.pCols[0], animation.delay)
                setPixelColor(q, animation.pCols[0], prolonged = true)
            }
            Direction.BACKWARD -> for (q in animation.startPixel..animation.endPixel) {
                for (i in animation.endPixel downTo q)
                    setPixelAndRevertAfterDelay(i, animation.pCols[0], animation.delay)
                setPixelColor(q, animation.pCols[0], prolonged = true)
            }
        }
    }


    /**
     * Runs a StackOverflow animation.
     *
     * Two Stack animations are started from opposite ends of the strip/section.
     * The stacks meet in the middle and 'overflow' their half. And yes, the pun
     * was very much intended. Note that this animation has a quadratic time
     * complexity, meaning it gets very long very quickly.
     */
    private val stackOverflow: (AnimationData, CoroutineScope) -> Unit = { animation, scope ->
        val baseAnimation = AnimationData()
            .animation(Animation.STACK)
            .delay(animation.delay)

        runParallelAndJoin(
            scope,
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
     * prolonged color.
     */
    @NonRepetitive
    private val wipe: (AnimationData, CoroutineScope) -> Unit = { animation, _ ->
        when (animation.direction) {
            Direction.FORWARD -> iterateOverPixels(animation) {
                setPixelColor(it, animation.pCols[0], prolonged = true)
                delayBlocking(animation.delay)
            }
            Direction.BACKWARD -> iterateOverPixelsReverse(animation) {
                setPixelColor(it, animation.pCols[0], prolonged = true)
                delayBlocking(animation.delay)
            }
        }
    }
}
