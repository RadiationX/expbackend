package ru.radiationx.data.repository

import io.ktor.util.date.GMTDate
import io.ktor.util.date.plus
import ru.radiationx.domain.OperationResult
import ru.radiationx.domain.repository.TimeRepository

class TimeRepositoryImpl : TimeRepository {

    @Volatile
    private var simulatedTime: GMTDate? = null

    @Volatile
    private var updatedTime: GMTDate = GMTDate()

    override suspend fun getTime(): GMTDate {
        val start = simulatedTime

        return if (start == null) {
            GMTDate()
        } else {
            val offset = GMTDate().timestamp - updatedTime.timestamp
            start + offset
        }
    }

    override suspend fun setTime(time: GMTDate?): OperationResult<GMTDate> {
        simulatedTime = time
        val created = simulatedTime == null
        updatedTime = GMTDate()
        return OperationResult(simulatedTime!!, created)
    }
}