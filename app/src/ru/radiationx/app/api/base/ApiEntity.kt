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
    val data: T,
    val uuid: String? = null
) : ApiEntity

data class WebSocketTextEvent(
    val event: String,
    val uuid: String? = null,
    val text: String
) : ApiEntity

data class WebSocketError(
    val error: ApiErrorResponse,
    val uuid: String? = null
) : ApiEntity

class WebSocketConverterException(
    override val message: String?
) : Exception()