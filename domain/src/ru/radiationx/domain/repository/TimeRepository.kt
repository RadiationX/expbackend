package ru.radiationx.domain.repository

import ru.radiationx.domain.OperationResult
import java.time.LocalDateTime

interface TimeRepository {

    suspend fun getTime(): LocalDateTime

    suspend fun setTime(time: LocalDateTime?): OperationResult<LocalDateTime>
}