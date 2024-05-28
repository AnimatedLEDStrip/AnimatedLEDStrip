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

package animatedledstrip.test.communication

import animatedledstrip.communication.Message
import animatedledstrip.communication.decodeJson
import animatedledstrip.communication.toUTF8String
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class MessageTest : StringSpec(
    {
        "encode JSON" {
            checkAll(Arb.string().filter { !it.contains("\"") && !it.contains("\\") }) { m ->
                Message(m).jsonString() shouldBe
                        """{"type":"Message","message":"$m"}"""
            }
        }

        "decode JSON" {
            checkAll(Arb.string().filter { !it.contains("\"") && !it.contains("\\") }) { m ->
                val json =
                    """{"type":"Message","message":"$m"}"""

                val correctData = Message(m)

                json.decodeJson() as Message shouldBe correctData
            }
        }

        "encode and decode JSON" {
            checkAll<String> { m ->
                val msg1 = Message(m)
                val msgBytes = msg1.json()

                val msg2 = msgBytes.toUTF8String().decodeJson() as Message

                msg2 shouldBe msg1
            }
        }

        "encode JSON with delimiter" {
            checkAll(Arb.string().filter { !it.contains("\"") && !it.contains("\\") }) { m ->
                Message(m).jsonStringWithDelimiter() shouldBe
                        """{"type":"Message","message":"$m"};;;"""
            }
        }

        "decode JSON with delimiter" {
            checkAll(Arb.string().filter { !it.contains("\"") && !it.contains("\\") }) { m ->
                val json =
                    """{"type":"Message","message":"$m"};;;"""

                val correctData = Message(m)

                json.decodeJson() as Message shouldBe correctData
            }
        }

        "encode and decode JSON with delimiter" {
            checkAll<String> { m ->
                val msg1 = Message(m)
                val msgBytes = msg1.jsonWithDelimiter()

                val msg2 = msgBytes.toUTF8String().decodeJson() as Message

                msg2 shouldBe msg1
            }
        }
    }
)
