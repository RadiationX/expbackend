package ru.radiationx.api

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.principal
import io.ktor.features.origin
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.request.receiveParameters
import io.ktor.routing.*
import ru.radiationx.api.entity.VoteData
import ru.radiationx.base.respondBase
import ru.radiationx.domain.entity.KotlinConfPrincipal
import ru.radiationx.domain.entity.Rating
import ru.radiationx.domain.usecase.*

internal fun Routing.attachApiRouting(
    favoriteUseCase: FavoriteUseCase,
    fullInfoUseCase: FullInfoUseCase,
    liveVideoUseCase: LiveVideoUseCase,
    sessionizeUseCase: SessionizeUseCase,
    timeUseCase: TimeUseCase,
    userUseCase: UserUseCase,
    voteUseCase: VoteUseCase
) {
    apiUsers(userUseCase)
    apiAll(fullInfoUseCase)
    apiVote(voteUseCase)
    apiFavorite(favoriteUseCase)
    apiSynchronize(sessionizeUseCase)
    apiTime(timeUseCase)
    apiLive(liveVideoUseCase)
}

/*
POST http://localhost:8080/user
1238476512873162837
 */
private fun Routing.apiUsers(userUseCase: UserUseCase) {
    route("users") {

        post {
            val userUUID = call.receive<String>()
            val ip = call.request.origin.remoteHost
            val created = userUseCase.createUser(userUUID, ip)
            if (created)
                call.respondBase(HttpStatusCode.Created)
            else
                call.respondBase(HttpStatusCode.Conflict)
        }

        get("count") {
            call.respondBase(data = userUseCase.getAllUsers().size.toString())
        }
    }
}

/*
GET http://localhost:8080/favorites
Accept: application/json
Authorization: Bearer 1238476512873162837
*/
private fun Routing.apiFavorite(favoriteUseCase: FavoriteUseCase) {
    route("favorites") {

        get {
            val principal = call.findPrincipal()
            val favorites = favoriteUseCase.getFavorites(principal)
            call.respondBase(data = favorites)
        }

        post {
            val principal = call.findPrincipal()
            val sessionId = call.receive<String>()
            favoriteUseCase.createFavorite(principal, sessionId)
            call.respondBase(HttpStatusCode.Created)
        }

        delete {
            val principal = call.findPrincipal()
            val sessionId = call.receive<String>()
            favoriteUseCase.deleteFavorite(principal, sessionId)
            call.respondBase(HttpStatusCode.OK)
        }
    }
}

/*
GET http://localhost:8080/votes
Accept: application/json
Authorization: Bearer 1238476512873162837
*/
private fun Routing.apiVote(voteUseCase: VoteUseCase) {
    route("votes") {

        get {
            val principal = call.findPrincipal()
            val votes = voteUseCase.getVotes(principal)
            call.respondBase(data = votes)
        }

        get("all") {
            val principal = call.findPrincipal()
            val votes = voteUseCase.getAllVotes(principal)
            call.respondBase(data = votes)
        }

        get("summary/{sessionId}") {
            val principal = call.findPrincipal()
            val sessionId = call.parameters["sessionId"]
            val votesSummary = voteUseCase
                .getVotesSummary(principal, sessionId)
                .mapKeys {
                    return@mapKeys when (it.key) {
                        Rating.OK -> "soso"
                        Rating.GOOD -> "good"
                        Rating.BAD -> "bad"
                        else -> "unknown"
                    }
                }
                .toMutableMap()

            if ("bad" !in votesSummary) votesSummary["bad"] = 0
            if ("good" !in votesSummary) votesSummary["good"] = 0
            if ("soso" !in votesSummary) votesSummary["soso"] = 0

            call.respondBase(data = votesSummary)
        }

        post {
            val principal = call.findPrincipal()
            val vote = call.receive<VoteData>()
            val sessionId = vote.sessionId
            val rating = vote.rating

            val status = if (voteUseCase.changeVote(principal, sessionId, rating)) {
                HttpStatusCode.Created
            } else {
                HttpStatusCode.OK
            }
            call.respondBase(status)
        }

        post("required/{count}") {
            val principal = call.findPrincipal()
            val count = call.parameters["count"]
            voteUseCase.setRequired(principal, count)
            call.respondBase(HttpStatusCode.OK)
        }

        delete {
            val principal = call.findPrincipal()
            val vote = call.receive<VoteData>()
            val sessionId = vote.sessionId
            voteUseCase.deleteVote(principal, sessionId)
            call.respondBase(HttpStatusCode.OK)
        }
    }
}

/*
GET http://localhost:8080/all
GET http://localhost:8080/all2019
Accept: application/json
Authorization: Bearer 1238476512873162837
*/
private fun Routing.apiAll(fullInfoUseCase: FullInfoUseCase) {

    get("all") {
        val principal = call.findPrincipal()
        fullInfoUseCase.getFullInfo(principal, true)
    }

    get("all2019") {
        val principal = call.findPrincipal()
        fullInfoUseCase.getFullInfo(principal, false)
    }
}

private fun Routing.apiTime(timeUseCase: TimeUseCase) {

    get("time") {
        call.respondBase(data = timeUseCase.getTime().timestamp)
    }

    post("time/{timestamp}") {
        val principal = call.findPrincipal()
        val timestamp = call.parameters["timestamp"]
        timeUseCase.setTime(principal, timestamp)
        call.respondBase(HttpStatusCode.OK)
    }
}

private fun Routing.apiLive(liveVideoUseCase: LiveVideoUseCase) {

    post("live") {
        val principal = call.findPrincipal()
        val form = call.receiveParameters()
        val room = form["roomId"]
        val video = form["video"]
        liveVideoUseCase.setVideo(principal, room, video)
        call.respondBase(HttpStatusCode.OK)
    }
}

/*
GET http://localhost:8080/sessionizeSync
*/
private fun Routing.apiSynchronize(sessionizeUseCase: SessionizeUseCase) {

    post("sessionizeSync") {
        val principal = call.findPrincipal()
        sessionizeUseCase.update(principal)
        call.respondBase(HttpStatusCode.OK)
    }
}

private fun ApplicationCall.findPrincipal(): KotlinConfPrincipal? = principal()


