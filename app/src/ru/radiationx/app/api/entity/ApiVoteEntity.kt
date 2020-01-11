package ru.radiationx.app.api.entity

import ru.radiationx.app.api.base.ApiEntity
import ru.radiationx.domain.entity.Rating
import java.time.LocalDateTime

data class ApiVoteSessionResponse(
    val sessionId: String,
    val rating: Rating? = null
) : ApiEntity

data class ApiVoteResponse(
    val id: Int,
    val user: ApiUserResponse?,
    val sessionId: String,
    val rating: Rating,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
) : ApiEntity

data class ApiVoteSummaryResponse(
    val bad: Int,
    val soso: Int,
    val good: Int
) : ApiEntity

data class ApiVoteRequiredResponse(
    val count: Int
) : ApiEntity

data class ApiVoteSessionRequest(
    val sessionId: String? = null,
    val rating: Rating? = null
) : ApiEntity
