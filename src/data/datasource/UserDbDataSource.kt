package ru.radiationx.data.datasource

import kotlinx.coroutines.withContext
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.mapLazy
import org.jetbrains.exposed.sql.transactions.transaction
import ru.radiationx.data.asUser
import ru.radiationx.data.entity.db.UserRow
import ru.radiationx.data.entity.db.UsersTable
import ru.radiationx.domain.entity.User
import kotlin.coroutines.CoroutineContext

class UserDbDataSource(
    private val dispatcher: CoroutineContext,
    private val database: Database
) {

    suspend fun getUser(login: String): User? = withContext(dispatcher) {
        transaction(database) {
            UserRow
                .find { UsersTable.login eq login }
                .limit(1)
                .mapLazy { it.asUser() }
                .firstOrNull()
        }
    }

    suspend fun getUser(userId: Int): User? = withContext(dispatcher) {
        transaction(database) {
            val entityId = EntityID(userId, UsersTable)
            UserRow
                .find { UsersTable.id eq entityId }
                .limit(1)
                .mapLazy { it.asUser() }
                .firstOrNull()
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