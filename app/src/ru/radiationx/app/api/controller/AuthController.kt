package ru.radiationx.app.api.controller

import io.ktor.application.ApplicationCall
import io.ktor.auth.UserPasswordCredential
import io.ktor.features.origin
import io.ktor.request.receive
import ru.radiationx.app.api.entity.AuthTokenResponse
import ru.radiationx.app.api.toResponse
import ru.radiationx.app.userPrincipal
import ru.radiationx.app.userToken
import ru.radiationx.app.base.respondBase
import ru.radiationx.app.domain.usecase.AuthService

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