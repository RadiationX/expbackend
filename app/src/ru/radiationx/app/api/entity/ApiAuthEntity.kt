package ru.radiationx.app.api.entity

import ru.radiationx.app.api.base.ApiEntity

data class ApiAuthTokenResponse(
    val token: String
) : ApiEntity

data class ApiAuthCredentialsRequest(
    val login: String? = null,
    val password: String? = null
) : ApiEntity