package animatedledstrip.colors

import com.google.gson.*
import java.lang.reflect.Type

class ColorContainerSerializer : JsonSerializer<ColorContainerInterface>, JsonDeserializer<ColorContainerInterface> {
    override fun serialize(
        color: ColorContainerInterface,
        p1: Type?,
        context: JsonSerializationContext
    ): JsonElement = context.serialize(color as ColorContainer)

    override fun deserialize(
        json: JsonElement,
        p1: Type?,
        context: JsonDeserializationContext
    ): ColorContainerInterface = context.deserialize(json, ColorContainer::class.java)

}