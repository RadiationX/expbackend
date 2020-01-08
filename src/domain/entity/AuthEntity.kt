package ru.radiationx.domain.entity

class Token(
    val id: Int,
    val user: User?,
    val token: String,
    val ip: String,
    val info: String?
)