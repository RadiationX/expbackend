package ru.radiationx.app.api

import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.request.receiveParameters
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.websocket.webSocket
import ru.radiationx.app.WS_AUTH
import ru.radiationx.app.api.controller.*
import ru.radiationx.app.data.datasource.AuthDbDataSource
import ru.radiationx.app.data.datasource.ChatDbDataSource

class ApiRouter(
    private val authController: AuthController,
    private val favoriteController: FavoriteController,
    private val fullInfoController: FullInfoController,
    private val liveVideoController: LiveVideoController,
    private val sessionizeController: SessionizeController,
    private val timeController: TimeController,
    private val usersController: UsersController,
    private val voteController: VoteController,
    private val chatController: ChatController,
    private val chatDbDataSource: ChatDbDataSource
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
        attachWsChat()

        routing.apply {
            route("test_chat") {
                get("room_users") {
                    val params = call.parameters
                    call.respond(chatDbDataSource.getUsersInRoom(params["roomId"]!!.toInt()))
                }
                get("room") {
                    val params = call.parameters
                    call.respond(chatDbDataSource.getRoom(params["roomId"]!!.toInt()))
                }
                post("room") {
                    val params = call.receiveParameters()
                    chatDbDataSource.createRoom(params["name"]!!)
                    call.respond("ok")
                }
                post("user_room") {
                    val params = call.receiveParameters()
                    chatDbDataSource.addUser(params["roomId"]!!.toInt(), params["userId"]!!.toInt())
                    call.respond("ok")
                }
            }
        }
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

    private fun Routing.attachWsChat() {
        authenticate(configurations = *arrayOf(WS_AUTH), optional = false) {
            webSocket("/chat") {
                chatController.handleSession(this)
            }
        }
    }
}