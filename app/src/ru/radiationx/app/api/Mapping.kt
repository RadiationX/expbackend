package ru.radiationx.app.api

import io.ktor.util.date.toJvmDate
import ru.radiationx.app.api.entity.*
import ru.radiationx.domain.entity.*
import ru.radiationx.domain.helper.asLocalDateTime

fun User.toResponse(): ApiUserResponse = ApiUserResponse(id, login, createdAt, updatedAt)

fun Favorite.toResponse(): ApiFavoriteResponse =
    ApiFavoriteResponse(id, user?.toResponse(), sessionId, createdAt, updatedAt)

fun Vote.toResponse(): ApiVoteResponse =
    ApiVoteResponse(id, user?.toResponse(), sessionId, rating, createdAt, updatedAt)

/* Some trash */

fun ApiAuthCredentialsRequest.toDomain() = AuthCredentials(login, password)
fun ApiFavoriteRequest.toDomain() = FavoriteRequest(sessionId)
fun ApiLiveVideoRequest.toDomain() = LiveVideoRequest(roomId, video)
fun ApiTimeRequest.toDomain() = TimeRequest(timestamp)
fun ApiVoteSessionRequest.toDomain() = VoteSessionRequest(sessionId, rating)

/*Some real trash*/
fun ApiSessionResponse.toDomain() = SessionData(
    id, isServiceSession, isPlenumSession, speakers, description,
    startsAt.toJvmDate().asLocalDateTime(),
    endsAt.toJvmDate().asLocalDateTime(),
    title, roomId, questionAnswers, categoryItems
)

fun ApiSessionizeResponse.toDomain() = SessionizeData(
    sessions.map { it.toDomain() },
    rooms, speakers, questions, categories, partners
)

fun ChatRoom.toResponse() = ApiChatRoomResponse(id, name, createdAt, updatedAt)

fun ChatMessage.toFullResponse() = ApiChatMessageResponse(
    id,
    user.toResponse(),
    user.id,
    room.toResponse(),
    room.id,
    text, createdAt, updatedAt
)

fun ChatMessage.toIdResponse() = ApiChatMessageResponse(
    id,
    null,
    user.id,
    null,
    room.id,
    text, createdAt, updatedAt
)