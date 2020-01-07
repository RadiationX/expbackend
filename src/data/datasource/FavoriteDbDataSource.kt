package ru.radiationx.data.datasource

import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.radiationx.data.asFavorite
import ru.radiationx.data.asUser
import ru.radiationx.data.entity.db.FavoriteRow
import ru.radiationx.data.entity.db.FavoritesTable
import ru.radiationx.domain.entity.Favorite
import kotlin.coroutines.CoroutineContext

class FavoriteDbDataSource(
    private val dispatcher: CoroutineContext,
    private val database: Database
) {

    suspend fun getFavorites(uuid: String): List<Favorite> = withContext(dispatcher) {
        transaction(database) {
            FavoriteRow
                .find { FavoritesTable.uuid eq uuid }
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

    suspend fun createFavorite(uuid: String, sessionId: String): Boolean = withContext(dispatcher) {
        transaction(database) {
            val count = FavoriteRow
                .find { (FavoritesTable.uuid eq uuid) and (FavoritesTable.sessionId eq sessionId) }
                .count()

            if (count == 0) {
                FavoritesTable.insert {
                    it[FavoritesTable.uuid] = uuid
                    it[FavoritesTable.sessionId] = sessionId
                }
            }
            count == 0
        }
    }

    suspend fun deleteFavorite(uuid: String, sessionId: String): Boolean = withContext(dispatcher) {
        transaction(database) {
            FavoritesTable
                .deleteWhere {
                    (FavoritesTable.uuid eq uuid) and (FavoritesTable.sessionId eq sessionId)
                }
            true
        }
    }
}