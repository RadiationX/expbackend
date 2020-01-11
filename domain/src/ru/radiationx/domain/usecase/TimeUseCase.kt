package ru.radiationx.domain.usecase

import ru.radiationx.domain.OperationResult
import ru.radiationx.domain.entity.TimeRequest
import ru.radiationx.domain.entity.User
import ru.radiationx.domain.exception.BadRequestException
import ru.radiationx.domain.helper.UserValidator
import ru.radiationx.domain.helper.asLocalDateTime
import ru.radiationx.domain.repository.TimeRepository
import java.time.LocalDateTime
import java.util.*

class TimeUseCase(
    private val userValidator: UserValidator,
    private val timeRepository: TimeRepository
) {

    suspend fun getTime(): LocalDateTime = timeRepository.getTime()

    suspend fun setTime(principal: User?, request: TimeRequest): OperationResult<LocalDateTime> {
        userValidator.checkIsAdmin(principal)
        request.timestamp ?: throw BadRequestException("No timestamp")

        val time = if (request.timestamp == "null") {
            null
        } else {
            val timestamp = request.timestamp.toLongOrNull() ?: throw BadRequestException("No wrong timestamp format")
            Date(timestamp).asLocalDateTime()
        }
        return timeRepository.setTime(time)
    }
}