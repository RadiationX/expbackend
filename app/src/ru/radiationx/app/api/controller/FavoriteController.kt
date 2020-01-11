package ru.radiationx.app.api.controller

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import ru.radiationx.app.api.entity.FavoriteRequest
import ru.radiationx.app.api.toResponse
import ru.radiationx.app.userPrincipal
import ru.radiationx.app.base.respondBase
import ru.radiationx.app.domain.usecase.FavoriteUseCase

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
        val request = call.receive<FavoriteRequest>()
        val favorite = favoriteUseCase.createFavorite(principal, request)
        call.respondBase(favorite.toResponse(), HttpStatusCode.Created)
    }

    suspend fun deleteFavorite(call: ApplicationCall) {
        val principal = call.userPrincipal
        val request = call.receive<FavoriteRequest>()
        favoriteUseCase.deleteFavorite(principal, request)
        call.respondBase(statusCode = HttpStatusCode.NoContent)
    }
}