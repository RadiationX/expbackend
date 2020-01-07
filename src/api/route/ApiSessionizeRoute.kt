package ru.radiationx.api.route

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.routing.Routing
import io.ktor.routing.post
import ru.radiationx.findPrincipal
import ru.radiationx.base.respondBase
import ru.radiationx.domain.usecase.SessionizeUseCase

class ApiSessionizeRoute(
    private val sessionizeUseCase: SessionizeUseCase
) : BaseApiRoute() {

    override fun attachRoute(routing: Routing) = routing.apply {

        post("sessionizeSync") {
            val principal = call.findPrincipal()
            sessionizeUseCase.update(principal)
            call.respondBase(HttpStatusCode.OK)
        }
    }
}