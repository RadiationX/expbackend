package ru.radiationx.api.route

import io.ktor.application.call
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
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import ru.radiationx.base.respondBase
import ru.radiationx.data.entity.db.UsersTable
import ru.radiationx.domain.usecase.UserUseCase
import java.time.Clock
import java.time.LocalDateTime
import java.util.*
import kotlin.coroutines.CoroutineContext

class ApiUsersRoute(
    private val userUseCase: UserUseCase,
    private val database: Database
) : BaseApiRoute() {

    companion object {
        val indexes = (0..1000).toList()
        val someDitch = indexes.map { UUID.randomUUID().toString() }.toTypedArray()
        val reversed = someDitch.map { it.reversed() }.toTypedArray()
        val times = indexes.map { LocalDateTime.now(Clock.systemUTC()).toString() }.toTypedArray()
    }

    override fun attachRoute(routing: Routing) = routing.apply {
        route("fill") {
            post {
                withContext(Dispatchers.IO) {
                    transaction(database) {
                        UsersTable.batchInsert(indexes) {
                            set(UsersTable.uuid, someDitch[it])
                            set(UsersTable.remote, reversed[it])
                            set(UsersTable.timestamp, times[it])
                        }
                    }
                }
                call.respondBase(HttpStatusCode.Created)
            }
        }

        route("users") {

            post {
                val userUUID = call.receive<String>()
                val ip = call.request.origin.remoteHost
                val created = userUseCase.createUser(userUUID, ip)
                if (created)
                    call.respondBase(HttpStatusCode.Created)
                else
                    call.respondBase(HttpStatusCode.Conflict)
            }

            get("count") {
                call.respondBase(data = userUseCase.getAllUsersCount().toString())
            }
        }
    }
}