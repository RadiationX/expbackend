package ru.radiationx.app.domain.repository

import io.ktor.auth.UserPasswordCredential
import ru.radiationx.app.domain.entity.User
import ru.radiationx.app.domain.entity.UserPrincipal

interface AuthRepository {

    suspend fun signUp(credentials: UserPasswordCredential): User

    suspend fun signIn(credentials: UserPasswordCredential, ip: String): String

    suspend fun signOut(userId: Int, token: String)

    suspend fun getPrincipal(userId: Int, token: String): UserPrincipal?
}