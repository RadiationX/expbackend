package ru.radiationx.api.controller

import io.ktor.application.ApplicationCall
import ru.radiationx.base.respondBase
import ru.radiationx.domain.usecase.UserUseCase

class UsersController(
    private val userUseCase: UserUseCase
) {

    suspend fun getAllUsersCount(call: ApplicationCall) {
        call.respondBase(data = userUseCase.getAllUsersCount().toString())
    }
}