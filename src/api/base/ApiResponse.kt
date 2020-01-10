package ru.radiationx.api.base

data class ApiResponse<T>(
    val data: T? = null
) : ApiEntity
