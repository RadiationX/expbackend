package ru.radiationx.app.api.controller

import io.ktor.application.ApplicationCall
import io.ktor.request.receive
import ru.radiationx.app.api.entity.TimeRequest
import ru.radiationx.app.api.entity.TimeResponse
import ru.radiationx.app.api.postHttpCode
import ru.radiationx.app.userPrincipal
import ru.radiationx.app.base.respondBase
import ru.radiationx.app.domain.usecase.TimeUseCase

class TimeController(
    private val timeUseCase: TimeUseCase
) {

    suspend fun getTime(call: ApplicationCall) {
        val data = TimeResponse(timeUseCase.getTime().timestamp)
        call.respondBase(data)
    }

    suspend fun setTime(call: ApplicationCall) {
        val principal = call.userPrincipal
        val request = call.receive<TimeRequest>()

        val result = timeUseCase.setTime(principal, request)
        call.respondBase(
            TimeResponse(result.data.timestamp),
            result.postHttpCode()
        )
    }
}