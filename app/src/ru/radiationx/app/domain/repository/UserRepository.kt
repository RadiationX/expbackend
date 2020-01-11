package ru.radiationx.app.domain.repository

import ru.radiationx.app.domain.entity.User

interface UserRepository {

    suspend fun getUser(userId: Int): User?

    suspend fun getAllUsers(): List<User>

    suspend fun getAllUsersCount(): Int
}