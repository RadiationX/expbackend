package ru.radiationx.app.api

import ru.radiationx.app.api.entity.FavoriteResponse
import ru.radiationx.app.api.entity.UserResponse
import ru.radiationx.app.api.entity.VoteResponse
import ru.radiationx.app.domain.entity.Favorite
import ru.radiationx.app.domain.entity.User
import ru.radiationx.app.domain.entity.Vote

fun User.toResponse(): UserResponse = UserResponse(id, login, createdAt, updatedAt)

fun Favorite.toResponse(): FavoriteResponse = FavoriteResponse(id, user?.toResponse(), sessionId, createdAt, updatedAt)

fun Vote.toResponse():VoteResponse = VoteResponse(id, user?.toResponse(), sessionId, rating, createdAt, updatedAt)