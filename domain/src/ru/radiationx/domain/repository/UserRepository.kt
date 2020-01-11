package ru.radiationx.domain.repository

import ru.radiationx.domain.entity.User

interface UserRepository {

    suspend fun getUser(login: String): User?

    suspend fun getUser(userId: Int): User?

    suspend fun getAllUsers(): List<User>

    suspend fun getAllUsersCount(): Int
}