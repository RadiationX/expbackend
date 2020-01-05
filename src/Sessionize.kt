package ru.radiationx

import io.ktor.application.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import ru.radiationx.common.SessionizeData
import java.time.*
import java.util.concurrent.*

@Volatile
private var sessionizeData: SessionizeData? = null

@Volatile
private var oldSessionizeData: SessionizeData? = null

val comeBackLater = HttpStatusCode(477, "Come Back Later")
val tooLate = HttpStatusCode(478, "Too Late")
val keynoteTimeZone = ZoneId.of("Europe/Copenhagen")!!
fun Application.launchSyncJob(
    sessionizeUrl: String,
    oldSessionizeUrl: String,
    sessionizeInterval: Long
) {
    log.info("Synchronizing each $sessionizeInterval minutes with $sessionizeUrl")
    GlobalScope.launch {
        while (true) {
            log.trace("Synchronizing to Sessionize…")
            synchronizeWithSessionize(sessionizeUrl, oldSessionizeUrl)
            log.trace("Finished loading data from Sessionize.")
            delay(TimeUnit.MINUTES.toMillis(sessionizeInterval))
        }
    }
}

suspend fun synchronizeWithSessionize(
    sessionizeUrl: String,
    oldSessionizeUrl: String
) {
    sessionizeData = client.get<SessionizeData>(sessionizeUrl)
    oldSessionizeData = client.get<SessionizeData>(oldSessionizeUrl)
}

fun getSessionizeData(): SessionizeData = sessionizeData ?: throw ServiceUnavailable()

fun getOldSessionizeData(): SessionizeData = oldSessionizeData ?: throw ServiceUnavailable()
