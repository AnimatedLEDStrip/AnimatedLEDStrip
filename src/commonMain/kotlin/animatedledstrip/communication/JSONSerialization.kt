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

package animatedledstrip.communication

import animatedledstrip.animations.Animation
import animatedledstrip.animations.groups.AnimationGroup
import animatedledstrip.animations.parameters.*
import animatedledstrip.colors.ColorContainer
import animatedledstrip.colors.ColorContainerInterface
import animatedledstrip.colors.PreparedColorContainer
import animatedledstrip.leds.animationmanagement.AnimationToRunParams
import animatedledstrip.leds.animationmanagement.EndAnimation
import animatedledstrip.leds.animationmanagement.RunningAnimationParams
import animatedledstrip.leds.colormanagement.CurrentStripColor
import animatedledstrip.leds.sectionmanagement.Section
import animatedledstrip.leds.stripmanagement.StripInfo
import animatedledstrip.utils.Logger
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

const val DELIMITER = ";;;"

private val serializerModule: SerializersModule = SerializersModule {
    polymorphic(SendableData::class) {
        subclass(AnimationGroup::class)
        subclass(Animation.AnimationInfo::class)
        subclass(AnimationToRunParams::class)
        subclass(ClientParams::class)
        subclass(Command::class)
        subclass(CurrentStripColor::class)
        subclass(EndAnimation::class)
        subclass(Message::class)
        subclass(AnimationGroup.NewAnimationGroupInfo::class)
        subclass(RunningAnimationParams::class)
        subclass(Section::class)
        subclass(StripInfo::class)
    }
    polymorphic(ColorContainerInterface::class) {
        subclass(ColorContainer::class)
        subclass(PreparedColorContainer::class)
    }
    polymorphic(Distance::class) {
        subclass(AbsoluteDistance::class)
        subclass(PercentDistance::class)
    }
    polymorphic(Rotation::class) {
        subclass(DegreesRotation::class)
        subclass(RadiansRotation::class)
    }
}

val serializer: Json = Json {
    encodeDefaults = true
    serializersModule = serializerModule
    ignoreUnknownKeys = true
}


/**
 * Convert a json string into a class
 */
fun String?.decodeJson(): SendableData {
    requireNotNull(this)
    return serializer.decodeFromString(this.removeSuffix(DELIMITER))
}

fun String?.decodeJsonOrNull(): SendableData? = try {
    decodeJson()
} catch (e: SerializationException) {
    Logger.e("JSON Decoder: Error decoding JSON", e)
    null
}

/**
 * Create a `String` from a `ByteArray` using the UTF-8 charset
 *
 * @param size The numbers of characters to include
 * (used to remove excess null bytes)
 */
fun ByteArray?.toUTF8String(size: Int = this?.size ?: 0): String {
    requireNotNull(this)
    return this.decodeToString(endIndex = size, throwOnInvalidSequence = true)
}