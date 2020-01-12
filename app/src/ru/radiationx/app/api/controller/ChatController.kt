package ru.radiationx.app.api.controller

import com.google.gson.Gson
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.websocket.DefaultWebSocketServerSession
import ru.radiationx.app.api.WebSocketHandler

class ChatController(
    private val gson: Gson,
    private val webSocketHandler: WebSocketHandler
) {

    init {
        webSocketHandler.textEventHandler = { session, frame ->
            val text = frame.readText()
            session.send(Frame.Text("YOU SAID: $text"))
        }
    }

    suspend fun handleSession(session: DefaultWebSocketServerSession) = webSocketHandler.handleSession(session)


}