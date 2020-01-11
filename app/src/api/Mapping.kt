package ru.radiationx.api

import ru.radiationx.api.entity.FavoriteResponse
import ru.radiationx.api.entity.UserResponse
import ru.radiationx.api.entity.VoteResponse
import ru.radiationx.domain.entity.Favorite
import ru.radiationx.domain.entity.User
import ru.radiationx.domain.entity.Vote

fun User.toResponse(): UserResponse = UserResponse(id, login, createdAt, updatedAt)

fun Favorite.toResponse(): FavoriteResponse = FavoriteResponse(id, user?.toResponse(), sessionId, createdAt, updatedAt)

fun Vote.toResponse():VoteResponse = VoteResponse(id, user?.toResponse(), sessionId, rating, createdAt, updatedAt)