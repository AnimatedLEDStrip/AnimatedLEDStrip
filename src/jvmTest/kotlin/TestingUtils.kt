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

package animatedledstrip.test

import animatedledstrip.leds.AnimatedLEDStrip
import org.pmw.tinylog.Configuration
import org.pmw.tinylog.Configurator
import org.pmw.tinylog.Level
import org.pmw.tinylog.LogEntry
import org.pmw.tinylog.writers.LogEntryValue
import org.pmw.tinylog.writers.Writer
import kotlin.test.assertTrue


fun AnimatedLEDStrip.Section.assertAllPixels(color: Long) {
    assertAllTemporaryPixels(color)
    assertAllProlongedPixels(color)
}

fun AnimatedLEDStrip.Section.assertAllTemporaryPixels(color: Long) {
    pixelTemporaryColorList.forEachIndexed { i, c ->
        assertTrue(
            "Pixel $i check failed (temporary). Expected: $color on all pixels. Actual (${startPixel + physicalStart}:${endPixel + physicalStart}): $pixelTemporaryColorList"
        ) { c == color }
    }
}

fun AnimatedLEDStrip.Section.assertAllProlongedPixels(color: Long) {
    pixelProlongedColorList.forEachIndexed { i, c ->
        assertTrue(
            "Pixel $i check failed (prolonged). Expected: $color on all pixels. Actual (${startPixel + physicalStart}:${endPixel + physicalStart}): $pixelProlongedColorList"
        ) { c == color }
    }
}

fun AnimatedLEDStrip.Section.assertPixels(indices: IntRange, color: Long) {
    assertTemporaryPixels(indices, color)
    assertProlongedPixels(indices, color)
}

fun AnimatedLEDStrip.Section.assertTemporaryPixels(indices: IntRange, color: Long) {
    indices.forEach {
        assertTrue(
            "Pixel $it check failed (temporary). Expected: $color on pixels ${indices.first}..${indices.last}. Actual (${startPixel + physicalStart}:${endPixel + physicalStart}): $pixelTemporaryColorList"
        ) { pixelTemporaryColorList[it] == color }
    }
}

fun AnimatedLEDStrip.Section.assertProlongedPixels(indices: IntRange, color: Long) {
    indices.forEach {
        assertTrue(
            "Pixel $it check failed (prolonged). Expected: $color on pixels ${indices.first}..${indices.last}. Actual (${startPixel + physicalStart}:${endPixel + physicalStart}): $pixelProlongedColorList"
        ) { pixelProlongedColorList[it] == color }
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
