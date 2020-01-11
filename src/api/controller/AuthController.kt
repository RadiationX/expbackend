package ru.radiationx.api.controller

import io.ktor.application.ApplicationCall
import io.ktor.auth.UserPasswordCredential
import io.ktor.features.origin
import io.ktor.request.receive
import ru.radiationx.api.entity.AuthTokenResponse
import ru.radiationx.api.toResponse
import ru.radiationx.base.respondBase
import ru.radiationx.domain.usecase.AuthService
import ru.radiationx.userPrincipal
import ru.radiationx.userToken

class AuthController(
    private val authService: AuthService
) {

    suspend fun signUp(call: ApplicationCall) {
        val credentials = call.receive<UserPasswordCredential>()
        val createdUser = authService.signUp(credentials)
        call.respondBase(createdUser.toResponse())
    }

    suspend fun signIn(call: ApplicationCall) {
        val principal = call.userPrincipal
        val credentials = call.receive<UserPasswordCredential>()
        val ip = call.request.origin.remoteHost
        val token = authService.signIn(credentials, principal, ip)
        call.respondBase(AuthTokenResponse(token))
    }

    suspend fun signOut(call: ApplicationCall) {
        val principal = call.userPrincipal
        val token = call.userToken
        authService.signOut(principal, token)
        call.respondBase()
    }

}