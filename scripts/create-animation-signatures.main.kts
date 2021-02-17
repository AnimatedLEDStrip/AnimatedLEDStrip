#!/usr/bin/env kotlin

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

@file:DependsOn("co.touchlab:kermit-jvm:0.1.8")
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.4.2")
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.0.1")
@file:DependsOn("../build/libs/animatedledstrip-core-jvm-1.0.0-pre3.3.jar")

import animatedledstrip.colors.ColorContainer
import animatedledstrip.colors.ccpresets.RainbowColors
import animatedledstrip.colors.ccpresets.randomColorList
import animatedledstrip.leds.animationmanagement.AnimationToRunParams
import animatedledstrip.leds.animationmanagement.startAnimation
import animatedledstrip.leds.emulation.createNewEmulatedStrip
import animatedledstrip.leds.stripmanagement.LEDStrip
import animatedledstrip.leds.stripmanagement.StripInfo
import animatedledstrip.utils.ALSLogger
import co.touchlab.kermit.Severity
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

ALSLogger.minSeverity = Severity.Error

fun newLEDStrip(fileName: String): LEDStrip = createNewEmulatedStrip(StripInfo(
    numLEDs = 240,
    isRenderLoggingEnabled = true,
    renderLogFile = "$fileName.csv",
    rendersBetweenLogSaves = 100,
))

fun String.createSigName(): String =
    "(?<=[a-zA-Z ])[A-Z]|(?<=[ ])[a-z]|\\(".toRegex().replace(this) {
        "_${it.value}"
    }.replace("[\\s()]".toRegex(), "").toLowerCase()

val anims = listOf(
    AnimationToRunParams("AlterFade", ColorContainer.randomColorList(3), runCount = 6),
    AnimationToRunParams("Alternate", ColorContainer.randomColorList(3), runCount = 6),
    AnimationToRunParams("Bounce", ColorContainer.randomColorList(), runCount = 1),
    AnimationToRunParams("Bounce to Color", ColorContainer.randomColorList(), runCount = 1),
    AnimationToRunParams("Bubble Sort", mutableListOf(ColorContainer.RainbowColors), runCount = 1),
    AnimationToRunParams("Cat Toy", ColorContainer.randomColorList(), runCount = 30),
    AnimationToRunParams("Cat Toy to Color", ColorContainer.randomColorList(), runCount = 1),
    AnimationToRunParams("Color", ColorContainer.randomColorList(), runCount = 1),
    AnimationToRunParams("Fade to Color", ColorContainer.randomColorList(), runCount = 1),
    AnimationToRunParams("Fireworks", ColorContainer.randomColorList(5), runCount = 10),
    AnimationToRunParams("Heap Sort", mutableListOf(ColorContainer.RainbowColors), runCount = 1),
    AnimationToRunParams("Merge Sort Parallel", mutableListOf(ColorContainer.RainbowColors), runCount = 1),
    AnimationToRunParams("Merge Sort Sequential", mutableListOf(ColorContainer.RainbowColors), runCount = 1),
    AnimationToRunParams("Meteor", ColorContainer.randomColorList(), runCount = 1),
    AnimationToRunParams("Pixel Marathon", ColorContainer.randomColorList(5), runCount = 20),
    AnimationToRunParams("Pixel Run", ColorContainer.randomColorList(), runCount = 1),
    AnimationToRunParams("Plane Run", ColorContainer.randomColorList(), runCount = 1),
    AnimationToRunParams("Quick Sort Parallel", mutableListOf(ColorContainer.RainbowColors), runCount = 1),
    AnimationToRunParams("Quick Sort Sequential", mutableListOf(ColorContainer.RainbowColors), runCount = 1),
    AnimationToRunParams("Ripple", ColorContainer.randomColorList(), runCount = 10),
    AnimationToRunParams("Runway Lights", ColorContainer.randomColorList(), runCount = 1),
    AnimationToRunParams("Runway Lights to Color", ColorContainer.randomColorList(), runCount = 1),
    AnimationToRunParams("Smooth Chase", mutableListOf(ColorContainer.RainbowColors), runCount = 1),
    AnimationToRunParams("Smooth Fade", mutableListOf(ColorContainer.RainbowColors), runCount = 1),
    AnimationToRunParams("Sparkle", ColorContainer.randomColorList(), runCount = 1),
    AnimationToRunParams("Sparkle Fade", ColorContainer.randomColorList(), runCount = 1),
    AnimationToRunParams("Sparkle to Color", ColorContainer.randomColorList(), runCount = 1),
    AnimationToRunParams("Splat", ColorContainer.randomColorList(), runCount = 1),
    AnimationToRunParams("Stack", ColorContainer.randomColorList(), runCount = 1),
    AnimationToRunParams("Stack Overflow", ColorContainer.randomColorList(2), runCount = 1),
    AnimationToRunParams("Wave", ColorContainer.randomColorList(), runCount = 5),
    AnimationToRunParams("Wipe", ColorContainer.randomColorList(), runCount = 1),
)

val progressChannel = Channel<String>()

runBlocking {
    launch {
        val total = anims.size
        var completed = 0
        for (p in progressChannel) {
            completed++
            println("Completed $p ($completed/$total)")
            if (completed >= total) break
        }
    }

    anims.map {
        launch {
            val ledStrip = newLEDStrip(it.animation.createSigName())

            ledStrip.startAnimationCallback = {
                println("Running ${it.animationName}")
            }
            val scope = this
            ledStrip.endAnimationCallback = {
                scope.launch {
                    progressChannel.send(it.animation.info.name)
                }
            }

            ledStrip.renderer.startRendering()

            val anim = ledStrip.animationManager.startAnimation(it)

            anim.join()
            delay(200)
            ledStrip.renderer.stopRendering()
            ledStrip.renderer.close()
        }
    }.joinAll()
}
