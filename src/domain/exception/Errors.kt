package ru.radiationx.domain.exception

import io.ktor.http.HttpStatusCode
import java.lang.Exception

open class BaseException(
    val title: String? = null,
    val description: String? = null
) : Exception(title)

open class AlreadyExistException(title: String = "Already exit") : Exception(title)

class ServiceUnavailable : Throwable()
class BadRequest : Throwable()
class Unauthorized : Throwable()
class NotFound : Throwable()
class SecretInvalidError : Throwable()
class ComeBackLater() : Throwable()
