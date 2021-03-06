package ru.radiationx.app.data

import ru.radiationx.app.data.entity.db.*
import ru.radiationx.app.data.entity.db.ChatMessageRow
import ru.radiationx.app.data.entity.db.ChatRoomRow
import ru.radiationx.app.data.entity.db.FavoriteRow
import ru.radiationx.app.data.entity.db.TokenRow
import ru.radiationx.app.data.entity.db.UserRow
import ru.radiationx.app.data.entity.db.VotesRow
import ru.radiationx.domain.entity.*


internal fun UserRow.asUser(): User = User(id.value, login, password, createdAt, updatedAt)

internal fun FavoriteRow.asFavorite(): Favorite = Favorite(
    id.value,
    user?.asUser(),
    sessionId, createdAt, updatedAt
)

internal fun VotesRow.asVote(): Vote = Vote(
    id.value,
    user?.asUser(),
    sessionId,
    Rating.valueOf(rating),
    createdAt, updatedAt
)

internal fun TokenRow.asToken(): Token = Token(
    id.value,
    user?.asUser(),
    token, ip, info
)

internal fun ChatMessageRow.asMessage(): ChatMessage = ChatMessage(
    id.value,
    room.asRoom(),
    user.asUser(),
    text, createdAt, updatedAt
)

internal fun ChatRoomRow.asRoom(): ChatRoom = ChatRoom(id.value, name, createdAt, updatedAt)
