/*
 * Copyright (c) 2018-2022 AnimatedLEDStrip
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

package animatedledstrip.leds.stripmanagement

import animatedledstrip.leds.colormanagement.LEDStripColorLogger
import animatedledstrip.leds.colormanagement.LEDStripColorManager
import animatedledstrip.utils.Logger
import kotlinx.coroutines.*

/**
 * Manages rendering the colors on the strip
 *
 * @param stripColorManager The color manager to get the color data from
 */
actual class LEDStripRenderer actual constructor(
    val ledStrip: NativeLEDStrip,
    val stripColorManager: LEDStripColorManager,
    val renderDelay: Long,
) {
    actual val stripColorLogger = LEDStripColorLogger(stripColorManager)

    @OptIn(DelicateCoroutinesApi::class)
    private val renderThread = newSingleThreadContext("Render Thread")

    actual var isRendering: Boolean = false

    actual fun startRendering() {
        isRendering = true
    }

    actual fun stopRendering() {
        isRendering = false
    }

    actual fun close() {
        job.cancel()
        renderThread.close()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private val job = GlobalScope.launch(renderThread) {
        var renderTime = 0
        val loopsBetweenRenders = renderDelay / 5
        val rolloverValue = loopsBetweenRenders * 1000000
        try {
            while (true) {
                if (isRendering)
                    try {
                        stripColorManager.pixelColors.forEach {
                            it.sendColorToStrip(
                                ledStrip,
                                doFade = renderTime % 2 == 0, // 2 * 5 iterations = 10 ms between fades
                            )
                        }
                        if (renderTime % loopsBetweenRenders == 0L) {
                            ledStrip.render()
                            stripColorLogger.saveStripState()
                        }
                    } catch (e: NullPointerException) {
                        Logger.e("Renderer: LEDStrip NullPointerException when rendering")
                        delay(1000)
                    }
                delay(5)
                renderTime++
                if (renderTime >= rolloverValue)
                    renderTime = 0
            }
        } finally {
            while (stripColorLogger.renderNum < stripColorManager.stripManager.stripInfo.rendersBetweenLogSaves)
                stripColorLogger.saveStripState()
        }
    }
}
