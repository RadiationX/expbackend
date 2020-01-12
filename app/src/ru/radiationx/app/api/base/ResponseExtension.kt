package ru.radiationx.app.api.base

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import ru.radiationx.app.api.base.ApiResponse

//suspend fun ApplicationCall.respondBase(data: Any) = respond(data)
suspend fun ApplicationCall.respondBase(data: Any? = null, statusCode: HttpStatusCode? = null) {
    if (statusCode != null) {
        respond(statusCode, ApiResponse(data))
    } else {
        respond(ApiResponse(data))
    }
}