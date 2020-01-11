package ru.radiationx.domain.helper

interface HashHelper {

    suspend fun check(original: String, hashed: String): Boolean

    suspend fun hash(original: String): String
}