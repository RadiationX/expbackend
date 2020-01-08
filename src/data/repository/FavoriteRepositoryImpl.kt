package ru.radiationx.data.repository

import ru.radiationx.data.datasource.FavoriteDbDataSource
import ru.radiationx.domain.entity.Favorite
import ru.radiationx.domain.repository.FavoriteRepository

class FavoriteRepositoryImpl(
    private val favoriteDbDataSource: FavoriteDbDataSource
) : FavoriteRepository {

    override suspend fun getFavorites(userId: Int): List<Favorite> = favoriteDbDataSource.getFavorites(userId)

    override suspend fun getAllFavorites(): List<Favorite> = favoriteDbDataSource.getAllFavorites()

    override suspend fun createFavorite(userId: Int, sessionId: String): Boolean =
        favoriteDbDataSource.createFavorite(userId, sessionId)

    override suspend fun deleteFavorite(userId: Int, sessionId: String): Boolean =
        favoriteDbDataSource.deleteFavorite(userId, sessionId)
}