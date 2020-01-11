package ru.radiationx.domain.entity

import java.time.LocalDateTime


data class User(
    val id: Int,
    val login: String,
    val password: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)