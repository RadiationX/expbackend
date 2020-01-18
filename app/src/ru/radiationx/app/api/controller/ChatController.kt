package ru.radiationx.app.api.controller

import io.ktor.features.callId
import io.ktor.websocket.DefaultWebSocketServerSession
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.*
import ru.radiationx.app.api.base.WebSocketError
import ru.radiationx.app.api.base.WebSocketEvent
import ru.radiationx.app.api.base.WebSocketTextEvent
import ru.radiationx.app.api.toIdResponse
import ru.radiationx.app.api.websocket.converter.WebSocketJsonEventConverter
import ru.radiationx.app.api.websocket.handler.WebSocketJsonEventHandler
import ru.radiationx.app.api.websocket.WebSocketSessionHandler
import ru.radiationx.app.userPrincipal
import ru.radiationx.app.wrapError
import ru.radiationx.domain.entity.ChatMessage
import ru.radiationx.domain.entity.ChatMessageRequest
import ru.radiationx.domain.exception.NotFound
import ru.radiationx.domain.usecase.ChatService
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class ChatController(
    private val webSocketHandler: WebSocketJsonEventHandler,
    private val jsonEventConverter: WebSocketJsonEventConverter,
    private val chatService: ChatService
) : WebSocketSessionHandler {

    companion object {
        private const val EVENT_SEND_MESSAGE = "sendMessage"
        private const val EVENT_OBSERVE_MESSAGE = "observeMessages"
    }

    init {
        webSocketHandler.textEventHandler = { textEvent ->
            attachRoute(textEvent)
        }

        webSocketHandler.errorHandler = { uuid, error ->
            val wrappedError = wrapError(error)
            webSocketHandler.respondError(this, WebSocketError(wrappedError, uuid))
        }

        webSocketHandler.connectHandler = {
            println("connect ${call.userPrincipal?.id}")
            observeMessages()
        }

        webSocketHandler.disconnectHandler = {
            println("disconnect ${call.userPrincipal?.id}")
        }

        webSocketHandler.closeEventHandler = {
            println("close bcs $it")
        }
    }

    override suspend fun handleSession(session: DefaultWebSocketServerSession) = webSocketHandler.handleSession(session)

    private suspend fun DefaultWebSocketServerSession.attachRoute(textEvent: WebSocketTextEvent) {
        when (textEvent.event) {
            EVENT_SEND_MESSAGE -> sendMessageRoute(textEvent)
            else -> throw NotFound()
        }
    }

    private suspend fun DefaultWebSocketServerSession.observeMessages() {
        chatService
            .observeMessages(call.userPrincipal?.user)
            .drop(1)
            .onEach {
                simpleRespond(EVENT_OBSERVE_MESSAGE, it.toIdResponse())
            }
            .launchIn(this)
    }

    private suspend fun DefaultWebSocketServerSession.sendMessageRoute(textEvent: WebSocketTextEvent) {
        val event = jsonEventConverter.parseFrameText<ChatMessageRequest>(textEvent)
        val message = chatService.sendMessage(call.userPrincipal?.user, event.data).toIdResponse()
        simpleRespond(textEvent, message)
    }

    private suspend fun <T> DefaultWebSocketServerSession.simpleRespond(event: String, data: T) {
        webSocketHandler.respondEvent(this, WebSocketEvent(event, data))
    }

    private suspend fun <T> DefaultWebSocketServerSession.simpleRespond(textEvent: WebSocketTextEvent, data: T) {
        webSocketHandler.simpleRespond(this, textEvent, data)
    }
}