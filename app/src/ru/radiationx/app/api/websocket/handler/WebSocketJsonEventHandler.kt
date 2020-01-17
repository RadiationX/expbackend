package ru.radiationx.app.api.websocket.handler

import io.ktor.websocket.DefaultWebSocketServerSession
import ru.radiationx.app.api.base.WebSocketError
import ru.radiationx.app.api.base.WebSocketEvent
import ru.radiationx.app.api.base.WebSocketTextEvent
import ru.radiationx.app.api.websocket.converter.WebSocketJsonEventConverter
import ru.radiationx.app.api.websocket.converter.WebSocketTextEventConverter

class WebSocketJsonEventHandler(
    private val webSocketHandler: WebSocketHandler,
    private val textEventConverter: WebSocketTextEventConverter,
    private val jsonEventConverter: WebSocketJsonEventConverter
) : WebSocketTextEventHandler(webSocketHandler, textEventConverter) {

    override suspend fun handleSession(session: DefaultWebSocketServerSession) = webSocketHandler.handleSession(session)

    suspend fun <T> simpleRespond(
        session: DefaultWebSocketServerSession,
        textEvent: WebSocketTextEvent,
        data: T
    ) {
        respondEvent(session, WebSocketEvent(textEvent.event, data, textEvent.uuid))
    }

    suspend fun <T> respondEvent(
        session: DefaultWebSocketServerSession,
        event: WebSocketEvent<T>
    ) {
        respondEvent(session, jsonEventConverter.createFrameText(event))
    }

    suspend fun respondError(
        session: DefaultWebSocketServerSession,
        errorEvent: WebSocketError
    ) {
        val event = WebSocketEvent("error", errorEvent.error, errorEvent.uuid)
        respondError(session, jsonEventConverter.createFrameText(event))
    }
}