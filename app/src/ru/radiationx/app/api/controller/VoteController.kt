package ru.radiationx.app.api.controller

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import ru.radiationx.app.api.entity.ApiVoteRequiredResponse
import ru.radiationx.app.api.entity.ApiVoteSessionRequest
import ru.radiationx.app.api.entity.ApiVoteSummaryResponse
import ru.radiationx.app.api.postHttpCode
import ru.radiationx.app.api.toDomain
import ru.radiationx.app.api.toResponse
import ru.radiationx.app.userPrincipal
import ru.radiationx.app.api.base.respondBase
import ru.radiationx.domain.entity.Rating
import ru.radiationx.domain.usecase.VoteUseCase

class VoteController(
    private val voteUseCase: VoteUseCase
) {

    suspend fun getVotes(call: ApplicationCall) {
        val principal = call.userPrincipal
        val votes = voteUseCase.getVotes(principal?.user)
        call.respondBase(votes.map { it.toResponse() })
    }

    suspend fun getAllVotes(call: ApplicationCall) {
        val principal = call.userPrincipal
        val votes = voteUseCase.getAllVotes(principal?.user)
        call.respondBase(votes.map { it.toResponse() })
    }

    suspend fun getVotesSummary(call: ApplicationCall) {
        val principal = call.userPrincipal
        val sessionId = call.parameters["sessionId"]
        val votesSummary = voteUseCase.getVotesSummary(principal?.user, sessionId)

        val responseData = ApiVoteSummaryResponse(
            votesSummary[Rating.BAD] ?: 0,
            votesSummary[Rating.OK] ?: 0,
            votesSummary[Rating.GOOD] ?: 0
        )
        call.respondBase(responseData)
    }

    suspend fun setVote(call: ApplicationCall) {
        val principal = call.userPrincipal
        val request = call.receive<ApiVoteSessionRequest>()

        val result = voteUseCase.setVote(principal?.user, request.toDomain())
        call.respondBase(
            result.data.toResponse(),
            result.postHttpCode()
        )
    }

    suspend fun setRequired(call: ApplicationCall) {
        val principal = call.userPrincipal
        val count = call.parameters["count"]

        val result = voteUseCase.setRequired(principal?.user, count)
        call.respondBase(
            ApiVoteRequiredResponse(result.data),
            result.postHttpCode()
        )
    }

    suspend fun deleteVote(call: ApplicationCall) {
        val principal = call.userPrincipal
        val request = call.receive<ApiVoteSessionRequest>()
        voteUseCase.deleteVote(principal?.user, request.toDomain())
        call.respondBase(statusCode = HttpStatusCode.NoContent)
    }
}