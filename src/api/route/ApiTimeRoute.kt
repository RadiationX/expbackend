package ru.radiationx.api.route

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import ru.radiationx.findPrincipal
import ru.radiationx.base.respondBase
import ru.radiationx.domain.usecase.TimeUseCase

class ApiTimeRoute(
    private val timeUseCase: TimeUseCase
) : BaseApiRoute() {

    override fun attachRoute(routing: Routing) = routing.apply {

        get("time") {
            call.respondBase(data = timeUseCase.getTime().timestamp)
        }

        post("time/{timestamp}") {
            val principal = call.findPrincipal()
            val timestamp = call.parameters["timestamp"]
            timeUseCase.setTime(principal, timestamp)
            call.respondBase(HttpStatusCode.OK)
        }
    }
}