package ru.radiationx

import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.*
import io.ktor.auth.jwt.jwt
import io.ktor.features.*
import io.ktor.gson.gson
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.default
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.util.date.GMTDate
import io.ktor.util.error
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject
import org.mindrot.jbcrypt.BCrypt
import ru.radiationx.api.BatchApiRouting
import ru.radiationx.api.job.launchSyncJob
import ru.radiationx.base.BaseError
import ru.radiationx.base.BaseErrorContainer
import ru.radiationx.base.BaseResponse
import ru.radiationx.common.GMTDateSerializer
import ru.radiationx.common.JwtConfig
import ru.radiationx.common.LocalDateTimeAdapter
import ru.radiationx.domain.config.ServiceConfigHolder
import ru.radiationx.domain.config.SessionizeConfigHolder
import ru.radiationx.domain.config.TokenConfigHolder
import ru.radiationx.domain.entity.KotlinConfPrincipal
import ru.radiationx.domain.exception.*
import ru.radiationx.domain.helper.UserValidator
import ru.radiationx.domain.repository.SessionizeRepository
import java.time.LocalDateTime


internal fun Application.main() {
    install(Koin) {
        modules(
            listOf(
                serviceConfigModule(this@main),
                sessionizeConfigModule(this@main),
                tokenConfigModule(this@main),
                domainModule(this@main),
                clientModule(this@main),
                appModule(this@main),
                dataModule(this@main),
                dataBaseModule(this@main)
            )
        )
    }

    val serviceConfigHolder by inject<ServiceConfigHolder>()
    val sessionizeConfigHolder by inject<SessionizeConfigHolder>()
    val tokenConfigHolder by inject<TokenConfigHolder>()
    val sessionizeRepository by inject<SessionizeRepository>()
    val batchApiModules by inject<BatchApiRouting>()
    val userValidator by inject<UserValidator>()
    val jwtConfig by inject<JwtConfig>()

    if (!serviceConfigHolder.production) {
        install(CallLogging)
    }

    install(DefaultHeaders)
    install(ConditionalHeaders)
    install(Compression)
    install(PartialContent)
    install(AutoHeadResponse)
    install(XForwardedHeaderSupport)
    install(StatusPages) {
        exception<ServiceUnavailable> { cause ->
            call.respond(withErrorCode(cause), wrapError(cause))
        }
        exception<BadRequest> { cause ->
            call.respond(withErrorCode(cause), wrapError(cause))
        }
        exception<Unauthorized> { cause ->
            call.respond(withErrorCode(cause), wrapError(cause))
        }
        exception<NotFound> { cause ->
            call.respond(withErrorCode(cause), wrapError(cause))
        }
        exception<SecretInvalidError> { cause ->
            call.respond(withErrorCode(cause), wrapError(cause))
        }
        status(HttpStatusCode.NotFound) {
            call.respond(HttpStatusCode.NotFound, wrapError(NotFound()))
        }
        exception<Throwable> { cause ->
            environment.log.error(cause)
            call.respond(withErrorCode(cause), wrapError(cause))
        }
    }

    install(ContentNegotiation) {
        gson {
            registerTypeAdapter(GMTDate::class.java, GMTDateSerializer)
            registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter)
            serializeNulls()
        }
    }

    install(CORS) {
        anyHost()
        header(HttpHeaders.Authorization)
        allowCredentials = true
        listOf(HttpMethod.Put, HttpMethod.Delete, HttpMethod.Options).forEach { method(it) }
    }


    install(Authentication) {
        jwt {
            verifier(jwtConfig.verifier)
            realm = tokenConfigHolder.realm
            validate {
                val token = userToken ?: return@validate null
                val userId = it.payload.getClaim("userId").asInt() ?: return@validate null
                userValidator.getPrincipal(userId, token)
            }
        }
    }

    install(Routing) {
        static {
            default("static/index.html")
            files("static")
        }
        batchApiModules.attachRoute(this)
    }

    launchSyncJob(sessionizeRepository, sessionizeConfigHolder)
}

val ApplicationCall.userToken
    get() = request.parseAuthorizationHeader()?.render()?.removePrefix("Bearer ")

val ApplicationCall.user
    get() = authentication.principal<UserPrincipal>()

class UserPrincipal(val id: Int) : Principal

private fun withErrorCode(throwable: Throwable): HttpStatusCode = when (throwable) {
    is ServiceUnavailable -> HttpStatusCode.ServiceUnavailable
    is BadRequest -> HttpStatusCode.BadRequest
    is Unauthorized -> HttpStatusCode.Unauthorized
    is NotFound -> HttpStatusCode.NotFound
    is SecretInvalidError -> HttpStatusCode.Forbidden
    else -> HttpStatusCode.InternalServerError
}

private fun wrapError(throwable: Throwable): BaseResponse =
    BaseResponse(
        BaseErrorContainer(
            listOf(
                BaseError(
                    throwable.message ?: "No specific message for ${throwable.javaClass.simpleName}"
                )
            )
        )
    )

fun ApplicationCall.findPrincipal(): KotlinConfPrincipal? = principal()