package ru.radiationx

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Payload
import io.ktor.application.*
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
import io.ktor.request.header
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.*
import io.ktor.util.date.GMTDate
import io.ktor.util.error
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject
import ru.radiationx.api.job.launchSyncJob
import ru.radiationx.api.BatchApiRouting
import ru.radiationx.base.BaseError
import ru.radiationx.base.BaseErrorContainer
import ru.radiationx.base.BaseResponse
import ru.radiationx.common.GMTDateSerializer
import ru.radiationx.common.LocalDateTimeAdapter
import ru.radiationx.data.datasource.UserDbDataSource
import ru.radiationx.domain.config.ServiceConfigHolder
import ru.radiationx.domain.config.SessionizeConfigHolder
import ru.radiationx.domain.entity.KotlinConfPrincipal
import ru.radiationx.domain.entity.User
import ru.radiationx.domain.exception.*
import ru.radiationx.domain.helper.UserValidator
import ru.radiationx.domain.repository.SessionizeRepository
import java.time.LocalDateTime
import java.util.*


internal fun Application.main() {
    install(Koin) {
        modules(
            listOf(
                serviceConfigModule(this@main),
                sessionizeConfigModule(this@main),
                domainModule(this@main),
                clientModule(this@main),
                apiModule(this@main),
                dataModule(this@main),
                dataBaseModule(this@main)
            )
        )
    }

    val serviceConfigHolder by inject<ServiceConfigHolder>()
    val sessionizeConfigHolder by inject<SessionizeConfigHolder>()
    val sessionizeRepository by inject<SessionizeRepository>()
    val batchApiModules by inject<BatchApiRouting>()

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

    val userDbDataSource by inject<UserDbDataSource>()
    val userValidator by inject<UserValidator>()

    install(Authentication) {
        jwt {
            verifier(JwtConfig.verifier)
            realm = "ktor.io"
            validate {
                val token = userToken
                it.payload
                    .getClaim("userId").asInt()
                    ?.let { userId -> userValidator.checkToken(token, userId) }
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

object JwtConfig {

    private const val secret = "zAP5MBA4B4Ijz0MZaS48"
    private const val issuer = "ktor.io"
    private const val validityInMs = 36_000_00 * 10 // 10 hours
    private val algorithm = Algorithm.HMAC512(secret)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()

    /**
     * Produce a token for this combination of User and Account
     */
    fun makeToken(user: UserPrincipal): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withClaim("userId", user.id)
        .withExpiresAt(getExpiration())
        .sign(algorithm)

    /**
     * Calculate the expiration Date based on current time + the given validity
     */
    private fun getExpiration() = Date(System.currentTimeMillis() + validityInMs)

}


/*private fun Route.authenticate() {
    val bearer = "Bearer "
    intercept(ApplicationCallPipeline.Features) {
        val authorization = call.request.header(HttpHeaders.Authorization) ?: return@intercept
        if (!authorization.startsWith(bearer)) return@intercept
        val token = authorization.removePrefix(bearer).trim()
        call.authentication.principal(KotlinConfPrincipal(token))
    }
}*/

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