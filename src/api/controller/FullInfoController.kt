package ru.radiationx.api.controller

import io.ktor.application.ApplicationCall
import ru.radiationx.base.respondBase
import ru.radiationx.domain.usecase.FullInfoUseCase
import ru.radiationx.user

class FullInfoController(
    private val fullInfoUseCase: FullInfoUseCase
) {

    suspend fun getFullInfo(call: ApplicationCall) {
        val principal = call.user
        call.respondBase(data = fullInfoUseCase.getFullInfo(principal, true))
    }

    suspend fun getFullInfo2019(call: ApplicationCall) {
        val principal = call.user
        call.respondBase(data = fullInfoUseCase.getFullInfo(principal, false))
    }
}