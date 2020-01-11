package ru.radiationx.api.job

import io.ktor.application.Application
import io.ktor.application.log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.radiationx.domain.config.SessionizeConfigHolder
import ru.radiationx.domain.repository.SessionizeRepository
import java.util.concurrent.TimeUnit

fun Application.launchSyncJob(
    sessionizeRepository: SessionizeRepository,
    sessionizeConfigHolder: SessionizeConfigHolder
) {
    sessionizeConfigHolder.apply {
        log.info("Synchronizing each $interval minutes with $url")
        GlobalScope.launch {
            while (true) {
                log.trace("Synchronizing to Sessionize…")
                try {
                    sessionizeRepository.update()
                    log.trace("Finished loading data from Sessionize. Next sync after $interval.")
                } catch (ex: Throwable) {
                    log.trace("Error load Sessionize by ${ex.message}. Next sync after $interval.")
                    ex.printStackTrace()
                }
                delay(TimeUnit.MINUTES.toMillis(interval))
            }
        }
    }
}