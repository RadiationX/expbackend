package ru.radiationx.api.route

import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.features.origin
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.transactions.transaction
import ru.radiationx.base.respondBase
import ru.radiationx.data.entity.db.UsersTable
import ru.radiationx.domain.usecase.UserUseCase
import java.time.Clock
import java.time.LocalDateTime
import java.util.*

class ApiUsersRoute(
    private val userUseCase: UserUseCase,
    private val database: Database
) : BaseApiRoute() {

    companion object {
        val indexes = (0..1000).toList()
    }

    override fun attachRoute(routing: Routing) = routing.apply {

        authenticate {
            route("fill") {
                post {
                    withContext(Dispatchers.IO) {
                        transaction(database) {
                            UsersTable.batchInsert(indexes) {
                                set(UsersTable.login, "user_$it")
                                set(UsersTable.password, "password_$it")
                            }
                        }
                    }
                    call.respondBase(HttpStatusCode.Created)
                }
            }
        }

        route("users") {
            get("count") {
                call.respondBase(data = userUseCase.getAllUsersCount().toString())
            }
        }
    }
}