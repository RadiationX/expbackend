package ru.radiationx.app.api.entity

import ru.radiationx.app.api.base.ApiEntity
import java.time.LocalDateTime

data class ApiFavoriteResponse(
    val id: Int,
    val user: ApiUserResponse?,
    val sessionId: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
) : ApiEntity

data class ApiFavoriteRequest(
    val sessionId: String? = null
) : ApiEntity