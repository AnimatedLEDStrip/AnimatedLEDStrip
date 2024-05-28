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

package animatedledstrip.test.communication

import animatedledstrip.communication.ClientParams
import animatedledstrip.communication.MessageFrequency
import animatedledstrip.communication.decodeJson
import animatedledstrip.communication.toUTF8String
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.long
import io.kotest.property.checkAll

class ClientParamsTest : StringSpec(
    {
        "encode JSON" {
            checkAll(Arb.boolean(), Arb.enum<MessageFrequency>(), Arb.long()) { b, m, l ->
                ClientParams(b, b, b, b, m, m, m, b, l).jsonString() shouldBe
                        """{"type":"ClientParams","sendDefinedAnimationInfoOnConnection":$b,"sendRunningAnimationInfoOnConnection":$b,"sendSectionInfoOnConnection":$b,"sendStripInfoOnConnection":$b,"sendAnimationStart":"$m","sendAnimationEnd":"$m","sendSectionCreation":"$m","sendLogs":$b,"bufferedMessageInterval":$l}"""
            }
        }

        "decode JSON" {
            checkAll(Arb.boolean(), Arb.enum<MessageFrequency>(), Arb.long()) { b, m, l ->
                val json =
                    """{"type":"ClientParams","sendDefinedAnimationInfoOnConnection":$b,"sendRunningAnimationInfoOnConnection":$b,"sendSectionInfoOnConnection":$b,"sendStripInfoOnConnection":$b,"sendAnimationStart":"$m","sendAnimationEnd":"$m","sendSectionCreation":"$m","sendLogs":$b,"bufferedMessageInterval":$l}"""

                val correctData = ClientParams(b, b, b, b, m, m, m, b, l)

                json.decodeJson() as ClientParams shouldBe correctData
            }
        }

        "encode and decode JSON" {
            checkAll(Arb.boolean(), Arb.enum<MessageFrequency>(), Arb.long()) { b, m, l ->
                val params1 = ClientParams(b, b, b, b, m, m, m, b, l)
                val paramsBytes = params1.json()

                val params2 = paramsBytes.toUTF8String().decodeJson() as ClientParams

                params2 shouldBe params1
            }
        }
    }
)
