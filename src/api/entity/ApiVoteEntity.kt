package ru.radiationx.api.entity

import ru.radiationx.api.base.ApiEntity
import ru.radiationx.domain.entity.Rating
import ru.radiationx.domain.entity.User
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