package ru.radiationx.domain.usecase

import ru.radiationx.domain.entity.UserPrincipal
import ru.radiationx.domain.entity.LiveVideo
import ru.radiationx.domain.exception.BadRequest
import ru.radiationx.domain.helper.UserValidator
import ru.radiationx.domain.repository.LiveVideoRepository

class LiveVideoUseCase(
    private val userValidator: UserValidator,
    private val liveVideoRepository: LiveVideoRepository
) {

    suspend fun getVideo(): List<LiveVideo> = liveVideoRepository.getVideos()

    suspend fun setVideo(principal: UserPrincipal?, roomIdParam: String?, videoParam: String?) {
        userValidator.checkIsAdmin(principal)
        val roomId = roomIdParam?.toIntOrNull() ?: throw BadRequest()
        liveVideoRepository.setVideo(roomId, videoParam)
    }

}