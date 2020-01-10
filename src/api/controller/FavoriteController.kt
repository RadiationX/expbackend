package ru.radiationx.api.controller

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import ru.radiationx.base.respondBase
import ru.radiationx.domain.usecase.FavoriteUseCase
import ru.radiationx.user

class FavoriteController(
    private val favoriteUseCase: FavoriteUseCase
) {

    suspend fun getFavorites(call: ApplicationCall) {
        val principal = call.user
        val favorites = favoriteUseCase.getFavorites(principal)
        call.respondBase(data = favorites)
    }

    suspend fun createFavorite(call: ApplicationCall) {
        val principal = call.user
        val sessionId = call.receive<String>()
        favoriteUseCase.createFavorite(principal, sessionId)
        call.respondBase(HttpStatusCode.Created)
    }

    suspend fun deleteFavorite(call: ApplicationCall) {
        val principal = call.user
        val sessionId = call.receive<String>()
        favoriteUseCase.deleteFavorite(principal, sessionId)
        call.respondBase(HttpStatusCode.OK)
    }
}