package ru.radiationx.api.route

import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.routing.Routing
import io.ktor.routing.get
import ru.radiationx.base.respondBase
import ru.radiationx.domain.usecase.FullInfoUseCase
import ru.radiationx.findPrincipal
import ru.radiationx.user

class ApiFullInfoRoute(
    private val fullInfoUseCase: FullInfoUseCase
) : BaseApiRoute() {

    override fun attachRoute(routing: Routing) = routing.apply {
        authenticate {
            get("all") {
                val principal = call.user
                call.respondBase(data = fullInfoUseCase.getFullInfo(principal, true))
            }

            get("all2019") {
                val principal = call.user
                call.respondBase(data = fullInfoUseCase.getFullInfo(principal, false))
            }
        }
    }
}