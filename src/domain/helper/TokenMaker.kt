package ru.radiationx.domain.helper

import ru.radiationx.UserPrincipal

interface TokenMaker {

    suspend fun makeToken(principal: UserPrincipal): String
}