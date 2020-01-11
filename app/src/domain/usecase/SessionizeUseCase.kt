package ru.radiationx.domain.usecase

import ru.radiationx.domain.entity.UserPrincipal
import ru.radiationx.domain.helper.UserValidator
import ru.radiationx.domain.repository.SessionizeRepository

class SessionizeUseCase(
    private val userValidator: UserValidator,
    private val sessionizeRepository: SessionizeRepository
) {

    suspend fun getData(old: Boolean) = sessionizeRepository.getData(old)

    suspend fun update(principal: UserPrincipal?) {
        userValidator.checkIsAdmin(principal)
        sessionizeRepository.update()
    }

}