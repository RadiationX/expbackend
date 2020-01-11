package ru.radiationx.app.domain.repository

import ru.radiationx.app.domain.OperationResult
import ru.radiationx.app.domain.entity.Rating
import ru.radiationx.app.domain.entity.Vote

interface VoteRepository {

    suspend fun getVotes(userId: Int): List<Vote>

    suspend fun getAllVotes(): List<Vote>

    suspend fun setVote(userId: Int, sessionId: String, rating: Rating): OperationResult<Vote>

    suspend fun deleteVote(userId: Int, sessionId: String): Boolean

    suspend fun getVotesSummary(sessionId: String): Map<Rating, Int>

    suspend fun getRequired(): Int

    suspend fun setRequired(count: Int): OperationResult<Int>
}
