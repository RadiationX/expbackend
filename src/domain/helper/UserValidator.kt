package ru.radiationx.domain.helper

import ru.radiationx.domain.entity.UserPrincipal
import ru.radiationx.domain.exception.Unauthorized
import ru.radiationx.domain.repository.AuthRepository

class UserValidator(
    private val authRepository: AuthRepository
) {

    companion object {
        private const val HOPE_IT_IS_ADMIN_ID = 1
    }

    suspend fun getPrincipal(userId: Int, token: String): UserPrincipal? = authRepository.getPrincipal(userId, token)

    fun checkIsAdmin(principal: UserPrincipal?): UserPrincipal = when {
        principal == null || principal.id != HOPE_IT_IS_ADMIN_ID -> throw Unauthorized()
        else -> principal
    }

    fun validateUser(principal: UserPrincipal?): UserPrincipal = principal ?: throw Unauthorized()
}