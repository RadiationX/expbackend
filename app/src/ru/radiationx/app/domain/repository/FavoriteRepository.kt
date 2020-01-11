package ru.radiationx.app.domain.repository

import ru.radiationx.app.domain.entity.Favorite

interface FavoriteRepository {

    suspend fun getFavorites(userId: Int): List<Favorite>

    suspend fun getAllFavorites(): List<Favorite>

    suspend fun createFavorite(userId: Int, sessionId: String): Favorite

    suspend fun deleteFavorite(userId: Int, sessionId: String)
}