package ru.radiationx.app

import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.Principal
import io.ktor.auth.authentication
import io.ktor.auth.jwt.jwt
import io.ktor.auth.parseAuthorizationHeader
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
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject
import ru.radiationx.app.api.ApiRouter
import ru.radiationx.app.api.job.launchSyncJob
import ru.radiationx.app.base.BaseErrorContainer
import ru.radiationx.app.common.GMTDateSerializer
import ru.radiationx.app.common.JwtConfig
import ru.radiationx.app.common.LocalDateTimeAdapter
import ru.radiationx.domain.config.ServiceConfigHolder
import ru.radiationx.domain.config.SessionizeConfigHolder
import ru.radiationx.domain.config.TokenConfigHolder
import ru.radiationx.domain.entity.User
import ru.radiationx.domain.exception.*
import ru.radiationx.domain.exception.BadRequestException
import ru.radiationx.domain.repository.AuthRepository
import ru.radiationx.domain.repository.SessionizeRepository
import ru.radiationx.domain.usecase.AuthService
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
    val apiRouter by inject<ApiRouter>()
    val authService by inject<AuthService>()
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
        exception<BadRequestException> { cause ->
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
                authService.getUser(userId, token)?.let { user ->
                    UserPrincipal(user)
                }
            }
        }
    }

    install(Routing) {
        static {
            default("static/index.html")
            files("static")
        }
        apiRouter.attachRouter(this)
    }

    launchSyncJob(sessionizeRepository, sessionizeConfigHolder)
}

private fun withErrorCode(throwable: Throwable): HttpStatusCode = when (throwable) {
    is ServiceUnavailable -> HttpStatusCode.ServiceUnavailable
    is BadRequestException -> HttpStatusCode.BadRequest
    is Unauthorized -> HttpStatusCode.Unauthorized
    is NotFound -> HttpStatusCode.NotFound
    is SecretInvalidError -> HttpStatusCode.Forbidden
    else -> HttpStatusCode.InternalServerError
}

private fun wrapError(throwable: Throwable): BaseErrorContainer = BaseErrorContainer(
    throwable.message ?: "No specific message for ${throwable.javaClass.simpleName}"
)

val ApplicationCall.userToken
    get() = request.parseAuthorizationHeader()?.render()?.removePrefix("Bearer ")

val ApplicationCall.userPrincipal
    get() = authentication.principal<UserPrincipal>()

class UserPrincipal(val user: User) : Principal {
    val id: Int = user.id
}