package ru.radiationx.domain.usecase

import ru.radiationx.UserPrincipal
import ru.radiationx.domain.entity.Favorite
import ru.radiationx.domain.exception.BadRequest
import ru.radiationx.domain.helper.UserValidator
import ru.radiationx.domain.repository.FavoriteRepository

class FavoriteUseCase(
    private val userValidator: UserValidator,
    private val favoriteRepository: FavoriteRepository
) {

    suspend fun getFavorites(principal: UserPrincipal?): List<Favorite> {
        val userId = userValidator.checkHasUser(principal).id
        return favoriteRepository.getFavorites(userId)
    }

    suspend fun createFavorite(principal: UserPrincipal?, sessionId: String?): Boolean {
        val uuid = userValidator.checkHasUser(principal).id
        sessionId ?: throw BadRequest()
        return favoriteRepository.createFavorite(uuid, sessionId)
    }

    suspend fun deleteFavorite(principal: UserPrincipal?, sessionId: String?): Boolean {
        val userId = userValidator.checkHasUser(principal).id
        sessionId ?: throw BadRequest()
        return favoriteRepository.deleteFavorite(userId, sessionId)
    }
}