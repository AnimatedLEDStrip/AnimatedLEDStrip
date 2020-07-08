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

package animatedledstrip.utils

import animatedledstrip.animationutils.Animation
import animatedledstrip.animationutils.AnimationData
import animatedledstrip.animationutils.EndAnimation
import animatedledstrip.animationutils.ParamUsage
import animatedledstrip.colors.ColorContainer
import animatedledstrip.colors.ColorContainerInterface
import animatedledstrip.colors.ColorContainerSerializer
import animatedledstrip.leds.AnimatedLEDStrip
import animatedledstrip.leds.StripInfo
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import java.nio.charset.Charset

/* GSON */

/**
 * JSON Parser
 */
val gson = GsonBuilder()
    .registerTypeAdapter(ColorContainerInterface::class.java, ColorContainerSerializer())
    .addSerializationExclusionStrategy(SendableData.Companion.ExStrategy)
    .addSerializationExclusionStrategy(AnimationData.Companion.ExStrategy)
    .addSerializationExclusionStrategy(AnimatedLEDStrip.Companion.SectionExStrategy)
    .create()
    ?: error("Could not create JSON parser")


/* 24-bit to 32-bit conversion */

/**
 * Convert a 24-bit `Long` to a 32-bit `Int`
 */
fun Long.toARGB(): Int = (this or 0xFF000000).toInt()

/**
 * Convert a 24-bit `Int` to a 32-bit `Int`
 */
fun Int.toARGB(): Int = (this or 0xFF000000.toInt())


/* Int/Long to ColorContainer conversion */

/**
 * Create a [ColorContainer] from this `Int`
 */
fun Int.toColorContainer(): ColorContainer = ColorContainer(this.toLong())

/**
 * Create a [ColorContainer] from this `Long`
 */
fun Long.toColorContainer(): ColorContainer = ColorContainer(this)


/* AnimationData to EndAnimation */

fun AnimationData.endAnimation(): EndAnimation = EndAnimation(this.id)

/* JSON parsing */

const val DELIMITER = ";;;"

inline fun <reified T : SendableData> String?.jsonToSendableData(): T {
    checkNotNull(this)
    try {
        return gson.fromJson(
            this.drop(5).removeSuffix(DELIMITER),
            T::class.java
        )
    } catch (e: JsonSyntaxException) {
        throw JsonSyntaxException("Malformed JSON: $this", e)
    }
}

fun String?.jsonToAnimationData(): AnimationData = jsonToSendableData()

fun String?.jsonToStripInfo(): StripInfo = jsonToSendableData()

fun String?.jsonToEndAnimation(): EndAnimation = jsonToSendableData()

fun String?.jsonToAnimationInfo(): Animation.AnimationInfo = jsonToSendableData()

fun String?.jsonToSection(): AnimatedLEDStrip.Section = jsonToSendableData()

/**
 * Get the first four characters in the string (used to indicate the type of data,
 * i.e. `DATA`, `INFO`, `SECT`, `ANIM`, `END `, `CMD ` (note extra space), etc.)
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

/**
 * Remove spaces from a `String`
 */
fun String.removeSpaces(): String = this.replace("\\s".toRegex(), "")
