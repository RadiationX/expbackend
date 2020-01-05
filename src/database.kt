package ru.radiationx

import com.zaxxer.hikari.*
import io.ktor.application.*
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.coroutines.*
import ru.radiationx.common.RatingData
import ru.radiationx.common.VoteData
import java.time.*

internal class DatabaseModule(application: Application) {
    private val dispatcher: CoroutineContext
    private val connectionPool: HikariDataSource
    private val connection: Database

    init {
        val appConfig = application.environment.config.config("database")
        val url = appConfig.property("connection").getString()
        val user = appConfig.property("user").getString()
        val pass = appConfig.property("pass").getString()
        val poolSize = appConfig.property("poolSize").getString().toInt()
        application.log.info("Connecting to database at '$url'")

        dispatcher = newFixedThreadPoolContext(poolSize, "database-pool")

        val hikariConfig = HikariConfig().apply {
            jdbcUrl = url
            maximumPoolSize = poolSize
            username = user
            password = pass
            validate()
        }

        connectionPool = HikariDataSource(hikariConfig)

        connection = Database.connect(connectionPool)

        transaction {
            SchemaUtils.create(Users, Favorites, Votes)
        }
    }

    suspend fun validateUser(uuid: String): Boolean = withContext(dispatcher) {
        transaction {
            Users
                .slice(Users.uuid)
                .select { Users.uuid eq uuid }
                .limit(1)
                .count() != 0
        }
    }

    suspend fun createUser(uuid: String, remote: String, timestamp: LocalDateTime): Boolean =
        withContext(dispatcher) {
            transaction {
                val count = Users
                    .slice(Users.uuid)
                    .select { Users.uuid eq uuid }
                    .count()
                if (count == 0) {
                    Users.insert {
                        it[Users.uuid] = uuid
                        it[Users.timestamp] = timestamp.toString()
                        it[Users.remote] = remote
                    }
                }
                count == 0
            }
        }

    suspend fun usersCount(): Int = withContext(dispatcher) {
        transaction {
            Users.slice().selectAll().count()
        }
    }

    suspend fun deleteFavorite(uuid: String, sessionId: String): Unit = withContext(dispatcher) {
        transaction {
            Favorites
                .deleteWhere {
                    (Favorites.uuid eq uuid) and (Favorites.sessionId eq sessionId)
                }
            Unit
        }
    }

    suspend fun createFavorite(uuid: String, sessionId: String) = withContext(dispatcher) {
        transaction {
            val count = Favorites
                .slice(Favorites.uuid, Favorites.sessionId)
                .select { (Favorites.uuid eq uuid) and (Favorites.sessionId eq sessionId) }
                .count()

            if (count == 0) {
                Favorites.insert {
                    it[Favorites.uuid] = uuid
                    it[Favorites.sessionId] = sessionId
                }
            }
            count == 0
        }
    }

    suspend fun getFavorites(userId: String): List<String> = withContext(dispatcher) {
        transaction {
            Favorites
                .slice(Favorites.sessionId)
                .select { Favorites.uuid eq userId }
                .map { it[Favorites.sessionId] }
        }
    }

    suspend fun getAllFavorites(): List<String> = withContext(dispatcher) {
        transaction {
            Favorites
                .slice(Favorites.sessionId)
                .selectAll()
                .map { it[Favorites.sessionId] }
        }
    }

    suspend fun getVotes(uuid: String): List<VoteData> = withContext(dispatcher) {
        transaction {
            Votes
                .slice(Votes.sessionId, Votes.rating)
                .select { Votes.uuid eq uuid }
                .map {
                    VoteData(
                        sessionId = it[Votes.sessionId],
                        rating = RatingData(it[Votes.rating])
                    )
                }
        }
    }

    suspend fun getAllVotes(): List<VoteData> = withContext(dispatcher) {
        transaction {
            Votes
                .slice(Votes.sessionId, Votes.rating)
                .selectAll()
                .map {
                    VoteData(
                        sessionId = it[Votes.sessionId],
                        rating = RatingData(it[Votes.rating])
                    )
                }
        }
    }

    suspend fun changeVote(
        uuid: String,
        sessionId: String,
        rating: Int,
        timestamp: LocalDateTime
    ): Boolean =
        withContext(dispatcher) {
            transaction {
                val count = Votes
                    .slice(Votes.uuid, Votes.sessionId)
                    .select { (Votes.uuid eq uuid) and (Votes.sessionId eq sessionId) }
                    .count()

                if (count == 0) {
                    Votes.insert {
                        it[Votes.uuid] = uuid
                        it[Votes.sessionId] = sessionId
                        it[Votes.rating] = rating
                        it[Votes.timestamp] = timestamp.toString()
                    }
                    true
                } else {
                    Votes.update({ (Votes.uuid eq uuid) and (Votes.sessionId eq sessionId) }) {
                        it[Votes.rating] = rating
                    }
                    false
                }
            }
        }

    suspend fun deleteVote(uuid: String, sessionId: String): Unit = withContext(dispatcher) {
        transaction {
            Votes.deleteWhere { (Votes.uuid eq uuid) and (Votes.sessionId eq sessionId) }
            Unit
        }
    }

    suspend fun getVotesSummary(sessionId: String): Map<String, Int> = withContext(dispatcher) {
        transaction {
            val votes = Votes
                .slice(Votes.rating, Votes.id.count())
                .select { Votes.sessionId eq sessionId }
                .groupBy(Votes.rating)

            val map = votes.associateTo(mutableMapOf()) {
                val rating = when (it[Votes.rating]) {
                    0 -> "soso"
                    1 -> "good"
                    -1 -> "bad"
                    else -> "unknown"
                }
                val count = it[Votes.id.count()]
                rating to count
            }
            if ("bad" !in map) map["bad"] = 0
            if ("good" !in map) map["good"] = 0
            if ("soso" !in map) map["soso"] = 0
            map
        }
    }
}

internal object Users : Table() {
    val id = integer("id").autoIncrement()
    val uuid = varchar("uuid", 50).index()
    val remote = varchar("remote", 50)
    val timestamp = varchar("timestamp", 50)

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

internal object Favorites : Table() {
    val id = integer("id").autoIncrement()
    val uuid = varchar("uuid", 50).index()
    val sessionId = varchar("sessionId", 50)

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

internal object Votes : Table() {
    val id = integer("id").autoIncrement()
    val timestamp = varchar("timestamp", 50)
    val uuid = varchar("uuid", 50).index()
    val sessionId = varchar("sessionId", 50).index()
    val rating = integer("rating")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}
