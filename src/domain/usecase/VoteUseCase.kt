package ru.radiationx.domain.usecase

import ru.radiationx.domain.entity.KotlinConfPrincipal
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

    suspend fun getVotes(principal: KotlinConfPrincipal?): List<Vote> {
        val uuid = userValidator.checkHasUser(principal).token
        return voteRepository.getVotes(uuid)
    }

    suspend fun getAllVotes(principal: KotlinConfPrincipal?): List<Vote> {
        userValidator.checkIsAdmin(principal)
        return voteRepository.getAllVotes()
    }

    suspend fun changeVote(principal: KotlinConfPrincipal?, sessionId: String?, rating: Rating?): Boolean {
        val uuid = userValidator.checkHasUser(principal).token
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
        return voteRepository.changeVote(uuid, sessionId, rating, timestamp)
    }

    suspend fun deleteVote(principal: KotlinConfPrincipal?, sessionId: String?): Boolean {
        val uuid = userValidator.checkHasUser(principal).token
        sessionId ?: throw BadRequest()
        return voteRepository.deleteVote(uuid, sessionId)
    }

    suspend fun getVotesSummary(principal: KotlinConfPrincipal?, sessionId: String?): Map<Rating, Int> {
        userValidator.checkIsAdmin(principal)
        sessionId ?: throw BadRequest()
        return voteRepository.getVotesSummary(sessionId)
    }

    suspend fun setRequired(principal: KotlinConfPrincipal?, countParam: String?) {
        userValidator.checkIsAdmin(principal)
        val count = countParam?.toIntOrNull() ?: throw BadRequest()
        voteRepository.setRequired(count)

    }
}