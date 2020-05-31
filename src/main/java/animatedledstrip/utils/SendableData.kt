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

            override fun shouldSkipField(field: FieldAttributes?): Boolean {
                return field?.name?.equals("prefix$1") == true
            }
        }
    }
}