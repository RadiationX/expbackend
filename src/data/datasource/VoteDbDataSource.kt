package ru.radiationx.data.datasource

import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.radiationx.data.asVote
import ru.radiationx.data.entity.db.VotesRow
import ru.radiationx.data.entity.db.VotesTable
import ru.radiationx.domain.entity.Rating
import ru.radiationx.domain.entity.Vote
import java.time.LocalDateTime
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

    suspend fun changeVote(
        userId: Int,
        sessionId: String,
        rating: Rating,
        timestamp: LocalDateTime
    ): Boolean = withContext(dispatcher) {
        transaction(database) {
            val entityId = VotesTable.getIdColumn(userId)
            val count = VotesRow
                .find { (VotesTable.userId eq entityId) and (VotesTable.sessionId eq sessionId) }
                .count()

            if (count == 0) {
                VotesTable.insert {
                    it[VotesTable.userId] = entityId
                    it[VotesTable.sessionId] = sessionId
                    it[VotesTable.rating] = rating.value
                }
                true
            } else {
                VotesTable.update({ (VotesTable.userId eq entityId) and (VotesTable.sessionId eq sessionId) }) {
                    it[VotesTable.rating] = rating.value
                }
                false
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