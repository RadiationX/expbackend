package ru.radiationx.domain.entity

import java.time.LocalDateTime

data class ChatMessageRequest(
    val roomId: Int,
    val text: String
)

data class ChatMessage(
    val id: Int,
    val room: ChatRoom,
    val user: User,
    val text: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)

data class ChatRoom(
    val id: Int,
    val name: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)