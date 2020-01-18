package ru.radiationx.app.api.entity

import java.time.LocalDateTime

data class ApiChatMessageResponse(
    val id: Int,
    val user: ApiUserResponse?,
    val userId: Int,
    val room: Any?,
    val roomId: Int,
    val text: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)

data class ApiChatRoomResponse(
    val id: Int,
    val name: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)