package ru.radiationx.app.api.entity

import ru.radiationx.app.api.base.ApiEntity
import ru.radiationx.app.domain.entity.Rating
import java.time.LocalDateTime

data class VoteSessionResponse(
    val sessionId: String,
    val rating: Rating? = null
) : ApiEntity

data class VoteResponse(
    val id: Int,
    val user: UserResponse?,
    val sessionId: String,
    val rating: Rating,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
) : ApiEntity

data class VoteSummaryResponse(
    val bad: Int,
    val soso: Int,
    val good: Int
) : ApiEntity

data class VoteRequiredResponse(
    val count: Int
) : ApiEntity

data class VoteSessionRequest(
    val sessionId: String? = null,
    val rating: Rating? = null
) : ApiEntity
