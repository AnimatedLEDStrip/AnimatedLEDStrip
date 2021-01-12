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

package animatedledstrip.test.communication

import animatedledstrip.communication.decodeJson
import animatedledstrip.communication.toUTF8String
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class JSONSerializationTests : StringSpec(
    {
        "decode JSON null check" {
            shouldThrow<IllegalArgumentException> { (null as String?).decodeJson() }
        }

        "bytearray to utf8 string length specified" {
            ByteArray(5).apply {
                this[0] = 't'.toByte()
                this[1] = 'e'.toByte()
                this[2] = 's'.toByte()
                this[3] = 't'.toByte()
                this[4] = 's'.toByte()
            }.toUTF8String(5) shouldBe "tests"
        }

        "bytearray to utf8 string take only part of array" {
            ByteArray(5).apply {
                this[0] = 't'.toByte()
                this[1] = 'e'.toByte()
                this[2] = 's'.toByte()
                this[3] = 't'.toByte()
                this[4] = 's'.toByte()
            }.toUTF8String(3) shouldBe "tes"
        }

        "bytearray to utf8 string length inferred" {
            ByteArray(5).apply {
                this[0] = 't'.toByte()
                this[1] = 'e'.toByte()
                this[2] = 's'.toByte()
                this[3] = 't'.toByte()
                this[4] = 's'.toByte()
            }.toUTF8String() shouldBe "tests"
        }

        "bytearray to string null check" {
            shouldThrow<IllegalArgumentException> { (null as ByteArray?).toUTF8String(5) }
        }
    }
)
