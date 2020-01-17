package ru.radiationx.app.api.websocket

import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.Frame
import io.ktor.websocket.DefaultWebSocketServerSession
import ru.radiationx.app.api.base.WebSocketConverterException
import ru.radiationx.app.api.base.WebSocketError
import ru.radiationx.app.api.base.WebSocketEvent
import ru.radiationx.app.api.base.WebSocketTextEvent

class WebSocketJsonEventHandler(
    private val webSocketHandler: WebSocketTextEventHandler,
    private val jsonEventConverter: WebSocketJsonEventConverter
) : WebSocketSessionHandler {

    var connectHandler: suspend DefaultWebSocketServerSession.() -> Unit = {}
    var disconnectHandler: suspend DefaultWebSocketServerSession.() -> Unit = {}
    var errorHandler: suspend DefaultWebSocketServerSession.(uuid: String?, error: Throwable) -> Unit = { _, _ -> }
    var textEventHandler: suspend DefaultWebSocketServerSession.(textEvent: WebSocketTextEvent) -> Unit = {}
    var binaryEventHandler: suspend DefaultWebSocketServerSession.(data: ByteArray) -> Unit = {}
    var closeEventHandler: suspend DefaultWebSocketServerSession.(closeReason: CloseReason?) -> Unit = {}
    var pingEventHandler: suspend DefaultWebSocketServerSession.() -> Unit = {}
    var pongEventHandler: suspend DefaultWebSocketServerSession.() -> Unit = {}

    init {
        webSocketHandler.connectHandler = {
            connectHandler.invoke(this)
        }
        webSocketHandler.disconnectHandler = {
            disconnectHandler.invoke(this)
        }
        webSocketHandler.textEventHandler = {
            textEventHandler.invoke(this, it)
        }
        webSocketHandler.errorHandler = { uuid, error ->
            errorHandler.invoke(this, uuid, error)
        }
        webSocketHandler.binaryEventHandler = {
            binaryEventHandler.invoke(this, it)
        }
        webSocketHandler.closeEventHandler = {
            closeEventHandler.invoke(this, it)
        }
        webSocketHandler.pingEventHandler = {
            pingEventHandler.invoke(this)
        }
        webSocketHandler.pongEventHandler = {
            pongEventHandler.invoke(this)
        }
    }

    override suspend fun handleSession(session: DefaultWebSocketServerSession) = webSocketHandler.handleSession(session)

    suspend fun <T> respondEvent(
        session: DefaultWebSocketServerSession,
        event: WebSocketEvent<T>
    ) {
        webSocketHandler.respondEvent(session, jsonEventConverter.createFrameText(event))
    }

    suspend fun respondError(
        session: DefaultWebSocketServerSession,
        errorEvent: WebSocketError
    ) {
        val event = WebSocketEvent("error", errorEvent.error, errorEvent.uuid)
        webSocketHandler.respondError(session, jsonEventConverter.createFrameText(event))
    }
}