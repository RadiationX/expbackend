package ru.radiationx.app.api

import io.ktor.http.cio.websocket.Frame
import io.ktor.websocket.DefaultWebSocketServerSession
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.channels.consumeEach

class WebSocketHandler {

    var connectHandler: suspend DefaultWebSocketServerSession.() -> Unit = {}
    var disconnectHandler: suspend DefaultWebSocketServerSession.() -> Unit = {}
    var errorHandler: suspend DefaultWebSocketServerSession.(error: Throwable) -> Unit = { }
    var textEventHandler: suspend DefaultWebSocketServerSession.(frame: Frame.Text) -> Unit = { }
    var binaryEventHandler: suspend DefaultWebSocketServerSession.(frame: Frame.Binary) -> Unit = { }
    var closeEventHandler: suspend DefaultWebSocketServerSession.(frame: Frame.Close) -> Unit = { }
    var pingEventHandler: suspend DefaultWebSocketServerSession.(frame: Frame.Ping) -> Unit = { }
    var pongEventHandler: suspend DefaultWebSocketServerSession.(frame: Frame.Pong) -> Unit = { }
    var frameHandler: suspend DefaultWebSocketServerSession.(frame: Frame) -> Unit = { frame ->
        when (frame) {
            is Frame.Text -> textEventHandler.invoke(this, frame)
            is Frame.Binary -> binaryEventHandler.invoke(this, frame)
            is Frame.Close -> closeEventHandler.invoke(this, frame)
            is Frame.Ping -> pingEventHandler.invoke(this, frame)
            is Frame.Pong -> pongEventHandler.invoke(this, frame)
        }
    }

    suspend fun handleSession(session: DefaultWebSocketServerSession) = session.apply {
        try {
            connectHandler.invoke(session)
            incoming.consumeEach { frame ->
                try {
                    frameHandler.invoke(session, frame)
                } catch (error: Throwable) {
                    when (error) {
                        is ClosedReceiveChannelException,
                        is ClosedSendChannelException -> throw error
                    }
                    errorHandler.invoke(session, error)
                }
            }
        } catch (e: ClosedReceiveChannelException) {
            println("onClose receive ${closeReason.await()}")
        } catch (e: ClosedSendChannelException) {
            println("onClose send ${closeReason.await()}")
        } catch (e: Throwable) {
            println("unhandled onError ${closeReason.await()}")
            e.printStackTrace()
        } finally {
            disconnectHandler.invoke(session)
        }
    }
}