package ru.radiationx.app.api.controller

import io.ktor.application.ApplicationCall
import io.ktor.auth.UserPasswordCredential
import io.ktor.features.origin
import io.ktor.request.receive
import ru.radiationx.app.api.entity.ApiAuthCredentialsRequest
import ru.radiationx.app.api.entity.ApiAuthTokenResponse
import ru.radiationx.app.api.toDomain
import ru.radiationx.app.api.toResponse
import ru.radiationx.app.userPrincipal
import ru.radiationx.app.userToken
import ru.radiationx.app.base.respondBase
import ru.radiationx.domain.usecase.AuthService

class AuthController(
    private val authService: AuthService
) {

    suspend fun signUp(call: ApplicationCall) {
        val credentials = call.receive<ApiAuthCredentialsRequest>()
        val createdUser = authService.signUp(credentials.toDomain())
        call.respondBase(createdUser.toResponse())
    }

    suspend fun signIn(call: ApplicationCall) {
        val principal = call.userPrincipal
        val credentials = call.receive<ApiAuthCredentialsRequest>()
        val ip = call.request.origin.remoteHost
        val token = authService.signIn(credentials.toDomain(), principal?.user, ip)
        call.respondBase(ApiAuthTokenResponse(token))
    }

    suspend fun signOut(call: ApplicationCall) {
        val principal = call.userPrincipal
        val token = call.userToken
        authService.signOut(principal?.user, token)
        call.respondBase()
    }

}