package ru.radiationx

import com.google.gson.reflect.TypeToken
import io.ktor.application.*
import io.ktor.auth.Principal
import io.ktor.auth.authentication
import io.ktor.features.*
import io.ktor.gson.gson
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.default
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.request.header
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.Routing
import io.ktor.util.date.GMTDate
import io.ktor.util.error
import ru.radiationx.base.BaseError
import ru.radiationx.base.BaseErrorContainer
import ru.radiationx.base.BaseResponse
import ru.radiationx.common.GMTDateSerializer
import java.lang.reflect.Type


internal fun Application.main() {
    val config = environment.config
    val serviceConfig = config.config("service")
    val mode = serviceConfig.property("environment").getString()
    log.info("Environment: $mode")
    val sessionizeConfig = config.config("sessionize")
    val sessionizeUrl = sessionizeConfig.property("url").getString()
    val oldSessionizeUrl = sessionizeConfig.property("oldUrl").getString()
    val sessionizeInterval = sessionizeConfig.property("interval").getString().toLong()
    val adminSecret = serviceConfig.property("secret").getString()
    val production = mode == "production"

    if (!production) {
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
            val listType: Type = object : TypeToken<GMTDate>() {}.type
            registerTypeAdapter(GMTDate::class.java, GMTDateSerializer)
            serializeNulls()
            //registerTypeHierarchyAdapter(GMTDate::class.java, GMTDateSerializer)
        }
    }

    install(CORS) {
        anyHost()
        header(HttpHeaders.Authorization)
        allowCredentials = true
        listOf(HttpMethod.Put, HttpMethod.Delete, HttpMethod.Options).forEach { method(it) }
    }

    val database = DatabaseModule(this)
    install(Routing) {
        authenticate()
        static {
            default("static/index.html")
            files("static")
        }

        api(database, sessionizeUrl, oldSessionizeUrl, adminSecret)
    }

    intercept(ApplicationCallPipeline.Features) {
        //throw Exception("Intercepted")
        call.response.header("kekkeke", call.response.status()?.description ?: "watafak")
    }
    launchSyncJob(sessionizeUrl, oldSessionizeUrl, sessionizeInterval)
}

private fun Route.authenticate() {
    val bearer = "Bearer "
    intercept(ApplicationCallPipeline.Features) {
        val authorization = call.request.header(HttpHeaders.Authorization) ?: return@intercept
        if (!authorization.startsWith(bearer)) return@intercept
        val token = authorization.removePrefix(bearer).trim()
        call.authentication.principal(KotlinConfPrincipal(token))
    }
}

internal class KotlinConfPrincipal(val token: String) : Principal

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
