package ru.radiationx.app.domain.entity

import java.time.LocalDateTime

data class Favorite(
    val id: Int,
    val user: User?,
    val sessionId: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)