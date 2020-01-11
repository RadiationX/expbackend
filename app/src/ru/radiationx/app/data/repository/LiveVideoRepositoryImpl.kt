package ru.radiationx.app.data.repository

import ru.radiationx.domain.entity.LiveVideo
import ru.radiationx.domain.repository.LiveVideoRepository
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.write

class LiveVideoRepositoryImpl : LiveVideoRepository {

    private val live = mutableMapOf<Int, String>()
    private val lock = ReentrantReadWriteLock()

    override suspend fun getVideos(): List<LiveVideo> = live.map { LiveVideo(it.key, it.value) }

    override suspend fun setVideo(roomId: Int, video: String?) {
        lock.write {
            if (video == null) {
                live.remove(roomId)
            } else {
                live[roomId] = video
            }
        }
    }
}