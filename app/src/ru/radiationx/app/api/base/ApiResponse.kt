package ru.radiationx.app.api.base

data class ApiResponse<T>(
    val data: T? = null
) : ApiEntity
