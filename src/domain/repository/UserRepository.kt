package ru.radiationx.domain.repository

import ru.radiationx.domain.entity.User
import java.time.LocalDateTime

interface UserRepository {

    suspend fun getUser(uuid: String): User?

    suspend fun createUser(uuid: String, remote: String, timestamp: LocalDateTime): Boolean

    suspend fun getAllUsers(): List<User>

    suspend fun getAllUsersCount(): Int
}