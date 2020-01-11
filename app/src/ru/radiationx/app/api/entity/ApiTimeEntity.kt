package ru.radiationx.app.api.entity

import ru.radiationx.app.api.base.ApiEntity

data class TimeResponse(
    val timestamp: Long
) : ApiEntity

data class TimeRequest(
    val timestamp: String?
) : ApiEntity