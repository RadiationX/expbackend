package ru.radiationx.app.api.controller

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import ru.radiationx.app.api.entity.ApiLiveVideoRequest
import ru.radiationx.app.api.toDomain
import ru.radiationx.app.userPrincipal
import ru.radiationx.app.api.base.respondBase
import ru.radiationx.domain.usecase.LiveVideoUseCase

class LiveVideoController(
    private val liveVideoUseCase: LiveVideoUseCase
) {

    suspend fun setVideo(call: ApplicationCall) {
        val principal = call.userPrincipal
        val request = call.receive<ApiLiveVideoRequest>()
        liveVideoUseCase.setVideo(principal?.user, request.toDomain())
        call.respondBase(statusCode = HttpStatusCode.OK)
    }
}