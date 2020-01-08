package ru.radiationx.domain.entity

import java.time.LocalDateTime

data class Vote(
    val id: Int,
    val user: User?,
    val sessionId: String,
    val rating: Rating,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
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