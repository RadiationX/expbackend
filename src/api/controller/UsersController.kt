package ru.radiationx.api.controller

import io.ktor.application.ApplicationCall
import io.ktor.response.respond
import ru.radiationx.api.entity.UserCountResponse
import ru.radiationx.base.respondBase
import ru.radiationx.domain.usecase.UserUseCase

class UsersController(
    private val userUseCase: UserUseCase
) {

    suspend fun getAllUsersCount(call: ApplicationCall) {
        val usersCount = userUseCase.getAllUsersCount()
        call.respondBase(UserCountResponse(usersCount))
    }
}