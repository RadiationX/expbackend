package ru.radiationx.api.route

import io.ktor.application.call
import io.ktor.routing.Routing
import io.ktor.routing.get
import ru.radiationx.base.respondBase
import ru.radiationx.domain.usecase.FullInfoUseCase
import ru.radiationx.findPrincipal

class ApiFullInfoRoute(
    private val fullInfoUseCase: FullInfoUseCase
) : BaseApiRoute() {

    override fun attachRoute(routing: Routing) = routing.apply {

        get("all") {
            val principal = call.findPrincipal()
            call.respondBase(data = fullInfoUseCase.getFullInfo(principal, true))
        }

        get("all2019") {
            val principal = call.findPrincipal()
            call.respondBase(data = fullInfoUseCase.getFullInfo(principal, false))
        }
    }
}