package ru.radiationx.app.api.websocket.handler

import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readReason
import io.ktor.http.cio.websocket.readText
import io.ktor.websocket.DefaultWebSocketServerSession
import ru.radiationx.app.api.base.WebSocketConverterException
import ru.radiationx.app.api.base.WebSocketTextEvent
import ru.radiationx.app.api.websocket.WebSocketSessionHandler
import ru.radiationx.app.api.websocket.converter.WebSocketTextEventConverter

open class WebSocketTextEventHandler(
    private val webSocketHandler: WebSocketHandler,
    private val textEventConverter: WebSocketTextEventConverter
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
        webSocketHandler.textFrameHandler = {
            val textEvent = textEventConverter.parseFrameText(it.readText())
            textEventHandler.invoke(this, textEvent)
        }
        webSocketHandler.errorHandler = { frame, error ->
            val uuid = if (frame is Frame.Text && error !is WebSocketConverterException) {
                textEventConverter.parseFrameText(frame.readText()).uuid
            } else {
                null
            }
            errorHandler.invoke(this, uuid, error)
        }
        webSocketHandler.binaryFrameHandler = {
            binaryEventHandler.invoke(this, it.data)
        }
        webSocketHandler.closeFrameHandler = {
            closeEventHandler.invoke(this, it.readReason())
        }
        webSocketHandler.pingFrameHandler = {
            pingEventHandler.invoke(this)
        }
        webSocketHandler.pongFrameHandler = {
            pongEventHandler.invoke(this)
        }
    }

    override suspend fun handleSession(session: DefaultWebSocketServerSession) = webSocketHandler.handleSession(session)

    suspend fun respondEvent(
        session: DefaultWebSocketServerSession,
        event: WebSocketTextEvent
    ) {
        session.outgoing.send(Frame.Text(textEventConverter.createFrameText(event)))
    }

    suspend fun respondError(
        session: DefaultWebSocketServerSession,
        errorEvent: WebSocketTextEvent
    ) {
        respondEvent(session, errorEvent.copy(event = "error"))
    }

}