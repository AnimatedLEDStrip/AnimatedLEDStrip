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

import animatedledstrip.communication.Command
import animatedledstrip.communication.decodeJson
import animatedledstrip.communication.toUTF8String
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class CommandTest : StringSpec(
    {
        "encode JSON" {
            checkAll(Arb.string().filter { !it.contains("\"") && !it.contains("\\") }) { c ->
                Command(c).jsonString() shouldBe
                        """{"type":"Command","command":"$c"}"""
            }
        }

        "decode JSON" {
            checkAll(Arb.string().filter { !it.contains("\"") && !it.contains("\\") }) { c ->
                val json =
                    """{"type":"Command", "command":"$c"}"""

                val correctData = Command(c)
                json.decodeJson() as Command shouldBe correctData
            }
        }

        "encode and decode JSON" {
            checkAll<String> { c ->
                val cmd1 = Command(c)
                val cmdBytes = cmd1.json()

                val cmd2 = cmdBytes.toUTF8String().decodeJson() as Command

                cmd2 shouldBe cmd1
            }
        }
    }
)
