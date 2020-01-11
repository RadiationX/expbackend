package ru.radiationx.app.data.repository

import io.ktor.util.date.GMTDate
import io.ktor.util.date.plus
import ru.radiationx.domain.OperationResult
import ru.radiationx.domain.helper.asDate
import ru.radiationx.domain.helper.asLocalDateTime
import ru.radiationx.domain.repository.TimeRepository
import java.time.LocalDateTime
import java.util.*

class TimeRepositoryImpl : TimeRepository {

    @Volatile
    private var simulatedTime: LocalDateTime? = null

    @Volatile
    private var updatedTime: LocalDateTime = LocalDateTime.now()

    override suspend fun getTime(): LocalDateTime {
        val start = simulatedTime

        return if (start == null) {
            LocalDateTime.now()
        } else {
            val offset = LocalDateTime.now().asDate().time - updatedTime.asDate().time
            Date(start.asDate().time + offset).asLocalDateTime()
        }
    }

    override suspend fun setTime(time: LocalDateTime?): OperationResult<LocalDateTime> {
        simulatedTime = time
        val created = simulatedTime == null
        updatedTime = LocalDateTime.now()
        return OperationResult(simulatedTime!!, created)
    }
}