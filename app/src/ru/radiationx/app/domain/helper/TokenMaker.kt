package ru.radiationx.app.domain.helper

import ru.radiationx.app.domain.entity.UserPrincipal

interface TokenMaker {

    suspend fun makeToken(principal: UserPrincipal): String
}