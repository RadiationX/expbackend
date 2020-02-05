package ru.radiationx.app.data.datasource

import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import ru.radiationx.app.data.asMessage
import ru.radiationx.app.data.asRoom
import ru.radiationx.app.data.asUser
import ru.radiationx.app.data.entity.db.*
import ru.radiationx.app.data.entity.db.ChatRoomToUserRow
import ru.radiationx.app.data.entity.db.ChatRoomToUsersTable
import ru.radiationx.app.data.entity.db.ChatRoomsTable
import ru.radiationx.app.data.entity.db.UsersTable
import ru.radiationx.domain.entity.ChatMessage
import ru.radiationx.domain.entity.ChatRoom
import ru.radiationx.domain.entity.User
import kotlin.coroutines.CoroutineContext

class ChatDbDataSource(
    private val dispatcher: CoroutineContext,
    private val database: Database
) {

    suspend fun createRoom(name: String) = withContext(dispatcher) {
        transaction(database) {
            ChatRoomsTable.insert {
                it[this.name] = name
            }
        }
    }

    suspend fun addUser(roomId: Int, userId: Int) = withContext(dispatcher) {
        transaction(database) {
            val entityRoomId = ChatRoomsTable.getIdColumn(roomId)
            val entityUserId = UsersTable.getIdColumn(userId)
            ChatRoomToUsersTable.insert {
                it[this.roomId] = entityRoomId
                it[this.userId] = entityUserId
            }
        }
    }

    suspend fun getUsersInRoom(roomId: Int): List<User> = withContext(dispatcher) {
        transaction(database) {
            UsersTable.innerJoin(ChatRoomToUsersTable)
                .slice(UsersTable.columns)
                .select { (ChatRoomToUsersTable.roomId eq roomId) and (ChatRoomToUsersTable.userId eq UsersTable.id) }
                .groupBy(UsersTable.id)
                .map { UserRow.wrapRow(it).asUser() }
                .toList()
        }
    }

    suspend fun getUserInRoom(roomId: Int, userId: Int): User? = withContext(dispatcher) {
        transaction(database) {
            UsersTable.innerJoin(ChatRoomToUsersTable)
                .slice(UsersTable.columns)
                .select { (ChatRoomToUsersTable.roomId eq roomId) and (ChatRoomToUsersTable.userId eq userId) }
                .groupBy(UsersTable.id)
                .firstOrNull()
                ?.let { UserRow.wrapRow(it).asUser() }
        }
    }

    suspend fun getRoom(roomId: Int): ChatRoom = withContext(dispatcher) {
        transaction(database) {
            ChatRoomRow[roomId].asRoom()
        }
    }

    suspend fun addMessage(roomId: Int, userId: Int, text: String): ChatMessage = withContext(dispatcher) {
        transaction(database) {
            val entityRoomId = ChatRoomsTable.getIdColumn(roomId)
            val entityUserId = UsersTable.getIdColumn(userId)
            val messageId = ChatMessagesTable.insertAndGetId {
                it[this.roomId] = entityRoomId
                it[this.userId] = entityUserId
                it[this.text] = text
            }
            ChatMessageRow[messageId].asMessage()
        }
    }


}