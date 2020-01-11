package ru.radiationx.domain.repository

import io.ktor.util.date.GMTDate
import ru.radiationx.domain.OperationResult

interface TimeRepository {

    suspend fun getTime(): GMTDate

    suspend fun setTime(time: GMTDate?): OperationResult<GMTDate>
}