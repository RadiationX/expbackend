package ru.radiationx.domain.usecase

import ru.radiationx.domain.entity.UserPrincipal
import ru.radiationx.domain.entity.Rating
import ru.radiationx.domain.entity.Vote
import ru.radiationx.domain.exception.BadRequest
import ru.radiationx.domain.exception.ComeBackLater
import ru.radiationx.domain.exception.NotFound
import ru.radiationx.domain.helper.UserValidator
import ru.radiationx.domain.repository.SessionizeRepository
import ru.radiationx.domain.repository.TimeRepository
import ru.radiationx.domain.repository.VoteRepository
import java.time.Clock
import java.time.LocalDateTime

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

    suspend fun changeVote(principal: UserPrincipal?, sessionId: String?, rating: Rating?): Boolean {
        val userId = userValidator.validateUser(principal).id
        sessionId ?: throw BadRequest()
        rating ?: throw BadRequest()

        val session = sessionizeRepository
            .getData(false)
            .sessions
            .firstOrNull { it.id == sessionId }
            ?: throw NotFound()
        val timestamp = LocalDateTime.now(Clock.systemUTC())

        val nowTime = timeRepository.getTime()
        val startVotesAt = session.startsAt
        val votingPeriodStarted = nowTime >= startVotesAt
        if (!votingPeriodStarted) {
            throw ComeBackLater()
        }
        return voteRepository.changeVote(userId, sessionId, rating, timestamp)
    }

    suspend fun deleteVote(principal: UserPrincipal?, sessionId: String?): Boolean {
        val userId = userValidator.validateUser(principal).id
        sessionId ?: throw BadRequest()
        return voteRepository.deleteVote(userId, sessionId)
    }

    suspend fun getVotesSummary(principal: UserPrincipal?, sessionId: String?): Map<Rating, Int> {
        userValidator.checkIsAdmin(principal)
        sessionId ?: throw BadRequest()
        return voteRepository.getVotesSummary(sessionId)
    }

    suspend fun setRequired(principal: UserPrincipal?, countParam: String?) {
        userValidator.checkIsAdmin(principal)
        val count = countParam?.toIntOrNull() ?: throw BadRequest()
        voteRepository.setRequired(count)

    }
}