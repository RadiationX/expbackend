package ru.radiationx.api.controller

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import ru.radiationx.base.respondBase
import ru.radiationx.domain.usecase.SessionizeUseCase
import ru.radiationx.userPrincipal

class SessionizeController(
    private val sessionizeUseCase: SessionizeUseCase
) {

    suspend fun update(call: ApplicationCall) {
        val principal = call.userPrincipal
        sessionizeUseCase.update(principal)
        call.respondBase(statusCode = HttpStatusCode.OK)
    }
}