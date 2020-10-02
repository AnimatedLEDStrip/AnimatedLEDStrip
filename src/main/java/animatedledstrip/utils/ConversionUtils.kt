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
import animatedledstrip.colors.ColorContainer
import animatedledstrip.colors.ColorContainerInterface
import animatedledstrip.colors.ColorContainerSerializer
import animatedledstrip.leds.AnimatedLEDStrip
import animatedledstrip.leds.StripInfo
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import java.nio.charset.Charset
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/* GSON */

/**
 * JSON Parser
 */
val gson: Gson = GsonBuilder()
    .registerTypeAdapter(ColorContainerInterface::class.java, ColorContainerSerializer())
    .addSerializationExclusionStrategy(SendableData.Companion.ExStrategy)
    .addSerializationExclusionStrategy(AnimationData.Companion.ExStrategy)
    .addSerializationExclusionStrategy(AnimatedLEDStrip.Companion.SectionExStrategy)
    .create()


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

/**
 * Convert a json string into a class
 *
 * @param T The type of sendable data to create
 */
inline fun <reified T : SendableData> String?.jsonToSendableData(): T {
    requireNotNull(this)
    try {
        return gson.fromJson(
            this.drop(5).removeSuffix(DELIMITER),
            T::class.java
        )
    } catch (e: JsonSyntaxException) {
        throw JsonSyntaxException("Malformed JSON: $this", e)
    }
}

fun jsonMissingPropertyMessage(property: KProperty<*>, kClass: KClass<*>) =
    "${property.name} property of ${kClass.qualifiedName} must be specified"

/**
 * Create an [AnimationData] from json
 */
fun String?.jsonToAnimationData(): AnimationData = jsonToSendableData()

/**
 * Create an [Animation.AnimationInfo] from json
 */
fun String?.jsonToAnimationInfo(): Animation.AnimationInfo = jsonToSendableData()

/**
 * Create a [Command] from json
 */
fun String?.jsonToCommand(): Command = jsonToSendableData()

/**
 * Create an [EndAnimation] from json
 */
fun String?.jsonToEndAnimation(): EndAnimation = jsonToSendableData()

/**
 * Create a [Message] from json
 */
fun String?.jsonToMessage(): Message = jsonToSendableData()

/**
 * Create an [AnimatedLEDStrip.Section] from json
 */
fun String?.jsonToSection(ledStrip: AnimatedLEDStrip): AnimatedLEDStrip.Section {
    requireNotNull(this)
    val map: Map<*, *> = try {
        gson.fromJson(this.drop(5).removeSuffix(DELIMITER), Map::class.java)
    } catch (e: JsonSyntaxException) {
        throw JsonSyntaxException("Malformed JSON: $this", e)
    }

    require(map["name"] != null || map["parent"] != null) {
        "name or parent property of animatedledstrip.leds.AnimatedLEDStrip.Section must be specified"
    }
    requireNotNull(map["startPixel"]) {
        jsonMissingPropertyMessage(AnimatedLEDStrip.Section::startPixel, AnimatedLEDStrip.Section::class)
    }
    requireNotNull(map["endPixel"]) {
        jsonMissingPropertyMessage(AnimatedLEDStrip.Section::endPixel, AnimatedLEDStrip.Section::class)
    }

    val name = map["name"] as String?
    val startPixel = (map["startPixel"] as Double).toInt()
    val endPixel = (map["endPixel"] as Double).toInt()
    val parent = map["parent"] as String?

    return if (parent != null) {
        ledStrip.getSection(parent).getSubSection(startPixel, endPixel)
    } else {
        ledStrip.createSection(name!!, startPixel, endPixel)
    }
}

/**
 * Create a [StripInfo] from json
 */
fun String?.jsonToStripInfo(): StripInfo = jsonToSendableData()


/**
 * Get the four characters at the beginning of the string that are used to
 * indicate the type of data, i.e. `DATA`, `AINF`, `CMD `, `END `, `MSG `,
 * `SECT`, `SINF`, etc.
 * Note the extra space at the end of `CMD `, `END ` and `MSG `.
 */
fun String?.getDataTypePrefix(): String {
    requireNotNull(this)
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
    requireNotNull(this)
    return this.toString(Charset.forName("utf-8")).take(size)
}

/**
 * Remove whitespace from a `String`
 */
fun String.removeWhitespace(): String = this.replace("\\s".toRegex(), "")
