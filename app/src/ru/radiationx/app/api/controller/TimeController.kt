package ru.radiationx.app.api.controller

import io.ktor.application.ApplicationCall
import io.ktor.request.receive
import ru.radiationx.app.api.entity.ApiTimeRequest
import ru.radiationx.app.api.entity.ApiTimeResponse
import ru.radiationx.app.api.postHttpCode
import ru.radiationx.app.api.toDomain
import ru.radiationx.app.userPrincipal
import ru.radiationx.app.api.base.respondBase
import ru.radiationx.domain.helper.asDate
import ru.radiationx.domain.usecase.TimeUseCase

class TimeController(
    private val timeUseCase: TimeUseCase
) {

    suspend fun getTime(call: ApplicationCall) {
        val data = ApiTimeResponse(timeUseCase.getTime().asDate().time)
        call.respondBase(data)
    }

    suspend fun setTime(call: ApplicationCall) {
        val principal = call.userPrincipal
        val request = call.receive<ApiTimeRequest>()

        val result = timeUseCase.setTime(principal?.user, request.toDomain())
        call.respondBase(
            ApiTimeResponse(result.data.asDate().time),
            result.postHttpCode()
        )
    }
}