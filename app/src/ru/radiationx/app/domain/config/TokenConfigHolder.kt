package ru.radiationx.app.domain.config

data class TokenConfigHolder(
    val issuer: String,
    val realm: String,
    val secret: String,
    val expiration: Long
)