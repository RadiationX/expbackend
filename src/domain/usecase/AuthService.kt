package ru.radiationx.domain.usecase

import io.ktor.auth.UserPasswordCredential
import ru.radiationx.domain.entity.User
import ru.radiationx.domain.entity.UserPrincipal
import ru.radiationx.domain.repository.AuthRepository

class AuthService(
    private val authRepository: AuthRepository
) {

    suspend fun signUp(credentials: UserPasswordCredential?): User {
        credentials ?: throw Exception("No credentials")
        if (!(2..50).contains(credentials.name.length)) {
            throw Exception("Login min=2 max=50")
        }
        if (!(6..64).contains(credentials.password.length)) {
            throw Exception("Password min=6 max=64")
        }
        return authRepository.signUp(credentials)
    }

    suspend fun signIn(credentials: UserPasswordCredential?, principal: UserPrincipal?, ip: String): String {
        credentials ?: throw Exception("No credentials")
        if (principal != null) throw Exception("You already logined, bruh")
        return authRepository.signIn(credentials, ip)
    }

    suspend fun signOut(principal: UserPrincipal?, token: String?) {
        token ?: throw Exception("No token")
        principal ?: throw Exception("No user info")
        return authRepository.signOut(principal.id, token)
    }
}