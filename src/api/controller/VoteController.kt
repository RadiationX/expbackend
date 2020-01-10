package ru.radiationx.api.controller

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import ru.radiationx.api.entity.VoteRequiredResponse
import ru.radiationx.api.entity.VoteSessionResponse
import ru.radiationx.api.entity.VoteSummaryResponse
import ru.radiationx.api.postHttpCode
import ru.radiationx.api.toResponse
import ru.radiationx.base.respondBase
import ru.radiationx.domain.entity.Rating
import ru.radiationx.domain.usecase.VoteUseCase
import ru.radiationx.userPrincipal

class VoteController(
    private val voteUseCase: VoteUseCase
) {

    suspend fun getVotes(call: ApplicationCall) {
        val principal = call.userPrincipal
        val votes = voteUseCase.getVotes(principal)
        call.respondBase(votes.map { it.toResponse() })
    }

    suspend fun getAllVotes(call: ApplicationCall) {
        val principal = call.userPrincipal
        val votes = voteUseCase.getAllVotes(principal)
        call.respondBase(votes.map { it.toResponse() })
    }

    suspend fun getVotesSummary(call: ApplicationCall) {
        val principal = call.userPrincipal
        val sessionId = call.parameters["sessionId"]
        val votesSummary = voteUseCase
            .getVotesSummary(principal, sessionId)

        val responseData = VoteSummaryResponse(
            votesSummary[Rating.BAD] ?: 0,
            votesSummary[Rating.OK] ?: 0,
            votesSummary[Rating.GOOD] ?: 0
        )
        call.respondBase(responseData)
    }

    suspend fun setVote(call: ApplicationCall) {
        val principal = call.userPrincipal
        val vote = call.receive<VoteSessionResponse>()
        val sessionId = vote.sessionId
        val rating = vote.rating

        val result = voteUseCase.setVote(principal, sessionId, rating)
        call.respondBase(
            result.data.toResponse(),
            result.postHttpCode()
        )
    }

    suspend fun setRequired(call: ApplicationCall) {
        val principal = call.userPrincipal
        val count = call.parameters["count"]

        val result = voteUseCase.setRequired(principal, count)
        call.respondBase(
            VoteRequiredResponse(result.data),
            result.postHttpCode()
        )
    }

    suspend fun deleteVote(call: ApplicationCall) {
        val principal = call.userPrincipal
        val vote = call.receive<VoteSessionResponse>()
        val sessionId = vote.sessionId
        voteUseCase.deleteVote(principal, sessionId)
        call.respondBase(statusCode = HttpStatusCode.NoContent)
    }
}