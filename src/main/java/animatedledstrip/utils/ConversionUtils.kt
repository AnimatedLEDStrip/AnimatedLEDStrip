package animatedledstrip.utils

/*
 *  Copyright (c) 2019 AnimatedLEDStrip
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


import animatedledstrip.animationutils.AnimationData
import animatedledstrip.animationutils.gson
import animatedledstrip.colors.ColorContainer
import animatedledstrip.leds.StripInfo
import java.nio.charset.Charset

/**
 * Convert a 24-bit `Long` to a 32-bit `Int`
 */
fun Long.toARGB(): Int = (this or 0xFF000000).toInt()

/**
 * Convert a 24-bit `Int` to a 32-bit `Int`
 */
fun Int.toARGB(): Int = (this or 0xFF000000.toInt())

/**
 * Create a [ColorContainer] from this `Int`
 */
fun Int.toColorContainer(): ColorContainer = ColorContainer(this.toLong())

/**
 * Create a [ColorContainer] from this `Long`
 */
fun Long.toColorContainer(): ColorContainer = ColorContainer(this)

fun ByteArray?.toUTF8(size: Int): String {
    checkNotNull(this)
    return this.toString(Charset.forName("utf-8")).take(size)
}

fun String?.getDataTypePrefix(): String {
    checkNotNull(this)
    return this.take(4)
}

fun AnimationData.json(): ByteArray = this.jsonString().toByteArray(Charset.forName("utf-8"))
fun AnimationData.jsonString(): String = "DATA:${gson.toJson(this)};"

fun String?.jsonToAnimationData(): AnimationData {
    checkNotNull(this)
    return gson.fromJson(
        this.removePrefix("DATA:").removeSuffix(";"),
        AnimationData::class.java
    )
}

fun StripInfo.json(): ByteArray = this.jsonString().toByteArray(Charset.forName("utf-8"))
fun StripInfo.jsonString(): String = "INFO:${gson.toJson(this)};"

fun String?.jsonToStripInfo(): StripInfo {
    checkNotNull(this)
    return gson.fromJson(
        this.removePrefix("INFO:").removeSuffix(";"),
        StripInfo::class.java
    )
}

