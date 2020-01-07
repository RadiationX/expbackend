package ru.radiationx.data.entity.db

import org.jetbrains.exposed.sql.Table

internal object UsersTable : Table("users") {
    val id = integer("id").autoIncrement()
    val uuid = varchar("uuid", 50).index().default("")
    val remote = varchar("remote", 50).default("")
    val timestamp = varchar("timestamp", 50).default("")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

internal object FavoritesTable : Table("favorites") {
    val id = integer("id").autoIncrement()
    val uuid = varchar("uuid", 50).index()
    val sessionId = varchar("sessionId", 50)

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

internal object VotesTable : Table("votes") {
    val id = integer("id").autoIncrement()
    val timestamp = varchar("timestamp", 50)
    val uuid = varchar("uuid", 50).index()
    val sessionId = varchar("sessionId", 50).index()
    val rating = integer("rating")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}
