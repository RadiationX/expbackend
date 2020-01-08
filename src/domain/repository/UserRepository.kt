package ru.radiationx.domain.repository

import ru.radiationx.domain.entity.User
import java.time.LocalDateTime

interface UserRepository {

    suspend fun getUser(userId: Int): User?

    suspend fun getAllUsers(): List<User>

    suspend fun getAllUsersCount(): Int
}