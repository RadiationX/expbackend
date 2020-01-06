package ru.radiationx.domain.usecase

import ru.radiationx.domain.entity.Conference
import ru.radiationx.domain.entity.KotlinConfPrincipal
import ru.radiationx.domain.exception.Unauthorized
import ru.radiationx.domain.helper.UserValidator
import ru.radiationx.domain.repository.FavoriteRepository
import ru.radiationx.domain.repository.LiveVideoRepository
import ru.radiationx.domain.repository.SessionizeRepository
import ru.radiationx.domain.repository.VoteRepository

class FullInfoUseCase(
    private val userValidator: UserValidator,
    private val voteRepository: VoteRepository,
    private val favoriteRepository: FavoriteRepository,
    private val sessionizeRepository: SessionizeRepository,
    private val liveVideoRepository: LiveVideoRepository
) {

    suspend fun getFullInfo(principal: KotlinConfPrincipal?, old: Boolean): Conference {
        val data = sessionizeRepository.getData(old)
        val liveInfo = liveVideoRepository.getVideos()
        val votesRequired = voteRepository.getRequired()
        return try {
            val uuid = userValidator.checkHasUser(principal).token
            val votes = voteRepository.getVotes(uuid)
            val favorites = favoriteRepository.getFavorites(uuid)
            Conference(data, favorites, votes, liveInfo, votesRequired)
        } catch (ex: Unauthorized) {
            Conference(data, emptyList(), emptyList(), liveInfo, votesRequired)
        }
    }

}