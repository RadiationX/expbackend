package ru.radiationx.api.entity

import ru.radiationx.api.base.ApiEntity
import java.time.LocalDateTime

data class UserCountResponse(
    val count: Int
) : ApiEntity

data class UserResponse(
    val id: Int,
    val login: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
) : ApiEntity