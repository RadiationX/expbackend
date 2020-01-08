package ru.radiationx.data.entity.db

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ReferenceOption

/*
* Tables
* В reference нужно указывать Table, а не column, если таблица от IntIdTable
* */
internal object TokensTable : BaseIntIdTable("auth_tokens") {
    val userId = reference("user_id", UsersTable, ReferenceOption.SET_NULL).nullable()
    val token = varchar("token", 255)
    val ip = varchar("ip", 50)
    val info = varchar("info", 255).nullable()
}

internal object UsersTable : BaseIntIdTable("users") {
    val login = varchar("login", 50).uniqueIndex("login")
    val password = varchar("password", 255)
}

internal object FavoritesTable : BaseIntIdTable("favorites") {
    val userId = reference("user_id", UsersTable, ReferenceOption.SET_NULL).nullable()
    val sessionId = varchar("session_id", 50)
}

internal object VotesTable : BaseIntIdTable("votes") {
    val userId = reference("user_id", UsersTable, ReferenceOption.SET_NULL).nullable()
    val sessionId = varchar("session_id", 50)
    val rating = integer("rating")
}

/*
* Rows
* */
internal class TokenRow(id: EntityID<Int>) : BaseIntEntity(id, TokensTable) {
    companion object : BaseIntEntityClass<TokenRow>(TokensTable)

    var userId by UserRow optionalReferencedOn TokensTable.userId
    var token by TokensTable.token
    var ip by TokensTable.ip
    var info by TokensTable.info
}

internal class UserRow(id: EntityID<Int>) : BaseIntEntity(id, UsersTable) {
    companion object : BaseIntEntityClass<UserRow>(UsersTable)

    var login by UsersTable.login
    var password by UsersTable.password
}

internal class FavoriteRow(id: EntityID<Int>) : BaseIntEntity(id, FavoritesTable) {
    companion object : BaseIntEntityClass<FavoriteRow>(FavoritesTable)

    var user by UserRow optionalReferencedOn FavoritesTable.userId
    var sessionId by FavoritesTable.sessionId
}

internal class VotesRow(id: EntityID<Int>) : BaseIntEntity(id, VotesTable) {
    companion object : BaseIntEntityClass<VotesRow>(VotesTable)

    var user by UserRow optionalReferencedOn VotesTable.userId
    var sessionId by VotesTable.sessionId
    var rating by VotesTable.rating
}
