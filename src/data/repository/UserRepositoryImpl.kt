package ru.radiationx.data.repository

import ru.radiationx.data.datasource.UserDbDataSource
import ru.radiationx.domain.entity.User
import ru.radiationx.domain.repository.UserRepository
import java.time.LocalDateTime

class UserRepositoryImpl(
    private val userDbDataSource: UserDbDataSource
) : UserRepository {

    override suspend fun getUser(userId: Int): User? = userDbDataSource.getUser(userId)

    override suspend fun getAllUsers(): List<User> = userDbDataSource.getAllUsers()

    override suspend fun getAllUsersCount(): Int = userDbDataSource.getAllUsersCount()
}