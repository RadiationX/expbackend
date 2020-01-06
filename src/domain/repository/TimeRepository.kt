package ru.radiationx.domain.repository

import io.ktor.util.date.GMTDate

interface TimeRepository {

    suspend fun getTime(): GMTDate

    suspend fun setTime(time: GMTDate?)
}