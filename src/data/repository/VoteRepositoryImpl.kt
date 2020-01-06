package ru.radiationx.data.repository

import ru.radiationx.data.datasource.VoteDbDataSource
import ru.radiationx.domain.entity.Rating
import ru.radiationx.domain.entity.Vote
import ru.radiationx.domain.repository.VoteRepository
import java.time.LocalDateTime

class VoteRepositoryImpl(
    private val voteDbDataSource: VoteDbDataSource
) : VoteRepository {

    @Volatile
    internal var votesRequired = 10

    override suspend fun getVotes(uuid: String): List<Vote> = voteDbDataSource.getVotes(uuid)

    override suspend fun getAllVotes(): List<Vote> = voteDbDataSource.getAllVotes()

    override suspend fun changeVote(
        uuid: String,
        sessionId: String,
        rating: Rating,
        timestamp: LocalDateTime
    ): Boolean =
        voteDbDataSource.changeVote(uuid, sessionId, rating, timestamp)

    override suspend fun deleteVote(uuid: String, sessionId: String): Boolean =
        voteDbDataSource.deleteVote(uuid, sessionId)

    override suspend fun getVotesSummary(sessionId: String): Map<Rating, Int> =
        voteDbDataSource.getVotesSummary(sessionId)

    override suspend fun getRequired(): Int = votesRequired

    override suspend fun setRequired(count: Int) {
        votesRequired = count
    }
}