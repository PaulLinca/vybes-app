package com.example.vybes.dependencyInjection

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ZonedDateTimeDeserializer : JsonDeserializer<ZonedDateTime> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): ZonedDateTime {
        val dateString = json?.asString ?: return ZonedDateTime.now()

        return try {
            ZonedDateTime.parse(dateString, DateTimeFormatter.ISO_ZONED_DATE_TIME)
        } catch (e: Exception) {
            // Fallback if parsing fails (for example, to ISO_LOCAL_DATE_TIME without timezone)
            ZonedDateTime.parse(dateString, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        }
    }

}