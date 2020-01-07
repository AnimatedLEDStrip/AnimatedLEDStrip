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

package animatedledstrip.utils

import animatedledstrip.animationutils.Animation
import animatedledstrip.animationutils.AnimationData
import animatedledstrip.animationutils.AnimationInfo
import animatedledstrip.animationutils.animationinfo.animationInfoList
import animatedledstrip.animationutils.gson
import animatedledstrip.colors.ColorContainer
import animatedledstrip.leds.StripInfo
import com.google.gson.JsonSyntaxException
import org.pmw.tinylog.Logger
import java.nio.charset.Charset

/* 24-bit to 32-bit conversion */

/**
 * Convert a 24-bit `Long` to a 32-bit `Int`
 */
fun Long.toARGB(): Int = (this or 0xFF000000).toInt()

/**
 * Convert a 24-bit `Int` to a 32-bit `Int`
 */
fun Int.toARGB(): Int = (this or 0xFF000000.toInt())


/* `Int`/`Long` to `ColorContainer` conversion */

/**
 * Create a [ColorContainer] from this `Int`
 */
fun Int.toColorContainer(): ColorContainer = ColorContainer(this.toLong())

/**
 * Create a [ColorContainer] from this `Long`
 */
fun Long.toColorContainer(): ColorContainer = ColorContainer(this)


/* JSON creation */

/**
 * Create a representation of an `AnimationData` instance ready to be sent over a socket
 */
fun AnimationData.json(): ByteArray = this.jsonString().toByteArray(Charset.forName("utf-8"))

/**
 * Create a representation of a `StripInfo` instance ready to be sent over a socket
 */
fun StripInfo.json(): ByteArray = this.jsonString().toByteArray(Charset.forName("utf-8"))


/**
 * Create a string representation of an `AnimationData` instance
 */
fun AnimationData.jsonString(): String = "DATA:${gson.toJson(this)};"

/**
 * Create a string representation of a `StripInfo` instance
 */
fun StripInfo.jsonString(): String = "INFO:${gson.toJson(this)};"


/* JSON parsing */

/**
 * Create an `AnimationData` instance from a JSON string created by the
 * creation function above
 */
fun String?.jsonToAnimationData(): AnimationData {
    checkNotNull(this)
    try {
        return gson.fromJson(
            this.removePrefix("DATA:").removeSuffix(";"),
            AnimationData::class.java
        )
    } catch (e: JsonSyntaxException) {
        Logger.warn("Malformed JSON: $this")
        throw e                                         // Re-throw exception so it can be handled by calling code
    }
}

/**
 * Create a `StripInfo` instance from a JSON string created by the
 * creation function above
 */
fun String?.jsonToStripInfo(): StripInfo {
    checkNotNull(this)
    try {
        return gson.fromJson(
            this.removePrefix("INFO:").removeSuffix(";"),
            StripInfo::class.java
        )
    } catch (e: JsonSyntaxException) {
        Logger.warn("Malformed JSON: $this")
        throw e                                         // Re-throw exception so it can be handled by calling code
    }
}

/**
 * Get the first four characters in the string (used to indicate the type of data,
 * i.e. `DATA`, `INFO`, `CMD ` (note extra space), etc.)
 */
fun String?.getDataTypePrefix(): String {
    checkNotNull(this)
    return this.take(4)
}


/* `ByteArray` to UTF-8 `String` */

/**
 * Create a `String` from a `ByteArray` using the UTF-8 charset
 *
 * @param size The numbers of characters to include
 * (used to remove excess null bytes)
 */
fun ByteArray?.toUTF8(size: Int = this?.size ?: 0): String {
    checkNotNull(this)
    return this.toString(Charset.forName("utf-8")).take(size)
}


/* `String` to `Animation` */

/**
 * Get an `Animation` by name
 */
fun String.getAnimation(): Animation =
    animationInfoList
        .find {
            this.toUpperCase().removeSpaces() ==
                    it.name.toUpperCase().removeSpaces()
        }!!
        .animation

/**
 * Get an `Animation` by name or null if no animation with that name is found
 */
fun String.getAnimationOrNull(): Animation? =
    animationInfoList
        .find {
            this.toUpperCase().removeSpaces() ==
                    it.name.toUpperCase().removeSpaces()
        }
        ?.animation

/**
 * Remove spaces from a `String`
 */
private fun String.removeSpaces(): String = this.replace("\\s".toRegex(), "")


/* `Animation` to `AnimationInfo` */

/**
 * Get the `AnimationInfo` instance associated with this animation
 */
fun Animation.info(): AnimationInfo =
    animationInfoList.find { it.animation == this }!!

/**
 * Get the `AnimationInfo` instance associated with this animation or null
 * if no associated `AnimationInfo` exists
 */
fun Animation.infoOrNull(): AnimationInfo? =
    animationInfoList.find { it.animation == this }