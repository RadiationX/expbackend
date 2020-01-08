package ru.radiationx.data.entity.db

import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.`java-time`.datetime
import java.time.Clock
import java.time.LocalDateTime

fun currentUtc(): LocalDateTime = LocalDateTime.now(Clock.systemUTC())

abstract class BaseIntIdTable(name: String = "", columnName: String = "id") : IntIdTable(name, columnName) {
    val createdAt = datetime("created_at").clientDefault { currentUtc() }
    val updatedAt = datetime("updated_at").nullable()

    fun getIdColumn(id: Int) = EntityID(id, this)
}

abstract class BaseIntEntity(id: EntityID<Int>, table: BaseIntIdTable) : IntEntity(id) {
    val createdAt by table.createdAt
    var updatedAt by table.updatedAt
}

abstract class BaseIntEntityClass<E : BaseIntEntity>(
    table: BaseIntIdTable,
    entityType: Class<E>? = null
) : IntEntityClass<E>(table, entityType) {

    init {
        EntityHook.subscribe { action ->
            if (action.changeType == EntityChangeType.Updated) {
                try {
                    action.toEntity(this)?.updatedAt = currentUtc()
                } catch (e: Exception) {
                    //nothing much to do here
                }
            }
        }
    }
}