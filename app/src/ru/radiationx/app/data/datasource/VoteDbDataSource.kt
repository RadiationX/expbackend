package ru.radiationx.app.data.datasource

import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.radiationx.app.data.asVote
import ru.radiationx.app.data.entity.db.VotesRow
import ru.radiationx.app.data.entity.db.VotesTable
import ru.radiationx.domain.OperationResult
import ru.radiationx.domain.entity.Rating
import ru.radiationx.domain.entity.Vote
import kotlin.coroutines.CoroutineContext

class VoteDbDataSource(
    private val dispatcher: CoroutineContext,
    private val database: Database
) {

    suspend fun getVotes(userId: Int): List<Vote> = withContext(dispatcher) {
        transaction(database) {
            val entityId = VotesTable.getIdColumn(userId)
            VotesRow
                .find { VotesTable.userId eq entityId }
                .map { it.asVote() }
        }
    }

    suspend fun getAllVotes(): List<Vote> = withContext(dispatcher) {
        transaction(database) {
            VotesRow
                .all()
                .map { it.asVote() }
        }
    }

    suspend fun setVote(
        userId: Int,
        sessionId: String,
        rating: Rating
    ): OperationResult<Vote> = withContext(dispatcher) {
        transaction(database) {
            val entityId = VotesTable.getIdColumn(userId)
            val count = VotesRow
                .find { (VotesTable.userId eq entityId) and (VotesTable.sessionId eq sessionId) }
                .count()

            if (count == 0) {
                val voteId = VotesTable.insertAndGetId {
                    it[VotesTable.userId] = entityId
                    it[VotesTable.sessionId] = sessionId
                    it[VotesTable.rating] = rating.value
                }
                val vote = VotesRow[voteId].asVote()
                OperationResult(vote, true)
            } else {
                val voteId =
                    VotesTable.update({ (VotesTable.userId eq entityId) and (VotesTable.sessionId eq sessionId) }) {
                        it[VotesTable.rating] = rating.value
                    }
                val vote = VotesRow[voteId].asVote()
                OperationResult(vote, true)
            }
        }
    }

    suspend fun deleteVote(userId: Int, sessionId: String): Boolean = withContext(dispatcher) {
        transaction(database) {
            val entityId = VotesTable.getIdColumn(userId)
            VotesTable.deleteWhere { (VotesTable.userId eq entityId) and (VotesTable.sessionId eq sessionId) }
            true
        }
    }

    suspend fun getVotesSummary(sessionId: String): Map<Rating, Int> = withContext(dispatcher) {
        transaction(database) {
            val votes = VotesTable
                .slice(VotesTable.rating, VotesTable.id.count())
                .select { VotesTable.sessionId eq sessionId }
                .groupBy(VotesTable.rating)

            val map = votes.associateTo(mutableMapOf()) {
                val rating = Rating.valueOf(it[VotesTable.rating])
                val count = it[VotesTable.id.count()]
                rating to count
            }
            map
        }
    }
}