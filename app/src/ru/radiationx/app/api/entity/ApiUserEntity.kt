package ru.radiationx.app.api.entity

import ru.radiationx.app.api.base.ApiEntity
import java.time.LocalDateTime

data class ApiUserCountResponse(
    val count: Int
) : ApiEntity

data class ApiUserResponse(
    val id: Int,
    val login: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
) : ApiEntity