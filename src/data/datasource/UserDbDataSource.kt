package ru.radiationx.data.datasource

import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
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
            UsersTable
                .select { UsersTable.uuid eq uuid }
                .limit(1)
                .map { it.asUser() }
                .firstOrNull()
        }
    }

    suspend fun createUser(uuid: String, remote: String, timestamp: LocalDateTime): Boolean = withContext(dispatcher) {
        transaction(database) {
            val count = UsersTable
                .slice(UsersTable.uuid)
                .select { UsersTable.uuid eq uuid }
                .count()

            if (count == 0) {
                UsersTable.insert {
                    it[UsersTable.uuid] = uuid
                    it[UsersTable.timestamp] = timestamp.toString()
                    it[UsersTable.remote] = remote
                }
            }
            count == 0
        }
    }

    suspend fun getAllUsers(): List<User> = withContext(dispatcher) {
        transaction(database) {
            UsersTable.selectAll().toList().map { it.asUser() }
        }
    }

    suspend fun getAllUsersCount(): Int = withContext(dispatcher) {
        transaction(database) {
            UsersTable.selectAll().count()
        }
    }

    private fun ResultRow.asUser(): User = User(
        get(UsersTable.id),
        get(UsersTable.uuid),
        get(UsersTable.remote),
        LocalDateTime.parse(get(UsersTable.timestamp))
    )
}