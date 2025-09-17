package com.example.vybes.network.adapters

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ZonedDateTimeTypeAdapter : TypeAdapter<ZonedDateTime>() {
    override fun write(out: JsonWriter, value: ZonedDateTime?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
        }
    }

    override fun read(input: JsonReader): ZonedDateTime? {
        val dateString = input.nextString()
        return ZonedDateTime.parse(dateString)
    }
}