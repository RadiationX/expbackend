package ru.radiationx.app.api

import io.ktor.http.cio.websocket.Frame
import io.ktor.websocket.DefaultWebSocketServerSession
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.channels.consumeEach

class WebSocketHandler {

    var connectHandler: suspend (session: DefaultWebSocketServerSession) -> Unit = {}
    var disconnectHandler: suspend (session: DefaultWebSocketServerSession) -> Unit = {}
    var errorHandler: suspend (session: DefaultWebSocketServerSession, error: Throwable) -> Unit = { _, _ -> }
    var textEventHandler: suspend (session: DefaultWebSocketServerSession, frame: Frame.Text) -> Unit = { _, _ -> }
    var binaryEventHandler: suspend (session: DefaultWebSocketServerSession, frame: Frame.Binary) -> Unit = { _, _ -> }
    var closeEventHandler: suspend (session: DefaultWebSocketServerSession, frame: Frame.Close) -> Unit = { _, _ -> }
    var pingEventHandler: suspend (session: DefaultWebSocketServerSession, frame: Frame.Ping) -> Unit = { _, _ -> }
    var pongEventHandler: suspend (session: DefaultWebSocketServerSession, frame: Frame.Pong) -> Unit = { _, _ -> }
    var frameHandler: suspend (session: DefaultWebSocketServerSession, frame: Frame) -> Unit = { session, frame ->
        when (frame) {
            is Frame.Text -> textEventHandler.invoke(session, frame)
            is Frame.Binary -> binaryEventHandler.invoke(session, frame)
            is Frame.Close -> closeEventHandler.invoke(session, frame)
            is Frame.Ping -> pingEventHandler.invoke(session, frame)
            is Frame.Pong -> pongEventHandler.invoke(session, frame)
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
                    error.printStackTrace()
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