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

package animatedledstrip.leds.colormanagement

import animatedledstrip.colors.b
import animatedledstrip.colors.g
import animatedledstrip.colors.r
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Logs the strip state from each render to a file
 *
 * @param stripColorManager The color manager to get the state data from
 */
actual class LEDStripColorLogger actual constructor(
    val stripColorManager: LEDStripColorManager,
) {

    private val fileName = when (stripColorManager.stripManager.stripInfo.debugFile) {
        null -> "signature_${SimpleDateFormat("MMDDYY_hhmmss").format(Date())}.csv"
        else -> {
            if (stripColorManager.stripManager.stripInfo.debugFile.endsWith(".csv"))
                stripColorManager.stripManager.stripInfo.debugFile
            else "${stripColorManager.stripManager.stripInfo.debugFile}.csv"
        }
    }

    private val saveStateBuffer = StringBuilder()
    private val saveStateChannel: Channel<String> = Channel(Channel.UNLIMITED)

    init {
        // Coroutine that handles collecting and saving the state data
        GlobalScope.launch {
            var renderNum = 0

            for (saveState in saveStateChannel) {
                renderNum++
                if (stripColorManager.stripManager.stripInfo.isDebugEnabled) {
                    saveStateBuffer.appendLine(saveState)
                    if (renderNum >= stripColorManager.stripManager.stripInfo.rendersBetweenDebugOutputs) {
                        launch(Dispatchers.IO) {
                            FileWriter(fileName, true).append(saveStateBuffer.toString()).close()
                            saveStateBuffer.clear()
                        }.join()
                        renderNum = 0
                    }
                }
            }
        }
    }

    /**
     * Save the strip's current state
     */
    actual suspend fun saveStripState() {
        saveStateChannel.send(
            stripColorManager.pixelActualColorList.joinToString(separator = ",") { "${it.r},${it.g},${it.b}" }
        )
    }
}
