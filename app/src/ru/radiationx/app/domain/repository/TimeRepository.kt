package ru.radiationx.app.domain.repository

import io.ktor.util.date.GMTDate
import ru.radiationx.app.domain.OperationResult

interface TimeRepository {

    suspend fun getTime(): GMTDate

    suspend fun setTime(time: GMTDate?): OperationResult<GMTDate>
}