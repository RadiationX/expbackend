package ru.radiationx.domain.usecase

import ru.radiationx.domain.entity.AuthCredentials
import ru.radiationx.domain.entity.User
import ru.radiationx.domain.helper.HashHelper
import ru.radiationx.domain.helper.TokenMaker
import ru.radiationx.domain.repository.AuthRepository
import ru.radiationx.domain.repository.UserRepository

class AuthService(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val hashHelper: HashHelper,
    private val tokenMaker: TokenMaker
) {

    suspend fun signUp(credentials: AuthCredentials?): User {
        val authCredentials = validateCredentials(credentials)
        val login = authCredentials.login!!
        val password = authCredentials.password!!

        userRepository.getUser(login)?.also { throw Exception("User already created, bro") }

        val hashedPassword = hashHelper.hash(password)

        return authRepository.signUp(login, hashedPassword)
    }

    suspend fun signIn(credentials: AuthCredentials?, principal: User?, ip: String): String {
        if (principal != null) throw Exception("You already logined, bruh")
        val authCredentials = validateCredentials(credentials)
        val login = authCredentials.login!!
        val password = authCredentials.password!!

        val user = userRepository.getUser(login) ?: throw Exception("User not found, bruh")
        if (!hashHelper.check(password, user.password)) throw Exception("Wrong password, bruh")

        val token = tokenMaker.makeToken(user)

        return authRepository.signIn(user.id, token, ip)
    }

    suspend fun signOut(principal: User?, token: String?) {
        token ?: throw Exception("No token")
        principal ?: throw Exception("No user info")
        return authRepository.signOut(principal.id, token)
    }

    suspend fun getUser(userId: Int, token: String): User? {
        val user = authRepository.getUser(userId, token)
        if (user == null || user.id != userId) {
            return null
        }
        return user
    }

    private fun validateCredentials(credentials: AuthCredentials?): AuthCredentials {
        credentials ?: throw Exception("No credentials")
        credentials.login ?: throw Exception("No login")
        credentials.password ?: throw Exception("No password")

        if (!(2..50).contains(credentials.login.length)) {
            throw Exception("Login min=2 max=50")
        }
        if (!(6..64).contains(credentials.password.length)) {
            throw Exception("Password min=6 max=64")
        }
        return credentials
    }
}