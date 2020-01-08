package ru.radiationx.base

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond

//suspend fun ApplicationCall.respondBase(data: Any) = respond(data)
suspend fun ApplicationCall.respondBase(status: HttpStatusCode = HttpStatusCode.OK, data: Any? = null) {
    if (data != null) {
        respond(data)
    } else {
        respond(BaseDataResponse<Unit>())
    }
}
