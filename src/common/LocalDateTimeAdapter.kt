package ru.radiationx.common

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.LocalDateTime

internal object LocalDateTimeAdapter : TypeAdapter<LocalDateTime>() {

    override fun write(writer: JsonWriter, obj: LocalDateTime?) {
        val value = obj?.toString()
        writer.value(value)
    }

    override fun read(reader: JsonReader): LocalDateTime? {
        val value = reader.nextString()
        return value?.let { LocalDateTime.parse(value) }
    }
}