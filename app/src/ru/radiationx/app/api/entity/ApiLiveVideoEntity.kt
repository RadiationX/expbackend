package ru.radiationx.app.api.entity

data class LiveVideoRequest(
    val roomId: Int? = null,
    val video: String? = null
)