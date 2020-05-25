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

import animatedledstrip.animationutils.predefinedAnimLoadComplete
import animatedledstrip.leds.AnimatedLEDStrip
import animatedledstrip.utils.delayBlocking
import org.pmw.tinylog.Configuration
import org.pmw.tinylog.Level
import org.pmw.tinylog.LogEntry
import org.pmw.tinylog.writers.LogEntryValue
import org.pmw.tinylog.writers.Writer
import kotlin.test.assertTrue

fun checkAllPixels(testLEDs: AnimatedLEDStrip.Section, color: Long) {
    testLEDs.pixelTemporaryColorList.forEach {
        assertTrue(
            "Pixel check failed. Expected: $color on all pixels. Actual: ${testLEDs.pixelTemporaryColorList}"
        ) { it == color }
    }
}

fun checkAllProlongedPixels(testLEDs: AnimatedLEDStrip.Section, color: Long) {
    testLEDs.pixelProlongedColorList.forEach {
        assertTrue(
            "Pixel check failed. Expected: $color on all pixels. Actual: ${testLEDs.pixelProlongedColorList}"
        ) { it == color }
    }
}

fun checkPixels(leds: IntRange, testLEDs: AnimatedLEDStrip.Section, color: Long) {
    leds.forEach {
        assertTrue(
            "Pixel check failed. Expected: $color on pixels ${leds.first}..${leds.last}. Actual: ${testLEDs.pixelTemporaryColorList}"
        ) { testLEDs.pixelTemporaryColorList[it] == color }
    }
}

fun checkProlongedPixels(leds: IntRange, testLEDs: AnimatedLEDStrip.Section, color: Long) {
    leds.forEach {
        assertTrue(
            "Pixel check failed. Expected: $color on pixels ${leds.first}..${leds.last}. Actual: ${testLEDs.pixelProlongedColorList}"
        ) { testLEDs.pixelProlongedColorList[it] == color }
    }
}

fun awaitPredefinedAnimationsLoaded() {
    while (!predefinedAnimLoadComplete) delayBlocking(250)
}

class TestLogWriter : Writer {
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

    fun checkLogs(expectedLogs: Set<Pair<Level, String>>) {
        val actualLogs = logs.map { Pair(it.level, it.message) }.toSet()

        assertTrue("logs do not match:\nexpected: $expectedLogs\nactual: $actualLogs") {
            actualLogs.containsAll(expectedLogs)
        }
        assertTrue("logs do not match:\nexpected: $expectedLogs\nactual: $actualLogs") {
            expectedLogs.containsAll(actualLogs)
        }
    }

}
