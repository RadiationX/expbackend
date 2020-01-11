package ru.radiationx.domain.repository

import ru.radiationx.domain.entity.AuthCredentials
import ru.radiationx.domain.entity.User

interface AuthRepository {

    suspend fun signUp(login: String, password: String): User

    suspend fun signIn(userId: Int, token: String, ip: String): String

    suspend fun signOut(userId: Int, token: String)

    suspend fun getUser(userId: Int, token: String): User?
}