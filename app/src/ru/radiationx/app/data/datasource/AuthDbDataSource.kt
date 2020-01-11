package ru.radiationx.app.data.datasource

import io.ktor.auth.UserPasswordCredential
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import ru.radiationx.app.data.asToken
import ru.radiationx.app.data.asUser
import ru.radiationx.app.data.entity.db.TokenRow
import ru.radiationx.app.data.entity.db.TokensTable
import ru.radiationx.app.data.entity.db.UserRow
import ru.radiationx.app.data.entity.db.UsersTable
import ru.radiationx.app.domain.entity.Token
import ru.radiationx.app.domain.entity.User
import kotlin.coroutines.CoroutineContext

class AuthDbDataSource(
    private val dispatcher: CoroutineContext,
    private val database: Database
) {

    suspend fun signUp(credentials: UserPasswordCredential): User = withContext(dispatcher) {
        transaction(database) {
            UserRow.new {
                this.login = credentials.name
                this.password = credentials.password
            }.asUser()
        }
    }

    suspend fun signIn(
        userId: Int,
        token: String,
        ip: String
    ): String = withContext(dispatcher) {
        transaction(database) {
            val entityId = UsersTable.getIdColumn(userId)
            TokensTable.insert {
                it[this.userId] = entityId
                it[this.token] = token
                it[this.ip] = ip
            }
        }
        token
    }

    suspend fun signOut(userId: Int, token: String) = withContext(dispatcher) {
        transaction {
            val entityId = UsersTable.getIdColumn(userId)
            TokenRow
                .find { (TokensTable.token eq token) and (TokensTable.userId eq entityId) }
                .firstOrNull()
                ?.delete()
            Unit
        }
    }

    suspend fun getToken(userId: Int, token: String): Token? = withContext(dispatcher) {
        transaction {
            val entityId = UsersTable.getIdColumn(userId)
            TokenRow
                .find { (TokensTable.token eq token) and (TokensTable.userId eq entityId) }
                .firstOrNull()
                ?.asToken()
        }
    }
}