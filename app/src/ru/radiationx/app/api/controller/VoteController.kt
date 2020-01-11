package ru.radiationx.app.api.controller

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import ru.radiationx.app.api.entity.VoteRequiredResponse
import ru.radiationx.app.api.entity.VoteSessionRequest
import ru.radiationx.app.api.entity.VoteSummaryResponse
import ru.radiationx.app.api.postHttpCode
import ru.radiationx.app.api.toResponse
import ru.radiationx.app.userPrincipal
import ru.radiationx.app.base.respondBase
import ru.radiationx.app.domain.entity.Rating
import ru.radiationx.app.domain.usecase.VoteUseCase

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
        val votesSummary = voteUseCase.getVotesSummary(principal, sessionId)

        val responseData = VoteSummaryResponse(
            votesSummary[Rating.BAD] ?: 0,
            votesSummary[Rating.OK] ?: 0,
            votesSummary[Rating.GOOD] ?: 0
        )
        call.respondBase(responseData)
    }

    suspend fun setVote(call: ApplicationCall) {
        val principal = call.userPrincipal
        val request = call.receive<VoteSessionRequest>()

        val result = voteUseCase.setVote(principal, request)
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
        val request = call.receive<VoteSessionRequest>()
        voteUseCase.deleteVote(principal, request)
        call.respondBase(statusCode = HttpStatusCode.NoContent)
    }
}