package ru.radiationx.app.api.entity

import ru.radiationx.app.api.base.ApiEntity

data class ApiLiveVideoRequest(
    val roomId: Int? = null,
    val video: String? = null
) : ApiEntity