package ru.radiationx.domain.helper

import ru.radiationx.domain.entity.User

interface TokenMaker {

    suspend fun makeToken(principal: User): String
}