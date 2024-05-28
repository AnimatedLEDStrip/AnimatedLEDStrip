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

package animatedledstrip.test.leds.stripmanagement

import animatedledstrip.communication.decodeJson
import animatedledstrip.communication.serializer
import animatedledstrip.communication.toUTF8String
import animatedledstrip.leds.stripmanagement.StripInfo
import animatedledstrip.test.filteredStringArb
import animatedledstrip.test.locationArb
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import kotlinx.serialization.encodeToString

class StripInfoTest : StringSpec(
    {
        "encode JSON" {
            checkAll(
                Arb.int(),
                Arb.int().orNull(),
                Arb.int(0, 255),
                Arb.long(),
                Arb.boolean(),
                filteredStringArb.orNull(),
                Arb.list(locationArb).orNull()
            ) { i, ni, br, l, b, s, loc ->
                StripInfo(
                    numLEDs = i,
                    pin = ni,
                    brightness = br,
                    renderDelay = l,
                    isRenderLoggingEnabled = b,
                    renderLogFile = s,
                    rendersBetweenLogSaves = i,
                    is1DSupported = b,
                    is2DSupported = b,
                    is3DSupported = b,
                    ledLocations = loc,
                ).jsonString() shouldBe """{"type":"StripInfo","numLEDs":$i,"pin":$ni,"brightness":$br,"renderDelay":$l,"isRenderLoggingEnabled":$b,"renderLogFile":${if (s == null) s else "\"$s\""},"rendersBetweenLogSaves":$i,"is1DSupported":$b,"is2DSupported":$b,"is3DSupported":$b,"ledLocations":${
                    loc?.joinToString(",", prefix = "[", postfix = "]") { serializer.encodeToString(it) }
                }}"""
            }
        }

        "decode JSON" {
            checkAll(
                Arb.int(),
                Arb.int().orNull(),
                Arb.int(0, 255),
                Arb.long(),
                Arb.boolean(),
                filteredStringArb.orNull(),
                Arb.list(locationArb).orNull()
            ) { i, ni, br, l, b, s, loc ->

                val json =
                    """{"type":"StripInfo","numLEDs":$i,"pin":$ni,"brightness":$br,"renderDelay":$l,"isRenderLoggingEnabled":$b,"renderLogFile":${if (s == null) s else "\"$s\""},"rendersBetweenLogSaves":$i,"is1DSupported":$b,"is2DSupported":$b,"is3DSupported":$b,"ledLocations":${
                        loc?.joinToString(",", prefix = "[", postfix = "]") { serializer.encodeToString(it) }
                    }}"""

                val correctData = StripInfo(
                    numLEDs = i,
                    pin = ni,
                    brightness = br,
                    renderDelay = l,
                    isRenderLoggingEnabled = b,
                    renderLogFile = s,
                    rendersBetweenLogSaves = i,
                    is1DSupported = b,
                    is2DSupported = b,
                    is3DSupported = b,
                    ledLocations = loc,
                )

                json.decodeJson() as StripInfo shouldBe correctData
            }
        }

        "encode and decode JSON" {
            checkAll(
                Arb.int(),
                Arb.int().orNull(),
                Arb.int(0, 255),
                Arb.long(),
                Arb.boolean(),
                Arb.string().orNull(),
                Arb.list(locationArb).orNull()
            ) { i, ni, br, l, b, s, loc ->
                val info1 = StripInfo(
                    numLEDs = i,
                    pin = ni,
                    brightness = br,
                    renderDelay = l,
                    isRenderLoggingEnabled = b,
                    renderLogFile = s,
                    rendersBetweenLogSaves = i,
                    is1DSupported = b,
                    is2DSupported = b,
                    is3DSupported = b,
                    ledLocations = loc,
                )

                val infoBytes = info1.json()

                val info2 = infoBytes.toUTF8String().decodeJson() as StripInfo

                info2 shouldBe info1
            }
        }
    }
)
