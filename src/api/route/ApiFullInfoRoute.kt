package ru.radiationx.api.route

import io.ktor.application.call
import io.ktor.routing.Routing
import io.ktor.routing.get
import ru.radiationx.findPrincipal
import ru.radiationx.domain.usecase.FullInfoUseCase

class ApiFullInfoRoute(
    private val fullInfoUseCase: FullInfoUseCase
) : BaseApiRoute() {

    override fun attachRoute(routing: Routing) = routing.apply {

        get("all") {
            val principal = call.findPrincipal()
            fullInfoUseCase.getFullInfo(principal, true)
        }

        get("all2019") {
            val principal = call.findPrincipal()
            fullInfoUseCase.getFullInfo(principal, false)
        }
    }
}