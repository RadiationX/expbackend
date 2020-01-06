package ru.radiationx.domain.config

class ServiceConfigHolder(
    val mode: String,
    val production: Boolean,
    val secret: String
)