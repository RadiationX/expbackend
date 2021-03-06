package ru.radiationx.app.common

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.radiationx.domain.config.TokenConfigHolder
import ru.radiationx.app.UserPrincipal
import ru.radiationx.domain.entity.User
import java.util.*

class JwtConfig(
    private val configHolder: TokenConfigHolder
) {
    private val algorithm = Algorithm.HMAC512(configHolder.secret)

    val verifier: JWTVerifier = JWT.require(algorithm)
        .withIssuer(configHolder.issuer)
        .build()

    suspend fun makeToken(principal: User): String =
        withContext(Dispatchers.Default) {
            JWT.create()
                .withSubject("Authentication")
                .withIssuer(configHolder.issuer)
                .withClaim("userId", principal.id)
                .withExpiresAt(getExpiration())
                .sign(algorithm)
        }

    private fun getExpiration() =
        Date(System.currentTimeMillis() + configHolder.expiration)

}