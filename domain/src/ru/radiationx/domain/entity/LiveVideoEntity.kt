package ru.radiationx.domain.entity

data class LiveVideo(
    val room: Int,
    val videoId: String
)

data class LiveVideoRequest(
    val roomId: Int? = null,
    val video: String? = null
)