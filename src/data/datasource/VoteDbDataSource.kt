package ru.radiationx.data.datasource

import io.ktor.util.date.GMTDate
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.radiationx.data.asUser
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

    suspend fun getVotes(uuid: String): List<Vote> = withContext(dispatcher) {
        transaction(database) {
            VotesRow
                .find { VotesTable.uuid eq uuid }
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
        uuid: String,
        sessionId: String,
        rating: Rating,
        timestamp: LocalDateTime
    ): Boolean = withContext(dispatcher) {
        transaction(database) {
            val count = VotesRow
                .find { (VotesTable.uuid eq uuid) and (VotesTable.sessionId eq sessionId) }
                .count()

            if (count == 0) {
                VotesTable.insert {
                    it[VotesTable.uuid] = uuid
                    it[VotesTable.sessionId] = sessionId
                    it[VotesTable.rating] = rating.value
                    it[VotesTable.timestamp] = timestamp.toString()
                }
                true
            } else {
                VotesTable.update({ (VotesTable.uuid eq uuid) and (VotesTable.sessionId eq sessionId) }) {
                    it[VotesTable.rating] = rating.value
                }
                false
            }
        }
    }

    suspend fun deleteVote(uuid: String, sessionId: String): Boolean = withContext(dispatcher) {
        transaction(database) {
            VotesTable.deleteWhere { (VotesTable.uuid eq uuid) and (VotesTable.sessionId eq sessionId) }
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