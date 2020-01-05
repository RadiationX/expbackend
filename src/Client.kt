package ru.radiationx

import io.ktor.client.HttpClient
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.util.date.GMTDate
import ru.radiationx.common.GMTDateSerializer

internal val client = HttpClient {
    install(JsonFeature) {
        serializer = GsonSerializer {
            registerTypeAdapter(GMTDate::class.java, GMTDateSerializer)
        }
    }
}
