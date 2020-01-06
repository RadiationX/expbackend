package ru.radiationx.domain.entity

import io.ktor.auth.Principal

class KotlinConfPrincipal(val token: String) : Principal