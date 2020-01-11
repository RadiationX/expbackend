package ru.radiationx.app.data.datasource

import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import ru.radiationx.app.data.asFavorite
import ru.radiationx.app.data.entity.db.FavoriteRow
import ru.radiationx.app.data.entity.db.FavoritesTable
import ru.radiationx.domain.entity.Favorite
import ru.radiationx.domain.exception.AlreadyExistException
import kotlin.coroutines.CoroutineContext

class FavoriteDbDataSource(
    private val dispatcher: CoroutineContext,
    private val database: Database
) {

    suspend fun getFavorites(userId: Int): List<Favorite> = withContext(dispatcher) {
        transaction(database) {
            val entityId = FavoritesTable.getIdColumn(userId)
            FavoriteRow
                .find { FavoritesTable.userId eq entityId }
                .map { it.asFavorite() }
        }
    }

    suspend fun getAllFavorites(): List<Favorite> = withContext(dispatcher) {
        transaction(database) {
            FavoriteRow
                .all()
                .map { it.asFavorite() }
        }
    }

    suspend fun createFavorite(userId: Int, sessionId: String): Favorite = withContext(dispatcher) {
        transaction(database) {
            val entityId = FavoritesTable.getIdColumn(userId)
            val count = FavoriteRow
                .find { (FavoritesTable.userId eq entityId) and (FavoritesTable.sessionId eq sessionId) }
                .count()

            if (count != 0) {
                throw AlreadyExistException()
            }
            val favoriteId = FavoritesTable.insertAndGetId {
                it[FavoritesTable.userId] = entityId
                it[FavoritesTable.sessionId] = sessionId
            }
            FavoriteRow[favoriteId].asFavorite()
        }
    }

    suspend fun deleteFavorite(userId: Int, sessionId: String) = withContext(dispatcher) {
        transaction(database) {
            val entityId = FavoritesTable.getIdColumn(userId)
            FavoritesTable
                .deleteWhere {
                    (FavoritesTable.userId eq entityId) and (FavoritesTable.sessionId eq sessionId)
                }
            Unit
        }
    }
}