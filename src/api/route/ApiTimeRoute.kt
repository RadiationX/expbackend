package ru.radiationx.api.route

import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import ru.radiationx.findPrincipal
import ru.radiationx.base.respondBase
import ru.radiationx.domain.usecase.TimeUseCase
import ru.radiationx.user

class ApiTimeRoute(
    private val timeUseCase: TimeUseCase
) : BaseApiRoute() {

    override fun attachRoute(routing: Routing) = routing.apply {

        get("time") {
            call.respondBase(data = timeUseCase.getTime().timestamp)
        }

        authenticate {
            post("time/{timestamp}") {
                val principal = call.user
                val timestamp = call.parameters["timestamp"]
                timeUseCase.setTime(principal, timestamp)
                call.respondBase(HttpStatusCode.OK)
            }
        }
    }
}