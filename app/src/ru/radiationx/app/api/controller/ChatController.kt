package ru.radiationx.app.api.controller

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.websocket.DefaultWebSocketServerSession
import ru.radiationx.app.api.WebSocketHandler
import ru.radiationx.app.api.base.ApiErrorResponse
import ru.radiationx.app.api.base.WebSocketError
import ru.radiationx.app.api.base.WebSocketEvent
import ru.radiationx.app.wrapError
import java.lang.reflect.Type

class ChatController(
    private val gson: Gson,
    private val webSocketHandler: WebSocketHandler
) {

    init {
        webSocketHandler.textEventHandler = { frame ->
            val event = frame.recieve<Message>()
            respondEvent(event.event, Message("YOU SAID: ${event.data.text}"))
        }

        webSocketHandler.errorHandler = { error ->
            respondError(wrapError(error))
        }
    }

    suspend fun handleSession(session: DefaultWebSocketServerSession) = webSocketHandler.handleSession(session)

    data class Message(val text: String)

    private val regex = Regex("^###([\\s\\S]+)###([\\s\\S]*)\$")

    private suspend fun DefaultWebSocketServerSession.respondEvent(event: String, data: Any? = null) {
        respond(event, data)
    }

    private suspend fun DefaultWebSocketServerSession.respondError(error: ApiErrorResponse) {
        respond("error", error)
    }

    private suspend fun DefaultWebSocketServerSession.respond(event: String? = null, responseData: Any? = null) {
        val jsonData = gson.toJson(responseData)
        val respondText = buildString {
            append("###")
            append(event)
            append("###")
            append(jsonData)
        }
        outgoing.send(Frame.Text(respondText))
    }

    private inline fun <reified T> Frame.Text.recieve(): WebSocketEvent<T> = recieve(genericType<T>())

    private inline fun <reified T> genericType(): Type = object : TypeToken<T>() {}.type

    private fun <T> Frame.Text.recieve(type: Type): WebSocketEvent<T> {
        val rawContent = readText()
        val result = regex.find(rawContent)
        if (result == null || result.groups.size != 3) {
            throw Exception("Wrong format")
        }
        val eventPart = result.groups[1]?.value ?: throw Exception("Wrong event")
        val jsonPart = result.groups[2]?.value ?: throw Exception("Wrong json")
        val data = gson.fromJson<T>(jsonPart, type)
        return WebSocketEvent(eventPart, data)
    }
}