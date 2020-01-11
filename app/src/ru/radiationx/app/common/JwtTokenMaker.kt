package ru.radiationx.app.common

import ru.radiationx.app.UserPrincipal
import ru.radiationx.domain.entity.User
import ru.radiationx.domain.helper.TokenMaker

class JwtTokenMaker(
    private val jwtConfig: JwtConfig
) : TokenMaker {

    override suspend fun makeToken(principal: User): String = jwtConfig.makeToken(principal)
}