package ru.radiationx.app.data.repository

import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.mapLazy
import org.jetbrains.exposed.sql.transactions.transaction
import ru.radiationx.app.data.asUser
import ru.radiationx.app.data.datasource.UserDbDataSource
import ru.radiationx.app.data.entity.db.UserRow
import ru.radiationx.app.data.entity.db.UsersTable
import ru.radiationx.domain.entity.User
import ru.radiationx.domain.repository.UserRepository

class UserRepositoryImpl(
    private val userDbDataSource: UserDbDataSource
) : UserRepository {

    override suspend fun getUser(login: String): User? = userDbDataSource.getUser(login)

    override suspend fun getUser(userId: Int): User? = userDbDataSource.getUser(userId)

    override suspend fun getAllUsers(): List<User> = userDbDataSource.getAllUsers()

    override suspend fun getAllUsersCount(): Int = userDbDataSource.getAllUsersCount()
}