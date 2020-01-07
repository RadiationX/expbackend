package ru.radiationx.api.route

import io.ktor.routing.Route
import io.ktor.routing.Routing

abstract class BaseApiRoute {

    abstract fun attachRoute(routing: Routing): Route
}