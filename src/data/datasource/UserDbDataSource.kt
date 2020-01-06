package ru.radiationx.data.datasource

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.radiationx.data.entity.db.UsersTable
import ru.radiationx.domain.entity.User
import java.time.LocalDateTime

class UserDbDataSource(
    private val database: Database
) {

    fun getUser(uuid: String): User? = transaction(database) {
        UsersTable
            .select { UsersTable.uuid eq uuid }
            .limit(1)
            .map { it.asUser() }
            .firstOrNull()
    }

    fun createUser(uuid: String, remote: String, timestamp: LocalDateTime): Boolean = transaction(database) {
        val count = UsersTable
            .slice(UsersTable.uuid)
            .select { UsersTable.uuid eq uuid }
            .count()

        if (count == 0) {
            UsersTable.insert {
                it[UsersTable.uuid] = uuid
                it[UsersTable.timestamp] = timestamp.toString()
                it[UsersTable.remote] = remote
            }
        }
        count == 0
    }

    fun getAllUsers(): List<User> = transaction(database) {
        println("db getAllUsers at thread ${Thread.currentThread()}")
        UsersTable.selectAll().toList().map { it.asUser() }
    }

    private fun ResultRow.asUser(): User = User(
        get(UsersTable.id),
        get(UsersTable.uuid),
        get(UsersTable.remote),
        LocalDateTime.parse(get(UsersTable.timestamp))
    )
}