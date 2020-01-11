package ru.radiationx.app.domain.usecase

import ru.radiationx.app.domain.entity.Conference
import ru.radiationx.app.domain.entity.UserPrincipal
import ru.radiationx.app.domain.exception.Unauthorized
import ru.radiationx.app.domain.helper.UserValidator
import ru.radiationx.app.domain.repository.FavoriteRepository
import ru.radiationx.app.domain.repository.LiveVideoRepository
import ru.radiationx.app.domain.repository.SessionizeRepository
import ru.radiationx.app.domain.repository.VoteRepository

class FullInfoUseCase(
    private val userValidator: UserValidator,
    private val voteRepository: VoteRepository,
    private val favoriteRepository: FavoriteRepository,
    private val sessionizeRepository: SessionizeRepository,
    private val liveVideoRepository: LiveVideoRepository
) {

    suspend fun getFullInfo(principal: UserPrincipal?, old: Boolean): Conference {
        val data = sessionizeRepository.getData(old)
        val liveInfo = liveVideoRepository.getVideos()
        val votesRequired = voteRepository.getRequired()
        return try {
            val userId = userValidator.validateUser(principal).id
            val votes = voteRepository.getVotes(userId)
            val favorites = favoriteRepository.getFavorites(userId)
            Conference(data, favorites, votes, liveInfo, votesRequired)
        } catch (ex: Unauthorized) {
            Conference(data, emptyList(), emptyList(), liveInfo, votesRequired)
        }
    }

}