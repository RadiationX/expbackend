package ru.radiationx.app.domain.usecase

import ru.radiationx.app.domain.entity.UserPrincipal
import ru.radiationx.app.domain.helper.UserValidator
import ru.radiationx.app.domain.repository.SessionizeRepository

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