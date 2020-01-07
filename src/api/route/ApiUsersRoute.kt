package ru.radiationx.api.route

import io.ktor.application.call
import io.ktor.features.origin
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import ru.radiationx.base.respondBase
import ru.radiationx.domain.usecase.UserUseCase

class ApiUsersRoute(
    private val userUseCase: UserUseCase
) : BaseApiRoute() {

    override fun attachRoute(routing: Routing) = routing.apply {

        route("users") {

            post {
                val userUUID = call.receive<String>()
                val ip = call.request.origin.remoteHost
                val created = userUseCase.createUser(userUUID, ip)
                if (created)
                    call.respondBase(HttpStatusCode.Created)
                else
                    call.respondBase(HttpStatusCode.Conflict)
            }

            get("count") {
                call.respondBase(data = userUseCase.getAllUsers().size.toString())
            }
        }
    }
}