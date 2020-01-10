package ru.radiationx.api.controller

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import ru.radiationx.base.respondBase
import ru.radiationx.domain.usecase.TimeUseCase
import ru.radiationx.user

class TimeController(
    private val timeUseCase: TimeUseCase
) {

    suspend fun getTime(call: ApplicationCall) {
        call.respondBase(data = timeUseCase.getTime().timestamp)
    }

    suspend fun setTime(call: ApplicationCall) {
        val principal = call.user
        val timestamp = call.parameters["timestamp"]
        timeUseCase.setTime(principal, timestamp)
        call.respondBase(HttpStatusCode.OK)
    }
}