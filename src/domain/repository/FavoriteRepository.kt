package ru.radiationx.domain.repository

import ru.radiationx.domain.entity.Favorite

interface FavoriteRepository {

    suspend fun getFavorites(userId: Int): List<Favorite>

    suspend fun getAllFavorites(): List<Favorite>

    suspend fun createFavorite(userId: Int, sessionId: String): Boolean

    suspend fun deleteFavorite(userId: Int, sessionId: String): Boolean
}