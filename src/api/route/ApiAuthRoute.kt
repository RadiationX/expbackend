package ru.radiationx.api.route

import io.ktor.application.call
import io.ktor.auth.UserPasswordCredential
import io.ktor.auth.authenticate
import io.ktor.features.origin
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.Routing
import io.ktor.routing.post
import ru.radiationx.base.respondBase
import ru.radiationx.domain.usecase.AuthUseCase
import ru.radiationx.user
import ru.radiationx.userToken

class ApiAuthRoute(
    private val authUseCase: AuthUseCase
) : BaseApiRoute() {

    override fun attachRoute(routing: Routing): Route = routing.apply {

        post("signup") {
            val credentials = call.receive<UserPasswordCredential>()
            val createdUser = authUseCase.signUp(credentials)
            call.respondBase(data = createdUser)
        }

        authenticate(optional = true) {
            post("signin") {
                val principal = call.user
                val credentials = call.receive<UserPasswordCredential>()
                val ip = call.request.origin.remoteHost
                val token = authUseCase.signIn(credentials, principal, ip)
                call.respond(token)
            }
        }

        authenticate {
            post("signout") {
                val principal = call.user
                val token = call.userToken
                authUseCase.signOut(principal, token)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}