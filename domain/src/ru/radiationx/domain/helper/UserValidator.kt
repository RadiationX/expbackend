package ru.radiationx.domain.helper

import ru.radiationx.domain.entity.User
import ru.radiationx.domain.exception.Unauthorized
import ru.radiationx.domain.repository.AuthRepository

class UserValidator(
    private val authRepository: AuthRepository
) {

    companion object {
        private const val HOPE_IT_IS_ADMIN_ID = 1
    }

    fun checkIsAdmin(principal: User?): User = when {
        principal == null || principal.id != HOPE_IT_IS_ADMIN_ID -> throw Unauthorized()
        else -> principal
    }

    fun validateUser(principal: User?): User = principal ?: throw Unauthorized()
}