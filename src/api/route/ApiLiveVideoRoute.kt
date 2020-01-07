package ru.radiationx.api.route

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveParameters
import io.ktor.routing.Routing
import io.ktor.routing.post
import ru.radiationx.findPrincipal
import ru.radiationx.base.respondBase
import ru.radiationx.domain.usecase.LiveVideoUseCase

class ApiLiveVideoRoute(
    private val liveVideoUseCase: LiveVideoUseCase
) : BaseApiRoute() {

    override fun attachRoute(routing: Routing) = routing.apply {

        post("live") {
            val principal = call.findPrincipal()
            val form = call.receiveParameters()
            val room = form["roomId"]
            val video = form["video"]
            liveVideoUseCase.setVideo(principal, room, video)
            call.respondBase(HttpStatusCode.OK)
        }
    }
}