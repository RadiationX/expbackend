package ru.radiationx.data.repository

import ru.radiationx.data.datasource.FavoriteDbDataSource
import ru.radiationx.domain.entity.Favorite
import ru.radiationx.domain.repository.FavoriteRepository

class FavoriteRepositoryImpl(
    private val favoriteDbDataSource: FavoriteDbDataSource
) : FavoriteRepository {

    override suspend fun getFavorites(uuid: String): List<Favorite> = favoriteDbDataSource.getFavorites(uuid)

    override suspend fun getAllFavorites(): List<Favorite> = favoriteDbDataSource.getAllFavorites()

    override suspend fun createFavorite(uuid: String, sessionId: String): Boolean =
        favoriteDbDataSource.createFavorite(uuid, sessionId)

    override suspend fun deleteFavorite(uuid: String, sessionId: String): Boolean =
        favoriteDbDataSource.deleteFavorite(uuid, sessionId)
}