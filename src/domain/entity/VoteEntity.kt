package ru.radiationx.domain.entity

import java.time.LocalDateTime

data class Vote(
    val id: Int,
    val timestamp: LocalDateTime,
    val user: User?,
    val sessionId: String,
    val rating: Rating
)

data class Rating(val value: Int) {

    companion object {
        val BAD = Rating(-1)
        val OK = Rating(0)
        val GOOD = Rating(1)

        fun valueOf(value: Int): Rating =
            listOf(
                BAD,
                OK,
                GOOD
            ).find { it.value == value } ?: error("Invalid rating value")
    }
}