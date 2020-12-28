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

package animatedledstrip.test.leds.animationmanagement

import animatedledstrip.leds.animationmanagement.EndAnimation
import animatedledstrip.communication.decodeJson
import animatedledstrip.communication.toUTF8String
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class EndAnimationTest : StringSpec(
    {
        "encode JSON" {
            EndAnimation("test").jsonString() shouldBe
                    """{"type":"animatedledstrip.leds.animationmanagement.EndAnimation","id":"test"};;;"""

        }

        "decode JSON" {
            val json =
                """{"type":"animatedledstrip.leds.animationmanagement.EndAnimation", "id":"12345"};;;"""

            val correctData = EndAnimation("12345")

            json.decodeJson() as EndAnimation shouldBe correctData
        }

        "encode and decode JSON" {
            val end1 = EndAnimation("a test")
            val endBytes = end1.json()

            val end2 = endBytes.toUTF8String(endBytes.size).decodeJson() as EndAnimation

            end2 shouldBe end1
        }

        "human readable string" {
            EndAnimation("15235").toHumanReadableString() shouldBe "End of animation 15235"
        }
    }
)
