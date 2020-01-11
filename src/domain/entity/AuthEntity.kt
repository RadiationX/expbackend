package ru.radiationx.domain.entity

data class Token(
    val id: Int,
    val user: User?,
    val token: String,
    val ip: String,
    val info: String?
)

data class AuthCredential(
    val login: String,
    val password: String
)