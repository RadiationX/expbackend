package ru.radiationx.domain.repository

import ru.radiationx.domain.entity.Rating
import ru.radiationx.domain.entity.Vote
import java.time.LocalDateTime

interface VoteRepository {

    suspend fun getVotes(userId: Int): List<Vote>

    suspend fun getAllVotes(): List<Vote>

    suspend fun changeVote(userId: Int, sessionId: String, rating: Rating, timestamp: LocalDateTime): Boolean

    suspend fun deleteVote(userId: Int, sessionId: String): Boolean

    suspend fun getVotesSummary(sessionId: String): Map<Rating, Int>

    suspend fun getRequired(): Int

    suspend fun setRequired(count: Int)
}
