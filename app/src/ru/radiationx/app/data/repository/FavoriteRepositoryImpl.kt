package ru.radiationx.app.data.repository

import ru.radiationx.app.data.datasource.FavoriteDbDataSource
import ru.radiationx.app.domain.entity.Favorite
import ru.radiationx.app.domain.repository.FavoriteRepository

class FavoriteRepositoryImpl(
    private val favoriteDbDataSource: FavoriteDbDataSource
) : FavoriteRepository {

    override suspend fun getFavorites(userId: Int): List<Favorite> = favoriteDbDataSource.getFavorites(userId)

    override suspend fun getAllFavorites(): List<Favorite> = favoriteDbDataSource.getAllFavorites()

    override suspend fun createFavorite(userId: Int, sessionId: String): Favorite =
        favoriteDbDataSource.createFavorite(userId, sessionId)

    override suspend fun deleteFavorite(userId: Int, sessionId: String) =
        favoriteDbDataSource.deleteFavorite(userId, sessionId)
}