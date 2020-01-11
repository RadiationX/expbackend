package ru.radiationx.app.common

import ru.radiationx.app.domain.entity.UserPrincipal
import ru.radiationx.app.domain.helper.TokenMaker

class JwtTokenMaker(
    private val jwtConfig: JwtConfig
) : TokenMaker {

    override suspend fun makeToken(principal: UserPrincipal): String = jwtConfig.makeToken(principal)
}