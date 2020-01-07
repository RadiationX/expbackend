package ru.radiationx.data.entity.db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import ru.radiationx.data.entity.db.UsersTable.default
import ru.radiationx.data.entity.db.UsersTable.index

internal object UsersTable : IntIdTable("users") {
    val uuid = varchar("uuid", 50).index().default("")
    val remote = varchar("remote", 50).default("")
    val timestamp = varchar("timestamp", 50).default("")
}

internal object FavoritesTable : IntIdTable("favorites") {
    val uuid = reference("uuid", UsersTable.uuid)
    val sessionId = varchar("sessionId", 50)
}

internal object VotesTable : IntIdTable("votes") {
    val uuid = reference("uuid", UsersTable.uuid)
    val timestamp = varchar("timestamp", 50)
    val sessionId = varchar("sessionId", 50).index()
    val rating = integer("rating")
}


internal class UserRow(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserRow>(UsersTable)

    var uuid by UsersTable.uuid
    var remote by UsersTable.remote
    var timestamp by UsersTable.timestamp
}

internal class FavoriteRow(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<FavoriteRow>(FavoritesTable)

    var user by UserRow referencedOn FavoritesTable.uuid
    var sessionId by FavoritesTable.sessionId
}

internal class VotesRow(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<VotesRow>(VotesTable)

    var timestamp by VotesTable.timestamp
    var uuid by UserRow referencedOn VotesTable.uuid
    var sessionId by VotesTable.sessionId
    var rating by VotesTable.rating
}
