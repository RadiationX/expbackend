package ru.radiationx.api.entity

import ru.radiationx.api.base.ApiEntity

data class TokenResponse(
    val token: String
) : ApiEntity