package ru.radiationx.data.datasource

import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.radiationx.data.asUser
import ru.radiationx.data.entity.db.UserRow
import ru.radiationx.data.entity.db.UsersTable
import ru.radiationx.domain.entity.User
import java.time.LocalDateTime
import kotlin.coroutines.CoroutineContext

class UserDbDataSource(
    private val dispatcher: CoroutineContext,
    private val database: Database
) {

    suspend fun getUser(uuid: String): User? = withContext(dispatcher) {
        transaction(database) {
            UserRow
                .find { UsersTable.uuid eq uuid }
                .limit(1)
                .mapLazy { it.asUser() }
                .firstOrNull()
        }
    }

    suspend fun createUser(uuid: String, remote: String, timestamp: LocalDateTime): Boolean = withContext(dispatcher) {
        transaction(database) {
            val count = UserRow
                .find { UsersTable.uuid eq uuid }
                .count()

            if (count == 0) {
                UserRow.new {
                    this.uuid = uuid
                    this.remote = remote
                    this.timestamp = timestamp.toString()
                }
            }
            count == 0
        }
    }

    suspend fun getAllUsers(): List<User> = withContext(dispatcher) {
        transaction(database) {
            UserRow.all().map { it.asUser() }
        }
    }

    suspend fun getAllUsersCount(): Int = withContext(dispatcher) {
        transaction(database) {
            UserRow.count()
        }
    }
}