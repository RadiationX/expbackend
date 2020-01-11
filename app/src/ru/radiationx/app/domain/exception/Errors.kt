package ru.radiationx.app.domain.exception

open class BaseException(
    val title: String? = null,
    val description: String? = null
) : Exception(title)

class AlreadyExistException(val title: String = "Already exit") : Exception(title)
class BadRequestException(val title: String? = null) : Exception(title)
class ValidationException(val field: String, val title: String) : Exception(title)

class ServiceUnavailable : Throwable()
class Unauthorized : Throwable()
class NotFound : Throwable()
class SecretInvalidError : Throwable()
class ComeBackLater() : Throwable()
