package ru.radiationx.app.domain.repository

import ru.radiationx.app.domain.entity.LiveVideo

interface LiveVideoRepository {

    suspend fun getVideos(): List<LiveVideo>

    suspend fun setVideo(roomId: Int, video: String?)
}