package ru.radiationx.app.domain.config

data class ServiceConfigHolder(
    val mode: String,
    val production: Boolean,
    val secret: String
)