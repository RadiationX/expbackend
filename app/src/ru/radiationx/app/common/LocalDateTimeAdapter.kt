package ru.radiationx.app.common

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import ru.radiationx.domain.helper.asDate
import ru.radiationx.domain.helper.asLocalDateTime
import java.time.LocalDateTime
import java.util.*

internal object LocalDateTimeAdapter : TypeAdapter<LocalDateTime>() {

    override fun write(writer: JsonWriter, obj: LocalDateTime?) {
        val value = obj?.asDate()?.time
        writer.value(value)
    }

    override fun read(reader: JsonReader): LocalDateTime? {
        val value = reader.nextLong()
        return Date(value).asLocalDateTime()
    }
}