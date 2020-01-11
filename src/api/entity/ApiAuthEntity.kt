package ru.radiationx.api.entity

import ru.radiationx.api.base.ApiEntity

data class AuthTokenResponse(
    val token: String
) : ApiEntity

data class AuthCredentialsRequest(
    val login: String? = null,
    val password: String? = null
) : ApiEntity