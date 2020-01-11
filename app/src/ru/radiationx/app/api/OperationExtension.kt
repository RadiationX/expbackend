package ru.radiationx.app.api

import io.ktor.http.HttpStatusCode
import ru.radiationx.domain.OperationResult

fun <T> OperationResult<T>.postHttpCode(): HttpStatusCode = if (status) {
    HttpStatusCode.Created
} else {
    HttpStatusCode.OK
}