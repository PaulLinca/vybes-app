package com.linca.vybes.network.adapters

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LocalDateTypeAdapter : TypeAdapter<LocalDate>() {

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    override fun write(out: JsonWriter, value: LocalDate?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value.format(formatter))
        }
    }

    override fun read(reader: JsonReader): LocalDate? {
        return if (reader.peek() == JsonToken.NULL) {
            reader.nextNull()
            null
        } else {
            val dateString = reader.nextString()
            LocalDate.parse(dateString, formatter)
        }
    }
}