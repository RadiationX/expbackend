package ru.radiationx.api.controller

import io.ktor.application.ApplicationCall
import io.ktor.request.receive
import ru.radiationx.api.entity.TimeRequest
import ru.radiationx.api.entity.TimeResponse
import ru.radiationx.api.postHttpCode
import ru.radiationx.base.respondBase
import ru.radiationx.domain.usecase.TimeUseCase
import ru.radiationx.userPrincipal

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