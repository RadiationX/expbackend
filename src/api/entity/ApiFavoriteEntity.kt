package ru.radiationx.api.entity

import ru.radiationx.api.base.ApiEntity
import java.time.LocalDateTime

data class FavoriteResponse(
    val id: Int,
    val user: UserResponse?,
    val sessionId: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
) : ApiEntity