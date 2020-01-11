package ru.radiationx.domain.usecase

import ru.radiationx.domain.entity.Favorite
import ru.radiationx.domain.entity.FavoriteRequest
import ru.radiationx.domain.entity.User
import ru.radiationx.domain.exception.BadRequestException
import ru.radiationx.domain.helper.UserValidator
import ru.radiationx.domain.repository.FavoriteRepository

class FavoriteUseCase(
    private val userValidator: UserValidator,
    private val favoriteRepository: FavoriteRepository
) {

    suspend fun getFavorites(principal: User?): List<Favorite> {
        val userId = userValidator.validateUser(principal).id
        return favoriteRepository.getFavorites(userId)
    }

    suspend fun createFavorite(principal: User?, request: FavoriteRequest): Favorite {
        val userId = userValidator.validateUser(principal).id
        val sessionId = request.sessionId ?: throw BadRequestException("No sessionId")
        return favoriteRepository.createFavorite(userId, sessionId)
    }

    suspend fun deleteFavorite(principal: User?, request: FavoriteRequest) {
        val userId = userValidator.validateUser(principal).id
        val sessionId = request.sessionId ?: throw BadRequestException("No sessionId")
        return favoriteRepository.deleteFavorite(userId, sessionId)
    }
}