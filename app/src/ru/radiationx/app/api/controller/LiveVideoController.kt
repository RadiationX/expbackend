package ru.radiationx.app.api.controller

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import ru.radiationx.app.api.entity.LiveVideoRequest
import ru.radiationx.app.userPrincipal
import ru.radiationx.app.base.respondBase
import ru.radiationx.app.domain.usecase.LiveVideoUseCase

class LiveVideoController(
    private val liveVideoUseCase: LiveVideoUseCase
) {

    suspend fun setVideo(call: ApplicationCall) {
        val principal = call.userPrincipal
        val request = call.receive<LiveVideoRequest>()
        liveVideoUseCase.setVideo(principal, request)
        call.respondBase(statusCode = HttpStatusCode.OK)
    }
}