package ru.radiationx.domain.entity

data class Favorite(
    val id: Int,
    val user: User,
    val sessionId: String
)