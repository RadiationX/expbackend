package ru.radiationx.app.api.controller

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import ru.radiationx.app.userPrincipal
import ru.radiationx.app.api.base.respondBase
import ru.radiationx.domain.usecase.SessionizeUseCase

class SessionizeController(
    private val sessionizeUseCase: SessionizeUseCase
) {

    suspend fun update(call: ApplicationCall) {
        val principal = call.userPrincipal
        sessionizeUseCase.update(principal?.user)
        call.respondBase(statusCode = HttpStatusCode.OK)
    }
}