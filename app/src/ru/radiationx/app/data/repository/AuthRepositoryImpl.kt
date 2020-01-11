package ru.radiationx.app.data.repository

import ru.radiationx.app.data.datasource.AuthDbDataSource
import ru.radiationx.domain.entity.User
import ru.radiationx.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authDbDataSource: AuthDbDataSource
) : AuthRepository {

    override suspend fun signUp(login: String, password: String): User =
        authDbDataSource.signUp(login, password)

    override suspend fun signIn(userId: Int, token: String, ip: String): String =
        authDbDataSource.signIn(userId, token, ip)

    override suspend fun signOut(userId: Int, token: String) =
        authDbDataSource.signOut(userId, token)

    override suspend fun getUser(userId: Int, token: String): User? =
        authDbDataSource.getToken(userId, token)?.user
}