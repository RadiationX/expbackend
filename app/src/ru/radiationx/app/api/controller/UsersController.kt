package ru.radiationx.app.api.controller

import io.ktor.application.ApplicationCall
import ru.radiationx.app.api.entity.UserCountResponse
import ru.radiationx.app.base.respondBase
import ru.radiationx.app.domain.usecase.UserUseCase

class UsersController(
    private val userUseCase: UserUseCase
) {

    suspend fun getAllUsersCount(call: ApplicationCall) {
        val usersCount = userUseCase.getAllUsersCount()
        call.respondBase(UserCountResponse(usersCount))
    }
}