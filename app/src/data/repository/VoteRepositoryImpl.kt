package ru.radiationx.data.repository

import ru.radiationx.data.datasource.VoteDbDataSource
import ru.radiationx.domain.OperationResult
import ru.radiationx.domain.entity.Rating
import ru.radiationx.domain.entity.Vote
import ru.radiationx.domain.repository.VoteRepository

class VoteRepositoryImpl(
    private val voteDbDataSource: VoteDbDataSource
) : VoteRepository {

    @Volatile
    internal var votesRequired = 10

    override suspend fun getVotes(userId: Int): List<Vote> = voteDbDataSource.getVotes(userId)

    override suspend fun getAllVotes(): List<Vote> = voteDbDataSource.getAllVotes()

    override suspend fun setVote(
        userId: Int,
        sessionId: String,
        rating: Rating
    ): OperationResult<Vote> =
        voteDbDataSource.setVote(userId, sessionId, rating)

    override suspend fun deleteVote(userId: Int, sessionId: String): Boolean =
        voteDbDataSource.deleteVote(userId, sessionId)

    override suspend fun getVotesSummary(sessionId: String): Map<Rating, Int> =
        voteDbDataSource.getVotesSummary(sessionId)

    override suspend fun getRequired(): Int = votesRequired

    override suspend fun setRequired(count: Int): OperationResult<Int> {
        votesRequired = count
        return OperationResult(votesRequired, false)
    }
}