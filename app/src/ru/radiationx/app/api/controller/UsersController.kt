package ru.radiationx.app.api.controller

import io.ktor.application.ApplicationCall
import ru.radiationx.app.api.entity.ApiUserCountResponse
import ru.radiationx.app.base.respondBase
import ru.radiationx.domain.usecase.UserUseCase

class UsersController(
    private val userUseCase: UserUseCase
) {

    suspend fun getAllUsersCount(call: ApplicationCall) {
        val usersCount = userUseCase.getAllUsersCount()
        call.respondBase(ApiUserCountResponse(usersCount))
    }
}