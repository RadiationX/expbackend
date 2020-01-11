package ru.radiationx.app.domain.usecase

import ru.radiationx.app.api.entity.LiveVideoRequest
import ru.radiationx.app.domain.entity.LiveVideo
import ru.radiationx.app.domain.entity.UserPrincipal
import ru.radiationx.app.domain.exception.BadRequestException
import ru.radiationx.app.domain.helper.UserValidator
import ru.radiationx.app.domain.repository.LiveVideoRepository

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