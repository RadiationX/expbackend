package ru.radiationx.app.api.controller

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import ru.radiationx.app.api.entity.ApiFavoriteRequest
import ru.radiationx.app.api.toDomain
import ru.radiationx.app.api.toResponse
import ru.radiationx.app.userPrincipal
import ru.radiationx.app.api.base.respondBase
import ru.radiationx.domain.usecase.FavoriteUseCase

class FavoriteController(
    private val favoriteUseCase: FavoriteUseCase
) {

    suspend fun getFavorites(call: ApplicationCall) {
        val principal = call.userPrincipal
        val favorites = favoriteUseCase.getFavorites(principal?.user)
        call.respondBase(favorites.map { it.toResponse() })
    }

    suspend fun createFavorite(call: ApplicationCall) {
        val principal = call.userPrincipal
        val request = call.receive<ApiFavoriteRequest>()
        val favorite = favoriteUseCase.createFavorite(principal?.user, request.toDomain())
        call.respondBase(favorite.toResponse(), HttpStatusCode.Created)
    }

    suspend fun deleteFavorite(call: ApplicationCall) {
        val principal = call.userPrincipal
        val request = call.receive<ApiFavoriteRequest>()
        favoriteUseCase.deleteFavorite(principal?.user, request.toDomain())
        call.respondBase(statusCode = HttpStatusCode.NoContent)
    }
}