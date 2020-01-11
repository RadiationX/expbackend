package ru.radiationx.app.api.entity

import ru.radiationx.app.api.base.ApiEntity
import java.time.LocalDateTime

data class FavoriteResponse(
    val id: Int,
    val user: UserResponse?,
    val sessionId: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
) : ApiEntity

data class FavoriteRequest(
    val sessionId: String? = null
) : ApiEntity