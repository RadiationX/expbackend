package ru.radiationx.data.datasource

import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.radiationx.data.entity.db.FavoritesTable
import ru.radiationx.domain.entity.Favorite
import kotlin.coroutines.CoroutineContext

class FavoriteDbDataSource(
    private val dispatcher: CoroutineContext,
    private val database: Database
) {

    suspend fun getFavorites(uuid: String): List<Favorite> = withContext(dispatcher) {
        transaction(database) {
            FavoritesTable
                .select { FavoritesTable.uuid eq uuid }
                .map { it.asFavorite() }
        }
    }

    suspend fun getAllFavorites(): List<Favorite> = withContext(dispatcher) {
        transaction(database) {
            FavoritesTable
                .selectAll()
                .map { it.asFavorite() }
        }
    }

    suspend fun createFavorite(uuid: String, sessionId: String): Boolean = withContext(dispatcher) {
        transaction(database) {
            val count = FavoritesTable
                .slice(FavoritesTable.uuid, FavoritesTable.sessionId)
                .select { (FavoritesTable.uuid eq uuid) and (FavoritesTable.sessionId eq sessionId) }
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

    private fun ResultRow.asFavorite(): Favorite = Favorite(
        get(FavoritesTable.id),
        get(FavoritesTable.uuid),
        get(FavoritesTable.sessionId)
    )
}