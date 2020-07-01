/*
 *  Copyright (c) 2020 AnimatedLEDStrip
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

package animatedledstrip.utils

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import java.io.Serializable

interface SendableData: Serializable {
    val prefix: String

    fun toHumanReadableString(): String

    fun jsonString(): String = "$prefix:${gson.toJson(this)}$DELIMITER"

    fun json(): ByteArray = this.jsonString().toByteArray(Charsets.UTF_8)

    companion object {
        object ExStrategy : ExclusionStrategy {
            override fun shouldSkipClass(p0: Class<*>?) = false

            override fun shouldSkipField(field: FieldAttributes): Boolean {
                return when (field.name) {
                    "prefix$1" -> true
                    else -> false
                }
            }
        }
    }
}
