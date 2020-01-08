package ru.radiationx.api.route

import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.routing.*
import ru.radiationx.api.entity.VoteData
import ru.radiationx.findPrincipal
import ru.radiationx.base.respondBase
import ru.radiationx.domain.entity.Rating
import ru.radiationx.domain.usecase.VoteUseCase
import ru.radiationx.user

class ApiVoteRoute(
    private val voteUseCase: VoteUseCase
) : BaseApiRoute() {

    override fun attachRoute(routing: Routing) = routing.apply {

        authenticate {
            route("votes") {

                get {
                    val principal = call.user
                    val votes = voteUseCase.getVotes(principal)
                    call.respondBase(data = votes)
                }

                get("all") {
                    val principal = call.user
                    val votes = voteUseCase.getAllVotes(principal)
                    call.respondBase(data = votes)
                }

                get("summary/{sessionId}") {
                    val principal = call.user
                    val sessionId = call.parameters["sessionId"]
                    val votesSummary = voteUseCase
                        .getVotesSummary(principal, sessionId)
                        .mapKeys {
                            return@mapKeys when (it.key) {
                                Rating.OK -> "soso"
                                Rating.GOOD -> "good"
                                Rating.BAD -> "bad"
                                else -> "unknown"
                            }
                        }
                        .toMutableMap()

                    if ("bad" !in votesSummary) votesSummary["bad"] = 0
                    if ("good" !in votesSummary) votesSummary["good"] = 0
                    if ("soso" !in votesSummary) votesSummary["soso"] = 0

                    call.respondBase(data = votesSummary)
                }

                post {
                    val principal = call.user
                    val vote = call.receive<VoteData>()
                    val sessionId = vote.sessionId
                    val rating = vote.rating

                    val status = if (voteUseCase.changeVote(principal, sessionId, rating)) {
                        HttpStatusCode.Created
                    } else {
                        HttpStatusCode.OK
                    }
                    call.respondBase(status)
                }

                post("required/{count}") {
                    val principal = call.user
                    val count = call.parameters["count"]
                    voteUseCase.setRequired(principal, count)
                    call.respondBase(HttpStatusCode.OK)
                }

                delete {
                    val principal = call.user
                    val vote = call.receive<VoteData>()
                    val sessionId = vote.sessionId
                    voteUseCase.deleteVote(principal, sessionId)
                    call.respondBase(HttpStatusCode.OK)
                }
            }
        }
    }
}