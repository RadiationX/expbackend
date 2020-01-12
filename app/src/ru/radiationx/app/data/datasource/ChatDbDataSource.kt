package ru.radiationx.app.data.datasource

import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import ru.radiationx.app.data.asUser
import ru.radiationx.app.data.entity.db.*
import ru.radiationx.app.data.entity.db.ChatRoomToUserRow
import ru.radiationx.app.data.entity.db.ChatRoomToUsersTable
import ru.radiationx.app.data.entity.db.ChatRoomsTable
import ru.radiationx.app.data.entity.db.UsersTable
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

    suspend fun getUsersInRoom(roomId: Int): Any = withContext(dispatcher) {
        transaction(database) {
            UsersTable.innerJoin(ChatRoomToUsersTable)
                .slice(UsersTable.columns)
                .select { (ChatRoomToUsersTable.roomId eq roomId) and (ChatRoomToUsersTable.userId eq UsersTable.id) }
                .groupBy(UsersTable.id)
                .toList()
        }
    }

    suspend fun getRoom(roomId: Int): Any = withContext(dispatcher) {
        transaction(database) {
            val users = UsersTable.innerJoin(ChatRoomToUsersTable)
                .slice(UsersTable.columns)
                .select { (ChatRoomToUsersTable.roomId eq roomId) and (ChatRoomToUsersTable.userId eq UsersTable.id) }
                .groupBy(UsersTable.id)
            val userRows = UserRow.wrapRows(users).map { it.asUser() }
            val room = ChatRoomRow[roomId]
            room.asRoom(userRows)
        }
    }

    internal fun ChatRoomRow.asRoom(users: List<User>? = null): Map<String, Any?> = mapOf(
        "id" to id.toString(),
        "name" to id.toString(),
        "users" to users
    )


}