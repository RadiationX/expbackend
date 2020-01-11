package ru.radiationx.app.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import ru.radiationx.app.domain.config.SessionizeConfigHolder
import ru.radiationx.app.domain.entity.SessionizeData
import ru.radiationx.app.domain.exception.ServiceUnavailable
import ru.radiationx.app.domain.repository.SessionizeRepository

class SessionizeRepositoryImpl(
    private val sessionizeConfigHolder: SessionizeConfigHolder,
    private val sessionizeClient: HttpClient
) : SessionizeRepository {

    @Volatile
    private var sessionizeData: SessionizeData? = null

    @Volatile
    private var oldSessionizeData: SessionizeData? = null

    override suspend fun getData(old: Boolean): SessionizeData {
        val data = if (old) oldSessionizeData else sessionizeData
        data ?: throw ServiceUnavailable()
        return data
    }

    override suspend fun update() {
        sessionizeConfigHolder.apply {
            sessionizeData = sessionizeClient.get<SessionizeData>(url)
            oldSessionizeData = sessionizeClient.get<SessionizeData>(oldUrl)
        }
    }
}