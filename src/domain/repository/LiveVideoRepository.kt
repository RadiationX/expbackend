package ru.radiationx.domain.repository

import ru.radiationx.domain.entity.LiveVideo

interface LiveVideoRepository {

    suspend fun getVideos(): List<LiveVideo>

    suspend fun setVideo(roomId: Int, video: String?)
}