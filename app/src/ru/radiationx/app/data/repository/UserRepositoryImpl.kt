package ru.radiationx.app.data.repository

import ru.radiationx.app.data.datasource.UserDbDataSource
import ru.radiationx.app.domain.entity.User
import ru.radiationx.app.domain.repository.UserRepository

class UserRepositoryImpl(
    private val userDbDataSource: UserDbDataSource
) : UserRepository {

    override suspend fun getUser(userId: Int): User? = userDbDataSource.getUser(userId)

    override suspend fun getAllUsers(): List<User> = userDbDataSource.getAllUsers()

    override suspend fun getAllUsersCount(): Int = userDbDataSource.getAllUsersCount()
}