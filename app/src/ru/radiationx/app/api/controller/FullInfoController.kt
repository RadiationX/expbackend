package ru.radiationx.app.api.controller

import io.ktor.application.ApplicationCall
import ru.radiationx.app.userPrincipal
import ru.radiationx.app.api.base.respondBase
import ru.radiationx.domain.usecase.FullInfoUseCase

class FullInfoController(
    private val fullInfoUseCase: FullInfoUseCase
) {

    suspend fun getFullInfo(call: ApplicationCall) {
        val principal = call.userPrincipal
        call.respondBase(fullInfoUseCase.getFullInfo(principal?.user, true))
    }

    suspend fun getFullInfo2019(call: ApplicationCall) {
        val principal = call.userPrincipal
        call.respondBase(fullInfoUseCase.getFullInfo(principal?.user, false))
    }
}