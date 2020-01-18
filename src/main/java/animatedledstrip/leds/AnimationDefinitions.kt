/*
 *  Copyright (c) 2018-2020 AnimatedLEDStrip
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
import animatedledstrip.colors.ccpresets.EmptyColorContainer
import animatedledstrip.utils.delayBlocking
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

/* Definitions of animations */


/**
 * Runs an Alternate animation.
 *
 * Strip alternates between `pCols[0]` and `pCols[1]`, delaying `delay`
 * milliseconds between changes.
 */
val alternate: AnimatedLEDStrip.(AnimationData, CoroutineScope) -> Unit = { animation, _ ->
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
val bounce: AnimatedLEDStrip.(AnimationData, CoroutineScope) -> Unit = { animation, _ ->
    iterateOver(0..((animation.endPixel - animation.startPixel) / 2)) { i ->
        val baseAnimation = animation.copy(
            animation = Animation.PIXELRUN,
            startPixel = animation.startPixel + i,
            endPixel = animation.endPixel - i
        )

        runSequential(baseAnimation.copy(direction = Direction.FORWARD))
        setAndFadePixel(
            pixel = animation.endPixel - i,
            color = animation.pCols[0],
            amountOfOverlay = 25,
            delay = 50,
            context = parallelAnimationThreadPool
        )

        runSequential(baseAnimation.copy(direction = Direction.BACKWARD))
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
val bounceToColor: AnimatedLEDStrip.(AnimationData, CoroutineScope) -> Unit = { animation, _ ->
    iterateOver(0..(animation.endPixel - animation.startPixel) / 2) { i ->
        val baseAnimation = animation.copy(
            animation = Animation.PIXELRUN,
            startPixel = animation.startPixel + i,
            endPixel = animation.endPixel - i
        )
        runSequential(baseAnimation.copy(direction = Direction.FORWARD))
        setPixelColor(animation.endPixel - i, animation.pCols[0], prolonged = true)

        runSequential(baseAnimation.copy(direction = Direction.BACKWARD))
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
val catToy: AnimatedLEDStrip.(AnimationData, CoroutineScope) -> Unit = { animation, _ ->
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
    setPixelColor(pixel1, animation.pCols[0])
    delayBlocking((Math.random() * 2500).toLong())
    revertPixel(pixel1)
    runSequential(
        animation.copy(
            animation = Animation.PIXELRUN,
            startPixel = pixel2,
            endPixel = pixel1,
            direction = Direction.BACKWARD
        )
    )
    setPixelColor(pixel2, animation.pCols[0])
    delayBlocking((Math.random() * 2500).toLong())
    revertPixel(pixel2)
    runSequential(
        animation.copy(
            animation = Animation.PIXELRUN,
            startPixel = pixel2,
            endPixel = pixel3,
            direction = Direction.FORWARD
        )
    )
    setPixelColor(pixel3, animation.pCols[0])
    delayBlocking((Math.random() * 2500).toLong())
    revertPixel(pixel3)
    runSequential(
        animation.copy(
            animation = Animation.PIXELRUN,
            startPixel = pixel4,
            endPixel = pixel3,
            direction = Direction.BACKWARD
        )
    )
    setPixelColor(pixel4, animation.pCols[0])
    delayBlocking((Math.random() * 2500).toLong())
    revertPixel(pixel4)
    runSequential(
        animation.copy(
            animation = Animation.PIXELRUN,
            startPixel = pixel4,
            endPixel = pixel5,
            direction = Direction.FORWARD
        )
    )
    setPixelColor(pixel5, animation.pCols[0])
    delayBlocking((Math.random() * 2500).toLong())
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
val catToyToColor: AnimatedLEDStrip.(AnimationData, CoroutineScope) -> Unit = { animation, _ ->
    val pixels = indices.shuffled()
    var oldPixel = animation.startPixel
    for (newPixel in pixels) {
        runSequential(
            animation.copy(
                animation = Animation.PIXELRUN,
                endPixel = max(oldPixel, newPixel),
                startPixel = min(oldPixel, newPixel),
                direction = if (oldPixel > newPixel) Direction.BACKWARD else Direction.FORWARD
            )
        )
        setPixelColor(newPixel, animation.pCols[0], prolonged = true)
        delayBlocking((Math.random() * 2500).toLong())
        oldPixel = newPixel
    }
}

@NonRepetitive
val fadeToColor: AnimatedLEDStrip.(AnimationData, CoroutineScope) -> Unit = { animation, scope ->
    iterateOverPixels(animation) {
        prolongedColors[it] = animation.pCols[0][it]
        scope.launch(sparkleThreadPool) {
            fadePixel(it)
        }
    }
}

val fireworks: AnimatedLEDStrip.(AnimationData, CoroutineScope) -> Unit = { animation, scope ->
    val color = animation.colors.random()
    if (color != EmptyColorContainer) {
        runParallel(
            animation.copy(
                colors = listOf(color),
                animation = Animation.RIPPLE,
                center = randomPixelIn(animation)
            ),
            scope = scope
        )
        delayBlocking(animation.delay * 20)
    }
}

/**
 * Runs a Meteor animation.
 *
 * Like a Pixel Run animation, but the 'running' pixel has a trail behind it
 * where the pixels fade back from `pCols[0]`.
 */
val meteor: AnimatedLEDStrip.(AnimationData, CoroutineScope) -> Unit = { animation, _ ->
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
 *
 * Similar to Pixel Run but with multiple LEDs at a specified spacing.
 */
val multiPixelRun: AnimatedLEDStrip.(AnimationData, CoroutineScope) -> Unit = { animation, _ ->
    when (animation.direction) {
        Direction.FORWARD ->
            iterateOver(0 until animation.spacing) { s ->
                val s2 = s - 1 % animation.spacing
                iterateOver(animation.startPixel..animation.endPixel step animation.spacing) { i ->
                    if (i + s2 <= animation.endPixel) revertPixel(i + s2)
                    if (i + s <= animation.endPixel)
                        setPixelColor(i + s, animation.pCols[0], prolonged = false)
                }
                delayBlocking(animation.delay)
            }
        Direction.BACKWARD ->
            iterateOver(animation.spacing - 1 downTo 0) { s ->
                val s2 = if (s + 1 == animation.spacing) 0 else s + 1
                iterateOver(animation.startPixel..animation.endPixel step animation.spacing) { i ->
                    if (i + s2 <= animation.endPixel) revertPixel(i + s2)
                    if (i + s <= animation.endPixel)
                        setPixelColor(i + s, animation.pCols[0], prolonged = false)
                }
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
val multiPixelRunToColor: AnimatedLEDStrip.(AnimationData, CoroutineScope) -> Unit = { animation, _ ->
    when (animation.direction) {
        Direction.FORWARD ->
            iterateOver(animation.spacing - 1 downTo 0) { s ->
                iterateOver(animation.startPixel..animation.endPixel step animation.spacing) { i ->
                    if (i + s <= animation.endPixel)
                        setPixelColor(i + s, animation.pCols[0], prolonged = true)
                }
                delayBlocking(animation.delay)
            }
        Direction.BACKWARD ->
            iterateOver(0 until animation.spacing) { s ->
                iterateOver(animation.startPixel..animation.endPixel step animation.spacing) { i ->
                    if (i + s <= animation.endPixel)
                        setPixelColor(i + s, animation.pCols[0], prolonged = true)
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
val pixelMarathon: AnimatedLEDStrip.(AnimationData, CoroutineScope) -> Unit = { animation, scope ->
    val baseAnimation = AnimationData().animation(Animation.PIXELRUN)
        .direction(animation.direction).delay(animation.delay)

    runParallel(baseAnimation.copy().color(animation.pCols[4]), scope = scope)
    delayBlocking((Math.random() * 500).toLong())

    runParallel(baseAnimation.copy().color(animation.pCols[3]), scope = scope)
    delayBlocking((Math.random() * 500).toLong())

    runParallel(baseAnimation.copy().color(animation.pCols[1]), scope = scope)
    delayBlocking((Math.random() * 500).toLong())

    runParallel(baseAnimation.copy().color(animation.pCols[2]), scope = scope)
    delayBlocking((Math.random() * 500).toLong())

    runParallel(baseAnimation.copy().color(animation.pCols[0]), scope = scope)
    delayBlocking((Math.random() * 500).toLong())

    runParallel(baseAnimation.copy().color(animation.pCols[1]), scope = scope)
    delayBlocking((Math.random() * 500).toLong())

    runParallel(baseAnimation.copy().color(animation.pCols[4]), scope = scope)
    delayBlocking((Math.random() * 500).toLong())

    runParallel(baseAnimation.copy().color(animation.pCols[2]), scope = scope)
    delayBlocking((Math.random() * 500).toLong())

    runParallel(baseAnimation.copy().color(animation.pCols[3]), scope = scope)
    delayBlocking((Math.random() * 500).toLong())

    runParallel(baseAnimation.copy().color(animation.pCols[0]), scope = scope)
    delayBlocking((Math.random() * 500).toLong())
}


/**
 * Runs a Pixel Run animation.
 *
 * A pixel colored from `pCols[0]` runs along the strip.
 * Similar to Multi-Pixel Run but with only one pixel.
 */
val pixelRun: AnimatedLEDStrip.(AnimationData, CoroutineScope) -> Unit = { animation, _ ->
    when (animation.direction) {
        Direction.FORWARD ->
            iterateOverPixels(animation) {
                setPixelAndRevertAfterDelay(it, animation.pCols[0], animation.delay)
            }
        Direction.BACKWARD ->
            iterateOverPixelsReverse(animation) {
                setPixelAndRevertAfterDelay(it, animation.pCols[0], animation.delay)
            }
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
val ripple: AnimatedLEDStrip.(AnimationData, CoroutineScope) -> Unit = { animation, scope ->
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
val smoothChase: AnimatedLEDStrip.(AnimationData, CoroutineScope) -> Unit = { animation, _ ->
    when (animation.direction) {
        Direction.FORWARD ->
            iterateOverPixelsReverse(animation) { m ->
                setStripColorWithOffset(animation.pCols[0], m - animation.startPixel)
                delayBlocking(animation.delay)
            }
        Direction.BACKWARD ->
            iterateOverPixels(animation) { m ->
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
val smoothFade: AnimatedLEDStrip.(AnimationData, CoroutineScope) -> Unit = { animation, _ ->
    iterateOverPixels(animation) {
        setStripColor(animation.pCols[0][it])
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
val sparkle: AnimatedLEDStrip.(AnimationData, CoroutineScope) -> Unit = { animation, scope ->
    val jobs = (animation.startPixel..animation.endPixel).map { n ->
        scope.launch(sparkleThreadPool) {
            delayBlocking((Math.random() * 5000).toLong() % 4950)
            setPixelAndRevertAfterDelay(n, animation.pCols[0], animation.delay)
        }
    }
    runBlockingNonCancellable {
        jobs.joinAll()
    }
    Unit        // Ensure sparkle returns Unit
}


/**
 * Runs a Sparkle Fade animation.
 *
 * Similar to Sparkle but pixels fade back to their prolonged color.
 */
val sparkleFade: AnimatedLEDStrip.(AnimationData, CoroutineScope) -> Unit = { animation, scope ->
    val jobs = (animation.startPixel..animation.endPixel).map { n ->
        scope.launch(sparkleThreadPool) {
            delayBlocking((Math.random() * 5000).toLong())
            setAndFadePixel(
                pixel = n,
                color = animation.pCols[0],
                amountOfOverlay = 25,
                context = sparkleThreadPool
            )
            delayBlocking(animation.delay)
        }
    }
    runBlockingNonCancellable {
        jobs.joinAll()
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
val sparkleToColor: AnimatedLEDStrip.(AnimationData, CoroutineScope) -> Unit = { animation, scope ->
    val jobs = (animation.startPixel..animation.endPixel).map { n ->
        scope.launch(sparkleThreadPool) {
            delayBlocking((Math.random() * 5000).toLong() % 4950)
            setPixelColor(n, animation.pCols[0], prolonged = true)
            delayBlocking(animation.delay)
        }
    }
    runBlockingNonCancellable {
        jobs.joinAll()
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
val splat: AnimatedLEDStrip.(AnimationData, CoroutineScope) -> Unit = { animation, scope ->
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
val stack: AnimatedLEDStrip.(AnimationData, CoroutineScope) -> Unit = { animation, _ ->
    val baseAnimation = animation.copy(animation = Animation.PIXELRUN)

    when (animation.direction) {
        Direction.FORWARD ->
            iterateOverPixelsReverse(animation) {
                runSequential(baseAnimation.copy(endPixel = it))
                setPixelColor(it, animation.pCols[0], prolonged = true)
            }
        Direction.BACKWARD ->
            iterateOverPixels(animation) {
                runSequential(baseAnimation.copy(startPixel = it))
                setPixelColor(it, animation.pCols[0], prolonged = true)
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
val stackOverflow: AnimatedLEDStrip.(AnimationData, CoroutineScope) -> Unit = { animation, scope ->
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
val wipe: AnimatedLEDStrip.(AnimationData, CoroutineScope) -> Unit = { animation, _ ->
    when (animation.direction) {
        Direction.FORWARD ->
            iterateOverPixels(animation) {
                setPixelColor(it, animation.pCols[0], prolonged = true)
                delayBlocking(animation.delay)
            }
        Direction.BACKWARD ->
            iterateOverPixelsReverse(animation) {
                setPixelColor(it, animation.pCols[0], prolonged = true)
                delayBlocking(animation.delay)
            }
    }
}
