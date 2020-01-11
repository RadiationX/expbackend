package ru.radiationx.app.data.datasource

import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.mapLazy
import org.jetbrains.exposed.sql.transactions.transaction
import ru.radiationx.app.data.asUser
import ru.radiationx.app.data.entity.db.UserRow
import ru.radiationx.app.data.entity.db.UsersTable
import ru.radiationx.app.domain.entity.User
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
            val entityId = UsersTable.getIdColumn(userId)
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