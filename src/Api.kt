package ru.radiationx

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.util.date.*
import ru.radiationx.base.respondBase
import ru.radiationx.common.ConferenceData
import ru.radiationx.common.VoteData
import java.time.*

internal fun Routing.api(
    databaseModule: DatabaseModule,
    sessionizeUrl: String,
    oldSessionizeUrl: String,
    adminSecret: String
) {
    apiUsers(databaseModule)
    apiAll(databaseModule)
    apiVote(databaseModule, adminSecret)
    apiFavorite(databaseModule)
    apiSynchronize(sessionizeUrl, oldSessionizeUrl, adminSecret)
    apiTwitter()
    apiTime(adminSecret)
    apiLive(adminSecret)
}

/*
POST http://localhost:8080/user
1238476512873162837
 */
private fun Routing.apiUsers(databaseModule: DatabaseModule) {
    route("users") {
        post {
            val userUUID = call.receive<String>()
            val ip = call.request.origin.remoteHost
            val timestamp = LocalDateTime.now(Clock.systemUTC())
            val created = databaseModule.createUser(userUUID, ip, timestamp)
            if (created)
                call.respondBase(HttpStatusCode.Created)
            else
                call.respondBase(HttpStatusCode.Conflict)
        }
        get("count") {
            call.respondBase(data = databaseModule.usersCount().toString())
        }
    }
}

/*
GET http://localhost:8080/favorites
Accept: application/json
Authorization: Bearer 1238476512873162837
*/
private fun Routing.apiFavorite(databaseModule: DatabaseModule) {
    route("favorites") {
        get {
            val principal = call.validatePrincipal(databaseModule) ?: throw Unauthorized()
            val favorites = databaseModule.getFavorites(principal.token)
            call.respondBase(data = favorites)
        }
        post {
            val principal = call.validatePrincipal(databaseModule) ?: throw Unauthorized()
            val sessionId = call.receive<String>()
            databaseModule.createFavorite(principal.token, sessionId)
            call.respondBase(HttpStatusCode.Created)
        }
        delete {
            val principal = call.validatePrincipal(databaseModule) ?: throw Unauthorized()
            val sessionId = call.receive<String>()
            databaseModule.deleteFavorite(principal.token, sessionId)
            call.respondBase(HttpStatusCode.OK)
        }
    }
}

/*
GET http://localhost:8080/votes
Accept: application/json
Authorization: Bearer 1238476512873162837
*/
private fun Routing.apiVote(
    databaseModule: DatabaseModule,
    adminSecret: String
) {
    route("votes") {
        get {
            val principal = call.validatePrincipal(databaseModule) ?: throw Unauthorized()
            val votes = databaseModule.getVotes(principal.token)
            call.respondBase(data = votes)
        }
        get("all") {
            call.validateSecret(adminSecret)

            val votes = databaseModule.getAllVotes()
            call.respondBase(data = votes)
        }
        get("summary/{sessionId}") {
            call.validateSecret(adminSecret)

            val id = call.parameters["sessionId"] ?: throw BadRequest()
            val votesSummary = databaseModule.getVotesSummary(id)
            call.respondBase(data = votesSummary)
        }
        post {
            val principal = call.validatePrincipal(databaseModule) ?: throw Unauthorized()
            val vote = call.receive<VoteData>()
            val sessionId = vote.sessionId
            val rating = vote.rating!!.value

            val session = getSessionizeData().sessions.firstOrNull { it.id == sessionId } ?: throw NotFound()
            val nowTime = now()

            val startVotesAt = session.startsAt
            val votingPeriodStarted = nowTime >= startVotesAt

            if (!votingPeriodStarted) {
                return@post call.respondBase(comeBackLater)
            }

            val timestamp = LocalDateTime.now(Clock.systemUTC())
            val status = if (databaseModule.changeVote(principal.token, sessionId, rating, timestamp)) {
                HttpStatusCode.Created
            } else {
                HttpStatusCode.OK
            }

            call.respondBase(status)
        }
        post("required/{count}") {
            call.validateSecret(adminSecret)
            val count = call.parameters["count"] ?: throw BadRequest()
            votesRequired = count.toInt()
            call.respondBase(HttpStatusCode.OK)
        }
        delete {
            val principal = call.validatePrincipal(databaseModule) ?: throw Unauthorized()
            val vote = call.receive<VoteData>()
            val sessionId = vote.sessionId
            databaseModule.deleteVote(principal.token, sessionId)
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
private fun Routing.apiAll(databaseModule: DatabaseModule) {
    get("all") {
        respondAll(call, databaseModule, old = true)
    }
    get("all2019") {
        respondAll(call, databaseModule, old = false)
    }
}

private suspend fun respondAll(
    call: ApplicationCall,
    databaseModule: DatabaseModule,
    old: Boolean
) {
    val data = if (old) getOldSessionizeData() else getSessionizeData()
    val principal = call.validatePrincipal(databaseModule)
    val (votes, favorites) = if (principal != null) {
        val votes = databaseModule.getVotes(principal.token)
        val favorites = databaseModule.getFavorites(principal.token)
        votes to favorites
    } else {
        emptyList<VoteData>() to emptyList<String>()
    }

    val responseData = ConferenceData(data, favorites, votes, liveInfo(), votesRequired)
    call.respondBase(data = responseData)
}

private fun Routing.apiTwitter() {
    get("feed") {
        call.respondBase(data = getFeedData())
    }
}

private fun Routing.apiTime(adminSecret: String) {
    get("time") {
        call.respondBase(data = now().timestamp)
    }
    post("time/{timestamp}") {
        call.validateSecret(adminSecret)

        val timestamp = call.parameters["timestamp"] ?: error("No time")
        val time = if (timestamp == "null") {
            null
        } else {
            GMTDate(timestamp.toLong())
        }

        updateTime(time)
        call.respondBase(HttpStatusCode.OK)
    }
}

private fun Routing.apiLive(adminSecret: String) {
    post("live") {
        call.validateSecret(adminSecret)

        val form = call.receiveParameters()
        val room = form["roomId"]?.toIntOrNull() ?: throw BadRequest()
        val video = form["video"]

        addLive(room, video)
        call.respondBase(HttpStatusCode.OK)
    }
}

/*
GET http://localhost:8080/sessionizeSync
*/
private fun Routing.apiSynchronize(sessionizeUrl: String, oldSessionizeUrl: String, adminSecret: String) {
    post("sessionizeSync") {
        call.validateSecret(adminSecret)

        synchronizeWithSessionize(sessionizeUrl, oldSessionizeUrl)
        call.respondBase(HttpStatusCode.OK)
    }
}

private fun ApplicationCall.validateSecret(adminSecret: String) {
    val principal = principal<KotlinConfPrincipal>()
    if (principal?.token != adminSecret) {
        throw Unauthorized()
    }
}

private suspend fun ApplicationCall.validatePrincipal(databaseModule: DatabaseModule): KotlinConfPrincipal? {
    val principal = principal<KotlinConfPrincipal>() ?: return null
    if (!databaseModule.validateUser(principal.token)) return null
    return principal
}

