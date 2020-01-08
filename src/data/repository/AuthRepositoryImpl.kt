package ru.radiationx.data.repository

import io.ktor.auth.UserPasswordCredential
import ru.radiationx.UserPrincipal
import ru.radiationx.data.datasource.AuthDbDataSource
import ru.radiationx.data.datasource.UserDbDataSource
import ru.radiationx.domain.entity.User
import ru.radiationx.domain.helper.HashHelper
import ru.radiationx.domain.helper.TokenMaker
import ru.radiationx.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authDbDataSource: AuthDbDataSource,
    private val userDbDataSource: UserDbDataSource,
    private val tokenMaker: TokenMaker,
    private val hashHelper: HashHelper
) : AuthRepository {

    override suspend fun signUp(credentials: UserPasswordCredential): User {
        userDbDataSource.getUser(credentials.name)?.also { throw Exception("User already created, bro") }
        val hashedPassword = hashHelper.hash(credentials.password)
        val hashedCredential = credentials.copy(password = hashedPassword)
        return authDbDataSource.signUp(hashedCredential)
    }

    override suspend fun signIn(credentials: UserPasswordCredential, ip: String): String {
        val user = userDbDataSource.getUser(credentials.name) ?: throw Exception("User not found, bruh")

        if (!hashHelper.check(credentials.password, user.password)) {
            throw Exception("Wrong password, bruh")
        }

        val principal = UserPrincipal(user.id)
        val token = tokenMaker.makeToken(principal)
        return authDbDataSource.signIn(user.id, token, ip)
    }

    override suspend fun signOut(userId: Int, token: String) =
        authDbDataSource.signOut(userId, token)

    override suspend fun getPrincipal(userId: Int, token: String): UserPrincipal? {
        val user = authDbDataSource.getToken(userId, token)?.user

        if (user == null || user.id != userId) {
            return null
        }
        return UserPrincipal(user.id)
    }
}