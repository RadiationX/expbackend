package ru.radiationx.domain.usecase

import ru.radiationx.api.entity.LiveVideoRequest
import ru.radiationx.domain.entity.LiveVideo
import ru.radiationx.domain.entity.UserPrincipal
import ru.radiationx.domain.exception.BadRequestException
import ru.radiationx.domain.helper.UserValidator
import ru.radiationx.domain.repository.LiveVideoRepository

class LiveVideoUseCase(
    private val userValidator: UserValidator,
    private val liveVideoRepository: LiveVideoRepository
) {

    suspend fun getVideo(): List<LiveVideo> = liveVideoRepository.getVideos()

    suspend fun setVideo(principal: UserPrincipal?, request: LiveVideoRequest) {
        userValidator.checkIsAdmin(principal)
        val roomId = request.roomId ?: throw BadRequestException("No room id")
        liveVideoRepository.setVideo(roomId, request.video)
    }

}