package ru.radiationx.api.controller

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import ru.radiationx.api.entity.VoteData
import ru.radiationx.base.respondBase
import ru.radiationx.domain.entity.Rating
import ru.radiationx.domain.usecase.VoteUseCase
import ru.radiationx.user
import kotlin.collections.contains
import kotlin.collections.mapKeys
import kotlin.collections.set
import kotlin.collections.toMutableMap

class VoteController(
    private val voteUseCase: VoteUseCase
) {

    suspend fun getVotes(call: ApplicationCall) {
        val principal = call.user
        val votes = voteUseCase.getVotes(principal)
        call.respondBase(data = votes)
    }

    suspend fun getAllVotes(call: ApplicationCall) {
        val principal = call.user
        val votes = voteUseCase.getAllVotes(principal)
        call.respondBase(data = votes)
    }

    suspend fun getVotesSummary(call: ApplicationCall) {
        val principal = call.user
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

    suspend fun changeVote(call: ApplicationCall) {
        val principal = call.user
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

    suspend fun setRequired(call: ApplicationCall) {
        val principal = call.user
        val count = call.parameters["count"]
        voteUseCase.setRequired(principal, count)
        call.respondBase(HttpStatusCode.OK)
    }

    suspend fun deleteVote(call: ApplicationCall) {
        val principal = call.user
        val vote = call.receive<VoteData>()
        val sessionId = vote.sessionId
        voteUseCase.deleteVote(principal, sessionId)
        call.respondBase(HttpStatusCode.OK)
    }
}