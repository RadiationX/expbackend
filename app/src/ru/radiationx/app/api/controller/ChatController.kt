package ru.radiationx.app.api.controller

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.websocket.DefaultWebSocketServerSession
import ru.radiationx.app.api.WebSocketHandler
import ru.radiationx.app.api.base.ApiErrorResponse
import ru.radiationx.app.wrapError
import java.lang.reflect.Type

class ChatController(
    private val gson: Gson,
    private val webSocketHandler: WebSocketHandler
) {

    init {
        webSocketHandler.textEventHandler = { frame ->
            val event = frame.recieve<WebSocketEvent<Message>>()
            respondEvent(event.event, Message("YOU SAID: ${event.data.text}"))
        }

        webSocketHandler.errorHandler = { error ->
            respondError(wrapError(error))
        }
    }

    suspend fun handleSession(session: DefaultWebSocketServerSession) = webSocketHandler.handleSession(session)

    data class WebSocketEvent<T>(
        val event: String,
        val data: T
    )

    data class WebSocketError(
        val error: ApiErrorResponse
    )

    data class Message(val text: String)

    private suspend fun DefaultWebSocketServerSession.respondEvent(event: String, data: Any? = null) {
        respond(WebSocketEvent(event, data))
    }

    private suspend fun DefaultWebSocketServerSession.respondError(error: ApiErrorResponse) {
        respond(WebSocketError(error))
    }

    private suspend fun DefaultWebSocketServerSession.respond(responseData: Any? = null) {
        val jsonData = gson.toJson(responseData)
        outgoing.send(Frame.Text(jsonData))
    }

    private inline fun <reified T> Frame.Text.recieve(): T = recieve(genericType<T>())

    private inline fun <reified T> genericType(): Type = object : TypeToken<T>() {}.type

    private fun <T> Frame.Text.recieve(type: Type): T {
        val rawContent = readText()
        return gson.fromJson<T>(rawContent, type)
    }
}