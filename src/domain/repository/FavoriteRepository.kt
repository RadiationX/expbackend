package ru.radiationx.domain.repository

import ru.radiationx.domain.entity.Favorite

interface FavoriteRepository {

    suspend fun getFavorites(uuid: String): List<Favorite>

    suspend fun getAllFavorites(): List<Favorite>

    suspend fun createFavorite(uuid: String, sessionId: String): Boolean

    suspend fun deleteFavorite(uuid: String, sessionId: String): Boolean
}