package ru.radiationx.app.data.entity.db

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

/*
* В reference нужно указывать Table, а не column, если таблица от IntIdTable
* */

/* Tokens */
internal object TokensTable : BaseIntIdTable("auth_tokens") {
    val userId = reference("user_id", UsersTable, ReferenceOption.SET_NULL).nullable()
    val token = varchar("token", 255)
    val ip = varchar("ip", 50)
    val info = varchar("info", 255).nullable()
}

internal class TokenRow(id: EntityID<Int>) : BaseIntEntity(id, TokensTable) {
    companion object : BaseIntEntityClass<TokenRow>(TokensTable)

    var user by UserRow optionalReferencedOn TokensTable.userId
    var token by TokensTable.token
    var ip by TokensTable.ip
    var info by TokensTable.info
}

/* Users */
internal object UsersTable : BaseIntIdTable("users") {
    val login = varchar("login", 50).uniqueIndex("login")
    val password = varchar("password", 255)
}

internal class UserRow(id: EntityID<Int>) : BaseIntEntity(id, UsersTable) {
    companion object : BaseIntEntityClass<UserRow>(UsersTable)

    var login by UsersTable.login
    var password by UsersTable.password
}

/* Favorites */
internal object FavoritesTable : BaseIntIdTable("favorites") {
    val userId = reference("user_id", UsersTable, ReferenceOption.SET_NULL).nullable()
    val sessionId = varchar("session_id", 50)
}

internal class FavoriteRow(id: EntityID<Int>) : BaseIntEntity(id, FavoritesTable) {
    companion object : BaseIntEntityClass<FavoriteRow>(FavoritesTable)

    var user by UserRow optionalReferencedOn FavoritesTable.userId
    var sessionId by FavoritesTable.sessionId
}

/* Votes */
internal object VotesTable : BaseIntIdTable("votes") {
    val userId = reference("user_id", UsersTable, ReferenceOption.SET_NULL).nullable()
    val sessionId = varchar("session_id", 50)
    val rating = integer("rating")
}

internal class VotesRow(id: EntityID<Int>) : BaseIntEntity(id, VotesTable) {
    companion object : BaseIntEntityClass<VotesRow>(VotesTable)

    var user by UserRow optionalReferencedOn VotesTable.userId
    var sessionId by VotesTable.sessionId
    var rating by VotesTable.rating
}

/* Chats */
internal object ChatRoomsTable : BaseIntIdTable("chat_rooms") {
    val name = varchar("name", 255)
}

internal class ChatRoomRow(id: EntityID<Int>) : BaseIntEntity(id, ChatRoomsTable) {
    companion object : BaseIntEntityClass<ChatRoomRow>(ChatRoomsTable)

    var name by ChatRoomsTable.name
}


internal object ChatRoomToUsersTable : BaseIntIdTable("chat_room_to_users") {
    val roomId = reference("room_id", ChatRoomsTable)
    val userId = reference("user_id", UsersTable)
}

internal class ChatRoomToUserRow(id: EntityID<Int>) : BaseIntEntity(id, ChatRoomToUsersTable) {
    companion object : BaseIntEntityClass<ChatRoomToUserRow>(ChatRoomToUsersTable)

    var room by ChatRoomRow referencedOn ChatRoomToUsersTable.roomId
    var user by UserRow referencedOn ChatRoomToUsersTable.userId
}

internal object ChatMessagesTable : BaseIntIdTable("chat_messages") {
    val roomId = reference("room_id", ChatRoomsTable)
    val userId = reference("user_id", UsersTable)
    val text = text("text")
}

internal class ChatMessageRow(id: EntityID<Int>) : BaseIntEntity(id, ChatMessagesTable) {
    companion object : BaseIntEntityClass<ChatMessageRow>(ChatMessagesTable)

    var room by ChatRoomRow referencedOn ChatMessagesTable.roomId
    var user by UserRow referencedOn ChatMessagesTable.userId
    var text by ChatMessagesTable.text
}