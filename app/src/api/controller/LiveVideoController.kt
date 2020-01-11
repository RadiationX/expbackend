package ru.radiationx.api.controller

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import ru.radiationx.api.entity.LiveVideoRequest
import ru.radiationx.base.respondBase
import ru.radiationx.domain.usecase.LiveVideoUseCase
import ru.radiationx.userPrincipal

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