package ru.radiationx.domain.entity

import java.time.LocalDateTime

data class User(
    val id: Int,
    val uuid: String,
    val remote: String,
    val timestamp: LocalDateTime
)