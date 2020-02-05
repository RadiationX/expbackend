package ru.radiationx.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.radiationx.domain.entity.ChatMessage
import ru.radiationx.domain.entity.ChatMessageRequest
import ru.radiationx.domain.entity.User
import ru.radiationx.domain.exception.Unauthorized
import ru.radiationx.domain.helper.UserValidator
import ru.radiationx.domain.repository.ChatRepository

class ChatService(
    private val userValidator: UserValidator,
    private val chatRepository: ChatRepository
) {

    suspend fun observeMessages(token: String?, principal: User?): Flow<ChatMessage> {
        token ?: throw Unauthorized()
        val userId = userValidator.validateUser(principal).id
        return chatRepository.observeMessages(token, userId)
    }

    suspend fun setObservableRooms(token: String?, rooms: Set<Int>) {
        token ?: throw Unauthorized()
        return chatRepository.setObservableRooms(token, rooms)
    }

    suspend fun sendMessage(principal: User?, request: ChatMessageRequest): ChatMessage {
        val userId = userValidator.validateUser(principal).id
        return chatRepository.sendMessage(userId, request)
    }
}