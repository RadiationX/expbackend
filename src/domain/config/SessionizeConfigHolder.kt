package ru.radiationx.domain.config

data class SessionizeConfigHolder(
    val url: String,
    val oldUrl: String,
    val interval: Long
)