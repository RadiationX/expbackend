package ru.radiationx.app.api.websocket

import io.ktor.websocket.DefaultWebSocketServerSession

interface WebSocketSessionHandler {

    suspend fun handleSession(session: DefaultWebSocketServerSession): DefaultWebSocketServerSession
}