package ru.radiationx.app.api.base

interface ApiEntity

data class ApiErrorResponse(
    val title: String,
    val description: String? = null
) : ApiEntity

data class ApiResponse<T>(
    val data: T? = null
) : ApiEntity

data class WebSocketEvent<T>(
    val event: String,
    val data: T
) : ApiEntity

data class WebSocketError(
    val error: ApiErrorResponse
) : ApiEntity