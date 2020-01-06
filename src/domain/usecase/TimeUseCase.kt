package ru.radiationx.domain.usecase

import io.ktor.util.date.GMTDate
import ru.radiationx.domain.entity.KotlinConfPrincipal
import ru.radiationx.domain.exception.BadRequest
import ru.radiationx.domain.helper.UserValidator
import ru.radiationx.domain.repository.TimeRepository

class TimeUseCase(
    private val userValidator: UserValidator,
    private val timeRepository: TimeRepository
) {

    suspend fun getTime(): GMTDate = timeRepository.getTime()

    suspend fun setTime(principal: KotlinConfPrincipal?, timestamp: String?) {
        userValidator.checkIsAdmin(principal)
        timestamp ?: throw BadRequest()

        val time = if (timestamp == "null") {
            null
        } else {
            GMTDate(timestamp.toLong())
        }
        timeRepository.setTime(time)
    }
}