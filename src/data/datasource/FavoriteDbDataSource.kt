package ru.radiationx.data.datasource

import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import ru.radiationx.data.asFavorite
import ru.radiationx.data.entity.db.FavoriteRow
import ru.radiationx.data.entity.db.FavoritesTable
import ru.radiationx.domain.entity.Favorite
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

    suspend fun createFavorite(userId: Int, sessionId: String): Boolean = withContext(dispatcher) {
        transaction(database) {
            val entityId = FavoritesTable.getIdColumn(userId)
            val count = FavoriteRow
                .find { (FavoritesTable.userId eq entityId) and (FavoritesTable.sessionId eq sessionId) }
                .count()

            if (count == 0) {
                FavoritesTable.insert {
                    it[FavoritesTable.userId] = entityId
                    it[FavoritesTable.sessionId] = sessionId
                }
            }
            count == 0
        }
    }

    suspend fun deleteFavorite(userId: Int, sessionId: String): Boolean = withContext(dispatcher) {
        transaction(database) {
            val entityId = FavoritesTable.getIdColumn(userId)
            FavoritesTable
                .deleteWhere {
                    (FavoritesTable.userId eq entityId) and (FavoritesTable.sessionId eq sessionId)
                }
            true
        }
    }
}