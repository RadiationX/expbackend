package ru.radiationx.app.api.websocket

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.radiationx.app.api.base.WebSocketEvent
import ru.radiationx.app.api.base.WebSocketConverterException
import ru.radiationx.app.api.base.WebSocketTextEvent
import java.lang.reflect.Type

class WebSocketJsonEventConverter(
    private val textEventConverter: WebSocketTextEventConverter,
    private val gson: Gson
) {

    suspend fun <T> createFrameText(event: WebSocketEvent<T>): String = withContext(Dispatchers.Default) {
        val jsonData = try {
            event.data?.let { gson.toJson(it) }.orEmpty()
        } catch (ex: JsonParseException) {
            throw WebSocketConverterException("Json create error: ${ex.message}")
        }
        val textEvent = WebSocketTextEvent(event.event, event.uuid, jsonData)
        textEventConverter.createFrameText(textEvent)
    }

    suspend inline fun <reified T> parseFrameText(frameText: String): WebSocketEvent<T> =
        receive(frameText, genericType<T>())

    inline fun <reified T> genericType(): Type = object : TypeToken<T>() {}.type

    suspend fun <T> receive(frameText: String, type: Type): WebSocketEvent<T> = withContext(Dispatchers.Default) {
        val textEvent = textEventConverter.parseFrameText(frameText)
        val data = try {
            gson.fromJson<T>(textEvent.text, type)
        } catch (ex: JsonParseException) {
            throw WebSocketConverterException("Json parse error: ${ex.message}")
        }
        WebSocketEvent(textEvent.event, textEvent.uuid, data)
    }

}