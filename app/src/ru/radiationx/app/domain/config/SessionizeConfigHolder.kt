package ru.radiationx.app.domain.config

data class SessionizeConfigHolder(
    val url: String,
    val oldUrl: String,
    val interval: Long
)