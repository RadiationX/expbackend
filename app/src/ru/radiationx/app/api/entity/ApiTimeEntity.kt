package ru.radiationx.app.api.entity

import ru.radiationx.app.api.base.ApiEntity

data class ApiTimeResponse(
    val timestamp: Long
) : ApiEntity

data class ApiTimeRequest(
    val timestamp: String?
) : ApiEntity