package ru.radiationx.domain.helper

import ru.radiationx.domain.config.ServiceConfigHolder
import ru.radiationx.domain.entity.KotlinConfPrincipal
import ru.radiationx.domain.exception.Unauthorized
import ru.radiationx.domain.repository.UserRepository

class UserValidator(
    private val serviceConfigHolder: ServiceConfigHolder,
    private val userRepository: UserRepository
) {

    fun checkIsAdmin(principal: KotlinConfPrincipal?): KotlinConfPrincipal {
        principal ?: throw Unauthorized()
        if (principal.token != serviceConfigHolder.secret) {
            throw Unauthorized()
        }
        return principal
    }

    suspend fun checkHasUser(principal: KotlinConfPrincipal?): KotlinConfPrincipal {
        principal ?: throw Unauthorized()
        if (userRepository.getUser(principal.token) == null) {
            throw Unauthorized()
        }
        return principal
    }
}