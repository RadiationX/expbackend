package ru.radiationx.domain.helper

import io.ktor.application.call
import io.ktor.auth.parseAuthorizationHeader
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import ru.radiationx.UserPrincipal
import ru.radiationx.data.asUser
import ru.radiationx.data.entity.db.TokenRow
import ru.radiationx.data.entity.db.TokensTable
import ru.radiationx.domain.config.ServiceConfigHolder
import ru.radiationx.domain.entity.KotlinConfPrincipal
import ru.radiationx.domain.exception.Unauthorized
import ru.radiationx.domain.repository.UserRepository
import ru.radiationx.user

class UserValidator(
    private val serviceConfigHolder: ServiceConfigHolder,
    private val userRepository: UserRepository
) {

    fun checkToken(token: String?, userId: Int): UserPrincipal {
        token ?: throw Exception("No token")

        val tokenRow = transaction {
            TokenRow
                .find { TokensTable.token eq token }
                .firstOrNull()
                ?: throw Exception("Token not found")
        }
        val user = tokenRow.userId?.asUser() ?: throw Exception("No user found by token")
        if (user.id != userId) {
            throw Exception("Wrong user by token. BRUH")
        }
        return UserPrincipal(user.id)
    }

    fun checkIsAdmin(principal: UserPrincipal?): UserPrincipal {
        principal ?: throw Unauthorized()
        if (true) {
            throw Unauthorized()
        }
        return principal
    }

    suspend fun checkHasUser(principal: UserPrincipal?): UserPrincipal {
        principal ?: throw Unauthorized()
        if (userRepository.getUser(0) == null) {
            throw Unauthorized()
        }
        return principal
    }
}