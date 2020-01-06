package ru.radiationx.api.entity

import ru.radiationx.domain.entity.Rating

class VoteData(
    val sessionId: String,
    val rating: Rating? = null
)