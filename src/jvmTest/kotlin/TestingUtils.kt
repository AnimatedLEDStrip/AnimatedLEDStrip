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

package animatedledstrip.test

import animatedledstrip.animations.Direction
import animatedledstrip.animations.predefined.color
import animatedledstrip.colors.PreparedColorContainer
import animatedledstrip.leds.animationmanagement.AnimationToRunParams
import animatedledstrip.leds.animationmanagement.RunningAnimationParams
import animatedledstrip.leds.colormanagement.pixelProlongedColorList
import animatedledstrip.leds.colormanagement.pixelTemporaryColorList
import animatedledstrip.leds.sectionmanagement.SectionManager
import io.kotest.core.Tag
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import org.pmw.tinylog.Configuration
import org.pmw.tinylog.Configurator
import org.pmw.tinylog.Level
import org.pmw.tinylog.LogEntry
import org.pmw.tinylog.writers.LogEntryValue
import org.pmw.tinylog.writers.Writer
import kotlin.test.assertTrue

object AnimationTestTag : Tag()

val newRunningAnimationParams: RunningAnimationParams
    get() = RunningAnimationParams(color, "", listOf(), "", "", -1,
                                   Direction.FORWARD, mapOf(), mapOf(), mapOf(),
                                   mapOf(), mapOf(), AnimationToRunParams())

fun haveProlongedColors(colors: PreparedColorContainer) = object : Matcher<SectionManager> {
    override fun test(value: SectionManager) =
        MatcherResult(passed = value.pixelProlongedColorList == colors.colors,
                      failureMessage = "Prolonged colors do not match:\nExpected:\n" +
                                       "    ${colors.colors}\nActual:\n    ${value.pixelProlongedColorList}",
                      negatedFailureMessage = "Prolonged colors match:\n" +
                                              "    ${value.pixelProlongedColorList}")
}

fun SectionManager.assertAllPixelProlongedColors(prolongedColor: Int) {
    val colors = pixelProlongedColorList
    colors.forEachIndexed { i, c ->
        assertTrue(
            "Pixel $i check failed (prolonged). Expected: $prolongedColor on all pixels. Actual: $colors"
        ) { c == prolongedColor }
    }
}

fun SectionManager.assertAllTemporaryPixels(color: Int) {
    stripManager.pixelTemporaryColorList.forEachIndexed { i, c ->
        assertTrue(
            "Pixel $i check failed (temporary). Expected: $color on all pixels. Actual (${
                startPixel + getPhysicalIndex(0)
            }:${endPixel + getPhysicalIndex(0)}): ${stripManager.pixelTemporaryColorList}"
        ) { c == color }
    }
}

fun SectionManager.assertAllProlongedPixels(color: Int) {
    stripManager.pixelProlongedColorList.forEachIndexed { i, c ->
        assertTrue(
            "Pixel $i check failed (prolonged). Expected: $color on all pixels. Actual (${
                startPixel + getPhysicalIndex(0)
            }:${endPixel + getPhysicalIndex(0)}): ${stripManager.pixelProlongedColorList}"
        ) { c == color }
    }
}

fun SectionManager.assertPixels(indices: IntRange, temporaryColor: Int, prolongedColor: Int) {
    assertTemporaryPixels(indices, temporaryColor)
    assertProlongedPixels(indices, prolongedColor)
}

fun SectionManager.assertTemporaryPixels(indices: IntRange, color: Int) {
    indices.forEach {
        assertTrue(
            "Pixel $it check failed (temporary). Expected: $color on pixels ${indices.first}..${indices.last}. Actual (${
                startPixel + getPhysicalIndex(0)
            }:${endPixel + getPhysicalIndex(0)}): ${stripManager.pixelTemporaryColorList}"
        ) { stripManager.pixelTemporaryColorList[it] == color }
    }
}

fun SectionManager.assertProlongedPixels(indices: IntRange, color: Int) {
    indices.forEach {
        assertTrue(
            "Pixel $it check failed (prolonged). Expected: $color on pixels ${indices.first}..${indices.last}. Actual (${
                startPixel + getPhysicalIndex(0)
            }:${endPixel + getPhysicalIndex(0)}): ${stripManager.pixelProlongedColorList}"
        ) { stripManager.pixelProlongedColorList[it] == color }
    }
}

/* Log Testing */

object TestLogWriter : Writer {
    private val logs = mutableSetOf<LogEntry>()

    fun clearLogs() = logs.clear()

    override fun getRequiredLogEntryValues(): MutableSet<LogEntryValue> =
        mutableSetOf(LogEntryValue.LEVEL, LogEntryValue.MESSAGE)

    override fun write(log: LogEntry) {
        logs.add(log)
    }

    override fun init(p0: Configuration?) {}

    override fun flush() {}

    override fun close() {}

    fun assertLogs(expectedLogs: Set<Pair<Level, String>>) {
        val actualLogs = logs.map { Pair(it.level, it.message) }.toSet()

        assertTrue(
            "logs do not match:\nexpected: $expectedLogs\nactual: $actualLogs\n" +
            "extra values in expected: ${expectedLogs.minus(actualLogs)}"
        ) {
            actualLogs.containsAll(expectedLogs)
        }
        assertTrue(
            "logs do not match:\nexpected: $expectedLogs\nactual: $actualLogs\n" +
            "extra values in actual: ${actualLogs.minus(expectedLogs)}"
        ) {
            expectedLogs.containsAll(actualLogs)
        }
    }
}

fun startLogCapture() {
    Configurator.currentConfig().addWriter(TestLogWriter, Level.DEBUG).activate()
    clearLogs()
}

fun stopLogCapture() {
    Configurator.currentConfig().removeWriter(TestLogWriter).activate()
}

fun assertLogs(expectedLogs: Set<Pair<Level, String>>) = TestLogWriter.assertLogs(expectedLogs)

fun clearLogs() = TestLogWriter.clearLogs()
