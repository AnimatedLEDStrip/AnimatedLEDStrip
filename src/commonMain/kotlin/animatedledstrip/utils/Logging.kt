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

package animatedledstrip.utils

import co.touchlab.kermit.Kermit
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity

object ALSLogger : Logger() {
    var minSeverity: Severity = Severity.Warn

    override fun isLoggable(severity: Severity): Boolean = severity >= minSeverity

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        println("$severity: ($tag) $message")
    }
}

object TestLogger : Logger() {
    private var trackLogs: Boolean = false
    var minSeverity: Severity = Severity.Warn

    fun startLogCapture() {
        trackLogs = true
    }

    fun stopLogCapture() {
        trackLogs = false
        logs.clear()
    }

    data class Log(val severity: Severity, val message: String, val tag: String)

    val logs: MutableList<Log> = mutableListOf()

    override fun isLoggable(severity: Severity): Boolean = trackLogs && severity >= minSeverity

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        logs.add(Log(severity, message, tag))
    }
}

// TODO Support additional loggers
val Logger = Kermit(ALSLogger, TestLogger).withTag("LEDs")

