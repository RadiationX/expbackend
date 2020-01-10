package ru.radiationx.api.controller

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveParameters
import ru.radiationx.base.respondBase
import ru.radiationx.domain.usecase.LiveVideoUseCase
import ru.radiationx.userPrincipal

class LiveVideoController(
    private val liveVideoUseCase: LiveVideoUseCase
) {

    suspend fun setVideo(call: ApplicationCall) {
        val principal = call.userPrincipal
        val form = call.receiveParameters()
        val room = form["roomId"]
        val video = form["video"]
        liveVideoUseCase.setVideo(principal, room, video)
        call.respondBase(HttpStatusCode.OK)
    }
}