package ru.radiationx.domain.usecase

import io.ktor.util.date.GMTDate
import ru.radiationx.api.entity.TimeRequest
import ru.radiationx.domain.OperationResult
import ru.radiationx.domain.entity.UserPrincipal
import ru.radiationx.domain.exception.BadRequestException
import ru.radiationx.domain.helper.UserValidator
import ru.radiationx.domain.repository.TimeRepository

class TimeUseCase(
    private val userValidator: UserValidator,
    private val timeRepository: TimeRepository
) {

    suspend fun getTime(): GMTDate = timeRepository.getTime()

    suspend fun setTime(principal: UserPrincipal?, request: TimeRequest): OperationResult<GMTDate> {
        userValidator.checkIsAdmin(principal)
        val timestamp = request.timestamp ?: throw BadRequestException("No timestamp")

        val time = if (timestamp == "null") {
            null
        } else {
            GMTDate(timestamp.toLong())
        }
        return timeRepository.setTime(time)
    }
}