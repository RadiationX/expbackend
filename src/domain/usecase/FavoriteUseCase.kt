package ru.radiationx.domain.usecase

import ru.radiationx.domain.entity.UserPrincipal
import ru.radiationx.domain.entity.Favorite
import ru.radiationx.domain.exception.BadRequest
import ru.radiationx.domain.helper.UserValidator
import ru.radiationx.domain.repository.FavoriteRepository

class FavoriteUseCase(
    private val userValidator: UserValidator,
    private val favoriteRepository: FavoriteRepository
) {

    suspend fun getFavorites(principal: UserPrincipal?): List<Favorite> {
        val userId = userValidator.validateUser(principal).id
        return favoriteRepository.getFavorites(userId)
    }

    suspend fun createFavorite(principal: UserPrincipal?, sessionId: String?): Boolean {
        val userId = userValidator.validateUser(principal).id
        sessionId ?: throw BadRequest()
        return favoriteRepository.createFavorite(userId, sessionId)
    }

    suspend fun deleteFavorite(principal: UserPrincipal?, sessionId: String?): Boolean {
        val userId = userValidator.validateUser(principal).id
        sessionId ?: throw BadRequest()
        return favoriteRepository.deleteFavorite(userId, sessionId)
    }
}