package ru.radiationx.app.api.controller

import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.websocket.DefaultWebSocketServerSession
import ru.radiationx.app.api.base.WebSocketConverterException
import ru.radiationx.app.api.base.WebSocketError
import ru.radiationx.app.api.base.WebSocketEvent
import ru.radiationx.app.api.base.WebSocketTextEvent
import ru.radiationx.app.api.websocket.WebSocketHandler
import ru.radiationx.app.api.websocket.WebSocketJsonEventConverter
import ru.radiationx.app.api.websocket.WebSocketJsonEventHandler
import ru.radiationx.app.api.websocket.WebSocketSessionHandler
import ru.radiationx.app.wrapError
import ru.radiationx.domain.exception.NotFound

class ChatController(
    private val webSocketHandler: WebSocketJsonEventHandler,
    private val jsonEventConverter: WebSocketJsonEventConverter
) : WebSocketSessionHandler {

    companion object {
        private const val EVENT_HELLO = "hello"
    }

    init {
        webSocketHandler.textEventHandler = { textEvent ->
            when (textEvent.event) {
                EVENT_HELLO -> helloRoute(textEvent)
                else -> throw NotFound()
            }
        }

        webSocketHandler.errorHandler = { uuid, error ->
            val wrappedError = wrapError(error)
            webSocketHandler.respondError(this, WebSocketError(wrappedError, uuid))
        }
    }

    class Message(val text: String)

    override suspend fun handleSession(session: DefaultWebSocketServerSession) = webSocketHandler.handleSession(session)

    private suspend fun DefaultWebSocketServerSession.helloRoute(textEvent: WebSocketTextEvent) {
        val event = jsonEventConverter.parseFrameText<Message>(textEvent)
        val webSocketEvent = WebSocketEvent(event.event, Message("YOU SAID: ${event.data.text}"), event.uuid)
        webSocketHandler.respondEvent(this, webSocketEvent)
    }
}