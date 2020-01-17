package ru.radiationx.app.api.controller

import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.websocket.DefaultWebSocketServerSession
import ru.radiationx.app.api.websocket.WebSocketHandler
import ru.radiationx.app.api.base.ApiErrorResponse
import ru.radiationx.app.api.base.WebSocketConverterException
import ru.radiationx.app.api.base.WebSocketEvent
import ru.radiationx.app.api.websocket.WebSocketJsonEventConverter
import ru.radiationx.app.api.websocket.WebSocketTextEventConverter
import ru.radiationx.app.wrapError

class ChatController(
    private val webSocketHandler: WebSocketHandler,
    private val jsonEventConverter: WebSocketJsonEventConverter,
    private val textEventConverter: WebSocketTextEventConverter
) {

    var connectHandler: suspend DefaultWebSocketServerSession.() -> Unit = {}
    var disconnectHandler: suspend DefaultWebSocketServerSession.() -> Unit = {}
    var errorHandler: suspend DefaultWebSocketServerSession.(uuid: String?, error: Throwable) -> Unit = { _, _ -> }
    var textFrameHandler: suspend DefaultWebSocketServerSession.(text: String) -> Unit = {}
    var binaryFrameHandler: suspend DefaultWebSocketServerSession.(data: ByteArray) -> Unit = {}
    var closeFrameHandler: suspend DefaultWebSocketServerSession.(closeReason: CloseReason) -> Unit = {}
    var pingFrameHandler: suspend DefaultWebSocketServerSession.() -> Unit = {}
    var pongFrameHandler: suspend DefaultWebSocketServerSession.() -> Unit = {}

    init {
        webSocketHandler.textFrameHandler = { frame ->
            val event = jsonEventConverter.parseFrameText<Message>(frame.readText())
            respondEvent(event.event, event.uuid, Message("YOU SAID: ${event.data.text}"))
        }

        webSocketHandler.errorHandler = { frame, error ->
            val wrappedError = wrapError(error)
            val uuid = if (frame is Frame.Text && error !is WebSocketConverterException) {
                textEventConverter.parseFrameText(frame.readText()).uuid
            } else {
                null
            }
            respondError(wrappedError, uuid)
        }
    }

    suspend fun handleSession(session: DefaultWebSocketServerSession) = webSocketHandler.handleSession(session)

    data class Message(val text: String)

    private suspend fun DefaultWebSocketServerSession.respondEvent(
        event: String,
        uuid: String? = null,
        data: Any? = null
    ) {
        respond(event, uuid, data)
    }

    private suspend fun DefaultWebSocketServerSession.respondError(error: ApiErrorResponse, uuid: String? = null) {
        respond("error", uuid, error)
    }

    private suspend fun DefaultWebSocketServerSession.respond(
        event: String,
        uuid: String? = null,
        responseData: Any? = null
    ) {
        val respondText = jsonEventConverter.createFrameText(WebSocketEvent(event, uuid, responseData))
        outgoing.send(Frame.Text(respondText))
    }

}