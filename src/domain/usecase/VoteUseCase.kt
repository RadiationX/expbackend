package ru.radiationx.domain.usecase

import ru.radiationx.api.entity.VoteSessionRequest
import ru.radiationx.domain.OperationResult
import ru.radiationx.domain.entity.Rating
import ru.radiationx.domain.entity.UserPrincipal
import ru.radiationx.domain.entity.Vote
import ru.radiationx.domain.exception.BadRequestException
import ru.radiationx.domain.exception.ComeBackLater
import ru.radiationx.domain.exception.NotFound
import ru.radiationx.domain.helper.UserValidator
import ru.radiationx.domain.repository.SessionizeRepository
import ru.radiationx.domain.repository.TimeRepository
import ru.radiationx.domain.repository.VoteRepository

class VoteUseCase(
    private val userValidator: UserValidator,
    private val voteRepository: VoteRepository,
    private val timeRepository: TimeRepository,
    private val sessionizeRepository: SessionizeRepository
) {

    suspend fun getVotes(principal: UserPrincipal?): List<Vote> {
        val userId = userValidator.validateUser(principal).id
        return voteRepository.getVotes(userId)
    }

    suspend fun getAllVotes(principal: UserPrincipal?): List<Vote> {
        userValidator.checkIsAdmin(principal)
        return voteRepository.getAllVotes()
    }

    suspend fun setVote(principal: UserPrincipal?, request: VoteSessionRequest): OperationResult<Vote> {
        val userId = userValidator.validateUser(principal).id
        val sessionId = request.sessionId ?: throw BadRequestException("No sessionId")
        val rating = request.rating ?: throw BadRequestException("No rating")

        val session = sessionizeRepository
            .getData(false)
            .sessions
            .firstOrNull { it.id == sessionId }
            ?: throw NotFound()

        val nowTime = timeRepository.getTime()
        val startVotesAt = session.startsAt
        val votingPeriodStarted = nowTime >= startVotesAt
        if (!votingPeriodStarted) {
            throw ComeBackLater()
        }
        return voteRepository.setVote(userId, sessionId, rating)
    }

    suspend fun deleteVote(principal: UserPrincipal?, request: VoteSessionRequest): Boolean {
        val userId = userValidator.validateUser(principal).id
        val sessionId = request.sessionId ?: throw BadRequestException("No sessionId")
        return voteRepository.deleteVote(userId, sessionId)
    }

    suspend fun getVotesSummary(principal: UserPrincipal?, sessionId: String?): Map<Rating, Int> {
        userValidator.checkIsAdmin(principal)
        sessionId ?: throw BadRequestException()
        return voteRepository.getVotesSummary(sessionId)
    }

    suspend fun setRequired(principal: UserPrincipal?, countParam: String?): OperationResult<Int> {
        userValidator.checkIsAdmin(principal)
        val count = countParam?.toIntOrNull() ?: throw BadRequestException()
        return voteRepository.setRequired(count)
    }
}