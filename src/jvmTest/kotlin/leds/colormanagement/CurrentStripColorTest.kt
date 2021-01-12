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
 *
 */

package animatedledstrip.test.leds.colormanagement

import animatedledstrip.communication.decodeJson
import animatedledstrip.communication.toUTF8String
import animatedledstrip.leds.colormanagement.CurrentStripColor
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll

class CurrentStripColorTest : StringSpec(
    {
        "construction" {
            checkAll<List<Int>> { c ->
                CurrentStripColor(c).color shouldBe c
            }
        }

        "encode JSON" {
            CurrentStripColor(listOf(0xFF, 0xFFFF, 0x1234)).jsonString() shouldBe
                    """{"type":"CurrentStripColor","color":[255,65535,4660]};;;"""
        }

        "decode JSON" {
            val json =
                """{"type":"CurrentStripColor","color":[10769581,12585646,11519448,381541,415164]};;;"""

            val correctData = CurrentStripColor(listOf(0xA454AD, 0xC00AAE, 0xAFC5D8,
                                                       0x05D265, 0x0655BC))

            json.decodeJson() as CurrentStripColor shouldBe correctData
        }

        "encode and decode JSON" {
            val col1 = CurrentStripColor(listOf(0xD88077, 0x74F49B, 0x07CF85,
                                                0xC8EE42, 0xC27AE4, 0x443707))
            val colBytes = col1.json()

            val col2 = colBytes.toUTF8String().decodeJson() as CurrentStripColor

            col2 shouldBe col1
        }
    }
)
