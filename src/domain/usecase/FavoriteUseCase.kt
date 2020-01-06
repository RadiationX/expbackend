package ru.radiationx.domain.usecase

import ru.radiationx.domain.entity.Favorite
import ru.radiationx.domain.entity.KotlinConfPrincipal
import ru.radiationx.domain.exception.BadRequest
import ru.radiationx.domain.helper.UserValidator
import ru.radiationx.domain.repository.FavoriteRepository

class FavoriteUseCase(
    private val userValidator: UserValidator,
    private val favoriteRepository: FavoriteRepository
) {

    suspend fun getFavorites(principal: KotlinConfPrincipal?): List<Favorite> {
        val uuid = userValidator.checkHasUser(principal).token
        return favoriteRepository.getFavorites(uuid)
    }

    suspend fun createFavorite(principal: KotlinConfPrincipal?, sessionId: String?): Boolean {
        val uuid = userValidator.checkHasUser(principal).token
        sessionId ?: throw BadRequest()
        return favoriteRepository.createFavorite(uuid, sessionId)
    }

    suspend fun deleteFavorite(principal: KotlinConfPrincipal?, sessionId: String?): Boolean {
        val uuid = userValidator.checkHasUser(principal).token
        sessionId ?: throw BadRequest()
        return favoriteRepository.deleteFavorite(uuid, sessionId)
    }
}