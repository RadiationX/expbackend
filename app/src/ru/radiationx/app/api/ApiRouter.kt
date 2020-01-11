package ru.radiationx.app.api

import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.routing.*
import ru.radiationx.app.api.controller.*

class ApiRouter(
    private val authController: AuthController,
    private val favoriteController: FavoriteController,
    private val fullInfoController: FullInfoController,
    private val liveVideoController: LiveVideoController,
    private val sessionizeController: SessionizeController,
    private val timeController: TimeController,
    private val usersController: UsersController,
    private val voteController: VoteController
) {

    fun attachRouter(routing: Routing) = routing.apply {
        attachAuth()
        attachFavorite()
        attachFullInfo()
        attachLiveVideo()
        attachSessionize()
        attachTime()
        attachUsers()
        attachVote()
    }

    private fun Routing.attachAuth() {
        post("signup") { authController.signUp(call) }

        authenticate(optional = true) {
            post("signin") { authController.signIn(call) }
            post("signout") { authController.signOut(call) }
        }
    }

    private fun Routing.attachFavorite() {
        authenticate {
            route("favorites") {
                get { favoriteController.getFavorites(call) }
                post { favoriteController.createFavorite(call) }
                delete { favoriteController.deleteFavorite(call) }
            }
        }
    }

    private fun Routing.attachFullInfo() {
        authenticate {
            get("all") { fullInfoController.getFullInfo(call) }
            get("all2019") { fullInfoController.getFullInfo2019(call) }
        }
    }

    private fun Routing.attachLiveVideo() {
        authenticate {
            post("live") { liveVideoController.setVideo(call) }
        }
    }

    private fun Routing.attachSessionize() {
        authenticate {
            post("sessionizeSync") { sessionizeController.update(call) }
        }
    }

    private fun Routing.attachTime() {
        get("time") { timeController.getTime(call) }
        authenticate {
            post("time/{timestamp}") { timeController.setTime(call) }
        }
    }

    private fun Routing.attachUsers() {
        route("users") {
            get("count") { usersController.getAllUsersCount(call) }
        }
    }

    private fun Routing.attachVote() {
        authenticate {
            route("votes") {
                get { voteController.getVotes(call) }
                get("all") { voteController.getAllVotes(call) }
                get("summary/{sessionId}") { voteController.getVotesSummary(call) }
                post { voteController.setVote(call) }
                post("required/{count}") { voteController.setRequired(call) }
                delete { voteController.deleteVote(call) }
            }
        }
    }
}