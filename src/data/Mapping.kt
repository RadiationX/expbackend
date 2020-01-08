package ru.radiationx.data

import ru.radiationx.data.entity.db.FavoriteRow
import ru.radiationx.data.entity.db.UserRow
import ru.radiationx.data.entity.db.VotesRow
import ru.radiationx.domain.entity.Favorite
import ru.radiationx.domain.entity.Rating
import ru.radiationx.domain.entity.User
import ru.radiationx.domain.entity.Vote
import java.time.LocalDateTime


internal fun UserRow.asUser(): User = User(
    id.value,
    uuid,
    remote,
    LocalDateTime.parse(timestamp)
)

internal fun FavoriteRow.asFavorite(): Favorite = Favorite(
    id.value,
    user?.asUser(),
    sessionId
)

internal fun VotesRow.asVote(): Vote = Vote(
    id.value,
    LocalDateTime.parse(timestamp),
    uuid?.asUser(),
    sessionId,
    Rating.valueOf(rating)
)