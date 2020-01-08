package ru.radiationx.api.route

import io.ktor.application.call
import io.ktor.auth.UserPasswordCredential
import io.ktor.auth.authenticate
import io.ktor.auth.parseAuthorizationHeader
import io.ktor.features.origin
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import ru.radiationx.JwtConfig
import ru.radiationx.UserPrincipal
import ru.radiationx.base.respondBase
import ru.radiationx.data.asUser
import ru.radiationx.data.entity.db.TokenRow
import ru.radiationx.data.entity.db.TokensTable
import ru.radiationx.data.entity.db.UserRow
import ru.radiationx.data.entity.db.UsersTable
import ru.radiationx.user
import ru.radiationx.userToken

class ApiAuthRoute(
    private val database: Database
) : BaseApiRoute() {

    override fun attachRoute(routing: Routing): Route = routing.apply {

        post("register") {
            val credentials = call.receive<UserPasswordCredential>()
            if (credentials.name.trim().length < 2) {
                throw Exception("So short name, bro")
            }
            if (credentials.password.length < 6) {
                throw Exception("So short password, bro")
            }
            val createdUser = transaction(database) {
                UserRow
                    .find { UsersTable.login eq credentials.name }
                    .firstOrNull()
                    ?.also { throw Exception("User already created, bro") }


                UserRow.new {
                    this.login = credentials.name
                    this.password = credentials.password
                }.asUser()
            }
            call.respondBase(data = createdUser)
        }
        authenticate(optional = true) {
            post("login") {
                val principal = call.user
                if (principal != null) {
                    throw Exception("You already logined, bruh")
                }

                val credentials = call.receive<UserPasswordCredential>()
                val userRow = transaction(database) {
                    UserRow
                        .find { (UsersTable.login eq credentials.name) and (UsersTable.password eq credentials.password) }
                        .firstOrNull()
                        ?: throw Exception("User not found, bruh")
                }

                val user = userRow.asUser()
                val userPrincipal = UserPrincipal(user.id)
                val token = JwtConfig.makeToken(userPrincipal)
                transaction(database) {
                    TokensTable.insert {
                        it[this.userId] = userRow.id
                        it[this.token] = token
                        it[this.ip] = call.request.origin.remoteHost
                    }
                }
                call.respond(token)
            }
        }

        authenticate(optional = true) {
            get("check") {
                val token = call.userToken ?: throw Exception("No auth header")
                val principal = call.user ?: throw Exception("No auth data")

                val user = transaction(database) {
                    val tokenRow = TokenRow
                        .find { TokensTable.token eq token }
                        .firstOrNull()
                    val userRow = tokenRow?.userId
                    tokenRow ?: throw Exception("Token not found")
                    userRow ?: throw Exception("No user found by token")
                    userRow.asUser()
                }

                if (user.id != principal.id) {
                    throw Exception("Wrong user by token. BRUH")
                }
                call.respond("authenticated!")
            }
        }
    }
}