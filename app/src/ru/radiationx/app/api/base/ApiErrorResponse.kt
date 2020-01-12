package ru.radiationx.app.api.base

data class ApiErrorResponse(
    val title: String,
    val description: String? = null
) : ApiEntity