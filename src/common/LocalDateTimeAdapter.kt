package ru.radiationx.common

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import io.ktor.util.date.GMTDate
import io.ktor.util.date.Month
import java.time.LocalDateTime

internal object LocalDateTimeAdapter : TypeAdapter<LocalDateTime>() {

    override fun write(writer: JsonWriter, obj: LocalDateTime) {
        writer.value(obj.toString())
    }

    override fun read(reader: JsonReader): LocalDateTime {
        return LocalDateTime.parse(reader.nextString())
    }
}