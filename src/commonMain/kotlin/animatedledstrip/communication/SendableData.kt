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

package animatedledstrip.communication

import kotlinx.serialization.PolymorphicSerializer

/**
 * Represents a class that can be sent over sockets between servers and clients.
 * Handles conversion of class to json.
 */
interface SendableData {
//    fun jsonString(): String = "${serializer.encodeToString(this)}$DELIMITER"

    fun jsonString(): String =
        serializer.encodeToString(PolymorphicSerializer(SendableData::class), this)

    fun json(): ByteArray = this.jsonString().encodeToByteArray()

    fun jsonStringWithDelimiter(): String = "${jsonString()}$DELIMITER"

    fun jsonWithDelimiter(): ByteArray = this.jsonStringWithDelimiter().encodeToByteArray()
}
