package ru.radiationx.domain.helper

import ru.radiationx.domain.entity.UserPrincipal

interface TokenMaker {

    suspend fun makeToken(principal: UserPrincipal): String
}