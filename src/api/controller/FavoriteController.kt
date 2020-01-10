package ru.radiationx.api.controller

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import ru.radiationx.api.toResponse
import ru.radiationx.base.respondBase
import ru.radiationx.domain.usecase.FavoriteUseCase
import ru.radiationx.userPrincipal

class FavoriteController(
    private val favoriteUseCase: FavoriteUseCase
) {

    suspend fun getFavorites(call: ApplicationCall) {
        val principal = call.userPrincipal
        val favorites = favoriteUseCase.getFavorites(principal)
        call.respondBase(favorites.map { it.toResponse() })
    }

    suspend fun createFavorite(call: ApplicationCall) {
        val principal = call.userPrincipal
        val sessionId = call.receive<String>()
        val favorite = favoriteUseCase.createFavorite(principal, sessionId)
        call.respondBase(favorite.toResponse(), HttpStatusCode.Created)
    }

    suspend fun deleteFavorite(call: ApplicationCall) {
        val principal = call.userPrincipal
        val sessionId = call.receive<String>()
        favoriteUseCase.deleteFavorite(principal, sessionId)
        call.respondBase(statusCode = HttpStatusCode.NoContent)
    }
}