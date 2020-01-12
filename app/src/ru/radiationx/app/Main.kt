package ru.radiationx.app

import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.*
import io.ktor.auth.jwt.JWTCredential
import io.ktor.auth.jwt.jwt
import io.ktor.features.*
import io.ktor.gson.gson
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.auth.HttpAuthHeader
import io.ktor.http.cio.websocket.*
import io.ktor.http.content.default
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.Routing
import io.ktor.routing.routing
import io.ktor.util.AttributeKey
import io.ktor.util.date.GMTDate
import io.ktor.util.error
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.ClosedSendChannelException
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
import ru.radiationx.domain.repository.SessionizeRepository
import ru.radiationx.domain.usecase.AuthService
import java.time.Duration
import java.time.LocalDateTime

const val REST_AUTH = "rest"
const val WS_AUTH = "ws"

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

            authHeader { call ->
                HttpAuthHeader.Single("Bearer", "${call.parameters["token"]}")
                val header = try {
                    call.request.parseAuthorizationHeader()
                } catch (ex: IllegalArgumentException) {
                    null
                }
                call.saveUserToken(header)
                header
            }
            validate {
                val token = userToken ?: return@validate null
                val userId = it.payload.getClaim("userId").asInt() ?: return@validate null
                authService.getUser(userId, token)?.let { user ->
                    UserPrincipal(user)
                }
            }
        }
        jwt(WS_AUTH) {
            verifier(jwtConfig.verifier)
            realm = tokenConfigHolder.realm
            authHeader { call ->
                val tokenParameter = call.parameters["token"]
                val header = tokenParameter?.let {
                    HttpAuthHeader.Single("Bearer", it)
                }
                call.saveUserToken(header)
                header
            }
            validate {
                val token = userToken ?: return@validate null
                val userId = it.payload.getClaim("userId").asInt() ?: return@validate null
                authService.getUser(userId, token)?.let { user ->
                    UserPrincipal(user)
                }
            }
        }
    }

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(60) // Disabled (null) by default
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE // Disabled (max value). The connection will be closed if surpassed this length.
        masking = false
    }

    install(Routing) {
        static {
            default("static/index.html")
            files("static")
        }
        apiRouter.attachRouter(this)
    }

    routing {
        authenticate(configurations = *arrayOf(WS_AUTH), optional = false) {

            webSocket("/") {

                // websocketSession
                println("Connect user ${call.userPrincipal?.user}")
                try {
                    for (frame in incoming) {
                        try {
                            when (frame) {
                                is Frame.Text -> {
                                    val text = frame.readText()
                                    outgoing.send(Frame.Text("YOU SAID: $text"))
                                    if (text.equals("bye", ignoreCase = true)) {
                                        close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                                    }
                                }
                            }
                        } catch (e: Throwable) {
                            if (e is ClosedReceiveChannelException || e is ClosedSendChannelException) {
                                throw e
                            }
                            println("onError ${1}, ${this.outgoing.isClosedForSend}")
                            e.printStackTrace()
                            outgoing.send(Frame.Text(e.message.orEmpty()))
                        }

                    }
                } catch (e: ClosedReceiveChannelException) {
                    println("onClose receive ${closeReason.await()}")
                } catch (e: ClosedSendChannelException) {
                    println("onClose send ${closeReason.await()}")
                } catch (e: Throwable) {
                    println("unhandled onError ${closeReason.await()}")
                    e.printStackTrace()
                }
            }

        }
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

private val authHeaderAttribute = AttributeKey<String>("AuthToken")

private fun ApplicationCall.saveUserToken(authHeader: HttpAuthHeader?) {
    if (authHeader != null) {
        attributes.put(authHeaderAttribute, authHeader.render())
    }
}

val ApplicationCall.userToken
    get() = attributes.getOrNull(authHeaderAttribute)?.removePrefix("Bearer ")

val ApplicationCall.userPrincipal
    get() = authentication.principal<UserPrincipal>()

data class UserPrincipal(val user: User) : Principal {
    val id: Int = user.id
}