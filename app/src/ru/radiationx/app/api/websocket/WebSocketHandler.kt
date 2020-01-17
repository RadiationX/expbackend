package ru.radiationx.app.api.websocket

import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.websocket.DefaultWebSocketServerSession
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.channels.consumeEach

class WebSocketHandler : WebSocketSessionHandler {

    var connectHandler: suspend DefaultWebSocketServerSession.() -> Unit = {}
    var disconnectHandler: suspend DefaultWebSocketServerSession.() -> Unit = {}
    var errorHandler: suspend DefaultWebSocketServerSession.(frame: Frame, error: Throwable) -> Unit = { _, _ -> }
    var textFrameHandler: suspend DefaultWebSocketServerSession.(frame: Frame.Text) -> Unit = {}
    var binaryFrameHandler: suspend DefaultWebSocketServerSession.(frame: Frame.Binary) -> Unit = {}
    var closeFrameHandler: suspend DefaultWebSocketServerSession.(frame: Frame.Close) -> Unit = {}
    var pingFrameHandler: suspend DefaultWebSocketServerSession.(frame: Frame.Ping) -> Unit = {}
    var pongFrameHandler: suspend DefaultWebSocketServerSession.(frame: Frame.Pong) -> Unit = {}
    var frameHandler: suspend DefaultWebSocketServerSession.(frame: Frame) -> Unit = { frame ->
        when (frame) {
            is Frame.Text -> textFrameHandler.invoke(this, frame)
            is Frame.Binary -> binaryFrameHandler.invoke(this, frame)
            is Frame.Close -> closeFrameHandler.invoke(this, frame)
            is Frame.Ping -> pingFrameHandler.invoke(this, frame)
            is Frame.Pong -> pongFrameHandler.invoke(this, frame)
        }
    }

    override suspend fun handleSession(session: DefaultWebSocketServerSession) = session.apply {
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
                    errorHandler.invoke(session, frame, error)
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