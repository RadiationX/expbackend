package ru.radiationx.domain.repository

import io.ktor.auth.UserPasswordCredential
import ru.radiationx.domain.entity.UserPrincipal
import ru.radiationx.domain.entity.User

interface AuthRepository {

    suspend fun signUp(credentials: UserPasswordCredential): User

    suspend fun signIn(credentials: UserPasswordCredential, ip: String): String

    suspend fun signOut(userId: Int, token: String)

    suspend fun getPrincipal(userId: Int, token: String): UserPrincipal?
}