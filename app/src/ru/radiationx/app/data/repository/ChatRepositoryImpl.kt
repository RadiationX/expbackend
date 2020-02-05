package ru.radiationx.app.data.repository

import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import ru.radiationx.app.data.datasource.ChatDbDataSource
import ru.radiationx.domain.entity.ChatMessage
import ru.radiationx.domain.entity.ChatMessageRequest
import ru.radiationx.domain.repository.ChatRepository
import java.util.concurrent.ConcurrentHashMap

class ChatRepositoryImpl(
    private val chatDbDataSource: ChatDbDataSource
) : ChatRepository {

    private val channel = ConflatedBroadcastChannel<ChatMessage>()

    private val tokenRoomMap = ConcurrentHashMap<String, Set<Int>>()

    override suspend fun observeMessages(token: String, userId: Int): Flow<ChatMessage> = channel
        .asFlow()
        .filter { tokenRoomMap[token]?.contains(it.room.id) ?: false }
        .filter { chatDbDataSource.getUserInRoom(it.room.id, userId) != null }

    override suspend fun setObservableRooms(token: String, rooms: Set<Int>) {
        tokenRoomMap[token] = rooms
    }

    override suspend fun sendMessage(userId: Int, request: ChatMessageRequest): ChatMessage {
        val user = chatDbDataSource.getUserInRoom(request.roomId, userId) ?: throw Exception("You are not in this room")
        return chatDbDataSource.addMessage(request.roomId, userId, request.text).also {
            channel.send(it)
        }
    }

}