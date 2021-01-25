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

package animatedledstrip.leds.stripmanagement

import animatedledstrip.communication.SendableData
import animatedledstrip.leds.locationmanagement.Location
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data class for setting properties of a LED strip.
 * (Done this way to simplify device libraries so they don't break if
 * more properties are added)
 *
 * @property numLEDs Number of LEDs in the strip
 * @property pin Physical pin the strip is connected to
 * @property renderDelay Delay between renders sent to the strip
 * @property isRenderLoggingEnabled If logging should be enabled
 * @property renderLogFile File to write log output to
 * @property rendersBetweenLogSaves Renders between log saves
 * @property is1DSupported Should 1D animations be included in the supported animations list
 * @property is2DSupported Should 2D animations be included in the supported animations list
 * @property is3DSupported Should 3D animations be included in the supported animations list
 * @property ledLocations Locations in 3D space of all LEDs.
 * If null, strip is assumed to be one dimensional and with LEDs spaced equally.
 */
@Serializable
@SerialName("StripInfo")
data class StripInfo(
    val numLEDs: Int = 0,
    val pin: Int? = null,
    val renderDelay: Long = 10,
    val isRenderLoggingEnabled: Boolean = false,
    val renderLogFile: String? = null,
    val rendersBetweenLogSaves: Int = 1000,
    val is1DSupported: Boolean = true,
    val is2DSupported: Boolean = false,
    val is3DSupported: Boolean = false,
    val ledLocations: List<Location>? = null,
) : SendableData
