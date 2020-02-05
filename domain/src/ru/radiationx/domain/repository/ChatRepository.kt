package ru.radiationx.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.radiationx.domain.entity.ChatMessage
import ru.radiationx.domain.entity.ChatMessageRequest

interface ChatRepository {

    suspend fun observeMessages(token: String, userId: Int): Flow<ChatMessage>

    suspend fun setObservableRooms(token: String, rooms: Set<Int>)

    suspend fun sendMessage(userId: Int, request: ChatMessageRequest): ChatMessage
}