package ru.radiationx.data.datasource

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.radiationx.data.entity.db.FavoritesTable
import ru.radiationx.domain.entity.Favorite

class FavoriteDbDataSource(
    private val database: Database
) {

    fun getFavorites(uuid: String): List<Favorite> = transaction(database) {
        FavoritesTable
            .select { FavoritesTable.uuid eq uuid }
            .map { it.asFavorite() }
    }

    fun getAllFavorites(): List<Favorite> = transaction(database) {
        FavoritesTable
            .selectAll()
            .map { it.asFavorite() }
    }

    fun createFavorite(uuid: String, sessionId: String): Boolean = transaction(database) {
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

    fun deleteFavorite(uuid: String, sessionId: String): Boolean = transaction(database) {
        FavoritesTable
            .deleteWhere {
                (FavoritesTable.uuid eq uuid) and (FavoritesTable.sessionId eq sessionId)
            }
        true
    }

    private fun ResultRow.asFavorite(): Favorite = Favorite(
        get(FavoritesTable.id),
        get(FavoritesTable.uuid),
        get(FavoritesTable.sessionId)
    )
}