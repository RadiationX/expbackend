package ru.radiationx.common

import ru.radiationx.UserPrincipal
import ru.radiationx.domain.helper.TokenMaker

class JwtTokenMaker(
    private val jwtConfig: JwtConfig
) : TokenMaker {

    override suspend fun makeToken(principal: UserPrincipal): String = jwtConfig.makeToken(principal)
}