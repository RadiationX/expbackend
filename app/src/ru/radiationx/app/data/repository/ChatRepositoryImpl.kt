package ru.radiationx.app.data.repository

import kotlinx.atomicfu.AtomicInt
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import ru.radiationx.app.data.datasource.ChatDbDataSource
import ru.radiationx.domain.entity.ChatMessage
import ru.radiationx.domain.entity.ChatMessageRequest
import ru.radiationx.domain.repository.ChatRepository
import kotlin.concurrent.fixedRateTimer

class ChatRepositoryImpl(
    private val chatDbDataSource: ChatDbDataSource
) : ChatRepository {

    private val channel = ConflatedBroadcastChannel<ChatMessage>()

    override suspend fun observeMessages(userId: Int): Flow<ChatMessage> = channel.asFlow()

    override suspend fun sendMessage(userId: Int, request: ChatMessageRequest): ChatMessage {
        val message = chatDbDataSource.addMessage(request.roomId, userId, request.text)
        channel.send(message)
        return message
    }

}