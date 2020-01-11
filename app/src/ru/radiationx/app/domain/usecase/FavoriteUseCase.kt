package ru.radiationx.app.domain.usecase

import ru.radiationx.app.api.entity.FavoriteRequest
import ru.radiationx.app.domain.entity.Favorite
import ru.radiationx.app.domain.entity.UserPrincipal
import ru.radiationx.app.domain.exception.BadRequestException
import ru.radiationx.app.domain.helper.UserValidator
import ru.radiationx.app.domain.repository.FavoriteRepository

class FavoriteUseCase(
    private val userValidator: UserValidator,
    private val favoriteRepository: FavoriteRepository
) {

    suspend fun getFavorites(principal: UserPrincipal?): List<Favorite> {
        val userId = userValidator.validateUser(principal).id
        return favoriteRepository.getFavorites(userId)
    }

    suspend fun createFavorite(principal: UserPrincipal?, request: FavoriteRequest): Favorite {
        val userId = userValidator.validateUser(principal).id
        val sessionId = request.sessionId ?: throw BadRequestException("No sessionId")
        return favoriteRepository.createFavorite(userId, sessionId)
    }

    suspend fun deleteFavorite(principal: UserPrincipal?, request: FavoriteRequest) {
        val userId = userValidator.validateUser(principal).id
        val sessionId = request.sessionId ?: throw BadRequestException("No sessionId")
        return favoriteRepository.deleteFavorite(userId, sessionId)
    }
}