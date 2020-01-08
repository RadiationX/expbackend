package ru.radiationx.api

import io.ktor.auth.authenticate
import io.ktor.routing.Routing
import ru.radiationx.api.route.*

class BatchApiRouting(
    private val authRoute: ApiAuthRoute,
    private val favoriteRoute: ApiFavoriteRoute,
    private val fullInfoRoute: ApiFullInfoRoute,
    private val liveVideoRoute: ApiLiveVideoRoute,
    private val sessionizeRoute: ApiSessionizeRoute,
    private val timeRoute: ApiTimeRoute,
    private val usersRoute: ApiUsersRoute,
    private val voteRoute: ApiVoteRoute
) {

    fun attachRoute(routing: Routing) {
        authRoute.attachRoute(routing)
        favoriteRoute.attachRoute(routing)
        fullInfoRoute.attachRoute(routing)
        liveVideoRoute.attachRoute(routing)
        sessionizeRoute.attachRoute(routing)
        timeRoute.attachRoute(routing)
        usersRoute.attachRoute(routing)
        voteRoute.attachRoute(routing)
    }
}