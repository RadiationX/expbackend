package ru.radiationx.api.route

import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.routing.*
import ru.radiationx.base.respondBase
import ru.radiationx.domain.usecase.FavoriteUseCase
import ru.radiationx.user

class ApiFavoriteRoute(
    private val favoriteUseCase: FavoriteUseCase
) : BaseApiRoute() {

    override fun attachRoute(routing: Routing) = routing.apply {

        authenticate {
            route("favorites") {

                get {
                    val principal = call.user
                    val favorites = favoriteUseCase.getFavorites(principal)
                    call.respondBase(data = favorites)
                }

                post {
                    val principal = call.user
                    val sessionId = call.receive<String>()
                    favoriteUseCase.createFavorite(principal, sessionId)
                    call.respondBase(HttpStatusCode.Created)
                }

                delete {
                    val principal = call.user
                    val sessionId = call.receive<String>()
                    favoriteUseCase.deleteFavorite(principal, sessionId)
                    call.respondBase(HttpStatusCode.OK)
                }
            }
        }
    }
}