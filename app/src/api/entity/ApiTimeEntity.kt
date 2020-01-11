package ru.radiationx.api.entity

import ru.radiationx.api.base.ApiEntity

data class TimeResponse(
    val timestamp: Long
) : ApiEntity

data class TimeRequest(
    val timestamp: String?
) : ApiEntity